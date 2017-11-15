package IndexerEngine.indexer;

/**
 * This class represents regarding a given term a tuple (docID, wt_norm) which states that the term is present
 * in the document with the id docID and it appears termFreq times
 */

public class PostingWtNorm extends Posting {
    private double wt_norm;

    /**
     * Creates a new instance of PostingWtNorm
     *
     * @param docID    the document id
     * @param wt_norm t
     */

    public PostingWtNorm(int docID, double wt_norm) {
        super(docID);
        this.wt_norm = wt_norm;
    }


    public double getWt_norm() {
        return wt_norm;
    }

    /**
     * String object representation of this PostingWtNorm
     *
     * @return a String object representing of this PostingWtNorm.
     */

    @Override
    public String toString() {
        return docID + ":" + wt_norm;
    }

}
