
package SearchEngine.Evaluation;

import java.util.*;
import java.util.stream.Collectors;

public class QueryMetrics {
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
    private Map<Double, Double> recall_precision;
    private List<Double> points;

    public QueryMetrics(int queryId) {
        this.queryId = queryId;
        this.documentsRelevant = new HashMap<>();
        recall_precision = new LinkedHashMap<>();
        points = new ArrayList<>();
    }

    public void addRecallPrecisionPoint(double recall, double precision) {
        recall_precision.put(recall, precision);
    }
    
    public void addDocumentRelevant(int docId, int relevance) {
        documentsRelevant.put(docId,relevance);
    }
    
    public void addQueryLatency(long queryLatency) {
        this.queryLatency = queryLatency;
    }
    
    public List<Integer> getDocumentsRelevant(int n_ratings) {
        return documentsRelevant.entrySet().stream().filter(entry -> entry.getValue() <= n_ratings).map(Map.Entry::getKey).collect(Collectors.toList());
    }
    
    public Map<Integer, Integer> getDocumentsRelevantWithRelevance(int n_ratings) {
        return documentsRelevant.entrySet().stream().filter(entry -> entry.getValue() <= n_ratings)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    public void calculatePrecision(double tp, double numDocRetrieved) {
        if (numDocRetrieved == 0.0)
            precision = 0.0;
        else
            precision = tp / numDocRetrieved;
    }

    public void calculateRecall(double tp, double relevantDocs) {
        if (relevantDocs == 0.0)
            recall = 0.0;
        else
            recall = tp / relevantDocs;
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

    public void setReciprocalRank(double reciprocalRank) {
        this.reciprocalRank = reciprocalRank;
    }
    
     public void setDcg(double discountedCumulativeGain) {
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

        QueryMetrics queryRelevance = (QueryMetrics) o;

        return queryId == queryRelevance.queryId;
    }

    @Override
    public int hashCode() {
        return queryId;
    }
    
    public String displayQueriesMetrics() {
        return String.format(Locale.ROOT, "%-9d| %-13.2f| %-10.4f | %.4f | %-10.4f| %.4f\n",
                queryId, (float) queryLatency, precision, recall, fmeasure, discountedCumulativeGain);
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

    public void interpolatePrecision(List<Double> levels) {
        Set<Double> keys = recall_precision.keySet();

        for (double level: levels) {
            double max_precision = 0.0;

            for (double rl: keys) {
                if (rl >= level && recall_precision.get(rl) > max_precision) {
                    max_precision = recall_precision.get(rl);
                }
            }
            this.points.add(max_precision);
        }

    }

    public List<Double> getPoints() {
        return points;
    }

    public void reset() {
        this.precision = 0.0;
        this.recall = 0.0;
        this.fmeasure = 0.0;
        this.averagePrecision = 0.0;
        this.averagePrecisionAtRank10 = 0.0;
        this.reciprocalRank = 0.0;
        this.discountedCumulativeGain = 0.0;
        this.recall_precision.clear();
        this.points.clear();
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%-9d| %-13f| %-9.5f | %-6.5f | %-10.5f| %-14.5f" +
                        "| %-22.5f| %-16.5f| %.5f\n",
                queryId , (float) queryLatency, precision, recall, fmeasure, averagePrecision, averagePrecisionAtRank10,
                reciprocalRank, discountedCumulativeGain);
    }
}
