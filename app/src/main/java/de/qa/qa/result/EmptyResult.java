package de.qa.qa.result;

import java.util.Random;

/**
 * Not really a result, but can be used to show the user that there is no result.
 */
public class EmptyResult extends QAResult {
    private static final String TAG = EmptyResult.class.getSimpleName();

    public EmptyResult(String question) {
        super(question);
    }

    @Override
    public String toString() {
        return "I don't know.";
    }
}
