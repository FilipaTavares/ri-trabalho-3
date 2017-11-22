package SearchEngine.QueryProcessing;

import IndexerEngine.indexer.Indexer;
import IndexerEngine.tokenizers.Tokenizer;
import SearchEngine.Evaluation.Evaluation;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Abstract class that contains a geral representation of the Retrieval model
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


    /**
     * Abstract method that define a general representation of the retrieval system
     * 
     * @param queryId
     * @param queryText 
     */
    public abstract void retrieve(int queryId, String queryText);

    /**
     * Saves the score of the documents for each query in a file
     *
     * @param filename name of the ouput file
     */

    public abstract void saveToFile(String filename);

    /**
     * Method that evaluate an algorithm score.
     * For each query, the retrieved documents have a score greater or equal
     * than the threshold given as a argument. The relevant documents for each query depends
     * on the relevance level given as a argument.
     * 
     * @param threshold fixed value of threshold
     * @param n_ratings relevance level 
     * @param displayQueryMetrics boolean that represents if the user want to see
     * the results of the query metrics
     */
    public void evaluateWithFixedThreshold(double threshold, int n_ratings, Boolean displayQueryMetrics) {
        System.out.println("Evaluating with fixed threshold: " + threshold + " and number of ratings: " + n_ratings + "\n");
        evaluation.setN_ratings(n_ratings);

        for (Query query: results ) {
            List<Integer> documentsRetrieved = query.getDoc_scores().entrySet().stream()
                    .sorted((o1, o2) -> o1.getValue().equals(o2.getValue())
                            ? o1.getKey().compareTo(o2.getKey()) : o2.getValue().compareTo(o1.getValue()))
                    .filter(entry -> entry.getValue() >= threshold)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            evaluation.calculateQueryMeasures(query.getQuery_id(), documentsRetrieved);
        }
        evaluation.calculateSystemMeasures();
        System.out.println("Precision values to plot precision-recall curve with recall levels [0,1] with a step of 0.1:\n"
                + evaluation.averageRecallPrecision());
        evaluation.printResults(displayQueryMetrics);
        evaluation.reset();
    }

    /**
     * Method that evaluate an algorithm score.
     * For each query, is got the maximum score where the retrieved documents 
     * have a score greater or equal than the multiplication between the maximum
     * score and threshold given as a argument. The relevant documents for each query depends
     * on the relevance level given as a argument.
     * 
     * @param threshold variable value of threshold
     * @param n_ratings relevance level
     * @param displayQueryMetrics boolean that represents if the user want to see
     * the results of the query metrics
     */
    public void evaluateWithVariableThreshold(double threshold, int n_ratings, Boolean displayQueryMetrics) {
        System.out.println("Evaluating with variable threshold: max_score_value * " + threshold + " and number of ratings: " + n_ratings + "\n");

        evaluation.setN_ratings(n_ratings);

        for (Query query: results ) {
            Collection<Double> values = query.getDoc_scores().values();
            double max = Collections.max(values);

            List<Integer> documentsRetrieved = query.getDoc_scores().entrySet().stream()
                    .sorted((o1, o2) -> o1.getValue().equals(o2.getValue())
                            ? o1.getKey().compareTo(o2.getKey()) : o2.getValue().compareTo(o1.getValue()))
                    .filter(entry -> entry.getValue() >= max * threshold)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            evaluation.calculateQueryMeasures(query.getQuery_id(), documentsRetrieved);
        }
        evaluation.calculateSystemMeasures();
        System.out.println("Precision values to plot precision-recall curve with recall levels [0,1] with a step of 0.1:\n"
                + evaluation.averageRecallPrecision());
        evaluation.printResults(displayQueryMetrics);
        evaluation.reset();
    }

    /**
     * Store or modify the Indexer object
     * 
     * @param indexer a new Indexer object
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
     * Store or modify the Evaluation objetct
     * 
     * @param evaluation a new Evaluation object
     */
    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }

}