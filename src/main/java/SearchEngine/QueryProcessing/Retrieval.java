
package SearchEngine.QueryProcessing;

public interface Retrieval {

    public void retrieve(int queryId, String queryText);
    
    public void saveToFile(String filename);
    
    public void calculateMeasures(int queryId);
    
    public void printAllEvaluations();
}
