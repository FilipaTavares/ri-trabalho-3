package IndexerEngine.documents;

/**
 * This abstract class serves as a base to represent different IndexerEngine.documents generated from several corpora
 */

public abstract class Document {

    private int id;
    private String text;

    /**
     * @param id   the document id
     * @param text the document text
     */

    public Document(int id, String text) {
        this.id = id;
        this.text = text;
    }

    /**
     * Returns the id of the document
     *
     * @return document id
     */
    public int getId() {
        return id;
    }

    /**
     * Return the text of the document
     *
     * @return document text
     */
    public String getText() {
        return text;
    }

    /**
     * Returns a String object representing the value of this Document.
     *
     * @return String object representing the value of this Document.
     */

    @Override
    public String toString() {
        return "Document{" + "id=" + id + ", text='" + text + '\'' + '}';
    }
}
