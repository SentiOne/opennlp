package opennlp.tools.formats;

import opennlp.tools.postag.POSSample;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.ObjectStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.*;

public class NkjpCorpusPOSSampleStreamTest {
	private ObjectStream<POSSample> sut;

	@Before
	public void setUp() throws Exception {
		sut = new NkjpCorpusPOSSampleStream(ZipArchiveResourceReader.getTestCorpus(), false);
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

		POSSample posSample;

		while ((posSample = sut.read()) != null) {
			sentenceCount += 1;
			wordCount += posSample.getSentence().length;
			posCount += posSample.getTags().length;
			System.out.println(posSample);
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
}

class ZipArchiveResourceReader {
	private static int BUFFER = 2048;

	private static byte[] readZipEntryAsByteArray(ZipInputStream zis) throws IOException {
		byte data[] = new byte[BUFFER];

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedOutputStream bos = new BufferedOutputStream(baos, BUFFER);

		int count;
		while ((count = zis.read(data, 0, BUFFER)) > -1) {
			bos.write(data, 0, count);
		}
		bos.flush();

		byte[] bytes = baos.toByteArray();

		bos.close();
		baos.close();

		return bytes;
	}

	public static InputStreamFactory[] getTestCorpus() throws Exception {
		ResourceAsStreamFactory in =
			new ResourceAsStreamFactory(NkjpPOSSampleStreamTest.class, "/opennlp/tools/formats/NKJP-PodkorpusMilionowy-1.0_test.zip.sample");

		InputStream inputStream = in.createInputStream();
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(inputStream));
		ZipEntry entry;

		List<InputStreamFactory> list = new ArrayList<>();

		while((entry = zis.getNextEntry()) != null) {
			if (!entry.isDirectory() && entry.getName().endsWith("ann_morphosyntax.xml")) {

				byte[] bytes = readZipEntryAsByteArray(zis);

				final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
				byteArrayInputStream.mark(bytes.length + 1);

				list.add(new InputStreamFactory() {
					@Override
					public InputStream createInputStream() throws IOException {
						byteArrayInputStream.reset();
						return byteArrayInputStream;
					}
				});
			}
		}

		zis.close();

		return list.toArray(new InputStreamFactory[list.size()]);
	}
}
