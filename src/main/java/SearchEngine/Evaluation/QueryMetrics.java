
package SearchEngine.Evaluation;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class that represents the metrics of the query
 * 
 */
public class QueryMetrics {
    private int queryId;
    private Map<Integer, Integer> relevantDocs;
    private double precision;
    private double recall;
    private double fmeasure;
    private double averagePrecision;
    private double averagePrecisionAtRank10;
    private double reciprocalRank;
    private double queryLatency;
    private double discountedCumulativeGain;
    private Map<Double, Double> recall_precision;
    private List<Double> points;

    public QueryMetrics(int queryId) {
        this.queryId = queryId;
        this.relevantDocs = new HashMap<>();
        recall_precision = new LinkedHashMap<>();
        points = new ArrayList<>();
    }

    /**
     * Method that saves the points for later make the interpolation
     * 
     * @param recall value of recall
     * @param precision value of precision
     */
    public void addRecallPrecisionPoint(double recall, double precision) {
        recall_precision.put(recall, precision);
    }
    
    /**
     * Method that saves the relevant documents for the query and its relevance level
     * 
     * @param docId document id
     * @param relevance relevance level
     */
    public void addRelevantDoc(int docId, int relevance) {
        relevantDocs.put(docId,relevance);
    }
    
    /**
     * Store or modify the query latency
     * 
     * @param queryLatency query latency
     */
    public void addQueryLatency(double queryLatency) {
        this.queryLatency = queryLatency;
    }
    
    /**
     * Return a list of the relevant documents based on the relevance level given as argument
     * 
     * @param n_ratings relevance level
     * @return list of the relevant documents
     */
    public List<Integer> getRelevantDocs(int n_ratings) {
        return relevantDocs.entrySet().stream().filter(entry -> entry.getValue() <= n_ratings).map(Map.Entry::getKey).collect(Collectors.toList());
    }
    
    /**
     * Returns a map where the keys are the id of the relevant documents and the 
     * value the relevance level of the document. This map is constructed based on 
     * the relevance level given as argument
     * 
     * @param n_ratings relevance level
     * @return map that contains the relevant documents and its relevance level
     */
    public Map<Integer, Integer> getRelevantDocsWithRelevance(int n_ratings) {
        return relevantDocs.entrySet().stream().filter(entry -> entry.getValue() <= n_ratings)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    /**
     * Method that calculate the precison of the query
     * 
     * @param tp value of true positives
     * @param numDocRetrieved number of retrieved documents
     */
    public void calculatePrecision(double tp, double numDocRetrieved) {
        if (numDocRetrieved == 0.0)
            precision = 0.0;
        else
            precision = tp / numDocRetrieved;
    }

    /**
     * Method that calculate the recall of the query
     * 
     * @param tp value of true positives
     * @param relevantDocs numver of relevant documents
     */
    public void calculateRecall(double tp, double relevantDocs) {
        if (relevantDocs == 0.0)
            recall = 0.0;
        else
            recall = tp / relevantDocs;
    }
    
    /**
     * Method that calculate the F-measure of the query
     * 
     */
    public void calculateFMeasure() {
        if (recall == 0.0 && precision == 0.0)
            fmeasure = 0.0;
        else
            fmeasure = (2 * recall * precision) / (recall + precision);
    }

    /**
     * Method that calculate the average precision of the query
     * 
     * @param precisions sum of the precisions
     * @param tp value of true positives
     */
    public void calculateAveragePrecision(double precisions, double tp) {
        if (tp != 0)
            averagePrecision = precisions / tp;
        else
            averagePrecision = 0.0;
    }

    /**
     * Method that calculate the average precison of the first 10 retrieved documents of the query
     * 
     * @param precisions sum of the precisions
     * @param tp value of true positives
     */
    public void calculateAveragePrecisionAtRank10(double precisions, double tp) {
        if (tp != 0)
            averagePrecisionAtRank10 = precisions / tp;
        else
            averagePrecisionAtRank10 = 0.0;
    }

    /**
     * Store or modify the reciprocal rank of the query
     * 
     * @param reciprocalRank reciprocal rank 
     */
    public void setReciprocalRank(double reciprocalRank) {
        this.reciprocalRank = reciprocalRank;
    }
    
    /**
     * Store or modify the discounted cumulative gain of the query
     * 
     * @param discountedCumulativeGain discounted cumulative gain
     */
     public void setDcg(double discountedCumulativeGain) {
        this.discountedCumulativeGain = discountedCumulativeGain;
    }

    /**
     * Returns the average precision of the query
     * 
     * @return average precision of the query
     */
    public double getAveragePrecision() {
        return averagePrecision;
    }

    /**
     * Returns the average precison of the first 10 retrieved documents of the query
     *
     * @return average precison of the first 10 retrieved documents of the query
     */
    public double getAveragePrecisionAtRank10() {
        return averagePrecisionAtRank10;
    }

    /**
     * Returns the reciprocal rank of the query
     * 
     * @return reciprocal rank of the query
     */
    public double getReciprocalRank() {
        return reciprocalRank;
    }

    /**
     * Returns the query latency
     * 
     * @return query latency
     */
    public double getQueryLatency() {
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
    
    /**
     * Returns the precision of the query
     * 
     * @return precision of the query 
     */
    public double getPrecision() {
        return precision;
    }

    /**
     * Returns the recall of the query
     * 
     * @return recall of the query
     */
    public double getRecall() {
        return recall;
    }

    /**
     * Return the F-measure of the query
     * 
     * @return F-measure of the query
     */
    public double getFmeasure() {
        return fmeasure;
    }

    /**
     * Method that calculate the precision for pre-defined recall values of [0.0,1.0].
     * This method is only used for the creation of the precision-recall graphs
     * 
     * @param levels list of the recall level
     */
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

    /**
     * Returns a list of points to create a precison-recall graph
     * 
     * @return list of points
     */
    public List<Double> getPoints() {
        return points;
    }

    /**
     * Method that restores the attributes of the class
     * 
     */
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

    /**
     * String object representation of this QueryMetrics
     *
     * @return a String object representing of this QueryMetrics
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%-9d| %-13f| %-9.5f | %-6.5f | %-10.5f| %-14.5f" +
                        "| %-22.5f| %-16.5f| %.5f\n",
                queryId , (float) queryLatency, precision, recall, fmeasure, averagePrecision, averagePrecisionAtRank10,
                reciprocalRank, discountedCumulativeGain);
    }
}
