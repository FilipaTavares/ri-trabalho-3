package SearchEngine.IndexReader;

import IndexerEngine.indexer.Indexer;
import IndexerEngine.indexer.Posting;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Class that reads and creates the index structure from an index file
 */
public class IndexReader {
    private List<Vector> vectors;
    private Map<String,Integer> documentsFrequency;
    private String tokenizerName;
    private int nDocs;
    
    public IndexReader() {
        this.vectors = new LinkedList<>();
        this.documentsFrequency = new HashMap<>();
        this.tokenizerName = "";
        this.nDocs = 0;
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

            if ((line = reader.readLine()) != null) {
                String[] s = line.split(" ");
                //tirar tokenizer daqui e meter em index
                tokenizerName = s[0];
                nDocs = Integer.parseInt(s[1]);
                indexer.setN_docs(nDocs);
                indexer.setTokenizerName(tokenizerName);
            }

            while ((line = reader.readLine()) != null) {
                String[] s = line.split("[ ,]");
                String term = s[0];
                List<Posting> postings = new LinkedList<>();
                int docFreq = 0;
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
                    
                    boolean exists = false;
                    for (Vector vector: vectors) {
                        if (vector.getDocId() == docId) {
                            vector.addTerm(term, wt);
                            exists = true;
                            break;
                        }
                    }
                    
                    if (!exists) {
                        Vector vector = new Vector(docId);
                        vector.addTerm(term, wt);
                        vectors.add(vector);
                    }
                    
                    docFreq++;
                    postings.add(new Posting(docId, wt));
                }
                documentsFrequency.put(term, docFreq);
                indexer.addToIndex(term, postings);
            }

        } catch (IOException e) {
            System.err.println("Error reading index file");
            System.exit(1);
        }

        return indexer;
    }
    
    public List<Vector> getVectors(){
        return vectors;
    }
    
    public Map<String, Integer> getDocumentsFrequency(){
        return documentsFrequency;
    }
    
    public String getTokenizerName(){
        return tokenizerName;
    }
    
    public int getNDocs(){
        return nDocs;
    }
}

