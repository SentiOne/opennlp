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
import java.util.HashMap;

public class NkjpPOSSampleStream implements ObjectStream<POSSample> {
	private final XPathExpression wordExpression;
	private final XPathExpression posExpression;
	private final boolean useUniversalTags;
	private List<Element> sentenceList;
	private int currentIndex = 0;
	// universal mapping
	private final static HashMap<String, String> universalPosPlMapping = createMapping();

	public NkjpPOSSampleStream(InputStreamFactory inputStreamFactory, boolean useUniversalTags) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
		this.sentenceList = new ArrayList<>();
		this.useUniversalTags = useUniversalTags;

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

			if (useUniversalTags) {
				pos = universalPosPlMapping.get(pos);
				if (pos == null) {
					throw new IOException("Unexpected pos interpretation format: " + morphData + "for word: " + word);
				}
			}

			return new AbstractMap.SimpleEntry<String, String>(word, pos);
		} catch (XPathExpressionException e1) {
			throw new IOException("Expressions didn't match ", e1);
		}
	}

	@Override
	public void reset() throws IOException, UnsupportedOperationException {
		currentIndex = 0;
	}

	@Override
	public void close() throws IOException {
	}

	private static HashMap<String, String> createMapping() {
		HashMap<String, String> map = new HashMap<>();
		map.put("adj", "ADJ");
		map.put("adja", "ADJ");
		map.put("adjc", "ADJ");
		map.put("adjp", "ADJ");
		map.put("adv", "ADV");
		map.put("aglt", "VERB");
		map.put("bedzie", "VERB");
		map.put("brev", "X");
		map.put("burk", "ADV");
		map.put("comp", "CONJ");
		map.put("conj", "CONJ");
		map.put("depr", "NOUN");
		map.put("fin", "VERB");
		map.put("ger", "VERB");
		map.put("ign", "X");
		map.put("imps", "VERB");
		map.put("impt", "VERB");
		map.put("inf", "VERB");
		map.put("interj", "X");
		map.put("interp", "INTERP");
		map.put("num", "NUM");
		map.put("numcol", "NUM");
		map.put("pact", "VERB");
		map.put("pant", "VERB");
		map.put("pcon", "VERB");
		map.put("ppas", "VERB");
		map.put("ppron12", "PRON");
		map.put("ppron3", "PRON");
		map.put("praet", "VERB");
		map.put("pred", "ADP");
		map.put("prep", "ADP");
		map.put("qub", "ADV");
		map.put("siebie", "PRON");
		map.put("subst", "NOUN");
		map.put("winien", "VERB");
		map.put("xxs", "X");
		map.put("xxx", "X");
		return map;
	}
}
