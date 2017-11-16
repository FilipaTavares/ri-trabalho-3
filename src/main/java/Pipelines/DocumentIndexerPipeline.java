package Pipelines;

import IndexerEngine.corpusReaders.CorpusReader;
import IndexerEngine.documents.Document;
import IndexerEngine.indexer.Indexer;
import IndexerEngine.indexer.IndexerTermFreq;
import IndexerEngine.tokenizers.Tokenizer;

import java.io.File;
import java.util.List;

/**
 * Pipeline that executes the document indexer program
 */
public class DocumentIndexerPipeline implements Pipeline{
    private File directory;
    private CorpusReader corpusReader;
    private Tokenizer tokenizer;
    private Indexer indexer;
    private String outputFileName;

    public DocumentIndexerPipeline(File directory, CorpusReader corpusReader, Tokenizer tokenizer, Indexer indexer,
                                   String outputFileName) {
        this.directory = directory;
        this.corpusReader = corpusReader;
        this.tokenizer = tokenizer;
        this.indexer = indexer;
        this.outputFileName = outputFileName;
    }

    /**
     * Method that executes the document indexer pipeline
     */
    @Override
    public void execute() {
        File[] fList = directory.listFiles(File::isFile);

        for (File file : fList) {
            Document document = corpusReader.read(file.toString());

            if (document != null) {
                List<String> tokens = tokenizer.tokenize(document.getText());
                indexer.index(tokens, document.getId());
            }
        }

        indexer.saveToFile(outputFileName, tokenizer.getClass().getSimpleName());


        System.out.println("Indexer size: " + indexer.size() + "\n");

        String indexerName = indexer.getClass().getSimpleName();
        if (indexerName.equals("IndexerTermFreq")) {
            IndexerTermFreq indexerTermFreq = (IndexerTermFreq) indexer;
            System.out.println("List of ten first terms that appear in only one document");
            List<String> termsInOneDoc = indexerTermFreq.getFirst10TermsInOneDoc();
            termsInOneDoc.forEach(System.out::println);
            System.out.println();

            System.out.println("List of ten first terms with higher document frequency");

            List<String> termsHigherDocFreq = indexerTermFreq.getFirst10TermsWithHigherDocFreq();
            termsHigherDocFreq.forEach(System.out::println);
            System.out.println();
        }
    }

    /**
     * Sets the corpus directory to be used by the pipeline
     * @param directory corpus directory
     */
    public void setDirectory(File directory) {
        this.directory = directory;
    }

    /**
     * Sets the corpus reader to be used by the pipeline
     * @param corpusReader corpus reader
     */
    public void setCorpusReader(CorpusReader corpusReader) {
        this.corpusReader = corpusReader;
    }

    /**
     * Sets the tokenizer to be used by the pipeline
     * @param tokenizer tokenizer
     */
    public void setTokenizer(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    /**
     * Sets the indexer to be used by the pipeline
     * @param indexer indexer
     */
    public void setIndexer(Indexer indexer) {
        this.indexer = indexer;
    }

    /**
     * Sets the output filename to be used by the pipeline
     * @param outputFileName output filename
     */

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }
}
