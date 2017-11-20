package SearchEngine.QueryProcessing;

import IndexerEngine.indexer.Indexer;
import IndexerEngine.indexer.Posting;
import IndexerEngine.indexer.PostingWtNorm;
import IndexerEngine.tokenizers.Tokenizer;
import SearchEngine.ScoringAlgorithms.CosineScore;
import SearchEngine.Evaluation.Evaluation;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class RankedRetrieval extends Retrieval {
    private CosineScore score;

    public RankedRetrieval(Indexer indexer, Tokenizer tokenizer, Evaluation evaluation) {
        super(indexer, tokenizer, evaluation);
        this.score = new CosineScore(indexer);
    }

    @Override
    public void retrieve(int queryID, String queryText) {
        long start = System.currentTimeMillis();
        List<String> terms = tokenizer.tokenize(queryText);
        Query query = new Query(queryID);

        score.computeScores(query, terms);

        results.add(query);
        long queryLatency = System.currentTimeMillis() - start;

        evaluation.addQueryLatency(queryID, queryLatency);
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
