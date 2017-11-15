package IndexerEngine.indexer;

public abstract class Posting implements Comparable<Posting> {
    protected int docID;

    public Posting(int docID) {
        this.docID = docID;
    }

    public int getDocID() {
        return docID;
    }

    @Override
    public int compareTo(Posting anotherPosting) {
        return Integer.compare(this.docID, anotherPosting.getDocID());
    }
}
