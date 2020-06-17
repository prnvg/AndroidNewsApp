package com.prnvg.newsapp.ui.headlines;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.prnvg.newsapp.R;
import com.prnvg.newsapp.RecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class sportsTab extends Fragment {

    private static final String TAG = "SportsTabFragment";
    private ArrayList<String> titleList = new ArrayList<>();
    private ArrayList<String> imageList = new ArrayList<>();
    private ArrayList<String> timestampList = new ArrayList<>();
    private ArrayList<String> sectionList = new ArrayList<>();
    private ArrayList<String> idList = new ArrayList<>();
    private ArrayList<String> urlList = new ArrayList<>();

    private View root;

    ProgressBar progressBar;
    TextView progressText;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RequestQueue queue;

    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;

    public sportsTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Log.d(TAG, "onCreateView: sports started.");
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_sports_tab, container, false);

        progressBar = root.findViewById(R.id.progressBar);
        progressText = root.findViewById(R.id.progressText);

        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);


        queue = Volley.newRequestQueue(getActivity());
        recyclerView = root.findViewById(R.id.news_container);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mSwipeRefreshLayout = root.findViewById(R.id.swipe_refresh);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to make your refresh action
                // CallYourRefreshingMethod();
                initData();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 1000);
            }
        });


        initData();

        return root;

    }

    private void initData() {
        Log.d(TAG, "initData world: preparing data.");

        //String url = "https://content.guardianapis.com/search?order-by=newest&show-fields=starRating,headline,thumbnail,short-url&api-key=495997ca-b766-4942-b0cc-4cf922a47369";
        String url = "https://pranav-newsapp-backend.appspot.com/app/sports";

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

                                if(titleList.size() == 10){
                                    break;
                                }

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

                            adapter = new RecyclerViewAdapter(titleList, imageList, timestampList, sectionList, idList, urlList, getActivity(), 0);
                            recyclerView.setAdapter(adapter);

                            progressBar.setVisibility(View.GONE);
                            progressText.setVisibility(View.GONE);

                            Log.d(TAG, "onResponse: world" + imageList.size() + "    " + titleList.size() + "    " + timestampList.size() + "    " + sectionList.size());
                            Log.d(TAG, "onResponse: world" + titleList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //for(int i = 0; i < response["response"].results.size())

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d(TAG, "onErrorResponse: world"+ error.getMessage());

                    }
                });

        queue.add(jsonObjectRequest);
    }

    public void onResume() {
        super.onResume();

        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }
}
