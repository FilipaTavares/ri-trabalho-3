
package SearchEngine.ScoringAlgorithms;

import IndexerEngine.indexer.Indexer;
import IndexerEngine.indexer.Posting;
import IndexerEngine.indexer.PostingWtNorm;
import SearchEngine.QueryProcessing.Vector;
import SearchEngine.QueryProcessing.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that compute the cosine similarity score for the query and each document vector
 * 
 */
public class CosineScore {
    private Indexer indexer;

    public CosineScore(Indexer indexer) {
        this.indexer = indexer;
    }

    /**
     * Method that calculate the score by the sum of the multiplications between the tf-idf weighting of
     * the term query and the log-frequency weighting of the term document
     * 
     * @param query an object Query
     * @param terms list of the query terms
     */
    public void computeScores(Query query, List<String> terms) {
        Map<String, Double> wtQuery = normalizeQuery(terms);
        Map<Integer, Vector> docVectors = createDocVectors(terms);

        for (Vector vector : docVectors.values()) {
            for (Map.Entry<String, Double> pair : wtQuery.entrySet()) {
                double prod = pair.getValue() * vector.getTermWeight(pair.getKey());
                if (prod != 0.0)
                    query.increaseDocScore(vector.getDocId(), prod);
            }
        }
    }

    /**
     * Method that calculates for each term of the query the respective 
     * tf-idf weighting
     * 
     * @param terms list of terms
     * @return tf-idf weighting for each term of the query
     */
    private Map<String, Double> normalizeQuery(List<String> terms) {
        Map<String, Double> temp = new HashMap<>();
        int nDocs = indexer.getN_docs();
        for (String term : terms) {
            double count = temp.getOrDefault(term, 0.0);
            temp.put(term, count + 1);
        }


        double sum_square_wt = 0.0;
        for (Map.Entry<String, Double> pair : temp.entrySet()) {
            if (indexer.getTermPostings(pair.getKey()) != null) {
                int docFreq = indexer.getTermPostings(pair.getKey()).size();
                double tfLog = 1.0 + Math.log10(pair.getValue());
                double idf = Math.log10(((double) nDocs / docFreq));
                double wt = tfLog * idf;
                pair.setValue(wt);
                sum_square_wt += Math.pow(wt, 2);
            } else {
                pair.setValue(0.0);
            }
        }

        if (sum_square_wt != 0) {
            for (Map.Entry<String, Double> pair : temp.entrySet()) {
                pair.setValue(pair.getValue() / Math.sqrt(sum_square_wt));
            }
        }

        return temp;
    }

    /**
     * Method that creates objects of the type Vector that represents documents,
     * which at least one term occurs in the document an in the query
     * 
     * @param terms list of terms
     * @return map where the keys is the document id and the values an object Vector
     */
    private Map<Integer, Vector> createDocVectors(List<String> terms) {
        Map<Integer, Vector> vec = new HashMap<>();
        for (String term : terms) {
            if (indexer.getTermPostings(term) != null) {
                List<Posting> postingList = indexer.getTermPostings(term);
                for (Posting posting1 : postingList) {
                    PostingWtNorm posting = (PostingWtNorm) posting1;

                    if (!vec.containsKey(posting.getDocID())) {
                        Vector vector = new Vector(posting.getDocID());
                        vector.addTerm(term, posting.getWt_norm());
                        vec.put(posting.getDocID(), vector);
                    } else {
                        vec.get(posting.getDocID()).addTerm(term, posting.getWt_norm());
                    }

                }
            }
        }
        return vec;
    }
}