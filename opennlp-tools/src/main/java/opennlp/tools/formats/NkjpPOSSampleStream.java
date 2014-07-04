package opennlp.tools.formats;

import opennlp.tools.postag.POSSample;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.ObjectStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathConstants;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NkjpPOSSampleStream implements ObjectStream<POSSample> {
	private final XPathExpression wordExpression;
	private final XPathExpression posExpression;
	private List<Element> sentenceList;
	private int currentIndex = 0;

	public NkjpPOSSampleStream(InputStreamFactory inputStreamFactory) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
		this.sentenceList = new ArrayList<>();

		InputStream inputStream = inputStreamFactory.createInputStream();
		if (inputStream == null) {
			throw new IOException("Input stream couldn't be created.");
		}

		Document document = DocumentBuilderFactory
				.newInstance()
				.newDocumentBuilder()
				.parse(inputStream);

		inputStream.close();

		NodeList sentences = document.getElementsByTagName("s");
		int sentenceCount = sentences.getLength();
		for (int j = 0; j < sentenceCount; j++) {
			this.sentenceList.add((Element)sentences.item(j));
		}

		if (this.sentenceList.size() == 0) {
			throw new IOException("Empty sentence list!");
		}

		XPath xPath = XPathFactory.newInstance().newXPath();
		this.wordExpression = xPath.compile("./fs/f[@name=\"orth\"]/string/text()");
		this.posExpression = xPath.compile(".//f[@name=\"interpretation\"]/string/text()");
	}

	@Override
	public POSSample read() throws IOException {
		if (sentenceList.size() <= currentIndex) {
			return null;
		}

		Element e = sentenceList.get(currentIndex);
		currentIndex++;

		List<String> sentence = new ArrayList<>();
		List<String> partOfSpeach = new ArrayList<>();

		NodeList segments = e.getElementsByTagName("seg");
		if (segments.getLength() == 0) {
			throw new IOException("No <seg> element!");
		}

		int segmentsCount = segments.getLength();
		for (int i = 0; i < segmentsCount; i++) {
			// Clonning makes extracting faster
			Node node = segments.item(i).cloneNode(true);

			Map.Entry<String, String> stringStringPair = ExtractWordAndPos(node);

			sentence.add(stringStringPair.getKey());
			partOfSpeach.add(stringStringPair.getValue());
		}

		// TODO: additional context?
		return new POSSample(
            sentence.toArray(new String[sentence.size()]),
            partOfSpeach.toArray(new String[partOfSpeach.size()])
        );
	}

	private Map.Entry<String, String> ExtractWordAndPos(Node node) throws IOException {
		try {
			String word = (String)wordExpression.evaluate(node, XPathConstants.STRING);
			String morphData = (String) posExpression.evaluate(node, XPathConstants.STRING);
			// there were colons in corpus
			int colonCount = word.length() - word.replace(":", "").length();
			// Format is some_word_form:pos:....
			String pos = morphData.split(":")[1 + colonCount];

			return new AbstractMap.SimpleEntry<String, String>(word, pos);
		} catch (XPathExpressionException e1) {
			throw new IOException("Expressions didn't match", e1);
		}
	}

	@Override
	public void reset() throws IOException, UnsupportedOperationException {
		currentIndex = 0;
	}

	@Override
	public void close() throws IOException {
	}
}
