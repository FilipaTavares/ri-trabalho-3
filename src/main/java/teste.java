
import IndexerEngine.corpusReaders.CorpusReader;
import IndexerEngine.corpusReaders.CranfieldReader;
import IndexerEngine.indexer.Indexer;
import IndexerEngine.tokenizers.ComplexTokenizer;
import IndexerEngine.tokenizers.Tokenizer;
import Pipelines.DocumentIndexerPipeline;

public class teste {
    public static void main(String[] args) {
        CorpusReader corpusReader = new CranfieldReader();
        Indexer indexer = new Indexer();
        Tokenizer tokenizer = new ComplexTokenizer();
        DocumentIndexerPipeline indexerPipeline = new DocumentIndexerPipeline
                (new java.io.File("/home/filipa/Desktop/pasta_teste"), corpusReader, tokenizer, indexer, "result");

        indexerPipeline.execute();
    }
}
