package SearchEngine.ScoringAlgorithms;

import IndexerEngine.indexer.Posting;
import SearchEngine.QueryProcessing.Query;
import java.util.List;

/**
 * Class that implements the ScoringAlgorithm interface in which the score of a document is the number of query terms
 * that appear in the document
 */

public class NumberOfQueryWords implements ScoringAlgorithm {

    /**
     * For a given query calculates the number of query terms for each document with terms present in the query
     * @param query the query being processed
     * @param postings postings list of the terms that are present in the query
     */

    @Override
    public void computeScores(Query query, List<Posting> postings) {
        postings.forEach(posting ->  query.increaseDocScore(posting.getDocID(), 1));
    }
}
