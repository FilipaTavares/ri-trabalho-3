package SearchEngine.IndexReader;

import IndexerEngine.indexer.Indexer;
import IndexerEngine.indexer.Posting;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Class that reads and creates the index structure from an index file
 */
public class IndexReader {
    private String tokenizerName;

    /**
     * Returns the tokenizer class name
     * 
     * @return tokenizer class name
     */
    public String getTokenizerName() {
        return tokenizerName;
    }

    /**
     * Method that reads the index file parsing the first line to save the tokenizer name used and the remaining lines
     * to construct the index structure which is returned.
     * @param filename index filename
     * @return a Indexer object that contains the index structure
     */
    public Indexer readIndex(String filename) {
        Indexer indexer = new Indexer();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))){
            String line;

            if ((line = reader.readLine()) != null)
                tokenizerName = line.trim();

            while ((line = reader.readLine()) != null) {
                String[] s = line.split("[ ,]");
                String term = s[0];
                List<Posting> postings = new LinkedList<>();

                for (int i = 1; i < s.length; i++) {
                    String[] split = s[i].split(":");
                    int docId;
                    int termFreq;
                    try {
                        docId = Integer.parseInt(split[0]);
                        termFreq = Integer.parseInt(split[1]);
                    } catch (NumberFormatException e) {
                        System.err.println("Error processing posting from file");
                        System.out.println(Arrays.toString(split));
                        continue;
                    }

                    postings.add(new Posting(docId, termFreq));
                }

                indexer.addToIndex(term, postings);
            }

        } catch (IOException e) {
            System.err.println("Error reading index file");
            System.exit(1);
        }

        return indexer;
    }
}

