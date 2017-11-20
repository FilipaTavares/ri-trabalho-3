package SearchEngine.QueryProcessing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Class that reads and processes the queries file
 */

public class QueryProcessor {

    /**
     * Method that reads and processes the queries saving the results to a file
     * @param queriesFilename the queries filename
     * @param retrieval the retrieval to be used
     * @param outputFilename output filename to save the scores
     */

    public void processQueries(String queriesFilename, Retrieval retrieval, String outputFilename){

        int query_id = 1;
        try (BufferedReader br = new BufferedReader(new FileReader(queriesFilename))) {

            String line;
            while ((line = br.readLine()) != null) {
                retrieval.retrieve(query_id, line);
                query_id++;
            }

            br.close();
            retrieval.saveToFile(outputFilename);

        } catch (IOException e) {
            System.err.println("Error reading queries file " + queriesFilename);
            System.exit(1);
        }


    }
}
