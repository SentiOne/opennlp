package opennlp.tools.formats;

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
		@ArgumentParser.ParameterDescription(valueName = "nkjp|universal")
		String getTagset();
	}

	protected <P> NkjpCorpusPOSSampleStreamFactory(Class<P> params) {
		super(params);
	}

	public static void registerFactory() {
		StreamFactoryRegistry.registerFactory(POSSample.class, "nkjp", new NkjpCorpusPOSSampleStreamFactory(Parameters.class));
	}

	@Override
	public ObjectStream<POSSample> create(String[] args) {
		Parameters params = ArgumentParser.parse(args, Parameters.class);

		try {
			String tagSet = params.getTagset();
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

			return new NkjpCorpusPOSSampleStream(corpusPosFiles);
		} catch (IOException e) {
			throw CmdLineUtil.createObjectStreamError(e);
		}
	}
}
