package opennlp.tools.formats;

import opennlp.tools.cmdline.SystemInputStreamFactory;
import opennlp.tools.postag.POSSample;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.ObjectStream;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class NkjpCorpusPOSSampleStream implements ObjectStream<POSSample> {
	private final List<InputStreamFactory> corpusPosFiles;
	private int currentIndex = 0;
	private ObjectStream<POSSample> currentPosStream;

	public NkjpCorpusPOSSampleStream(InputStreamFactory[] fileInputStreamFactory) throws IOException {
		corpusPosFiles = Arrays.asList(fileInputStreamFactory);
		init();
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
			NkjpPOSSampleStream newStream =  new NkjpPOSSampleStream(corpusPosFiles.get(currentIndex));
			currentIndex++;
			return newStream;
		} catch (Exception e) {
			throw new IOException("Couldn't create POS stream for nkjp file.", e);
		}
	}

	private void init() throws IOException {
		currentIndex = 0;
		currentPosStream = getNextPosStream();
	}

	@Override
	public void reset() throws IOException {
		init();
	}

	@Override
	public void close() throws IOException {
	}
}
