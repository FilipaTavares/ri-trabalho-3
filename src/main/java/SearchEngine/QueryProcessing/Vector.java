
package SearchEngine.QueryProcessing;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that represents a document as a vector
 * 
 */
public class Vector {
    private int docId;
    private Map<String, Double> weightMatrix;
    
    public Vector(int docId) {
        this.docId = docId;
        this.weightMatrix = new HashMap<>();
    }
    
    /**
     * Method that add a new entry to the hashmap weightMatrix
     * 
     * @param term term of the query
     * @param wt weight normalized
     */
    public void addTerm(String term, double wt) {
        weightMatrix.put(term, wt);
    }
    
    /**
     * Returns the value of the weight normalized for the term
     * @param term term of the query
     * @return weight normalized if the term its in hashmap, otherwise is returned 0
     */
    public double getTermWeight(String term) {
        if (weightMatrix.containsKey(term))
            return weightMatrix.get(term);
        return 0.0;
    }
       
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector vector = (Vector) o;

        return docId == vector.docId;
    }

    @Override
    public int hashCode() {
        return docId;
    }

    /**
     * Returns the document id
     * 
     * @return document id
     */
    public int getDocId() {
        return docId;
    }

    @Override
    public String toString() {
        return "Vector{" + "docId=" + docId + ", weightMatrix=" + weightMatrix + '}';
    }

    
}