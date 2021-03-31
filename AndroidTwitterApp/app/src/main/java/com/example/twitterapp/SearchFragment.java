package com.example.twitterapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
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
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

@SuppressWarnings("ALL")
public class SearchFragment extends Fragment {
    final String bearer_token ="AAAAAAAAAAAAAAAAAAAAAKKKJgEAAAAAVZIrYYGptE0VSFNQH5ubeUjkzMc%3Dua4ieHyDn2LorzmbjxabHWBVf0lTzkJ7VvX4WiqsUo4vckg0Nf";
    final String URL = "https://api.twitter.com/1.1/search/tweets.json?q=";
//    final String APIKEY = "gxGZNWC2MPB0hd94ZbcFxBIx0";
//    final String APISECRET = "LhPhGPct9GZu1Ig8yKSUCiDuYXGtu5bBXXNeo34BSJA4hg2YNw";
    private EditText searchtxt;
    private TextView textView;
    private DatabaseReference ref,yazdir;
    private FirebaseDatabase database;
    RecyclerView recyclerView;
    private List<Tweet>tweets;
    private Adapter adapter;
    private ViewGroup viewGroup;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup= (ViewGroup) inflater.inflate(R.layout.fragment_search,container,false);
        tweets=new ArrayList<>();
        setUpSearchQuery(viewGroup);
        return viewGroup;
    }

    private void setUpSearchQuery(ViewGroup view) {
        searchtxt=view.findViewById(R.id.enter_search_tweet);
        searchtxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_SEARCH)) {
                    tweets.clear();
                    String searchTerm=searchtxt.getText().toString();
                    if(searchTerm.length()>0){
                        try{
                            String properties="&count=100&lang=en&tweet_mode=extended";
                            String encodedSearch= URLEncoder.encode(searchTerm,"UTF-8");
                            String searchURL=URL+encodedSearch+properties;
                            database= FirebaseDatabase.getInstance();
                            ref=database.getReference().child("searchKeys").child(encodedSearch);
                            new GetFeedTask().execute(bearer_token, searchURL);

                        }catch (Exception e){
                            e.printStackTrace();
                            Log.e("Warning","Error"+e.getMessage());
                        }
                    }else if(searchTerm.length()==0){
                        Toast.makeText(getContext(), "Lütfen aramak için bir şeyler yazın..", Toast.LENGTH_SHORT).show();

                    }
                }
                return false;
            }
        });

    }

    protected class GetFeedTask extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... params) {
            try{
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpGet httpget = new HttpGet(params[1]);
                httpget.setHeader("Authorization", "Bearer " + params[0]);
                httpget.setHeader("Content-type", "application/json");

                InputStream inputStream = null;
                HttpResponse response = httpclient.execute(httpget);
                StatusLine searchStatus=response.getStatusLine();
                StringBuilder sb = new StringBuilder();
                HttpEntity entity = response.getEntity();
                inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                return sb.toString();
            }catch (Exception e){
                Log.e("GetFeedTask","Error:" + e.getMessage());
                return null;
            }

        }

        @Override
        protected void onPostExecute(String jsonText) {

            try {
                JSONObject resultObject = new JSONObject(jsonText);
                org.json.JSONArray tweetArray = resultObject.getJSONArray("statuses");
                for (int t=0; t<tweetArray.length(); t++) {
                    JSONObject tweetObject = tweetArray.getJSONObject(t);
                    Tweet tweet=new Tweet();
                    tweet.setId(tweetObject.getString("id_str").toString());
                    tweet.setTweetText(tweetObject.getString("full_text").toString());
                    tweets.add(tweet);
                    yazdir=ref.child(tweet.getId().toString());
                    yazdir.setValue(tweet.getTweetText().toString());


                }

            }catch (Exception e){
                Log.e("GetFeedTask", "Error:" + e.getMessage());
            }
            recyclerView=viewGroup.findViewById(R.id.tweetsList);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter=new Adapter(getContext(),tweets);
            recyclerView.setAdapter(adapter);


        }
    }


}
