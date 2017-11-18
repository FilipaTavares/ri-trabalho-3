
package SearchEngine.Evaluation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryMeasure {
    private int queryId;
    private Map<Integer, Integer> documentsRelevant;
    private double precision;
    private double recall;
    private double fmeasure;
    private double averagePrecision;
    private double averagePrecisionAtRank10;
    private double reciprocalRank;
    private long queryLatency;
    private double discountedCumulativeGain;
    
    public QueryMeasure(int queryId) {
        this.queryId = queryId;
        this.documentsRelevant = new HashMap<>();
    }
    
    public void addDocumentRelevant(int docId, int relevance) {
        documentsRelevant.put(docId,relevance);
    }
    
    public void addQueryLatency(long queryLatency) {
        this.queryLatency = queryLatency;
    }
    
    public List<Integer> getDocumentsRelevant() {
        return documentsRelevant.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
    }
    
    public Map<Integer, Integer> getDocumentsRelevantWithRelevance() {
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
            fmeasure = (2 * recall * precision) / (recall + precision);
    }

    public void calculateAveragePrecision(double precisions, double tp) {
        if (tp != 0)
            averagePrecision = precisions / tp;
        else
            averagePrecision = 0.0;
    }
    public void calculateAveragePrecisionAtRank10(double precisions, double tp) {
        if (tp != 0)
            averagePrecisionAtRank10 = precisions / tp;
        else
            averagePrecisionAtRank10 = 0.0;
    }

    public void calculateReciprocalRank(double reciprocalRank) {
        this.reciprocalRank = reciprocalRank;
    }
    
     public void calculateDCG(double discountedCumulativeGain) {
        this.discountedCumulativeGain = discountedCumulativeGain;
    }

    public double getAveragePrecision() {
        return averagePrecision;
    }

    public double getAveragePrecisionAtRank10() {
        return averagePrecisionAtRank10;
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
        return String.format("%4d | %10.2f | %8.4f | %.4f | %7.4f | %.4f\n",
                queryId, (float) queryLatency, precision, recall, fmeasure, discountedCumulativeGain);
    }
    
}
