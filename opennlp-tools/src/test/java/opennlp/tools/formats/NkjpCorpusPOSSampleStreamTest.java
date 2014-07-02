package opennlp.tools.formats;

import opennlp.tools.postag.POSSample;
import opennlp.tools.util.ObjectStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.*;

public class NkjpCorpusPOSSampleStreamTest {
	private ObjectStream<POSSample> sut;

	@Before
	public void setUp() throws Exception {
		NkjpCorpusPOSSampleStreamFactory factory = new NkjpCorpusPOSSampleStreamFactory(NkjpCorpusPOSSampleStreamFactory.Parameters.class);
		sut = factory.create(new String[] { "-data", "/home/slafulk/Downloads/NKJP-PodkorpusMilionowy-1.0", "-tagset", "nkjp", "-encoding", "utf-8" });
	}

	@After
	public void tearDown() throws Exception {
		sut.close();
	}

	@Test
	public void testRead() throws Exception {
		long sentenceCount = 0;
		long wordCount = 0;
		long posCount = 0;

		POSSample posSample = sut.read();
		while (posSample != null) {
			sentenceCount += 1;
			wordCount += posSample.getSentence().length;
			posCount += posSample.getTags().length;
			System.out.println(posSample);
			posSample = sut.read();
		}

		System.out.flush();

		assertEquals(57, sentenceCount);
		assertEquals(940, wordCount);
		assertEquals(wordCount, posCount);
	}

	@Test
	public void testReset() throws Exception {
		testRead();
		sut.reset();
		testRead();
	}

	@Ignore
	@Test
	public void generateInputFile() throws Exception {
		PrintWriter writer = new PrintWriter("/home/slafulk/opennlp_input", "UTF-8");

		POSSample posSample = sut.read();
		while (posSample != null) {
			writer.println(posSample.toString());
			posSample = sut.read();
		}
		writer.close();
	}
}
