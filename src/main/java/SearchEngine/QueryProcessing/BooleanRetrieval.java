package SearchEngine.QueryProcessing;

import IndexerEngine.indexer.IndexerWtNorm;
import IndexerEngine.tokenizers.Tokenizer;
import SearchEngine.ScoringAlgorithms.ScoringAlgorithm;

import java.util.LinkedList;
import java.util.List;

/**
 * Abstract class that contains a geral representation of the Boolean Retrieval model
 */
public abstract class BooleanRetrieval {
    protected List<Query> results;
    protected IndexerWtNorm indexer;
    protected Tokenizer tokenizer;
    protected ScoringAlgorithm scoringAlgorithm;

    public BooleanRetrieval() {
        this.results = new LinkedList<>();
    }

    /**
     * Method that process the query according to the operator type
     * 
     * @param query_id id of the query
     * @param query content of the query
     */
    public abstract void retrieve(int query_id, String query);

    /**
     * Saves the score of the documents for each query in a file
     * 
     * @param filename name of the ouput file
     */
    public abstract void saveToFile(String filename);

    /**
     * Store or modify the IndexerWtNorm object
     * 
     * @param indexer a new IndexerWtNorm object
     */
    public void setIndexer(IndexerWtNorm indexer) {
        this.indexer = indexer;
    }

    /**
     * Store or modify the Tokenizer object
     * 
     * @param tokenizer a new Tokenizer object
     */
    public void setTokenizer(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    /**
     * Store or modify the ScoreAlgorithm object
     * 
     * @param scoringAlgorithm a new ScoringAlgorithm object
     */
    public void setScoringAlgorithm(ScoringAlgorithm scoringAlgorithm) {
        this.scoringAlgorithm = scoringAlgorithm;
    }
}