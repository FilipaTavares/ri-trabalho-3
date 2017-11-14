
package SearchEngine.QueryProcessing;

import java.util.HashMap;
import java.util.Map;


public class Vector {
    private int docId;
    private Map<String, Double> weightMatrix;
    
    public Vector(int docId) {
        this.docId = docId;
        this.weightMatrix = new HashMap<>();
    }
    
    public void addTerm(String term, double wt) {
        weightMatrix.put(term, wt);
    }
    
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

    public int getDocId() {
        return docId;
    }

    @Override
    public String toString() {
        return "Vector{" + "docId=" + docId + ", weightMatrix=" + weightMatrix + '}';
    }

    
}