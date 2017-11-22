package IndexerEngine.corpusReaders;

import IndexerEngine.documents.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;

/**
 * This class reads a xml file from the cranfield corpus and returns the contents in a document object.
 * It uses a specific SAXParser to parse the xml tags and select the desired fields
 */

public class CranfieldReader implements CorpusReader {
    private SAXParser saxParser;
    private SaxParserHandler handler;

    public CranfieldReader() {
        createSaxParser();
    }

    /**
     * Instantiation of only one object of the type SAXParserFactory and the type SaxParserHandler
     * 
     */
    
    private void createSaxParser() {
        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            this.saxParser = saxParserFactory.newSAXParser();
            this.handler = new SaxParserHandler();
        } catch (ParserConfigurationException | SAXException e) {
            System.err.println("Unable to create document parser");
            System.exit(1);
        }
    }

    /**
     * Reads a file and returns the contents in a document object
     *
     * @param filename name of the file to be read/parsed
     * @return a document object generated from the file or null if occurs an error while parsing
     */
    
    @Override
    public Document read(String filename) {
        try {
            saxParser.parse(filename, handler);
            return handler.getDoc();

        } catch (SAXException | IOException e) {
            System.err.println("Unable to parse document: " + filename);
        }

        return null;
    }
}
