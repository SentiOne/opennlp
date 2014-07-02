package opennlp.tools.formats;

import opennlp.tools.cmdline.SystemInputStreamFactory;
import opennlp.tools.postag.POSSample;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.ObjectStream;

import java.io.*;
import java.util.List;
import java.util.Arrays;
import java.util.Comparator;

public class NkjpCorpusPOSSampleStream implements ObjectStream<POSSample> {
	private final List<File> corpusPosFiles;
	private int currentIndex = 0;
	private ObjectStream<POSSample> currentPosStream;

	public NkjpCorpusPOSSampleStream(File[] xmlfiles) throws IOException {
		corpusPosFiles = Arrays.asList(xmlfiles);
		currentPosStream = getNextPosStream();
	}

	@Override
	public POSSample read() throws IOException {
		return getNextPosSample();
	}

	private POSSample getNextPosSample() throws IOException {
		if (currentPosStream == null)
			return null;

		POSSample sample = currentPosStream.read();
		if (sample == null) {
			currentPosStream.close();
			currentPosStream = getNextPosStream();
			return getNextPosSample();
		}

		return sample;
	}

	private NkjpPOSSampleStream getNextPosStream() throws IOException {
		if (corpusPosFiles.size() <= currentIndex) {
			return null;
		}

		try {
			NkjpPOSSampleStream newStream =  new NkjpPOSSampleStream(new InputStreamFactory() {
				@Override
				public InputStream createInputStream() throws IOException {
					return new FileInputStream(corpusPosFiles.get(currentIndex));
				}
			});
			currentIndex++;
			return newStream;
		} catch (Exception e) {
			throw new IOException("Couldn't create POS stream for nkjp file.", e);
		}
	}

	@Override
	public void reset() throws IOException, UnsupportedOperationException {
		currentIndex = 0;
		close();
		currentPosStream = getNextPosStream();
	}

	@Override
	public void close() throws IOException {
		if (currentPosStream != null) {
			currentPosStream.close();
		}
	}
}
