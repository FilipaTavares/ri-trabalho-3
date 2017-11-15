
import IndexerEngine.corpusReaders.CorpusReader;
import IndexerEngine.corpusReaders.CranfieldReader;
import IndexerEngine.indexer.IndexerWtNorm;
import IndexerEngine.tokenizers.ComplexTokenizer;
import IndexerEngine.tokenizers.Tokenizer;
import Pipelines.DocumentIndexerPipeline;
import SearchEngine.IndexReader.IndexWtNormReader;
import SearchEngine.QueryProcessing.QueryProcessor;
import SearchEngine.QueryProcessing.RankedRetrieval;
import SearchEngine.ScoringAlgorithms.CosineScore;
import SearchEngine.ScoringAlgorithms.NumberOfQueryWords;

public class teste {
    public static void main(String[] args) {
        CorpusReader corpusReader = new CranfieldReader();
        IndexerWtNorm indexer = new IndexerWtNorm();
        Tokenizer tokenizer = new ComplexTokenizer();
        DocumentIndexerPipeline indexerPipeline = new DocumentIndexerPipeline
                (new java.io.File("C:\\Users\\Andreia Machado\\Desktop\\trabalhos para a escola\\trabalhos\\MEI\\1 ano\\1 semestre\\RI\\A3\\teste"), corpusReader, tokenizer, indexer, "result");

        indexerPipeline.execute();
        
        IndexWtNormReader indexReader = new IndexWtNormReader();
        IndexerWtNorm indexer2 = indexReader.readIndex("result");
        CosineScore score = new CosineScore();
        RankedRetrieval rank = new RankedRetrieval(indexer2, tokenizer, new NumberOfQueryWords(), score);
        QueryProcessor processor = new QueryProcessor();
        processor.processQueries("C:\\Users\\Andreia Machado\\Desktop\\trabalhos para a escola\\trabalhos\\MEI\\1 ano\\1 semestre\\RI\\A3\\query.txt"
                , rank, "results.txt");
    }
}
