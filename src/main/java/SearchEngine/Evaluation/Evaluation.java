
package SearchEngine.Evaluation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

    private double map;
    private double map10;
    private double mrr;
    private long mql;
    private double query_throughput;

    private double precision;
    private double recall;
    private double fmeasure;
    
    public Evaluation(String filename, double threshold) {
        this.queriesMeasure = new LinkedList<>();
        this.threshold = threshold;
        readFile(filename);
    }

    private void readFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))){
            String line;

            while ((line = reader.readLine()) != null) {
                String[] s = line.split(" +");
                int queryId = Integer.parseInt(s[0]);
                int docId = Integer.parseInt(s[1]);
                int relevance = 5 - Integer.parseInt(s[2]);
                
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
        System.out.println(true_positives);
        System.out.println(retrieved_docs);
        this.precision =  true_positives / retrieved_docs;

    }

    public void calculateSystemRecall() {
        System.out.println(relevant_docs);
        this.recall = true_positives / relevant_docs;

    }

    public void calculateSystemFmeasure() {
        double precision = true_positives / retrieved_docs;
        double recall = true_positives / relevant_docs;
        if (recall == 0.0 && precision == 0.0)
            this.fmeasure = 0.0;
        else
            this.fmeasure = (2 * recall * precision) / (recall + precision);
    }

    public void calculatePrecision(int query, List<Integer> documentsRetrieved) {
        List<Integer> relevantDocs = queriesMeasure.get(query - 1).getDocumentsRelevant();
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

    public void calculateRecall(int query, List<Integer> documentsRetrieved) {
        List<Integer> relevantDocs = queriesMeasure.get(query-1).getDocumentsRelevant();
        double tp = 0;
        for (int docId: relevantDocs) {
            if (documentsRetrieved.contains(docId))
                tp++;
        }
        queriesMeasure.get(query - 1).calculateRecall(tp);
    }

    public void calculateFmeasure(int query) {
        queriesMeasure.get(query-1).calculateFMeasure();
    }
    
    public void calculateAveragePrecision(int query, List<Integer> documentsRetrieved) {

        List<Integer> documentsRelevant = queriesMeasure.get(query - 1).getDocumentsRelevant();
        double precisionsSum = 0.0;
        double countTotal = 1;
        double tp = 0;
        for (int docId: documentsRetrieved) {
            if (documentsRelevant.contains(docId)) {
                tp++;
                precisionsSum += tp/countTotal;
            }
            if (tp == documentsRelevant.size())
                break;
            countTotal++;
        }
        queriesMeasure.get(query - 1).calculateAveragePrecision(precisionsSum, tp);
    }

    public void calculateAveragePrecisionAtRank10(int query, List<Integer> documentsRetrieved) {
        List<Integer> documentsRelevant = queriesMeasure.get(query - 1).getDocumentsRelevant();
        double precisionsSum = 0.0;
        double countTotal = 1;
        double tp = 0;

        //QUANDO COM THRESHOLD VER SIZE PODE DAR MENOS DE 10

        for (int docId: documentsRetrieved.subList(0, 10)) {
            if (documentsRelevant.contains(docId)) {
                tp++;
                precisionsSum += tp/countTotal;
            }
            if (tp == documentsRelevant.size())
                break;
            countTotal++;
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
    
    public void calculateReciprocalRank(int query, List<Integer> documentsRetrieved) {
        List<Integer> documentsRelevant = queriesMeasure.get(query-1).getDocumentsRelevant();
        double rr = 0.0;
        double countTotal = 1;
        for (int docId: documentsRetrieved) {
            if (documentsRelevant.contains(docId)) {
                rr = 1 / countTotal;
                break;
            }
            countTotal++;
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
        Map<Integer, Integer> documentsRelevance = queriesMeasure.get(queryId - 1).getDocumentsRelevantWithRelevance();
        double dcg = documentsRelevance.containsKey(documentsRetrieved.get(0)) 
                ? documentsRelevance.get(documentsRetrieved.get(0)) : 0.0;
        for (int i = 1; i < documentsRetrieved.size(); i++) {
            if (documentsRelevance.containsKey(documentsRetrieved.get(i)))
                dcg += (double) documentsRelevance.get(documentsRetrieved.get(i))/(Math.log(i+1)/Math.log(2));
        }
        queriesMeasure.get(queryId - 1).calculateDCG(dcg);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s | %s | %s  | %s | %s| %s\n", "Query Id", "Latency (ms)", "Precision", "Recall","F-measure", "DCG"));
        sb.append("-------------------------------------------------------------------\n");
        queriesMeasure.forEach(queryMeasure -> sb.append(queryMeasure.toString()));
        return sb.toString();
    }

    public void calculateQueryMeasures(int queryId, List<Integer> keys) {
        calculatePrecision(queryId, keys);
        calculateRecall(queryId, keys);
        calculateFmeasure(queryId);
        calculateAveragePrecision(queryId, keys);
        calculateAveragePrecisionAtRank10(queryId, keys);
        calculateReciprocalRank(queryId, keys);
        calculateDCG(queryId, keys);
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
        System.out.println(this.toString());

        System.out.println("Precision: " + precision);
        System.out.println("Recall: " + recall);
        System.out.println("Fmeasure: " + fmeasure);
        System.out.printf("Mean Average Precision: %.5f\n", map);
        System.out.printf("Mean Average Precision at Rank 10: %.5f\n", map10);
        System.out.printf("Mean Reciprocal Rank: %.5f\n", mrr);
        System.out.println("Query Throughput per second: " + query_throughput);
        System.out.println("Median Query Latency: "+ mql + "\n");

    }
}
