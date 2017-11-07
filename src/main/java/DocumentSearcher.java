import IndexerEngine.indexer.Indexer;
import IndexerEngine.tokenizers.Tokenizer;
import SearchEngine.IndexReader.IndexReader;
import SearchEngine.QueryProcessing.BooleanRetrieval;
import SearchEngine.QueryProcessing.DisjunctiveBooleanRetrieval;
import SearchEngine.QueryProcessing.QueryProcessor;
import SearchEngine.ScoringAlgorithms.FrequencyOfQueryWords;
import SearchEngine.ScoringAlgorithms.NumberOfQueryWords;
import SearchEngine.ScoringAlgorithms.ScoringAlgorithm;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * <h2>Document Searcher</h2>
 * Receives as arguments:
 * <p>The index filename</p>
 * <p>The queries filename</p>
 * <p>The choice of the scoring algorithm</p>
 * <p>The output filename to store the score of the documents of each query</p>
 *
 * @author Ana Filipa Tavares 76629
 * @author Andreia Machado 76501
 */
public class DocumentSearcher {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        ArgumentParser parser = ArgumentParsers.newFor("DocumentSearcher").build()
                .defaultHelp(true).description("A simple searcher that uses an index file");

        parser.addArgument("<indexfile>").type(Arguments.fileType().verifyIsFile())
                .help("Index file");
        parser.addArgument("<queriesfile>").type(Arguments.fileType().verifyIsFile())
                .help("Queries file");
        parser.addArgument("<scoring_algorithm>").metavar("<scoring_algorithm>").choices("qwNumber",
                "qwFrequency").setDefault("qwNumber").help("The scoring algorithm to be used given the " +
                "following choices:\nqwNumber - score based on number of query words in the document.\nqwFrequency - " +
                "score based on frequency of query words in the document");
        parser.addArgument("<outputfile>").help("Output file to save results");

        Namespace ns = parser.parseArgsOrFail(args);

        String index_file = ns.getString("<indexfile>");
        IndexReader indexReader = new IndexReader();
        Indexer indexer = indexReader.readIndex(index_file);

        String tokenizerClassName = indexReader.getTokenizerName();
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

        String queries_file = ns.getString("<queriesfile>");
        String scoring_algorithm = ns.getString("<scoring_algorithm>");
        String output_file = ns.getString("<outputfile>");

        BooleanRetrieval booleanRetrieval = new DisjunctiveBooleanRetrieval();
        booleanRetrieval.setIndexer(indexer);
        booleanRetrieval.setTokenizer(tokenizer);

        ScoringAlgorithm scoringAlgorithm = null;
        switch(scoring_algorithm){
            case "qwNumber":
                scoringAlgorithm = new NumberOfQueryWords();
                break;

            case "qwFrequency":
                scoringAlgorithm = new FrequencyOfQueryWords();
                break;

            default:
                System.err.println("Scoring algorithm not recognized");
                System.exit(1);
                break;
        }

        booleanRetrieval.setScoringAlgorithm(scoringAlgorithm);

        QueryProcessor processor = new QueryProcessor();
        processor.processQueries(queries_file, booleanRetrieval, output_file);

        long elapsedTime = System.currentTimeMillis() - start;
        System.out.println("Execution time in ms: " + elapsedTime);
    }
}

