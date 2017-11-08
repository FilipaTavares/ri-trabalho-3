package IndexerEngine.indexer;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import static java.util.Comparator.comparingInt;

/**
 * This class indexes a document in a data structure composed of an hashmap in which a key is a term and the value
 * a list of Postings
 *
 * @see Posting
 */

public class Indexer {
    private Map<String, List<Posting>> invertedIndex;
    private int n_docs;
    private String tokenizerName;

    /**
     * Creates a new instance of Indexer
     */
    public Indexer() {
        this.invertedIndex = new HashMap<>();
        this.n_docs = 0;
        this.tokenizerName = "";
    }

    /**
     * Indexes a document given its list of terms and id
     *
     * @param terms list of terms of a document
     * @param docID document id
     */
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
                postingList.add(new Posting(docID, pair.getValue()));
                invertedIndex.put(pair.getKey(), postingList);
            }

            else {
                List<Posting> postingList = invertedIndex.get(pair.getKey());
                postingList.add(new Posting(docID, pair.getValue()));
            }
        }
        n_docs++;


        System.out.println(temp);
    }

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
     * String representation of this Indexer Object
     *
     * @return a String representation of this Indexer Object
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

    //FICAR COM ESTE MAIS PEQUENO
    public void saveToFile2(String filename, String tokenizerName) {

        try(PrintWriter writer = new PrintWriter(filename)) {
            List<String> orderedKeys = invertedIndex.keySet().stream().sorted().collect(Collectors.toList());

            writer.println(tokenizerName);

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

    public void saveToFile(String filename, String tokenizerName) {
        try {

            OutputStreamWriter streamWriter = new OutputStreamWriter(new FileOutputStream(filename),
                    "UTF-8");
            BufferedWriter writer = new BufferedWriter(streamWriter);

            List<String> orderedKeys = invertedIndex.keySet().stream().sorted().collect(Collectors.toList());

            writer.write(tokenizerName + " " + n_docs + "\n");

            for (String key : orderedKeys) {

                StringBuilder builder = new StringBuilder(key).append(" ");

                Collections.sort(invertedIndex.get(key));

                invertedIndex.get(key).forEach(posting ->
                        builder.append(posting.toString()).append(","));

                String postingList = builder.toString();
                postingList = postingList.substring(0, postingList.length() - 1);
                writer.write(postingList);
                writer.newLine();
            }
            writer.close();

        } catch (IOException e) {
            System.err.println("Unable to save index to file" + e);
        }
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

    public int getN_docs() {
        return n_docs;
    }

    public void setN_docs(int n_docs) {
        this.n_docs = n_docs;
    }

    public String getTokenizerName() {
        return tokenizerName;
    }

    public void setTokenizerName(String tokenizerName) {
        this.tokenizerName = tokenizerName;
    }
}
