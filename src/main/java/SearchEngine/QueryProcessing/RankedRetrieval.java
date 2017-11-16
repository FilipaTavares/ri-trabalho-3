package SearchEngine.QueryProcessing;

import IndexerEngine.indexer.Indexer;
import IndexerEngine.indexer.Posting;
import IndexerEngine.indexer.PostingWtNorm;
import IndexerEngine.tokenizers.Tokenizer;
import SearchEngine.ScoringAlgorithms.CosineScore;
import SearchEngine.Evaluation.Evaluation;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RankedRetrieval implements Retrieval {

    private List<Query> results;
    private Indexer indexer;
    private Tokenizer tokenizer;
    private CosineScore score;
    private List<Vector> vectors;
    private double threshold;
    private Evaluation evaluation;

    public RankedRetrieval(Indexer indexer, Tokenizer tokenizer, Evaluation evaluation, CosineScore score) {
        this.results = new LinkedList<>();
        this.indexer = indexer;
        this.tokenizer = tokenizer;
        this.score = score;
        this.vectors = new LinkedList<>();
        this.threshold = 0.0;
        this.evaluation = evaluation;
    }

    @Override
    public void retrieve(int queryID, String queryText) {
        vectors.clear();
        long start = System.currentTimeMillis();
        List<String> terms = tokenizer.tokenize(queryText);
        Map<String, Double> temp = normalizeQuery(terms);

        Query query = new Query(queryID);
        fillVectors(terms);
        score.computeScores(query, temp, vectors);
        query.getDoc_scores().entrySet().removeIf((entry) -> entry.getValue() < threshold);
        results.add(query);
        long queryThroughput = System.currentTimeMillis() - start;
        evaluation.addQueryLatency(queryID, queryThroughput);
    }

    @Override
    public void saveToFile(String filename) {
        try (PrintWriter out = new PrintWriter(filename)) {
            results.sort(Comparator.comparingInt(Query::getQuery_id));
            for (Query query : results) {
                int id = query.getQuery_id();
                query.getDoc_scores().entrySet().stream().sorted((o1, o2) -> o1.getValue().equals(o2.getValue())
                        ? o1.getKey().compareTo(o2.getKey()) : o2.getValue().compareTo(o1.getValue())).
                        forEach(entry -> out.printf("%d\t%d\t%f\n", id, entry.getKey(), entry.getValue()));
            }
        } catch (IOException e) {
            System.err.println("Error writing results to file");
            System.exit(1);
        }
    }
    
    
    @Override
    public void calculateMeasures(int queryId) {
        Set<Integer> keySet = results.get(queryId-1).getDoc_scores().keySet();
        evaluation.calculatePrecision(queryId, keySet);
        evaluation.calculateRecall(queryId, keySet);
        evaluation.calculateFmeasure(queryId);
        evaluation.calculateAveragePrecision(queryId,keySet);
        evaluation.calculateReciprocalRank(queryId, keySet);
    }
    
    @Override
    public void printAllEvaluations() {
        double map = evaluation.calculateMAP();
        double map10 = evaluation.calculateMAPtoTen();
        double mrr = evaluation.calculateMRR();
        long mql = evaluation.calculateMedianQueryLatency();
        int nQuery = evaluation.calculateQueryThroughput();
        System.out.println(evaluation.toString());
        System.out.printf("Mean Average Precision: %.5f\n",map);
        System.out.printf("Mean Average Precision at Rank 10: %.5f\n",map10);
        System.out.printf("Mean Reciprocal Rank: %.5f\n",mrr);
        System.out.println("Query Throughput: "+nQuery);
        System.out.println("Median Query Latency: "+mql+"\n");
    }
    
    private Map<String, Double> normalizeQuery(List<String> terms) {
        Map<String, Double> temp = new HashMap<>();
        int nDocs = indexer.getN_docs();
        for (String term : terms) {
            double count = temp.getOrDefault(term, 0.0);
            temp.put(term, count + 1);
        }

        double sum_square_wt = 0.0;
        for (Map.Entry<String, Double> pair : temp.entrySet()) {
            if (indexer.getTermPostings(pair.getKey()) != null) {
                int docFreq = indexer.getTermPostings(pair.getKey()).size();
                double tfTerm = 1 + Math.log10(pair.getValue());
                double idf = Math.log10((nDocs / docFreq));
                double wt = tfTerm * idf;
                temp.put(pair.getKey(), wt);
                sum_square_wt += Math.pow(wt, 2);
            } else {
                temp.put(pair.getKey(), 0.0);
            }
        }
        double finalSum_square_wt = sum_square_wt;
        temp.replaceAll((term, wt) -> wt / Math.sqrt(finalSum_square_wt));
        return temp;
    }

    private void fillVectors(List<String> terms) {
        for (String term : terms) {
            if (indexer.getTermPostings(term) != null) {
                List<Posting> postingList = indexer.getTermPostings(term);
                for (Posting posting1: postingList) {
                    PostingWtNorm posting = (PostingWtNorm) posting1;
                    Vector vector = new Vector(posting.getDocID());
                    vector.addTerm(term, posting.getWt_norm());
                    vectors.add(vector);
                }
            }
        }
    }

}
