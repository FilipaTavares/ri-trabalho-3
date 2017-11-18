
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
        Tokenizer tokenizer = new ComplexTokenizer("stop.txt");

        System.out.println("COSINE SCORE");

        IndexReader indexReader = new IndexWtNormReader();
        Indexer indexer2 = indexReader.readIndex("indexWT");

        Evaluation evaluation = new Evaluation("cranfield.query.relevance.txt");

        Retrieval rank = new RankedRetrieval(indexer2, tokenizer, evaluation);
        QueryProcessor processor = new QueryProcessor();

        processor.processQueries("cranfield.queries.txt"
                , rank, "resultsCosineScore2.txt");

        System.out.println("------------------------------------------------------------------------------------");



        rank.evaluate(2, 4);

        /*

        System.out.println("----------------------------------------------------------");

        System.out.println("FREQ SCORE");

        indexReader = new IndexTermFreqReader();
        indexer2 = indexReader.readIndex("indexFreq");
        evaluation = new Evaluation("cranfield.query.relevance.txt",0);
        ScoringAlgorithm scoringAlgorithm = new FrequencyOfQueryWords();
        rank = new DisjunctiveBooleanRetrieval(indexer2, tokenizer, evaluation,scoringAlgorithm);
        processor.processQueries("cranfield.queries.txt"
                , rank, "resultsFreq.txt");

        System.out.println("----------------------------------------------------------");

        System.out.println("NUMBER SCORE");
        scoringAlgorithm = new NumberOfQueryWords();

        evaluation = new Evaluation("cranfield.query.relevance.txt",0);
        rank = new DisjunctiveBooleanRetrieval(indexer2, tokenizer, evaluation, scoringAlgorithm);

        processor.processQueries("cranfield.queries.txt"
                , rank, "resultsScore.txt");
                */

    }
}
