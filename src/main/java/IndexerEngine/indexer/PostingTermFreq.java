package IndexerEngine.indexer;

/**
 * This class represents regarding a given term a tuple (docID, termFreq) which states that the term is present
 * in the document with the id docID and it appears termFreq times
 */

public class PostingTermFreq extends Posting {
    private int termFreq;

    /**
     * Creates a new instance of PostingTermFreq
     *
     * @param docID    the document id
     * @param termFreq the term frequency
     */
    public PostingTermFreq(int docID, int termFreq) {
        super(docID);
        this.termFreq = termFreq;
    }

    /**
     * Return the term frequency in the document
     *
     * @return the term frequency in the document
     */
    public int getTermFreq() {
        return termFreq;
    }

    /**
     * Increments the term frequency
     */
    public void incrementTermFrequency() {
        this.termFreq++;
    }

    /**
     * String object representation of this PostingTermFreq
     *
     * @return a String object representing of this PostingTermFreq.
     */

    @Override
    public String toString() {
        return docID + ":" + termFreq;
    }

}
