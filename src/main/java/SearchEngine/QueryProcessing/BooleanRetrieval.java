package SearchEngine.QueryProcessing;

import IndexerEngine.indexer.Indexer;
import IndexerEngine.tokenizers.Tokenizer;
import SearchEngine.Evaluation.Evaluation;
import SearchEngine.ScoringAlgorithms.ScoringAlgorithm;

import java.util.LinkedList;
import java.util.List;

/**
 * Abstract class that contains a geral representation of the Boolean Retrieval model
 */
public abstract class BooleanRetrieval implements Retrieval {
    protected List<Query> results;
    protected Indexer indexer;
    protected Tokenizer tokenizer;
    protected ScoringAlgorithm scoringAlgorithm;
    protected Evaluation evaluation;

    public BooleanRetrieval() {
        this.results = new LinkedList<>();
    }

    /**
     * Method that process the query according to the operator type
     * 
     * @param query_id id of the query
     * @param query content of the query
     */
    @Override
    public abstract void retrieve(int query_id, String query);

    /**
     * Saves the score of the documents for each query in a file
     * 
     * @param filename name of the ouput file
     */
    @Override
    public abstract void saveToFile(String filename);

    /**
     * Store or modify the IndexerWtNorm object
     * 
     * @param indexer a new IndexerWtNorm object
     */
    public void setIndexer(Indexer indexer) {
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
    
    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }
}