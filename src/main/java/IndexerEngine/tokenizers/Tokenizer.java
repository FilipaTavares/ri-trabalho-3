package IndexerEngine.tokenizers;

import java.util.List;

/**
 * The root interface in the tokenizer hierarchy. A tokenizer receives as input a text and forms tokens from that
 * text.
 */

public interface Tokenizer {

    /**
     * Splits a text to form words/tokens
     *
     * @param inputText text to be processed
     * @return list of generated tokens
     */
    List<String> tokenize(String inputText);
}
