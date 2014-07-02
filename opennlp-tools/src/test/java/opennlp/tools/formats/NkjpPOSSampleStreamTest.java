package opennlp.tools.formats;

import opennlp.tools.postag.POSSample;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NkjpPOSSampleStreamTest {
	private NkjpPOSSampleStream sut;
	private ResourceAsStreamFactory in;

	@Before
	public void setUp() throws Exception {
		in = new ResourceAsStreamFactory(NkjpPOSSampleStreamTest.class, "/opennlp/tools/formats/nkjp.sample");
		sut = new NkjpPOSSampleStream(in);
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
			posSample = sut.read();
		}

		assertEquals(30, sentenceCount);
		assertEquals(459, wordCount);
		assertEquals(wordCount, posCount);
	}

	@Test
	public void testRead2() throws Exception {
		POSSample firstPosSample = sut.read();
		assertNotNull(firstPosSample);

		assertEquals(57, firstPosSample.getSentence().length);
		assertEquals(firstPosSample.getSentence().length, firstPosSample.getTags().length);

		assertEquals("Zatrzasnął", firstPosSample.getSentence()[0]);
		assertEquals("praet", firstPosSample.getTags()[0]);

		POSSample secondPosSample = sut.read();
		assertNotNull(secondPosSample);

		assertEquals(8, secondPosSample.getSentence().length);
		assertEquals(secondPosSample.getSentence().length, secondPosSample.getTags().length);

		assertEquals("Bohaterem", secondPosSample.getSentence()[0]);
		assertEquals("subst", secondPosSample.getTags()[0]);

		assertNotNull(sut.read());
	}

	@Test
	public void testReset() throws Exception {
		NkjpPOSSampleStream sut = new NkjpPOSSampleStream(in);
		POSSample firstSample = sut.read();
		sut.reset();
		POSSample shouldBeFirstSample = sut.read();
		assertArrayEquals(firstSample.getSentence(), shouldBeFirstSample.getSentence());
		sut.close();
	}
}