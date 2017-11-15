package SearchEngine.IndexReader;


import IndexerEngine.indexer.Indexer;

public interface IndexReader {

    public Indexer readIndex(String filename);
}
