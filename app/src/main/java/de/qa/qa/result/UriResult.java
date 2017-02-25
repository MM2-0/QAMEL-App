package de.qa.qa.result;

import android.net.Uri;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.qa.misc.Utils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UriResult extends QAResult {
    private static final String TAG = UriResult.class.getSimpleName();

    Uri mUri;
    String mName;

    public UriResult(String question, String uri) {
        super(question);
        mUri = Uri.parse(uri);
        mName = uri;
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            getNameForUri();
        }
    }

    @Override
    public String toString() {
        return mName;
    }

    private void getNameForUri() {
        String url = null;
        try {
            url = Utils.sparQlQueryUrl("SELECT ?lbl { <"+mUri.toString()+"> rdfs:label ?lbl . }");
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            JSONObject resultObject = new JSONObject(response.body().string());
            JSONArray bindings = resultObject.getJSONObject("results").getJSONArray("bindings");
            for (int i = 0; i < bindings.length(); i++) {
                String deviceLang = Locale.getDefault().getLanguage();
                String lang = bindings.getJSONObject(i).getJSONObject("lbl").getString("xml:lang");
                //English as fallback
                if (lang.equals("en")) {
                    mName = bindings.getJSONObject(i).getJSONObject("lbl").getString("value");
                }
                //Device language if available
                if (lang.equals(deviceLang)) {
                    mName = bindings.getJSONObject(i).getJSONObject("lbl").getString("value");
                    return;
                }

            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    public Uri getUri() {
        return mUri;
    }
}
