package opennlp.tools.formats;

import opennlp.tools.postag.POSSample;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class NkjpPOSSampleStreamTest {
	private ResourceAsStreamFactory in;

	@Before
	public void setUp() throws Exception {
		in = new ResourceAsStreamFactory(NkjpPOSSampleStreamTest.class, "/opennlp/tools/formats/nkjp.sample");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRead() throws Exception {
		NkjpPOSSampleStream sut = new NkjpPOSSampleStream(in, NkjpPOSSampleStream.NkjpTagset.NKJP_SIMPLE);
		long sentenceCount = 0;
		long wordCount = 0;
		long posCount = 0;

		POSSample posSample;
		while ((posSample = sut.read()) != null) {
			sentenceCount += 1;
			wordCount += posSample.getSentence().length;
			posCount += posSample.getTags().length;
		}

		assertEquals(30, sentenceCount);
		assertEquals(459, wordCount);
		assertEquals(wordCount, posCount);

		sut.close();
	}

	@Test
	public void testRead2() throws Exception {
		NkjpPOSSampleStream sut = new NkjpPOSSampleStream(in, NkjpPOSSampleStream.NkjpTagset.NKJP_SIMPLE);
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

		sut.close();
	}

	@Test
	public void testRead2_universal() throws Exception {
		NkjpPOSSampleStream sut = new NkjpPOSSampleStream(in, NkjpPOSSampleStream.NkjpTagset.UNIVERSAL_TAGSET);
		POSSample firstPosSample = sut.read();
		assertNotNull(firstPosSample);

		assertEquals(57, firstPosSample.getSentence().length);
		assertEquals(firstPosSample.getSentence().length, firstPosSample.getTags().length);

		assertEquals("Zatrzasnął", firstPosSample.getSentence()[0]);
		assertEquals("VERB", firstPosSample.getTags()[0]);

		POSSample secondPosSample = sut.read();
		assertNotNull(secondPosSample);

		assertEquals(8, secondPosSample.getSentence().length);
		assertEquals(secondPosSample.getSentence().length, secondPosSample.getTags().length);

		assertEquals("Bohaterem", secondPosSample.getSentence()[0]);
		assertEquals("NOUN", secondPosSample.getTags()[0]);

		assertNotNull(sut.read());

		sut.close();
	}

	@Test
	public void testRead2_nkjpfull() throws Exception {
		NkjpPOSSampleStream sut = new NkjpPOSSampleStream(in, NkjpPOSSampleStream.NkjpTagset.NKJP_FULL);
		POSSample firstPosSample = sut.read();
		assertNotNull(firstPosSample);

		assertEquals(57, firstPosSample.getSentence().length);
		assertEquals(firstPosSample.getSentence().length, firstPosSample.getTags().length);

		assertEquals("Zatrzasnął", firstPosSample.getSentence()[0]);
		assertEquals("praet:sg:m1:perf", firstPosSample.getTags()[0]);

		POSSample secondPosSample = sut.read();
		assertNotNull(secondPosSample);

		assertEquals(8, secondPosSample.getSentence().length);
		assertEquals(secondPosSample.getSentence().length, secondPosSample.getTags().length);

		assertEquals("Bohaterem", secondPosSample.getSentence()[0]);
		assertEquals("subst:sg:inst:m1", secondPosSample.getTags()[0]);

		assertNotNull(sut.read());

		sut.close();
	}
	@Test
	public void testReset() throws Exception {
		NkjpPOSSampleStream sut = new NkjpPOSSampleStream(in, NkjpPOSSampleStream.NkjpTagset.NKJP_SIMPLE);
		POSSample firstSample = sut.read();
		sut.reset();
		POSSample shouldBeFirstSample = sut.read();
		assertArrayEquals(firstSample.getSentence(), shouldBeFirstSample.getSentence());
		sut.close();
	}
}
