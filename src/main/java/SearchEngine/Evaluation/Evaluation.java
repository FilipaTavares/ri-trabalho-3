
package SearchEngine.Evaluation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
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
                String[] s = line.split(" ");
                int queryId = Integer.parseInt(s[0]);
                int docId = Integer.parseInt(s[1]);
                
                int index = queriesMeasure.indexOf(new QueryMeasure(queryId));
                if (index != -1) {
                    QueryMeasure queryMeasure = queriesMeasure.get(index);
                    queryMeasure.addDocumentRelevant(docId);
                }
                else {
                    QueryMeasure queryMeasure = new QueryMeasure(queryId);
                    queryMeasure.addDocumentRelevant(docId);
                    queriesMeasure.add(queryMeasure);
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading query relevance file");
            System.exit(1);
        }

    }
    
    public void calculatePrecision(int query, Set<Integer> documentsRetrieved) {
        List<Integer> documentsRelevant = queriesMeasure.get(query-1).getDocumentsRelevant();
        double tp = 0.0;
        for (int docId: documentsRelevant) {
            if (documentsRetrieved.contains(docId))
                tp++;
        }
        queriesMeasure.get(query-1).calculatePrecision(tp,documentsRetrieved.size());
    }
    
    public void calculateRecall(int query, Set<Integer> documentsRetrieved) {
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
    
    public void calculateAveragePrecision(int query, Set<Integer> documentsRetrieved) {
        List<Integer> documentsRelevant = queriesMeasure.get(query-1).getDocumentsRelevant();
        double precisions = 0.0;
        double countTotal = 1;
        double tp = 0;
        for (int docId: documentsRetrieved) {
            if (documentsRelevant.contains(docId)) {
                tp++;
                precisions += tp/countTotal;
            }
            if (tp == documentsRelevant.size())
                break;
            countTotal++;
        }
        queriesMeasure.get(query-1).calculateAveragePrecision(precisions,tp);
    }
    
    public double calculateMAPtoTen() {
        if (queriesMeasure.size()>10){
            double averagePrecisionSum = 0.0;
            for (QueryMeasure queryMeasure: queriesMeasure.subList(0, 10))
                averagePrecisionSum += queryMeasure.getAveragePrecision();
            return averagePrecisionSum/10;
        }
        else {
            return calculateMAP();
        }
    }

    public double calculateMAP() {
        double averagePrecisionSum = 0.0;
        for (QueryMeasure queryMeasure: queriesMeasure) {
            averagePrecisionSum += queryMeasure.getAveragePrecision();
        }
        return averagePrecisionSum/queriesMeasure.size();
    }
    
    public void calculateReciprocalRank(int query, Set<Integer> documentsRetrieved) {
        List<Integer> documentsRelevant = queriesMeasure.get(query-1).getDocumentsRelevant();
        double rr = 0.0;
        double countTotal = 1;
        for (int docId: documentsRetrieved) {
            if (documentsRelevant.contains(docId)) {
                rr = 1/countTotal;
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
               reciprocalRankSum += 1.0/queryMeasure.getReciprocalRank();
        }
        return (double) reciprocalRankSum/queriesMeasure.size();
    }
    
    public void addQueryLatency(int queryId, long queryLatency) {
        queriesMeasure.get(queryId-1).addQueryLatency(queryLatency);
    }
    
    public int calculateQueryThroughput() {
        long queryLatencySum = 0L;
        int nQuery = 0;
        for (QueryMeasure queryMeasure: queriesMeasure) {
            queryLatencySum += queryMeasure.getQueryLatency();
            nQuery++;
            if (queryLatencySum > 1000)
                break;
        }
        return nQuery;
    }
    
    public long calculateMedianQueryLatency() {
        List<Long> queryLatency = queriesMeasure.stream().map(QueryMeasure::getQueryLatency).sorted().collect(Collectors.toList());
        if (queryLatency.size()%2 == 0) {
            return (queryLatency.get(queryLatency.size()/2)+queryLatency.get((queryLatency.size()/2)+1))/2;
        }
        else {
            return queryLatency.get((int) Math.ceil(queryLatency.size() / 2.0));
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s | %s | %s  | %s | %s\n", "Query Id", "Query Throughput", "Precision", "Recall","F-measure"));
        sb.append("-------------------------------------------------------------\n");
        queriesMeasure.forEach(queryMeasure -> sb.append(queryMeasure.toString()));
        return sb.toString();
    }

}
