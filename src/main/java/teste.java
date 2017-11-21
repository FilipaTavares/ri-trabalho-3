
import IndexerEngine.indexer.Indexer;
import IndexerEngine.tokenizers.ComplexTokenizer;
import IndexerEngine.tokenizers.Tokenizer;
import SearchEngine.IndexReader.IndexReader;
import SearchEngine.IndexReader.IndexTermFreqReader;
import SearchEngine.IndexReader.IndexWtNormReader;
import SearchEngine.QueryProcessing.DisjunctiveBooleanRetrieval;
import SearchEngine.QueryProcessing.QueryProcessor;
import SearchEngine.QueryProcessing.RankedRetrieval;
import SearchEngine.QueryProcessing.Retrieval;
import SearchEngine.ScoringAlgorithms.CosineScore;
import SearchEngine.Evaluation.Evaluation;
import SearchEngine.ScoringAlgorithms.FrequencyOfQueryWords;
import SearchEngine.ScoringAlgorithms.NumberOfQueryWords;
import SearchEngine.ScoringAlgorithms.ScoringAlgorithm;

import javax.swing.plaf.synth.SynthOptionPaneUI;

public class teste {
    public static void main(String[] args) {
        long startTime = System.nanoTime();
        System.out.println(startTime);

        double milliSeconds = startTime / 1e6;
        System.out.println(milliSeconds);

    }
}
