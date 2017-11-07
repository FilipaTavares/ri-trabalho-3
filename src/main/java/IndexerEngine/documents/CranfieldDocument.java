package IndexerEngine.documents;

/**
 * Document representation of a xml file from the cranfield corpus
 */

public class CranfieldDocument extends Document {

    /**
     * Creates a new instance of CranfieldDocument
     *
     * @param id   the document id
     * @param text the document text (concatenation of title and actual text)
     */
    public CranfieldDocument(int id, String text) {
        super(id, text);
    }
}
