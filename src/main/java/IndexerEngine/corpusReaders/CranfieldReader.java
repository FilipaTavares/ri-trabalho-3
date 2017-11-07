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

    /**
     * Reads a file and returns the contents in a document object
     *
     * @param filename name of the file to be read/parsed
     * @return a document object generated from the file or null if occurs an error while parsing
     */
    @Override
    public Document read(String filename) {
        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            SaxParserHandler handler = new SaxParserHandler();
            saxParser.parse(filename, handler);

            return handler.getDoc();

        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.err.println("Unable to parse document: " + filename);
        }
        return null;
    }
}
