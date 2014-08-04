package opennlp.tools.formats;

import com.sun.org.apache.xpath.internal.operations.Bool;
import opennlp.tools.cmdline.ArgumentParser;
import opennlp.tools.cmdline.CmdLineUtil;
import opennlp.tools.cmdline.StreamFactoryRegistry;
import opennlp.tools.cmdline.params.BasicFormatParams;
import opennlp.tools.postag.POSSample;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class NkjpCorpusPOSSampleStreamFactory extends AbstractSampleStreamFactory<POSSample> {
	interface Parameters extends BasicFormatParams {
		@ArgumentParser.ParameterDescription(valueName = "nkjp_full|nkjp|universal", description = "Which tagset should model learn. Nkjp which has 32 tags, or universal which is simplified and has only 12.")
		String getTagset();

		@ArgumentParser.ParameterDescription(valueName = "true|false", description = "Should ąęćłńóśżźĄĘĆŁŃÓŚŻŹ be replaced to aeclnoszzAECLNOSZZ when reading data.")
		Boolean getReplacePolishCharacters();

		@Override
		@ArgumentParser.ParameterDescription(valueName = "sampleData", description = "Path to the nkjp directory corpus. It just should be an extracted nkjp corpus.")
		File getData();
	}

	protected <P> NkjpCorpusPOSSampleStreamFactory(Class<P> params) {
		super(params);
	}

	public static void registerFactory() {
		StreamFactoryRegistry.registerFactory(POSSample.class, "nkjp", new NkjpCorpusPOSSampleStreamFactory(Parameters.class));
	}

	private NkjpPOSSampleStream.NkjpTagset ParseTagSet(String argument) {
		switch (argument) {
			case "nkjp_full" : return NkjpPOSSampleStream.NkjpTagset.NKJP_FULL;
			case "nkjp" : return NkjpPOSSampleStream.NkjpTagset.NKJP_SIMPLE;
			case "universal" : return NkjpPOSSampleStream.NkjpTagset.UNIVERSAL_TAGSET;
			default: throw new IllegalArgumentException("Unknown tagset: " + argument);
		}
	}

	@Override
	public ObjectStream<POSSample> create(String[] args) {
		Parameters params = ArgumentParser.parse(args, Parameters.class);


		try {
			NkjpPOSSampleStream.NkjpTagset chosenTagset = ParseTagSet(params.getTagset());
			File rootCorpusDictionary = params.getData();

			if (!rootCorpusDictionary.exists()) {
				throw new IOException("Directory doesn't exist.");
			}
			if (!rootCorpusDictionary.isDirectory()) {
				throw new IOException("Data path should be a dictionary with corpus.");
			}

			File[] directories = rootCorpusDictionary.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.isDirectory();
				}
			});

			Arrays.sort(directories, new Comparator<File>() {
				@Override
				public int compare(File o1, File o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});

			ArrayList<InputStreamFactory> corpusPosFileList = new ArrayList<InputStreamFactory>();
			for (File dir : directories) {
				MarkableFileInputStreamFactory mfisf = new MarkableFileInputStreamFactory(new File(dir, "ann_morphosyntax.xml"));
				corpusPosFileList.add(mfisf);
			}

			InputStreamFactory[] corpusPosFiles = corpusPosFileList.toArray(new InputStreamFactory[corpusPosFileList.size()]);

			return new NkjpCorpusPOSSampleStream(corpusPosFiles, chosenTagset, params.getReplacePolishCharacters());
		} catch (IOException e) {
			throw CmdLineUtil.createObjectStreamError(e);
		}
	}
}
