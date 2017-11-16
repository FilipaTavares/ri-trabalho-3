package IndexerEngine.indexer;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingInt;

/**
 * This class indexes a document in a data structure composed of an hashmap in which a key is a term and the value
 * a list of Postings
 *
 * @see PostingTermFreq
 */

public class IndexerTermFreq extends Indexer {
    /**
     * Indexes a document given its list of terms and id
     *
     * @param terms list of terms of a document
     * @param docID document id
     */
    @Override
    public void index(List<String> terms, int docID) {

        for (String term : terms) {
            if (!invertedIndex.containsKey(term)) {

                List<Posting> postingList = new LinkedList<>();
                postingList.add(new PostingTermFreq(docID, 1));
                invertedIndex.put(term, postingList);

            } else {

                List<Posting> list = invertedIndex.get(term);
                PostingTermFreq lastPosting = (PostingTermFreq) list.get(list.size() - 1);

                if (lastPosting.getDocID() != docID) {
                    list.add(new PostingTermFreq(docID, 1));
                } else {
                    lastPosting.incrementTermFrequency();
                }
            }
        }
        n_docs++;
    }

    /**
     * Lists the ten first terms (in alphabetic order) that appear in only one document
     *
     * @return a list of the ten first terms (in alphabetic order) that appear in only one document
     */
    public List<String> getFirst10TermsInOneDoc() {
        List<String> terms = invertedIndex.keySet().stream().sorted()
                .filter((term) -> invertedIndex.get(term).size() == 1)
                .collect(Collectors.toList());
        return (this.size() < 10) ? terms.subList(0, this.size()) : terms.subList(0, 10);
    }

    /**
     * Lists the ten terms with higher document frequency
     *
     * @return a list of the ten terms with higher document frequency
     */
    public List<String> getFirst10TermsWithHigherDocFreq() {
        List<String> terms = invertedIndex.keySet().stream().
                sorted(comparingInt(term -> invertedIndex.get(term).size()).reversed())
                .collect(Collectors.toList());
        return (this.size() < 10) ? terms.subList(0, this.size()) : terms.subList(0, 10);
    }
}
