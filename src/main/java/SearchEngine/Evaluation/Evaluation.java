
package SearchEngine.Evaluation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Evaluation {
    private List<QueryMetrics> queriesMetrics;
    private double true_positives;
    private double retrieved_docs;
    private double relevant_docs;
    private int n_ratings;

    private double map;
    private double map10;
    private double mrr;
    private long mql;
    private double query_throughput;

    private double precision;
    private double recall;
    private double fmeasure;
    private List<Double> recall_levels;

    public Evaluation(String filename) {
        this.queriesMetrics = new LinkedList<>();
        readFile(filename);

        // fixo porque de forma iterativa valores de double não são exatos ex: 0.3 -> 0.30000000004
        recall_levels = new ArrayList<>();
        recall_levels.add(0.0);
        recall_levels.add(0.1);
        recall_levels.add(0.2);
        recall_levels.add(0.3);
        recall_levels.add(0.4);
        recall_levels.add(0.5);
        recall_levels.add(0.6);
        recall_levels.add(0.7);
        recall_levels.add(0.8);
        recall_levels.add(0.9);
        recall_levels.add(1.0);
    }

    private void readFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))){
            String line;

            while ((line = reader.readLine()) != null) {
                String[] s = line.split(" +");
                int queryId = Integer.parseInt(s[0]);
                int docId = Integer.parseInt(s[1]);
                int relevance = Integer.parseInt(s[2]);
                
                int index = queriesMetrics.indexOf(new QueryMetrics(queryId));
                if (index != -1) {
                    QueryMetrics queryMetrics = queriesMetrics.get(index);
                    queryMetrics.addRelevantDoc(docId, relevance);
                }
                else {
                    QueryMetrics queryMetrics = new QueryMetrics(queryId);
                    queryMetrics.addRelevantDoc(docId, relevance);
                    queriesMetrics.add(queryMetrics);
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading query relevance file");
            System.exit(1);
        }

    }

    private void calculateSystemPrecision() {
        if (retrieved_docs == 0.0)
            this.precision = 0.0;
        else
            this.precision =  true_positives / retrieved_docs;
    }

    private void calculateSystemRecall() {
        if (relevant_docs == 0.0)
            this.recall = 0.0;
        else
            this.recall = true_positives / relevant_docs;

    }

    private void calculateSystemFmeasure() {
        if (this.recall == 0.0 && this.precision == 0.0)
            this.fmeasure = 0.0;
        else
            this.fmeasure = (2 * recall * precision) / (recall + precision);
    }

    private void calculatePrecision(int queryID, List<Integer> retrievedDocs, List<Integer> relevantDocs) {
        double tp = 0.0;
        for (int docId: relevantDocs) {
            if (retrievedDocs.contains(docId))
                tp++;
        }

        this.true_positives += tp;
        this.retrieved_docs += retrievedDocs.size();
        this.relevant_docs += relevantDocs.size();
        queriesMetrics.get(queryID - 1).calculatePrecision(tp, retrievedDocs.size());
    }

    private void calculateRecall(int queryID, List<Integer> retrievedDocs, List<Integer> relevantDocs) {
        double tp = 0.0;
        for (int docId: relevantDocs) {
            if (retrievedDocs.contains(docId))
                tp++;
        }
        queriesMetrics.get(queryID - 1).calculateRecall(tp, relevantDocs.size());
    }

    private void calculateAveragePrecision(int query, List<Integer> documentsRetrieved, List<Integer> relevantDocs) {

        double precisionsSum = 0.0;
        int nRelevant = relevantDocs.size();
        double countRetr = 0;
        double tp = 0;
        for (int docId: documentsRetrieved) {
            countRetr++;
            if (relevantDocs.contains(docId)) {
                tp++;
                double recall = tp / nRelevant;
                double precision = tp / countRetr;
                queriesMetrics.get(query - 1).addRecallPrecisionPoint(recall, precision);
                precisionsSum += precision;
            }

            if (tp == relevantDocs.size())
                break;

        }
        queriesMetrics.get(query - 1).calculateAveragePrecision(precisionsSum, tp);
        queriesMetrics.get(query - 1).interpolatePrecision(recall_levels);
    }

    public List<Double> averageRecallPrecision() {
        List<Double> averagePoints = new ArrayList<>();
        for (QueryMetrics queryMetrics : queriesMetrics) {
            List<Double> points = queryMetrics.getPoints();

            if (averagePoints.isEmpty()) {
                averagePoints.addAll(points);
            } else {
                for (int i = 0; i < points.size(); i++)
                    averagePoints.set(i, averagePoints.get(i) + points.get(i));

            }
        }
        averagePoints.replaceAll(sum -> sum / queriesMetrics.size());

        return averagePoints;
    }

    private void calculateAveragePrecisionAtRank10(int query, List<Integer> documentsRetrieved,
                                                   List<Integer> relevantDocs) {
        double precisionsSum = 0.0;
        double countTotal = 0;
        double tp = 0;

        if(documentsRetrieved.size() >= 10) {
            documentsRetrieved = documentsRetrieved.subList(0,10);
        }

        for (int docId: documentsRetrieved) {
            countTotal++;
            if (relevantDocs.contains(docId)) {
                tp++;
                precisionsSum += tp / countTotal;
            }
            if (tp == relevantDocs.size())
                break;

        }

        queriesMetrics.get(query - 1).calculateAveragePrecisionAtRank10(precisionsSum, tp);
    }

    private void calculateMAP() {
        double averagePrecisionSum = 0.0;
        for (QueryMetrics queryMetrics : queriesMetrics) {
            averagePrecisionSum += queryMetrics.getAveragePrecision();
        }
        this.map = averagePrecisionSum / queriesMetrics.size();
    }

    private void calculateMAPatRank10() {
        double averagePrecisionSum = 0.0;
        for (QueryMetrics queryMetrics : queriesMetrics) {
            averagePrecisionSum += queryMetrics.getAveragePrecisionAtRank10();
        }
        this.map10 = averagePrecisionSum / queriesMetrics.size();
    }
    
    private void calculateReciprocalRank(int query, List<Integer> documentsRetrieved, List<Integer> relevantDocs) {

        double rr = 0.0;
        double countTotal = 0;
        for (int docId: documentsRetrieved) {
            countTotal++;

            if (relevantDocs.contains(docId)) {
                rr = 1 / countTotal;
                break;
            }
        }
        queriesMetrics.get(query-1).setReciprocalRank(rr);
    }
    
    private void calculateMRR() {
        double reciprocalRankSum = 0.0;
        for (QueryMetrics queryMetrics : queriesMetrics) {
            if (queryMetrics.getReciprocalRank() != 0.0)
               reciprocalRankSum += queryMetrics.getReciprocalRank();
        }
        this.mrr = reciprocalRankSum / queriesMetrics.size();
    }
    
    public void addQueryLatency(int queryId, long queryLatency) {
        queriesMetrics.get(queryId - 1).addQueryLatency(queryLatency);
    }
    
    private void calculateQueryThroughput() {
        long queryLatencySum = queriesMetrics.stream().mapToLong(QueryMetrics::getQueryLatency).sum();
        double totalTimeSeconds = (queryLatencySum / 1000.0);

        this.query_throughput =  (queriesMetrics.size() / totalTimeSeconds);
    }
    
    private void calculateMedianQueryLatency() {
        List<Long> queryLatency = queriesMetrics.stream().map(QueryMetrics::getQueryLatency).sorted().collect(Collectors.toList());
        if (queryLatency.size() %2  == 0) {
            long l = queryLatency.get((queryLatency.size() / 2) - 1) + queryLatency.get(queryLatency.size() / 2);
            this.mql = (long) (l / 2.0);
        }
        else {
            this.mql = queryLatency.get((int) Math.floor(queryLatency.size() / 2.0));
        }
    }

    private void calculateDCG(int queryId, List<Integer> documentsRetrieved) {

        Map<Integer, Integer> documentsRelevance = queriesMetrics.get(queryId - 1).getRelevantDocsWithRelevance(n_ratings);

        if (!documentsRetrieved.isEmpty()) {
            double dcg = documentsRelevance.containsKey(documentsRetrieved.get(0))
                    ? 5 - documentsRelevance.get(documentsRetrieved.get(0)) : 0.0;
            for (int i = 1; i < documentsRetrieved.size(); i++) {
                if (documentsRelevance.containsKey(documentsRetrieved.get(i)))
                    dcg += (double) (5 - documentsRelevance.get(documentsRetrieved.get(i)))/(Math.log(i+1)/Math.log(2));
            }
            queriesMetrics.get(queryId - 1).setDcg(dcg);
        }
        else
            queriesMetrics.get(queryId - 1).setDcg(0.0);
    }
    

    public void calculateQueryMeasures(int queryID, List<Integer> retrievedDocuments) {
        List<Integer> relevantDocuments = queriesMetrics.get(queryID - 1).getRelevantDocs(n_ratings);
        calculatePrecision(queryID, retrievedDocuments, relevantDocuments);
        calculateRecall(queryID, retrievedDocuments, relevantDocuments);
        queriesMetrics.get(queryID-1).calculateFMeasure();
        calculateAveragePrecision(queryID, retrievedDocuments, relevantDocuments);
        calculateAveragePrecisionAtRank10(queryID, retrievedDocuments, relevantDocuments);
        calculateReciprocalRank(queryID, retrievedDocuments, relevantDocuments);
        calculateDCG(queryID, retrievedDocuments);
    }

    public void calculateSystemMeasures() {
        calculateMAP();
        calculateMAPatRank10();
        calculateMRR();
        calculateMedianQueryLatency();
        calculateQueryThroughput();
        calculateSystemPrecision();
        calculateSystemRecall();
        calculateSystemFmeasure();
    }

    private String displayQueriesMetrics() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s | %s | %s | %s  | %s | %s | %s | %s | %s\n", "Query Id", "Latency (ms)",
                "Precision", "Recall","F-measure", "Av. precision", "Av. precision rank 10",
                "Reciprocal rank", "DCG\n"));

        queriesMetrics.forEach(queryMetrics -> sb.append(queryMetrics.toString()));
        return sb.toString();
    }

    public void printResults(boolean displayQueriesResults){
        if(displayQueriesResults)
            System.out.println(displayQueriesMetrics());

        System.out.println("True Positives " + true_positives);
        System.out.println("Retrieved Docs " + retrieved_docs);
        System.out.println("Relevant Docs " + relevant_docs);

        System.out.println("Precision: " + precision);
        System.out.println("Recall: " + recall);
        System.out.println("Fmeasure: " + fmeasure);
        System.out.printf("Mean Average Precision: %.5f\n", map);
        System.out.printf("Mean Average Precision at Rank 10: %.5f\n", map10);
        System.out.printf("Mean Reciprocal Rank: %.5f\n", mrr);
        System.out.println("Query Throughput per second: " + query_throughput);
        System.out.println("Median Query Latency in ms: "+ mql + "\n");

    }

    public void setN_ratings(int n_ratings) {
        this.n_ratings = n_ratings;
    }

    public void reset(){
        true_positives = 0.0;
        retrieved_docs = 0.0;
        relevant_docs = 0.0;
        map = 0.0;
        map10 = 0.0;
        mrr = 0.0;
        mql = (long) 0.0;
        query_throughput = 0.0;
        precision = 0.0;
        recall = 0.0;
        fmeasure = 0.0;

        queriesMetrics.forEach(QueryMetrics::reset);
    }



}
