package SearchEngine.QueryProcessing;

import IndexerEngine.indexer.Indexer;
import IndexerEngine.tokenizers.Tokenizer;
import SearchEngine.ScoringAlgorithms.CosineScore;
import SearchEngine.Evaluation.Evaluation;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Class that implements a ranked retrieval system
 * 
 */
public class RankedRetrieval extends Retrieval {
    private CosineScore score;

    public RankedRetrieval(Indexer indexer, Tokenizer tokenizer, Evaluation evaluation) {
        super(indexer, tokenizer, evaluation);
        this.score = new CosineScore(indexer);
    }

    /**
     * Method that tokenize the text of the query, to get the list of terms and
     * at the end, is computed the score of the documents for that query
     * 
     * @param queryID query id
     * @param queryText text of the query
     */
    @Override
    public void retrieve(int queryID, String queryText) {
        List<String> terms = tokenizer.tokenize(queryText);
        long startTime = System.nanoTime();
        Query query = new Query(queryID);


        score.computeScores(query, terms);

        results.add(query);
        long queryLatency = System.nanoTime() - startTime;

        evaluation.addQueryLatency(queryID, queryLatency / 1e6);
    }

    /**
     * Method that saves the score of the documents for each query in a file
     * 
     * @param filename output filename
     */
    @Override
    public void saveToFile(String filename) {
        try (PrintWriter out = new PrintWriter(filename)) {
            results.sort(Comparator.comparingInt(Query::getQuery_id));
            for (Query query : results) {
                int id = query.getQuery_id();
                query.getDoc_scores().entrySet().stream().sorted((o1, o2) -> o1.getValue().equals(o2.getValue())
                        ? o1.getKey().compareTo(o2.getKey()) : o2.getValue().compareTo(o1.getValue())).
                        forEach(entry -> out.printf("%d\t%d\t%f\n", id, entry.getKey(), entry.getValue()));
            }
        } catch (IOException e) {
            System.err.println("Error writing results to file");
            System.exit(1);
        }
    }
}
