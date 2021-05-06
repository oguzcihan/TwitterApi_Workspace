package com.example.twitterapp;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

@SuppressWarnings("ALL")
public class HomeFragment extends Fragment {
    final String URL = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=";
    final String APIKEY = "gxGZNWC2MPB0hd94ZbcFxBIx0";
    final String APISECRET = "LhPhGPct9GZu1Ig8yKSUCiDuYXGtu5bBXXNeo34BSJA4hg2YNw";
    final String bearer_token = "AAAAAAAAAAAAAAAAAAAAAKKKJgEAAAAAVZIrYYGptE0VSFNQH5ubeUjkzMc%3Dua4ieHyDn2LorzmbjxabHWBVf0lTzkJ7VvX4WiqsUo4vckg0Nf";
    ViewGroup viewGroup;
    RecyclerView recyclerView;
    private List<Tweet> tweets;
    private Adapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);
        tweets = new ArrayList<>();
        setUpSearchQuery(viewGroup);
        return viewGroup;
    }

    private String user_name = "elonmusk";
    private void setUpSearchQuery(ViewGroup view) {
        try {
            String properties = user_name+"&include_rts=1";
            String searchURL = URL + properties;
            new HomeFragment.GetFeedTask().execute(bearer_token, searchURL);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Warning", "Error" + e.getMessage());
        }
    }


    protected class GetFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpGet httpget = new HttpGet(params[1]);
                httpget.setHeader("Authorization", "Bearer " + params[0]);
                httpget.setHeader("Content-type", "application/json");

                InputStream inputStream = null;
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();

                inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                return sb.toString();
            } catch (Exception e) {
                Log.e("GetFeedTask", "Error:" + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String jsonText) {

            try {
                TextView textt=viewGroup.findViewById(R.id.user_name);
                textt.setText("Kullanıcı Adı: "+user_name);
                JSONArray jsonarray = new JSONArray(jsonText);
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    String id = jsonobject.getString("id_str");
                    String text = jsonobject.getString("text");
                    Tweet tweet = new Tweet();
                    tweet.setId(id.toString());
                    tweet.setTweetText(text.toString());
                    tweets.add(tweet);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            recyclerView = viewGroup.findViewById(R.id.tweetsListhome);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new Adapter(getContext(), tweets);
            recyclerView.setAdapter(adapter);
        }
    }

}
