package IndexerEngine.corpusReaders;

import IndexerEngine.documents.CranfieldDocument;
import IndexerEngine.documents.Document;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Specific handler that parses a xml cranfield file according to some xml tags and creates a document
 */

public class SaxParserHandler extends DefaultHandler {
    private int doc_id;
    private String title;
    private String body;

    private Document doc;
    private String elementName;                  // holds the name of a xml tag to identify the element being processed
    private StringBuilder stringBuilder;

    /**
     * Receive notification of the start of an element.
     * If that element name matches the xml tags needed then a string builder is instantiated to store the tag's data
     */

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        elementName = qName;

        if (elementName.equals("DOCNO") || elementName.equalsIgnoreCase("TITLE") ||
                elementName.equalsIgnoreCase("TEXT")) {
            stringBuilder = new StringBuilder();
        }
    }

    /**
     * Receive notification of character data inside an desired element and append the data to variable since
     * multiple calls to this method can be made
     */

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (stringBuilder != null)
            stringBuilder.append(ch, start, length);
    }

    /**
     * Receive notification of the end of an element and saves the intended data in a variable to generate the
     * Document object
     */

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        if (stringBuilder != null) {
            String element = stringBuilder.toString();

            if (elementName.equals("DOCNO")) {
                element = element.replaceAll("[^\\d.]", "");
                doc_id = Integer.parseInt(element);

            } else if (elementName.equalsIgnoreCase("TITLE")) {
                title = element;
            } else if (elementName.equalsIgnoreCase("TEXT")) {
                body = element;
            }
        }
        stringBuilder = null;
    }

    /**
     * Receive notification of the end of the document
     */

    @Override
    public void endDocument() throws SAXException {
        doc = new CranfieldDocument(doc_id, title + body);
    }

    /**
     * Returns a Document object
     *
     * @return a Document object more precisely a CranfieldDocument object
     */
    public Document getDoc() {
        return doc;
    }
}
