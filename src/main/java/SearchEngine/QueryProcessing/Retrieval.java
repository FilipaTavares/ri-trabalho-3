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
    private int n_ratings;


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

    public void evaluate(double threshold, int n_ratings) {
        this.n_ratings = n_ratings;
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
        System.out.println("Precision values to plot: " + evaluation.averageRecallPrecision());
        evaluation.printResults();
        evaluation.reset();
    }

    public void evaluate(int base, int exp, int n_ratings) {
        evaluation.setN_ratings(n_ratings);

        for (Query query: results ) {
            Collection<Double> values = query.getDoc_scores().values();
            double max = Collections.max(values);
            double min = Collections.min(values);
            //System.out.println("MAX " + max);
            //System.out.println("MIN " + min);
            //System.out.println("MAX / 2**2 " + max / Math.pow(base, exp));

            List<Integer> documentsRetrieved = query.getDoc_scores().entrySet().stream()
                    .sorted((o1, o2) -> o1.getValue().equals(o2.getValue())
                            ? o1.getKey().compareTo(o2.getKey()) : o2.getValue().compareTo(o1.getValue()))
                    .filter(entry -> entry.getValue() >= max / Math.pow(base, exp))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            evaluation.calculateQueryMeasures(query.getQuery_id(), documentsRetrieved);
        }
        evaluation.calculateSystemMeasures();
        System.out.println("Precision values to plot: " + evaluation.averageRecallPrecision());
        evaluation.printResults();
        evaluation.reset();
    }



    public void calculateMeasures(int queryId) {

        List<Integer> keys = results.get(queryId - 1).getDoc_scores().entrySet().stream()
                .sorted((o1, o2) -> o1.getValue().equals(o2.getValue())
                ? o1.getKey().compareTo(o2.getKey()) : o2.getValue().compareTo(o1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        evaluation.calculateQueryMeasures(queryId, keys);
    }

    public void printAllEvaluations() {
        evaluation.calculateSystemMeasures();
        evaluation.printResults();
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

    public List<Double> getMax_MinValue() {
        List<Double> list = new ArrayList<>();
        for (Query query : results) {
            list.add(Collections.max(query.getDoc_scores().values()));
        }

        List<Double> temp = new ArrayList<>();
        temp.add(Collections.max(list));
        temp.add(Collections.min(list));

        return temp;
    }


}