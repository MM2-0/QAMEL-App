package de.qa.qa.result;

/**
 * Placeholder class - does not contain any data. Will be used as a placeholder in result list to
 * mark the position where a footer is to be inserted.
 */
public class FooterResult extends QAResult {
    private static final String TAG = FooterResult.class.getSimpleName();

    public FooterResult(String question) {
        super(question);
    }

    @Override
    public String toString() {
        return "";
    }
}
