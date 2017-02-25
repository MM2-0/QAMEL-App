package de.qa.qa.result;

import android.support.annotation.Nullable;

public class QAResult {

    private String mData;
    private String mQuestion;

    public static QAResult newInstance(String question, String type, @Nullable String dataType,
                                       String data) {
        switch (type) {
            case "uri":
                return new UriResult(question, data);
            case "typed-literal":
                switch (dataType) {
                    case "http://www.w3.org/2001/XMLSchema#date":
                        return new DateResult(question, data);
                }
        }
        return new QAResult(question, data);
    }

    public QAResult(String question) {
        mQuestion = question;
    }

    public QAResult(String question, String data) {
        this(question);
        mData = data;
    }

    @Override
    public String toString() {
        return mData;
    }

    public String getQuestion() {
        return mQuestion;
    }
}
