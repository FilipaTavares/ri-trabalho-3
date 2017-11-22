package SearchEngine.IndexReader;

import IndexerEngine.indexer.Indexer;
import IndexerEngine.indexer.IndexerWtNorm;
import IndexerEngine.indexer.Posting;
import IndexerEngine.indexer.PostingWtNorm;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Class that reads and creates the index structure from an index file
 */
public class IndexWtNormReader implements IndexReader{

    /**
     * Method that reads the index file parsing the first line to save the tokenizer name used and
     * the number of documents. The remaining lines
     * to construct the index structure which is returned.
     * 
     * @param filename index filename
     * @return a IndexerWtNorm object that contains the index structure
     */
    @Override
    public Indexer readIndex(String filename) {

        Indexer indexer = new IndexerWtNorm();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))){
            String line;

            if ((line = reader.readLine()) != null) {
                String[] s = line.split(" ");

                String tokenizerName = s[0];
                int n_docs = Integer.parseInt(s[1]);
                indexer.setN_docs(n_docs);
                indexer.setTokenizerName(tokenizerName);
            }

            while ((line = reader.readLine()) != null) {
                String[] s = line.split("[ ,]");
                String term = s[0];
                List<Posting> postings = new LinkedList<>();

                for (int i = 1; i < s.length; i++) {
                    String[] split = s[i].split(":");
                    int docId;
                    double wt;
                    try {
                        docId = Integer.parseInt(split[0]);
                        wt = Double.parseDouble(split[1]);
                    } catch (NumberFormatException e) {
                        System.err.println("Error processing posting from file");
                        System.out.println(Arrays.toString(split));
                        continue;
                    }

                    postings.add(new PostingWtNorm(docId, wt));
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

