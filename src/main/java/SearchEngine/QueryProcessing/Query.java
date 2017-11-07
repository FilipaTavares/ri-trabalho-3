package SearchEngine.QueryProcessing;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that represents a query
 */

public class Query {
    private int query_id;
    private Map<Integer, Integer> doc_scores;

    /**
     * Creates a new instance of a query given an integer value that represents the query id
     * @param query_id query id
     */
    public Query(int query_id) {
        this.query_id = query_id;
        doc_scores = new HashMap<>();
    }

    /**
     * Returns the query id
     * @return the query id
     */

    public int getQuery_id() {
        return query_id;
    }

    /**
     * Return a map containing pair values (doc_id, doc_score) for this query
     * @return query's doc scores
     */
    public Map<Integer, Integer> getDoc_scores() {
        return doc_scores;
    }

    /**
     * Increases the score for a given document by a numeric value
     * @param docID the document id
     * @param number the value to be added to the score
     */
    public void increaseDocScore(int docID, int number) {
        doc_scores.put(docID, doc_scores.containsKey(docID) ? doc_scores.get(docID) + number : number);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Query query = (Query) o;

        return query_id == query.query_id;
    }

    @Override
    public int hashCode() {
        return query_id;
    }

    @Override
    public String toString() {
        return "query_id=" + query_id;
    }
}
