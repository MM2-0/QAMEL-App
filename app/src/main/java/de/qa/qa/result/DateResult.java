package de.qa.qa.result;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateResult extends QAResult {
    private static final String TAG = DateResult.class.getSimpleName();
    Date mDate;

    public DateResult(String question, String data) {
        super(question, data);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            mDate = dateFormat.parse(data);
        } catch (ParseException e) {
            Log.e(TAG, Log.getStackTraceString(e));
            mDate = new Date(0);
        }
    }

    @Override
    public String toString() {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        return dateFormat.format(mDate);
    }
}
