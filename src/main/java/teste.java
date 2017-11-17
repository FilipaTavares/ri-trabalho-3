
import IndexerEngine.indexer.Indexer;
import IndexerEngine.tokenizers.ComplexTokenizer;
import IndexerEngine.tokenizers.Tokenizer;
import SearchEngine.IndexReader.IndexReader;
import SearchEngine.IndexReader.IndexWtNormReader;
import SearchEngine.QueryProcessing.QueryProcessor;
import SearchEngine.QueryProcessing.RankedRetrieval;
import SearchEngine.QueryProcessing.Retrieval;
import SearchEngine.ScoringAlgorithms.CosineScore;
import SearchEngine.Evaluation.Evaluation;

public class teste {
    public static void main(String[] args) {
        Tokenizer tokenizer = new ComplexTokenizer("stop.txt");

        IndexReader indexReader = new IndexWtNormReader();

        Indexer indexer2 = indexReader.readIndex("result");

        Evaluation evaluation = new Evaluation("cranfield.query.relevance.txt");
        Retrieval rank = new RankedRetrieval(indexer2, tokenizer, evaluation);
        QueryProcessor processor = new QueryProcessor();
        processor.processQueries("cranfield.queries.txt"
                , rank, "resultsCosineScore.txt");
    }
}
