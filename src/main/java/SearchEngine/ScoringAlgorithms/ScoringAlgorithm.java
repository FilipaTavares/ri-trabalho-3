package SearchEngine.ScoringAlgorithms;

import IndexerEngine.indexer.Posting;
import SearchEngine.QueryProcessing.Query;
import SearchEngine.QueryProcessing.Vector;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Interface that defines the methods to calculate the scores of documents relevant to a given query
 */
public interface ScoringAlgorithm {

    /**
     * Method that calculates the scores of documents with terms of a given query
     * @param query the query being processed
     * @param postings postings list of the terms that are present in the query
     */
    public void computeScores(Query query, List<Posting> postings);
}
