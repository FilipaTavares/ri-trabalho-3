
package SearchEngine.Evaluation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Evaluation {
    private List<QueryMeasure> queriesMeasure;
    private double true_positives;
    private double retrieved_docs;
    private double relevant_docs;
    private double threshold;
    private int n_ratings;

    private double map;
    private double map10;
    private double mrr;
    private long mql;
    private double query_throughput;

    private double precision;
    private double recall;
    private double fmeasure;
    List<Double> levels;

    public Evaluation(String filename) {
        this.queriesMeasure = new LinkedList<>();
        this.threshold = threshold;
        readFile(filename);

        levels = new ArrayList<>();
        levels.add(0.0);
        levels.add(0.1);
        levels.add(0.2);
        levels.add(0.3);
        levels.add(0.4);
        levels.add(0.5);
        levels.add(0.6);
        levels.add(0.7);
        levels.add(0.8);
        levels.add(0.9);
        levels.add(1.0);
    }

    private void readFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))){
            String line;

            while ((line = reader.readLine()) != null) {
                String[] s = line.split(" +");
                int queryId = Integer.parseInt(s[0]);
                int docId = Integer.parseInt(s[1]);
                int relevance = Integer.parseInt(s[2]);
                
                int index = queriesMeasure.indexOf(new QueryMeasure(queryId));
                if (index != -1) {
                    QueryMeasure queryMeasure = queriesMeasure.get(index);
                    queryMeasure.addDocumentRelevant(docId, relevance);
                }
                else {
                    QueryMeasure queryMeasure = new QueryMeasure(queryId);
                    queryMeasure.addDocumentRelevant(docId, relevance);
                    queriesMeasure.add(queryMeasure);
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading query relevance file");
            System.exit(1);
        }

    }

    public void calculateSystemPrecision() {
        double sum = 0.0;
        for (QueryMeasure q: queriesMeasure) {
            sum += q.getPrecision();
        }

        System.out.println("AVERAGE PRECISION " + sum / queriesMeasure.size());

        if (retrieved_docs == 0.0)
            this.precision = 0.0;
        else
            this.precision =  true_positives / retrieved_docs;
    }

    public void calculateSystemRecall() {
        double sum = 0.0;
        for (QueryMeasure q: queriesMeasure) {
            sum += q.getRecall();
        }

        System.out.println("AVERAGE RECALL " + sum / queriesMeasure.size());

        if (relevant_docs == 0.0)
            this.recall = 0.0;
        else
            this.recall = true_positives / relevant_docs;

    }

    public void calculateSystemFmeasure() {
        double sum = 0.0;
        for (QueryMeasure q: queriesMeasure) {
            sum += q.getFmeasure();
        }

        System.out.println("AVERAGE FMEASURE " + sum / queriesMeasure.size());


        if (this.recall == 0.0 && this.precision == 0.0)
            this.fmeasure = 0.0;
        else
            this.fmeasure = (2 * recall * precision) / (recall + precision);
    }

    public void calculatePrecision(int query, List<Integer> documentsRetrieved, List<Integer> relevantDocs) {
        double tp = 0.0;
        for (int docId: relevantDocs) {
            if (documentsRetrieved.contains(docId))
                tp++;
        }

        this.true_positives += tp;
        this.retrieved_docs += documentsRetrieved.size();
        this.relevant_docs += relevantDocs.size();
        queriesMeasure.get(query - 1).calculatePrecision(tp, documentsRetrieved.size());
    }

    public void calculateRecall(int query, List<Integer> documentsRetrieved, List<Integer> relevantDocs) {
        double tp = 0;
        for (int docId: relevantDocs) {
            if (documentsRetrieved.contains(docId))
                tp++;
        }
        queriesMeasure.get(query - 1).calculateRecall(tp, relevantDocs.size());
    }

    public void calculateFmeasure(int query) {
        queriesMeasure.get(query-1).calculateFMeasure();
    }
    
    public void calculateAveragePrecision(int query, List<Integer> documentsRetrieved, List<Integer> relevantDocs) {

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
                queriesMeasure.get(query - 1).addRecallPrecisionPoint(recall, precision);
                precisionsSum += precision;
            }

            if (tp == relevantDocs.size())
                break;

        }
        queriesMeasure.get(query - 1).calculateAveragePrecision(precisionsSum, tp);
        queriesMeasure.get(query - 1).interpolatePrecision(levels);
    }

    public List<Double> averageRecallPrecision() {
        List<Double> averagePoints = new ArrayList<>();
        for (QueryMeasure queryMeasure : queriesMeasure) {
            List<Double> points = queryMeasure.getPoints();

            if (averagePoints.isEmpty()) {
                averagePoints.addAll(points);
            } else {
                for (int i = 0; i < points.size(); i++)
                    averagePoints.set(i, averagePoints.get(i) + points.get(i));

            }
        }
        averagePoints.replaceAll(sum -> sum / queriesMeasure.size());

        return averagePoints;
    }

    public void calculateAveragePrecisionAtRank10(int query, List<Integer> documentsRetrieved,
                                                  List<Integer> relevantDocs) {
        double precisionsSum = 0.0;
        double countTotal = 0;
        double tp = 0;

        //QUANDO COM THRESHOLD VER SIZE PODE DAR MENOS DE 10
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

        queriesMeasure.get(query - 1).calculateAveragePrecisionAtRank10(precisionsSum, tp);
    }

    public void calculateMAP() {
        double averagePrecisionSum = 0.0;
        for (QueryMeasure queryMeasure: queriesMeasure) {
            averagePrecisionSum += queryMeasure.getAveragePrecision();
        }
        this.map = averagePrecisionSum / queriesMeasure.size();
    }



    public void calculateMAPatRank10() {
        double averagePrecisionSum = 0.0;
        for (QueryMeasure queryMeasure: queriesMeasure) {
            averagePrecisionSum += queryMeasure.getAveragePrecisionAtRank10();
        }
        this.map10 = averagePrecisionSum / queriesMeasure.size();
    }
    
    public void calculateReciprocalRank(int query, List<Integer> documentsRetrieved,  List<Integer> relevantDocs) {

        double rr = 0.0;
        double countTotal = 0;
        for (int docId: documentsRetrieved) {
            countTotal++;

            if (relevantDocs.contains(docId)) {
                rr = 1 / countTotal;
                break;
            }
        }
        queriesMeasure.get(query-1).calculateReciprocalRank(rr);
    }
    
    public void calculateMRR() {
        double reciprocalRankSum = 0.0;
        for (QueryMeasure queryMeasure: queriesMeasure) {
            if (queryMeasure.getReciprocalRank() != 0.0)
               reciprocalRankSum += queryMeasure.getReciprocalRank();
        }
        this.mrr = reciprocalRankSum / queriesMeasure.size();
    }
    
    public void addQueryLatency(int queryId, long queryLatency) {
        queriesMeasure.get(queryId - 1).addQueryLatency(queryLatency);
    }
    
    public void calculateQueryThroughput() {
        long queryLatencySum = queriesMeasure.stream().mapToLong(QueryMeasure::getQueryLatency).sum();
        double totalTimeSeconds = (queryLatencySum / 1000.0);

        this.query_throughput =  (queriesMeasure.size() / totalTimeSeconds);
    }
    
    public void calculateMedianQueryLatency() {
        List<Long> queryLatency = queriesMeasure.stream().map(QueryMeasure::getQueryLatency).sorted().collect(Collectors.toList());
        if (queryLatency.size() %2  == 0) {
            long l = queryLatency.get((queryLatency.size() / 2) - 1) + queryLatency.get(queryLatency.size() / 2);
            this.mql = (long) (l / 2.0);
        }
        else {
            this.mql = queryLatency.get((int) Math.floor(queryLatency.size() / 2.0));
        }
    }

    public void calculateDCG(int queryId, List<Integer> documentsRetrieved) {

        Map<Integer, Integer> documentsRelevance = queriesMeasure.get(queryId - 1).getDocumentsRelevantWithRelevance(n_ratings);

        if (!documentsRetrieved.isEmpty()) {
            double dcg = documentsRelevance.containsKey(documentsRetrieved.get(0))
                    ? 5 - documentsRelevance.get(documentsRetrieved.get(0)) : 0.0;
            for (int i = 1; i < documentsRetrieved.size(); i++) {
                if (documentsRelevance.containsKey(documentsRetrieved.get(i)))
                    dcg += (double) (5 - documentsRelevance.get(documentsRetrieved.get(i)))/(Math.log(i+1)/Math.log(2));
            }
            queriesMeasure.get(queryId - 1).calculateDCG(dcg);
        }
        else
            queriesMeasure.get(queryId - 1).calculateDCG(0.0);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s | %s | %s  | %s | %s| %s\n", "Query Id", "Latency (ms)", "Precision", "Recall","F-measure", "DCG"));
        sb.append("-------------------------------------------------------------------\n");
        queriesMeasure.forEach(queryMeasure -> sb.append(queryMeasure.toString()));
        return sb.toString();
    }

    public void calculateQueryMeasures(int queryId, List<Integer> retrievedDocuments) {
        List<Integer> relevantDocuments = queriesMeasure.get(queryId - 1).getDocumentsRelevant(n_ratings);
        calculatePrecision(queryId, retrievedDocuments, relevantDocuments);
        calculateRecall(queryId, retrievedDocuments, relevantDocuments);
        calculateFmeasure(queryId);
        calculateAveragePrecision(queryId, retrievedDocuments, relevantDocuments);
        calculateAveragePrecisionAtRank10(queryId, retrievedDocuments, relevantDocuments);
        calculateReciprocalRank(queryId, retrievedDocuments, relevantDocuments);
        calculateDCG(queryId, retrievedDocuments);
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

    public void printResults(){
        //System.out.println(this.toString());

        System.out.println("TP " + true_positives);
        System.out.println("RETRIVED DOCS " + retrieved_docs);
        System.out.println("RELEVANT DOCS " + relevant_docs);

        System.out.println("Precision: " + precision);
        System.out.println("Recall: " + recall);
        System.out.println("Fmeasure: " + fmeasure);
        System.out.printf("Mean Average Precision: %.5f\n", map);
        System.out.printf("Mean Average Precision at Rank 10: %.5f\n", map10);
        System.out.printf("Mean Reciprocal Rank: %.5f\n", mrr);
        System.out.println("Query Throughput per second: " + query_throughput);
        System.out.println("Median Query Latency: "+ mql + "\n");

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

        queriesMeasure.forEach(QueryMeasure::reset);
    }
}
