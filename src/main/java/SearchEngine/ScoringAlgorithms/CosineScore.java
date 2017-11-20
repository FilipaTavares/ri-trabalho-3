
package SearchEngine.ScoringAlgorithms;

import IndexerEngine.indexer.Indexer;
import IndexerEngine.indexer.Posting;
import IndexerEngine.indexer.PostingWtNorm;
import SearchEngine.QueryProcessing.Vector;
import SearchEngine.QueryProcessing.Query;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CosineScore {
    private Map<Integer, Vector> vectors;
    private Indexer indexer;

    public CosineScore(Indexer indexer) {
        this.vectors = new HashMap<>();
        this.indexer = indexer;
    }

    public void computeScores(Query query, List<String> terms) {
        Map<String, Double> wtQuery = normalizeQuery(terms);
        fillVectors(terms);

        for (Vector vector : vectors.values()) {
            for (Map.Entry<String, Double> pair : wtQuery.entrySet()) {
                double prod = pair.getValue() * vector.getTermWeight(pair.getKey());
                if (prod != 0.0)
                    query.increaseDocScore(vector.getDocId(), prod);
            }
        }
    }

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
                temp.put(pair.getKey(), wt);
                sum_square_wt += Math.pow(wt, 2);
            } else {
                temp.put(pair.getKey(), 0.0);
            }
        }

        if (sum_square_wt != 0) {
            for (Map.Entry<String, Double> pair : temp.entrySet()) {
                pair.setValue(pair.getValue() / Math.sqrt(sum_square_wt));
            }
        }

        return temp;
    }

    private void fillVectors(List<String> terms) {
        for (String term : terms) {
            if (indexer.getTermPostings(term) != null) {
                List<Posting> postingList = indexer.getTermPostings(term);
                for (Posting posting1 : postingList) {
                    PostingWtNorm posting = (PostingWtNorm) posting1;

                    if (!vectors.containsKey(posting.getDocID())) {
                        Vector vector = new Vector(posting.getDocID());
                        vector.addTerm(term, posting.getWt_norm());
                        vectors.put(posting.getDocID(), vector);
                    } else {
                        vectors.get(posting.getDocID()).addTerm(term, posting.getWt_norm());
                    }

                }
            }
        }
    }
}