package de.qa.qa.result;

import java.text.NumberFormat;
import java.util.Locale;

public class IntegerResult extends QAResult {
    private static final String TAG = IntegerResult.class.getSimpleName();
    private int mData;

    public IntegerResult(String question, String data) {
        super(question, data);
        mData = Integer.parseInt(data);
    }

    @Override
    public String toString() {
        NumberFormat nf = NumberFormat.getIntegerInstance(Locale.getDefault());
        return nf.format(mData);
    }
}
