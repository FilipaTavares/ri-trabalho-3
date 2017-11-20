import IndexerEngine.indexer.Indexer;
import IndexerEngine.tokenizers.Tokenizer;
import SearchEngine.IndexReader.IndexReader;
import SearchEngine.IndexReader.IndexWtNormReader;
import SearchEngine.QueryProcessing.QueryProcessor;
import SearchEngine.QueryProcessing.RankedRetrieval;
import SearchEngine.QueryProcessing.Retrieval;
import SearchEngine.Evaluation.Evaluation;
import SearchEngine.IndexReader.IndexTermFreqReader;
import SearchEngine.QueryProcessing.DisjunctiveBooleanRetrieval;
import SearchEngine.ScoringAlgorithms.FrequencyOfQueryWords;
import SearchEngine.ScoringAlgorithms.NumberOfQueryWords;
import SearchEngine.ScoringAlgorithms.ScoringAlgorithm;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * <h2>Document Searcher</h2>
 * Receives as arguments:
 * <p>The index filename</p>
 *
 * @author Ana Filipa Tavares 76629
 * @author Andreia Machado 76501
 */
public class DocumentSearcher {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        ArgumentParser parser = ArgumentParsers.newFor("DocumentSearcher").build()
                .defaultHelp(true).description("A simple searcher that uses an index file and computes the " +
                        "rank metrics allowing the user to customize the parameters of such evaluation");

        parser.addArgument("<indexfile>").type(Arguments.fileType().verifyIsFile())
                .help("Index file");

        parser.addArgument("<queriesfile>").type(Arguments.fileType().verifyIsFile())
                .help("Queries file");

        parser.addArgument("<stopwordsFile>").type(Arguments.fileType().verifyIsFile())
                .help("stopwords file to use in the complex tokenizer");

        parser.addArgument("<scoring_algorithm>").metavar("<scoring_algorithm>").choices("qwNumber",
                "qwFrequency","cosineScore").setDefault("qwNumber").help("The scoring algorithm to be used given the " +
                "following choices:\nqwNumber - score based on number of query words in the document.\nqwFrequency - " +
                "score based on frequency of query words in the document\ncosineScore - compute the cosine similarity score "+
                "for the query vector and each document vector");

        parser.addArgument("<outputfile>").help("Output file to save results");

        parser.addArgument("<queriesRelevanceFile>").type(Arguments.fileType().verifyIsFile())
                .help("File that contains the relevant documents for each query");

        parser.addArgument("--fixedThreshold")
                .metavar("t")
                .type(Double.class)
                .nargs("+")
                .help("a fixed threshold value meaning that for each query all docs with score >= threshold" +
                        "will be retrieved");

        parser.addArgument("--variableThreshold")
                .metavar("t").type(Double.class)
                .nargs("+")
                .help("threshold value meaning that for each query all docs with score >= (max_score * threshold) " +
                        "will be retrieved");

        parser.addArgument("--relevance")
                .metavar("r").type(Integer.class)
                .nargs("+")
                .help("number of relevance levels to use in the gold standard. Example: a value of 3 considers" +
                        " the levels, 1, 2 and 3");

        parser.addArgument("--displayQueryMetrics")
                .action(Arguments.storeTrue())
                .help("displays not only system metrics but also for each query its metrics");

        Namespace ns = parser.parseArgsOrFail(args);

        //indexWT cranfield.queries.txt stop.txt cosineScore resultsCosineScore cranfield.query.relevance.txt

        String index_file = ns.getString("<indexfile>");
        String stopwordsFilename = ns.getString("<stopwordsFile>");
        String queries_file = ns.getString("<queriesfile>");
        String scoring_algorithm = ns.getString("<scoring_algorithm>");
        String output_file = ns.getString("<outputfile>");
        String querieRelevanceFile = ns.getString("<queriesRelevanceFile>");

        List<Double> fixed_threshold = ns.getList("fixedThreshold");
        List<Double> variable_threshold = ns.getList("variableThreshold");
        List<Integer> relevances = ns.getList("relevance");
        Boolean displayQueryMetrics = ns.getBoolean("displayQueryMetrics");

        Boolean default_evaluation = null;

        if (fixed_threshold == null && variable_threshold == null && relevances == null) {
            default_evaluation = true;
        }

        else if (fixed_threshold == null && variable_threshold == null && relevances != null) {
            String message = "--relevance must be used combined with --fixedThreshold and/or --variableThreshold";
            parser.handleError(new ArgumentParserException(message, parser));
            System.exit(1);
        }

        else if ((fixed_threshold != null || variable_threshold != null) && relevances == null) {
            String message = "to few arguments must define --relevance if using threshold values";
            parser.handleError(new ArgumentParserException(message, parser));
            System.exit(1);
        }

        else {
            default_evaluation = false;
        }


        System.out.println(ns);

        ScoringAlgorithm scoringAlgorithm = null;
        IndexReader indexReader = new IndexWtNormReader();
        
        switch(scoring_algorithm){
            case "qwNumber":
                indexReader = new IndexTermFreqReader();
                scoringAlgorithm = new NumberOfQueryWords();
                break;

            case "qwFrequency":
                indexReader = new IndexTermFreqReader();
                scoringAlgorithm = new FrequencyOfQueryWords();
                break;
            
            case "cosineScore":
                break;

            default:
                System.err.println("Scoring algorithm not recognized");
                System.exit(1);
                break;
        }
        
        Indexer indexer = indexReader.readIndex(index_file);

        String tokenizerClassName = indexer.getTokenizerName();
        Tokenizer tokenizer = null;
        Class tokenizerClass;

        try {
            tokenizerClass = Class.forName(Tokenizer.class.getPackage().getName() + "." + tokenizerClassName);
            tokenizer = (Tokenizer) tokenizerClass.getConstructor(String.class).newInstance(stopwordsFilename);

        } catch (ClassNotFoundException | NoSuchMethodException e) {
            System.err.println("The tokenizer " + tokenizerClassName + " doesn't exist.");
            System.exit(1);
        } catch (IllegalAccessException | InstantiationException | SecurityException 
                | IllegalArgumentException | InvocationTargetException e) {
            System.err.println("Tokenizer instantiation failed " + tokenizerClassName + e);
            System.exit(1);
        }
        
        Evaluation evaluation = new Evaluation(querieRelevanceFile);
        Retrieval retrieval;
        if (!scoring_algorithm.equals("cosineScore"))
            retrieval = new DisjunctiveBooleanRetrieval(indexer, tokenizer, evaluation, scoringAlgorithm);

        else {
            retrieval = new RankedRetrieval(indexer, tokenizer, evaluation);
        }

        QueryProcessor processor = new QueryProcessor();
        processor.processQueries(queries_file, retrieval, output_file);

        if (default_evaluation) {
            retrieval.evaluateWithFixedThreshold(0.0, 4, displayQueryMetrics);
        }

        else {
            for (int relevance: relevances) {

                if (fixed_threshold != null) {
                    for (double f_threshold: fixed_threshold) {
                        retrieval.evaluateWithFixedThreshold(f_threshold, relevance, displayQueryMetrics);
                    }
                }

                if (variable_threshold != null) {
                    for (double v_threshold: variable_threshold) {
                        retrieval.evaluateWithVariableThreshold(v_threshold, relevance, displayQueryMetrics);
                    }
                }
            }
        }

        long elapsedTime = System.currentTimeMillis() - start;
        System.out.println("Execution time in ms: " + elapsedTime);
    }

}

