import IndexerEngine.corpusReaders.CorpusReader;
import IndexerEngine.corpusReaders.CranfieldReader;
import IndexerEngine.indexer.IndexerWtNorm;
import IndexerEngine.tokenizers.Tokenizer;
import Pipelines.DocumentIndexerPipeline;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;

import java.io.File;
import java.io.IOException;


public class DocumentIndexer {
    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();

        ArgumentParser parser = ArgumentParsers.newFor("DocumentIndexer").build()
                .defaultHelp(true).description("A simple indexer");

        parser.addArgument("<directoryForFiles>").type(Arguments.fileType().verifyIsDirectory())
                .help("Corpus directory");

        parser.addArgument("<tokenizerClassName>").metavar("<tokenizerClassName>").choices("ComplexTokenizer")
                .setDefault("ComplexTokenizer").help("The tokenizer to be used given the following choices:\n" +
                "ComplexTokenizer - splits the text on a sequence of one or more non alphanumeric characters and" +
                "that have adjacent digits and non-digits." +
                "Uses a stopword list and an english stemmer");

        parser.addArgument("<outputFile>").help("Output file to save results");

        Namespace ns = parser.parseArgsOrFail(args);

        CorpusReader corpusReader = new CranfieldReader();
        IndexerWtNorm indexer = new IndexerWtNorm();
        File directory = new File(ns.getString("<directoryForFiles>"));

        String tokenizerClassName = ns.getString("<tokenizerClassName>");
        Tokenizer tokenizer = null;
        Class tokenizerClass;

        try {
            tokenizerClass = Class.forName(Tokenizer.class.getPackage().getName() + "." + tokenizerClassName);
            tokenizer = (Tokenizer) tokenizerClass.newInstance();

        } catch (ClassNotFoundException e) {
            System.err.println("The tokenizer " + tokenizerClassName + " doesn't exist.");
            System.exit(1);
        } catch (IllegalAccessException | InstantiationException e) {
            System.err.println("Tokenizer instantiation failed " + tokenizerClassName + e);
            System.exit(1);
        }

        DocumentIndexerPipeline indexerPipeline = new DocumentIndexerPipeline(directory, corpusReader, tokenizer,
                indexer, ns.getString("<outputFile>"));

        indexerPipeline.execute();

        long elapsedTime = System.currentTimeMillis() - start;
        System.out.println("Execution time in ms: " + elapsedTime);
    }
}