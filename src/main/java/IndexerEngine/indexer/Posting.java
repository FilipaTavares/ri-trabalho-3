package IndexerEngine.indexer;

/**
 * This class represents regarding a given term a tuple, where is constituted by a docID
 * and the other value is defined by the subclass that implements its class
 * 
 */

public abstract class Posting implements Comparable<Posting> {
    protected int docID;

    public Posting(int docID) {
        this.docID = docID;
    }

    /**
     * Returns the document id
     * 
     * @return document id
     */
    public int getDocID() {
        return docID;
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
