package IndexerEngine.tokenizers;

import org.tartarus.snowball.ext.englishStemmer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * A more complex tokenizer implementation
 */

public class ComplexTokenizer implements Tokenizer {

    private static final String STOPFILE = "stop.txt";      // file that hold all the stopwords/terms to be removed
    private List<String> stopWords;

    /**
     * Creates a new instance of the complex tokenizer
     */

    public ComplexTokenizer() {
        stopWords = new ArrayList<>();
        collectAllStopWords();
    }

    /**
     * Method that splits the text on a sequence of one or more non alphanumeric characters and
     * also splits tokens that have adjacent digits and non-digits.
     * Tokens that appear on a stopwords list are also removed and it is used the porter stemmer algorithm
     * for the english language so that different forms of the same word are mapped as the same term.
     *
     * @param inputText text to be processed
     * @return list of generated tokens
     */

    @Override
    public List<String> tokenize(String inputText) {
        List<String> tokens = new ArrayList<>();

        Arrays.stream(inputText.split("[^\\p{Alnum}]+")).map(String::toLowerCase).map(splitToken -> splitToken.split
                ("(?<=\\d)(?=\\D)|(?=\\d)(?<=\\D)")).forEach(
                tokensLists -> {
                    for (String token : tokensLists) {
                        if (!token.matches("^\\s*$") && !stopWords.contains(token))
                            tokens.add(token);
                    }
                });

        englishStemmer stemmer = new englishStemmer();

        for (int i = 0; i < tokens.size(); i++) {
            stemmer.setCurrent(tokens.get(i));
            stemmer.stem();
            tokens.set(i, stemmer.getCurrent());
        }

        return tokens;
    }


    /**
     * Method that collects all stopwords from a file to a list
     */
    private void collectAllStopWords() {
        try (Scanner scanner = new Scanner(new FileInputStream(new File(STOPFILE)), "UTF-8")) {
            while (scanner.hasNextLine()) {
                stopWords.add(scanner.nextLine().toLowerCase());
            }
        } catch (IOException e) {
            System.err.println("Error parsing the file " + STOPFILE);
        }
    }
}
