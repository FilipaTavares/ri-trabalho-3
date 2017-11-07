package IndexerEngine.indexer;

/**
 * This class represents regarding a given term a tuple (docID, wt_norm) which states that the term is present
 * in the document with the id docID and it appears termFreq times
 */

public class Posting implements Comparable<Posting> {
    private int docID;
    private double wt_norm;

    /**
     * Creates a new instance of Posting
     *
     * @param docID    the document id
     * @param wt_norm the term frequency
     */

    public Posting(int docID, double wt_norm) {
        this.docID = docID;
        this.wt_norm = wt_norm;
    }

    /**
     * Returns the document id
     *
     * @return the document id
     */
    public int getDocID() {
        return docID;
    }

    public double getWt_norm() {
        return wt_norm;
    }

    /**
     * String object representation of this Posting
     *
     * @return a String object representing of this Posting.
     */

    @Override
    public String toString() {
        return docID + ":" + wt_norm;
    }

    /**
     * Compares two Posting objects numerically according to the document id
     *
     * @param anotherPosting the Posting to be compared
     * @return the value 0 if this Posting is equal to the argument Posting; a value less than 0 if this Posting is
     * numerically less than the argument Posting; and a value greater
     * than 0 if this Posting is numerically greater than the argument Posting (signed comparison).
     */

    @Override
    public int compareTo(Posting anotherPosting) {
        return Integer.compare(this.docID, anotherPosting.getDocID());
    }
}
