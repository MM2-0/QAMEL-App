package de.qa.fragment;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.qa.R;
import de.qa.misc.Utils;
import de.qa.qa.result.UriResult;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UriDetailFragment extends Fragment implements RequestListener<String, GlideDrawable> {

    private static final String TAG = UriDetailFragment.class.getSimpleName();

    private View mRootView;
    private TextView mTitleView;
    private ImageView mThumbnailView;
    private TextView mAbstractView;
    private AppCompatButton mWikipediaBtn;
    private UriResult mUriResult;
    private ProgressBar mProgressBar;

    public static UriDetailFragment newInstance(Context context, UriResult uriResult) {
        UriDetailFragment fragment = new UriDetailFragment();
        fragment.setArguments(uriResult);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            Transition saTransition = TransitionInflater.from(context).inflateTransition(
                    R.transition.fragment_shared_element_transition);
            Transition transition = TransitionInflater.from(context).inflateTransition(
                    R.transition.fragment_transition);
            fragment.setSharedElementEnterTransition(saTransition);
            fragment.setSharedElementReturnTransition(saTransition);
            fragment.setEnterTransition(transition);
            fragment.setReturnTransition(transition);
        }
        return fragment;
    }

    private void setArguments(UriResult uriResult) {
        mUriResult = uriResult;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_uri_detail, container, false);
        mTitleView = (TextView) mRootView.findViewById(R.id.titleView);
        mTitleView.setText(mUriResult.toString());
        mAbstractView = (TextView) mRootView.findViewById(R.id.abstractView);
        mThumbnailView = (ImageView) mRootView.findViewById(R.id.thumbnailView);
        mWikipediaBtn = (AppCompatButton) mRootView.findViewById(R.id.wikipediaButton);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.progressBar);
        new LoadDataTask().execute();
        return mRootView;
    }

    @Override
    public boolean onException(Exception e, String model, Target<GlideDrawable> target,
                               boolean isFirstResource) {
        mProgressBar.setVisibility(View.GONE);
        return false;
    }

    @Override
    public boolean onResourceReady(GlideDrawable resource, String model,
                                   Target<GlideDrawable> target, boolean isFromMemoryCache,
                                   boolean isFirstResource) {
        mProgressBar.setVisibility(View.GONE);
        return false;
    }

    private class LoadDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... voids) {
            String[] results = new String[3];
            results[0] = "";
            results[1] = null;
            results[2] = null;
            String url;
            try {
                /** Thumbnail request **/
                String sparQlQuery = "SELECT ?thumb ?abstr ?wikiid {  <"
                        + mUriResult.getUri().toString() + "> dbo:abstract ?abstr . <"
                        + mUriResult.getUri().toString() + "> dbo:thumbnail ?thumb . <"
                        + mUriResult.getUri().toString() + "> dbo:wikiPageID ?wikiid . }";
                url = Utils.sparQlQueryUrl(sparQlQuery);
                Log.d(TAG, url);
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
                if (bindings.length() > 0) {
                    results[1] = bindings.getJSONObject(0).getJSONObject("thumb")
                            .getString("value");
                }
                String wikiPageId = bindings.getJSONObject(0).getJSONObject("wikiid")
                        .getString("value");
                for (int i = 0; i < bindings.length(); i++) {
                    String deviceLang = Locale.getDefault().getLanguage();
                    String lang = bindings.getJSONObject(i).getJSONObject("abstr")
                            .getString("xml:lang");
                    //English as fallback
                    if (lang.equals("en")) {
                        results[0] = bindings.getJSONObject(i).getJSONObject("abstr")
                                .getString("value");
                    }
                    //Device language if available
                    if (lang.equals(deviceLang)) {
                        results[0] = bindings.getJSONObject(i).getJSONObject("abstr")
                                .getString("value");
                        break;
                    }

                }
                /** Wikipedia URL **/
                url = "https://en.wikipedia.org/w/api.php?action=query&prop=info&pageids="
                        + wikiPageId + "&inprop=url&format=json";
                client = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();
                request = new Request.Builder()
                        .url(url)
                        .build();
                response = client.newCall(request).execute();
                resultObject = new JSONObject(response.body().string());
                results[2] = resultObject.getJSONObject("query")
                        .getJSONObject("pages")
                        .getJSONObject(wikiPageId)
                        .getString("fullurl");
                Log.d(TAG, results[2]);
            } catch (IOException | JSONException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
            return results;
        }

        @Override
        protected void onPostExecute(final String[] results) {
            super.onPostExecute(results);
            if (results[0] != null) mAbstractView.setText(results[0]);
            else {
                mProgressBar.setVisibility(View.GONE);
            }
            if (results[2] != null) {
                mWikipediaBtn.setVisibility(View.VISIBLE);
                mWikipediaBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                        builder.setToolbarColor(getContext().getResources()
                                .getColor(R.color.colorPrimary));
                        builder.setShowTitle(true);
                        CustomTabsIntent customTabsIntent = builder.build();
                        customTabsIntent.launchUrl(getContext(), Uri.parse(results[2]));
                    }
                });
            }
            if (results[1] != null) {
                Log.d(TAG, results[1]);
                Glide.with(getContext()).load(results[1]).listener(UriDetailFragment.this)
                        .into(mThumbnailView);
            } else {
                mProgressBar.setVisibility(View.GONE);
            }
        }
    }
}
