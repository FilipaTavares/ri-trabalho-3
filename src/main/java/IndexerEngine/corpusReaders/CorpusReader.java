package IndexerEngine.corpusReaders;

import IndexerEngine.documents.Document;

public interface CorpusReader {

    /**
     * Reads a file and returns the contents in a document object
     *
     * @param file name of the file to be read/parsed
     * @return a document object generated from the file
     */

    Document read(String file);

}
