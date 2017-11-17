package SearchEngine.QueryProcessing;

import IndexerEngine.indexer.Indexer;
import IndexerEngine.tokenizers.Tokenizer;
import SearchEngine.Evaluation.Evaluation;
import SearchEngine.ScoringAlgorithms.CosineScore;
import SearchEngine.ScoringAlgorithms.ScoringAlgorithm;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Abstract class that contains a geral representation of the Boolean Retrieval model
 */
public abstract class Retrieval {
    protected List<Query> results;
    protected Indexer indexer;
    protected Tokenizer tokenizer;
    protected Evaluation evaluation;


    public Retrieval(Indexer indexer, Tokenizer tokenizer, Evaluation evaluation) {
        this.results = new LinkedList<>();
        this.indexer = indexer;
        this.tokenizer = tokenizer;
        this.evaluation = evaluation;
    }

    public abstract void retrieve(int queryId, String queryText);

    /**
     * Saves the score of the documents for each query in a file
     *
     * @param filename name of the ouput file
     */

    public abstract void saveToFile(String filename);

    public void calculateMeasures(int queryId) {

        List<Integer> keys = results.get(queryId - 1).getDoc_scores().entrySet().stream()
                .sorted((o1, o2) -> o1.getValue().equals(o2.getValue())
                ? o1.getKey().compareTo(o2.getKey()) : o2.getValue().compareTo(o1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        evaluation.calculatePrecision(queryId, keys);
        evaluation.calculateRecall(queryId, keys);
        evaluation.calculateFmeasure(queryId);
        evaluation.calculateAveragePrecision(queryId, keys);
        evaluation.calculateAveragePrecisionAtRank10(queryId, keys);
        evaluation.calculateReciprocalRank(queryId, keys);
    }

    public void printAllEvaluations() {
        double map = evaluation.calculateMAP();
        double map10 = evaluation.calculateMAPatRank10();
        double mrr = evaluation.calculateMRR();
        long mql = evaluation.calculateMedianQueryLatency();
        double query_throughput = evaluation.calculateQueryThroughput();

        System.out.println(evaluation.toString());
        System.out.printf("Mean Average Precision: %.5f\n", map);
        System.out.printf("Mean Average Precision at Rank 10: %.5f\n", map10);
        System.out.printf("Mean Reciprocal Rank: %.5f\n", mrr);
        System.out.println("Query Throughput per second: " + query_throughput);
        System.out.println("Median Query Latency: "+ mql + "\n");
    }

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


    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }
}