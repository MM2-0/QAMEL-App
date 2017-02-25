package de.qa.qa;


import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import de.qa.R;
import de.qa.misc.Utils;
import de.qa.qa.result.EmptyResult;
import de.qa.qa.result.QAResult;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class QuestionAnswerer {

    private static final String TAG = QuestionAnswerer.class.getSimpleName();

    private Context mContext;
    private Handler mHandler;
    private QAListener mQAListener;
    private String mQuestion;
    private QAResult[] mAnswers;

    private Runnable mResultRunnable = new Runnable() {
        @Override
        public void run() {
            mQAListener.onQuestionAnswered(mAnswers);
        }
    };

    public QuestionAnswerer(Context context, QAListener qaListener) {
        mContext = context;
        mHandler = new Handler();
        mQAListener = qaListener;
    }

    public void answerQuestion(final String question) {
        mQuestion = question;
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAnswers = new QAResult[0];
                // Always try to go online for now.
                if (Utils.isOffline(mContext) && false) {
                    Log.d(TAG, "Device is offline.");
                    //TODO handle offline request
                } else {
                    Log.d(TAG, "Device is online.");
                    try {
                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(10, TimeUnit.SECONDS)
                                .writeTimeout(10, TimeUnit.SECONDS)
                                .readTimeout(30, TimeUnit.SECONDS)
                                .build();
                        RequestBody requestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("query", mQuestion)
                                .build();
                        Request request = new Request.Builder()
                                .url(mContext.getString(R.string.config_wdaquaUrl))
                                .post(requestBody)
                                .build();
                        Response response = client.newCall(request).execute();
                        JSONObject resultObject = new JSONObject(response.body().string());
                        JSONObject answersObject = new JSONObject(
                                resultObject.getJSONArray("questions")
                                        .getJSONObject(0).getJSONObject("question")
                                        .getString("answers"));
                        JSONArray bindings = answersObject.getJSONObject("results")
                                .getJSONArray("bindings");
                        mAnswers = new QAResult[bindings.length()];
                        for (int i = 0; i < bindings.length(); i++) {
                            JSONObject binding = bindings.getJSONObject(i);
                            mAnswers[i] = QAResult.newInstance(mQuestion,
                                    binding.getJSONObject("x").getString("type"),
                                    binding.getJSONObject("x").optString("datatype"),
                                    //Nah
                                    binding.getJSONObject("x").getString("value"));
                            Log.d(TAG, "Result: " + binding.toString());
                        }
                    } catch (IOException | JSONException e) {
                        Log.e(TAG, Log.getStackTraceString(e));
                    }
                    if (mAnswers.length == 0) {
                        mAnswers = new QAResult[]{new EmptyResult(mQuestion)};
                    }
                    mHandler.post(mResultRunnable);
                }
            }
        }).start();
    }

    public interface QAListener {
        void onQuestionAnswered(QAResult[] results);
    }
}
