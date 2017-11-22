import IndexerEngine.corpusReaders.CorpusReader;
import IndexerEngine.corpusReaders.CranfieldReader;
import IndexerEngine.indexer.Indexer;
import IndexerEngine.indexer.IndexerTermFreq;
import IndexerEngine.indexer.IndexerWtNorm;
import IndexerEngine.tokenizers.ComplexTokenizer;
import IndexerEngine.tokenizers.Tokenizer;
import Pipelines.DocumentIndexerPipeline;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;

import java.io.File;
import java.io.IOException;

/**
 * <h2>Document IndexerEngine.indexer</h2>
 * Receives as arguments:
 * <p>the choice of the type of the indexer</p>
 * <p>The directory containing the IndexerEngine.documents</p>
 * <p>The filename that contains the stopwords</p>
 * <p>The output file name to store de index results</p>
 *
 * @author Ana Filipa Tavares 76629
 * @author Andreia Machado 76501
 */
public class DocumentIndexer {
    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();

        ArgumentParser parser = ArgumentParsers.newFor("DocumentIndexer").build()
                .defaultHelp(true).description("A simple indexer");

        parser.addArgument("<indexer>").metavar("<indexer>").choices("freq",
                "weighted").setDefault("weighted").help("The indexer type to construct given the " +
                "following choices:\nfreq - term frequency indexer.\nweighted - " +
                "weighted tf-idf indexer (lnc)");

        parser.addArgument("<directoryForFiles>").type(Arguments.fileType().verifyIsDirectory())
                .help("Corpus directory");

        parser.addArgument("<stopwordsFile>").type(Arguments.fileType().verifyIsFile())
                .help("stopwords file to use in the complex tokenizer");

        parser.addArgument("<outputFile>").help("Output file to save results");

        Namespace ns = parser.parseArgsOrFail(args);

        CorpusReader corpusReader = new CranfieldReader();

        String indexerType = ns.getString("<indexer>");
        File directory = new File(ns.getString("<directoryForFiles>"));
        String stopwordsFilename = ns.getString("<stopwordsFile>");
        Indexer indexer = null;


        switch (indexerType){
            case "freq":
                indexer = new IndexerTermFreq();
                break;

            case "weighted":
                indexer = new IndexerWtNorm();
                break;
        }

        Tokenizer tokenizer = new ComplexTokenizer(stopwordsFilename);

        DocumentIndexerPipeline indexerPipeline = new DocumentIndexerPipeline(directory, corpusReader, tokenizer,
                indexer, ns.getString("<outputFile>"));

        indexerPipeline.execute();

        long elapsedTime = System.currentTimeMillis() - start;
        System.out.println("Execution time in ms: " + elapsedTime);
    }
}