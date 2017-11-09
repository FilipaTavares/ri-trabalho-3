
import IndexerEngine.corpusReaders.CorpusReader;
import IndexerEngine.corpusReaders.CranfieldReader;
import IndexerEngine.indexer.Indexer;
import IndexerEngine.tokenizers.ComplexTokenizer;
import IndexerEngine.tokenizers.Tokenizer;
import Pipelines.DocumentIndexerPipeline;
import SearchEngine.IndexReader.IndexReader;
import SearchEngine.IndexReader.Vector;
import SearchEngine.QueryProcessing.QueryProcessor;
import SearchEngine.QueryProcessing.RankedRetrieval;
import SearchEngine.ScoringAlgorithms.CosineScore;
import SearchEngine.ScoringAlgorithms.NumberOfQueryWords;
import java.util.List;
import java.util.Map;

public class teste {
    public static void main(String[] args) {
        CorpusReader corpusReader = new CranfieldReader();
        Indexer indexer = new Indexer();
        Tokenizer tokenizer = new ComplexTokenizer();
        DocumentIndexerPipeline indexerPipeline = new DocumentIndexerPipeline
                (new java.io.File("C:\\Users\\Andreia Machado\\Desktop\\trabalhos para a escola\\trabalhos\\MEI\\1 ano\\1 semestre\\RI\\A3\\teste"), corpusReader, tokenizer, indexer, "result");
        indexerPipeline.execute();
        
        IndexReader indexReader = new IndexReader();
        Indexer indexer2 = indexReader.readIndex("result");
        Map<String, Integer> documentsFrequency = indexReader.getDocumentsFrequency();
        List<Vector> vectors = indexReader.getVectors();
        int nDocs = indexReader.getNDocs();
        CosineScore score = new CosineScore();
        RankedRetrieval rank = new RankedRetrieval(indexer2, tokenizer, new NumberOfQueryWords(), nDocs
                , vectors, documentsFrequency, score);
        QueryProcessor processor = new QueryProcessor();
        processor.processQueries("C:\\Users\\Andreia Machado\\Desktop\\trabalhos para a escola\\trabalhos\\MEI\\1 ano\\1 semestre\\RI\\A3\\query.txt"
                , rank, "results.txt");
    }
}
