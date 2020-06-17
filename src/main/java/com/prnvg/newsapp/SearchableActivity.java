package com.prnvg.newsapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class SearchableActivity extends AppCompatActivity {

    private static final String TAG = "SearchableActivity";

    private ArrayList<String> titleList = new ArrayList<>();
    private ArrayList<String> imageList = new ArrayList<>();
    private ArrayList<String> timestampList = new ArrayList<>();
    private ArrayList<String> sectionList = new ArrayList<>();
    private ArrayList<String> idList = new ArrayList<>();
    private ArrayList<String> urlList = new ArrayList<>();

    private Toolbar toolbar;
    private RequestQueue searchQueue;

    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;

    ProgressBar progressBar;
    TextView progressText;


    Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        toolbar = findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);

        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);

        searchQueue = Volley.newRequestQueue(this);

        recyclerView = findViewById(R.id.search_container);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        String query = intent.getStringExtra("key");
        setTitle("Search Results for " + query);

        doMySearch(query);

    }


    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //getMenuInflater().inflate(R.menu.detailed_menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;

            default:
                Log.d(TAG, "onOptionsItemSelected: " + item.getItemId());
                return super.onOptionsItemSelected(item);
        }
    }



    private void doMySearch(String query) {
        Log.d(TAG, "doMySearch: " + query);
        Log.d(TAG, "doMySearch: preparing search data");

        String url = "https://pranav-newsapp-backend.appspot.com/guardian/search?q=" + query;

        titleList = new ArrayList<>();
        imageList = new ArrayList<>();
        timestampList = new ArrayList<>();
        sectionList = new ArrayList<>();
        idList = new ArrayList<>();
        urlList = new ArrayList<>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray= response.getJSONObject("response").getJSONArray("results");

                            for(int i = 0; i < jsonArray.length(); i++) {


                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                String image;
                                String title = jsonObject.getString("webTitle");
                                String section = jsonObject.getString("sectionName");
                                String timestamp = jsonObject.getString("webPublicationDate");
                                String id = jsonObject.getString("id");
                                String url = jsonObject.getString("webUrl");

                                try {
                                    image = jsonObject.getJSONObject("blocks").getJSONObject("main").getJSONArray("elements").getJSONObject(0).getJSONArray("assets").getJSONObject(0).getString("file");
                                }
                                catch (JSONException e) {
                                    image = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png";
                                }

                                imageList.add(image);
                                titleList.add(title);
                                timestampList.add(timestamp);
                                sectionList.add(section);
                                idList.add(id);
                                urlList.add(url);
                            }

                            adapter = new RecyclerViewAdapter(titleList, imageList, timestampList, sectionList, idList, urlList, mContext , 0);
                            recyclerView.setAdapter(adapter);

//                            progressBar.setVisibility(View.GONE);
//                            progressText.setVisibility(View.GONE);

                            Log.d(TAG, "onResponse: " + imageList.size() + "    " + titleList.size() + "    " + timestampList.size() + "    " + sectionList.size());
                            Log.d(TAG, "onResponse: " + titleList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d(TAG, "onErrorResponse: "+ error.getMessage());

                    }
                });

        searchQueue.add(jsonObjectRequest);

    }



}
