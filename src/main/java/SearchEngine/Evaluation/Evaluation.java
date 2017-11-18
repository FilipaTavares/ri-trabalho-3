
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
    
    public Evaluation(String filename) {
        this.queriesMeasure = new LinkedList<>();
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
    
    public void calculatePrecision(int query, List<Integer> documentsRetrieved) {
        List<Integer> relevantDocs = queriesMeasure.get(query - 1).getDocumentsRelevant();
        double tp = 0.0;
        for (int docId: relevantDocs) {
            if (documentsRetrieved.contains(docId))
                tp++;
        }
        queriesMeasure.get(query - 1).calculatePrecision(tp, documentsRetrieved.size());
    }
    
    public void calculateRecall(int query, List<Integer> documentsRetrieved) {
        List<Integer> documentsRelevant = queriesMeasure.get(query-1).getDocumentsRelevant();
        double tp = 0;
        for (int docId: documentsRelevant) {
            if (documentsRetrieved.contains(docId))
                tp++;
        }
        queriesMeasure.get(query-1).calculateRecall(tp);
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

    public double calculateMAP() {
        double averagePrecisionSum = 0.0;
        for (QueryMeasure queryMeasure: queriesMeasure) {
            averagePrecisionSum += queryMeasure.getAveragePrecision();
        }
        return averagePrecisionSum / queriesMeasure.size();
    }

    public double calculateMAPatRank10() {
        double averagePrecisionSum = 0.0;
        for (QueryMeasure queryMeasure: queriesMeasure) {
            averagePrecisionSum += queryMeasure.getAveragePrecisionAtRank10();
        }
        return averagePrecisionSum / queriesMeasure.size();
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
    
    public double calculateMRR() {
        double reciprocalRankSum = 0.0;
        for (QueryMeasure queryMeasure: queriesMeasure) {
            if (queryMeasure.getReciprocalRank() != 0.0)
               reciprocalRankSum += queryMeasure.getReciprocalRank();
        }
        return reciprocalRankSum / queriesMeasure.size();
    }
    
    public void addQueryLatency(int queryId, long queryLatency) {
        queriesMeasure.get(queryId - 1).addQueryLatency(queryLatency);
    }
    
    public double calculateQueryThroughput() {
        long queryLatencySum = queriesMeasure.stream().mapToLong(QueryMeasure::getQueryLatency).sum();
        double totalTimeSeconds = (queryLatencySum / 1000.0);

        return (queriesMeasure.size() / totalTimeSeconds);
    }
    
    public long calculateMedianQueryLatency() {
        List<Long> queryLatency = queriesMeasure.stream().map(QueryMeasure::getQueryLatency).sorted().collect(Collectors.toList());
        if (queryLatency.size() %2  == 0) {
            long l = queryLatency.get((queryLatency.size() / 2) - 1) + queryLatency.get(queryLatency.size() / 2);
            return (long) (l / 2.0);
        }
        else {
            return queryLatency.get((int) Math.floor(queryLatency.size() / 2.0));
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
        sb.append(String.format("%s | %s | %s  | %s | %s | %s\n", "Query Id", "Query Latency", "Precision", "Recall","F-measure", "DCG"));
        sb.append("----------------------------------------------------------------\n");
        queriesMeasure.forEach(queryMeasure -> sb.append(queryMeasure.toString()));
        return sb.toString();
    }

}
