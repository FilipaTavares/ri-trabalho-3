package SearchEngine.QueryProcessing;

import IndexerEngine.indexer.Posting;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class that implements a disjunctive (OR) Boolean Retrieval model
 * 
 */
public class DisjunctiveBooleanRetrieval extends BooleanRetrieval {

    /**
     * Method that combine all the terms of the query using the OR operator, where is obtained all the postings list of
     * each term. At the end, is computed the score of the documents for that query.
     * 
     * @param query_id id of the query
     * @param query_text content of the query
     */
    @Override
    public void retrieve(int query_id, String query_text) {
        List<String> terms = tokenizer.tokenize(query_text);
        List<Posting> allPostings = new LinkedList<>();
        for(String term : terms) {
            if (indexer.getTermPostings(term) != null)
                allPostings.addAll(indexer.getTermPostings(term));
        }
        Query query = new Query(query_id);
        scoringAlgorithm.computeScores(query, allPostings);
        results.add(query);
    }

    /**
     * Save the score of the documents to a file. The first column is the query id, the second is the doc id
     * and the third the doc score
     * @param filename output filename
     */
    @Override
    public void saveToFile(String filename) {
        try {
            PrintWriter out = new PrintWriter(filename);

            results.sort(Comparator.comparingInt(Query::getQuery_id));

            for (Query query: results) {
                int id = query.getQuery_id();

                query.getDoc_scores().entrySet().stream().sorted((o1, o2) -> o1.getValue().equals(o2.getValue()) ?
                        o1.getKey().compareTo(o2.getKey()) : o2.getValue().compareTo(o1.getValue())).
                        forEach(entry -> out.printf("%-7d%-8d%d\n",id,entry.getKey(),entry.getValue()));
            }
            out.close();
        } catch (IOException e) {
            System.err.println("Error writing results to file");
            System.exit(1);
        }
    }
}