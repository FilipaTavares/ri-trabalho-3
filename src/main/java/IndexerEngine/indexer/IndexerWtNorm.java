package IndexerEngine.indexer;

import java.util.*;

/**
 * This class indexes a document in a data structure composed of an hashmap in which a key is a term and the value
 * a list of Postings
 *
 * @see PostingWtNorm
 */

public class IndexerWtNorm extends Indexer {
    /**
     * Indexes a document given its list of terms and id
     *
     * @param terms list of terms of a document
     * @param docID document id
     */
    @Override
    public void index(List<String> terms, int docID) {
        Map<String, Double> temp = new HashMap<>();

        for (String term : terms) {

            double count = temp.getOrDefault(term, 0.0);
            temp.put(term, count + 1);
        }

        double sum_square_wt = 0.0;

        for (Map.Entry<String, Double> pair : temp.entrySet()) {
            //Arrendondar valor??
            double wt = 1 + Math.log10(pair.getValue());
            temp.put(pair.getKey(), wt);
            sum_square_wt += Math.pow(wt, 2);
        }

        /* ou usar??
        for (String key : map.keys()) {
          map.put(key, ..(key));
        }
         */
        double finalSum_square_wt = sum_square_wt;
        temp.replaceAll((term, wt) -> wt / Math.sqrt(finalSum_square_wt));

        for (Map.Entry<String, Double> pair : temp.entrySet()) {
            if (!invertedIndex.containsKey(pair.getKey())) {
                List<Posting> postingList = new LinkedList<>();
                postingList.add(new PostingWtNorm(docID, pair.getValue()));
                invertedIndex.put(pair.getKey(), postingList);
            }

            else {
                List<Posting> postingList = invertedIndex.get(pair.getKey());
                postingList.add(new PostingWtNorm(docID, pair.getValue()));
            }
        }
        n_docs++;

    }
}
