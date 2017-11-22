package SearchEngine.IndexReader;


import IndexerEngine.indexer.Indexer;

/**
 * Interfaces that defines the methods to read a index file
 * 
 */
public interface IndexReader {

    /**
     * Method that read the index file
     * 
     * @param filename filename of the index
     * @return an object Indexer
     */
    public Indexer readIndex(String filename);
}
