package SearchEngine.QueryProcessing;

import IndexerEngine.indexer.Indexer;
import IndexerEngine.indexer.Posting;
import IndexerEngine.indexer.PostingWtNorm;
import IndexerEngine.tokenizers.Tokenizer;
import SearchEngine.ScoringAlgorithms.CosineScore;
import SearchEngine.Evaluation.Evaluation;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class RankedRetrieval extends Retrieval {
    private CosineScore score;
    private List<Vector> vectors;

    public RankedRetrieval(Indexer indexer, Tokenizer tokenizer, Evaluation evaluation) {
        super(indexer, tokenizer, evaluation);
        this.score = new CosineScore();
        this.vectors = new LinkedList<>();
    }

    @Override
    public void retrieve(int queryID, String queryText) {
        long start = System.currentTimeMillis();
        vectors.clear();
        List<String> terms = tokenizer.tokenize(queryText);
        Map<String, Double> temp = normalizeQuery(terms);

        Query query = new Query(queryID);
        fillVectors(terms);
        score.computeScores(query, temp, vectors);
        results.add(query);
        long queryLatency = System.currentTimeMillis() - start;

        evaluation.addQueryLatency(queryID, queryLatency);
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

        for (Map.Entry<String, Double> pair : temp.entrySet()) {
            pair.setValue(pair.getValue() / Math.sqrt(sum_square_wt));
        }

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
}
