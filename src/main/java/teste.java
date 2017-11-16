
import IndexerEngine.corpusReaders.CorpusReader;
import IndexerEngine.corpusReaders.CranfieldReader;
import IndexerEngine.indexer.Indexer;
import IndexerEngine.indexer.IndexerWtNorm;
import IndexerEngine.tokenizers.ComplexTokenizer;
import IndexerEngine.tokenizers.Tokenizer;
import Pipelines.DocumentIndexerPipeline;
import SearchEngine.IndexReader.IndexReader;
import SearchEngine.IndexReader.IndexWtNormReader;
import SearchEngine.QueryProcessing.QueryProcessor;
import SearchEngine.QueryProcessing.RankedRetrieval;
import SearchEngine.QueryProcessing.Retrieval;
import SearchEngine.ScoringAlgorithms.CosineScore;
import SearchEngine.ScoringAlgorithms.NumberOfQueryWords;
import SearchEngine.Evaluation.Evaluation;

public class teste {
    public static void main(String[] args) {
        CorpusReader corpusReader = new CranfieldReader();
        Indexer indexer = new IndexerWtNorm();
        Tokenizer tokenizer = new ComplexTokenizer();
        DocumentIndexerPipeline indexerPipeline = new DocumentIndexerPipeline
                (new java.io.File("C:\\Users\\Andreia Machado\\Desktop\\trabalhos para a escola\\trabalhos\\MEI\\1 ano\\1 semestre\\RI\\A3\\teste"), corpusReader, tokenizer, indexer, "result");

        indexerPipeline.execute();
        
        IndexReader indexReader = new IndexWtNormReader();
        Indexer indexer2 = indexReader.readIndex("result");
        CosineScore score = new CosineScore();
        Evaluation evaluation = new Evaluation("cranfield.query.relevance.txt");
        Retrieval rank = new RankedRetrieval(indexer2, tokenizer, evaluation, score);
        QueryProcessor processor = new QueryProcessor();
        processor.processQueries("C:\\Users\\Andreia Machado\\Desktop\\trabalhos para a escola\\trabalhos\\MEI\\1 ano\\1 semestre\\RI\\A3\\query.txt"
                , rank, "results.txt");
    }
}
