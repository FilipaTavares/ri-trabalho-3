
package SearchEngine.Evaluation;

import java.util.LinkedList;
import java.util.List;

public class QueryMeasure {
    private int queryId;
    private List<Integer> documentsRelevant;
    private double precision;
    private double recall;
    private double fmeasure;
    private double averagePrecision;
    private double reciprocalRank;
    private long queryLatency;
    
    public QueryMeasure(int queryId) {
        this.queryId = queryId;
        this.documentsRelevant = new LinkedList<>();
    }
    
    public void addDocumentRelevant(int docId) {
        documentsRelevant.add(docId);
    }
    
    public void addQueryLatency(long queryLatency) {
        this.queryLatency = queryLatency;
    }
    
    public List<Integer> getDocumentsRelevant() {
        return documentsRelevant;
    }
    
    public void calculatePrecision(double tp, double numDocRetrieved) {
        precision = tp/numDocRetrieved;
    }

    public void calculateRecall(double tp) {
        recall = tp/documentsRelevant.size();
    }
    
    public void calculateFMeasure() {
        if (recall == 0.0 && precision == 0.0)
            fmeasure = 0.0;
        else
            fmeasure = (2*recall*precision)/(recall+precision);
    }

    public void calculateAveragePrecision(double precisions, double tp) {
        if (tp!=0)
            averagePrecision = precisions/tp;
        else
            averagePrecision = 0.0;
    }

    public void calculateReciprocalRank(double reciprocalRank) {
        this.reciprocalRank = reciprocalRank;
    }
   
    public double getPrecision() {
        return precision;
    }

    public double getRecall() {
        return recall;
    }

    public double getFmeasure() {
        return fmeasure;
    }

    public double getAveragePrecision() {
        return averagePrecision;
    }

    public double getReciprocalRank() {
        return reciprocalRank;
    }

    public long getQueryLatency() {
        return queryLatency;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QueryMeasure queryRelevance = (QueryMeasure) o;

        return queryId == queryRelevance.queryId;
    }

    @Override
    public int hashCode() {
        return queryId;
    }
    
    @Override
    public String toString() {
        return String.format("%4d %-3s | %10.2f %-5s | %8.4f %-1s | %.4f | %7.4f\n", 
                queryId, "", (float) queryLatency, "", precision, "", recall, fmeasure);
    }
    
}
