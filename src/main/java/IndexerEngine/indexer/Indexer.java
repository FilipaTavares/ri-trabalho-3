package IndexerEngine.indexer;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Abstract class that represents in a general way an indexer
 * 
 */
public abstract class Indexer {
    protected Map<String, List<Posting>> invertedIndex;
    protected int n_docs;
    protected String tokenizerName;

    public Indexer() {
        this.invertedIndex = new HashMap<>();
        this.n_docs = 0;
        this.tokenizerName = "";
    }

    /**
     * Abstract method that should index a document
     * 
     * @param terms list of terms of a document
     * @param docID document id
     */
    public abstract void index(List<String> terms, int docID);

    /**
     * Add to the index structure a new entry
     *
     * @param term word that appear in the document
     * @param postings list of postings
     */
    public void addToIndex(String term, List<Posting> postings) {
        invertedIndex.put(term, postings);
    }

    /**
     * Get the list of postings of the term
     *
     * @param term word to obtain the list of postings
     * @return list of postings of the term
     */
    public List<Posting> getTermPostings(String term) {
        return invertedIndex.get(term);
    }

    /**
     * Returns the number of key-value mappings of the IndexerEngine.indexer
     *
     * @return the size of the IndexerEngine.indexer (the number of key-value mappings)
     */
    public int size() {
        return invertedIndex.size();
    }

    /**
     * String representation of this IndexerWtNorm Object
     *
     * @return a String representation of this IndexerWtNorm Object
     */
    @Override
    public String toString() {
        return invertedIndex.toString();
    }

    /**
     * Save the resulting index to a file using the following format (one term per line): term,doc id:term freq, ...
     *
     * @param filename output file name
     * @param tokenizerName tokenizer class name
     */

    public void saveToFile(String filename, String tokenizerName) {

        try(PrintWriter writer = new PrintWriter(filename)) {
            List<String> orderedKeys = invertedIndex.keySet().stream().sorted().collect(Collectors.toList());

            writer.println(tokenizerName + " " + n_docs);

            for (String key : orderedKeys) {

                StringBuilder builder = new StringBuilder(key).append(" ");

                Collections.sort(invertedIndex.get(key));

                invertedIndex.get(key).forEach(posting ->
                        builder.append(posting.toString()).append(","));

                String postingList = builder.toString();
                postingList = postingList.substring(0, postingList.length() - 1);
                writer.println(postingList);
            }
            writer.close();
        } catch (FileNotFoundException e) {
            System.err.println("Unable to save index to file" + e);
        }

    }

    /**
     * Returns the number of documents processed
     * 
     * @return number of documents processed
     */
    public int getN_docs() {
        return n_docs;
    }

    /**
     * Store or modify the number of documents processed
     * 
     * @param n_docs the number of documents processed
     */
    public void setN_docs(int n_docs) {
        this.n_docs = n_docs;
    }

    /**
     * Returns the tokenizer class name
     * 
     * @return tokenizer class name
     */
    public String getTokenizerName() {
        return tokenizerName;
    }

    /**
     * Store or modify the tokenizer class name
     * 
     * @param tokenizerName tokenizer class name
     */
    public void setTokenizerName(String tokenizerName) {
        this.tokenizerName = tokenizerName;
    }
}
