package SearchEngine.ScoringAlgorithms;

import IndexerEngine.indexer.Posting;
import SearchEngine.QueryProcessing.Query;

import java.util.List;

/**
 * Class that implements the ScoringAlgorithm interface in which the score of a document is the sum of the frequencies
 * of the query terms that appear the document
 */

public class FrequencyOfQueryWords implements ScoringAlgorithm {

    /**
     * For a given query calculates the sum of the frequencies of the query terms for each document with terms present
     * in the query
     * @param query the query being processed
     * @param postings postings list of the terms that are present in the query
     */

    @Override
    public void computeScores(Query query, List<Posting> postings) {
        //postings.forEach(posting ->  query.increaseDocScore(posting.getDocID(), posting.getTermFreq()) );
    }
}
