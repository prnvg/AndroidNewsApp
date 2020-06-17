package com.prnvg.newsapp.ui.trending;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;

import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;


import com.prnvg.newsapp.R;
import com.prnvg.newsapp.RecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TrendingFragment extends Fragment {

    private static final String TAG = "TrendingTab";
    private RequestQueue queue;
    private List<Entry> valueList;
    private View root;

    private TrendingViewModel trendingViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        trendingViewModel =
                ViewModelProviders.of(this).get(TrendingViewModel.class);
        root = inflater.inflate(R.layout.fragment_trending, container, false);
        final TextView textView = root.findViewById(R.id.text_trending);
        trendingViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        queue = Volley.newRequestQueue(getActivity());


        final EditText editText = root.findViewById(R.id.trending_text);


        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.d(TAG, "onEditorAction: fired outside if: " + editText.getText());
                initData(editText.getText().toString());
                if(actionId== EditorInfo.IME_ACTION_SEND) {
                    Log.d(TAG, "onEditorAction: FIRED!");
                    return true;
                }
                return false;
            }

        });

        initData("Coronavirus");








        Log.d(TAG, "onCreateView: valuelist" + valueList);
        return root;
    }

    private void initData(String term) {
        valueList = new ArrayList<>();
        
        if(term.length() == 0) {
            term = "Coronavirus";
        }

        final String label = "Trending Chart for " + term;
        
        Log.d(TAG, "initData: started");
        String url = "https://pranav-newsapp-backend.appspot.com/app/trending?term=" + term;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray= response.getJSONObject("default").getJSONArray("timelineData");

                            Log.d(TAG, "onResponse: "+jsonArray);

                            for(int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                //Log.d(TAG, "onResponse: " + jsonObject);

                                valueList.add(new Entry(i, jsonObject.getJSONArray("value").getInt(0)));

                                Log.d(TAG, "onResponse: " + valueList);

                            }
                            Log.d(TAG, "onResponse: ValueList Size: " + valueList.size());


                            LineDataSet set1 = new LineDataSet(valueList, label);
                            //set1.setAxisDependency(YAxis.AxisDependency.LEFT);

                            set1.setCircleHoleColor(getActivity().getColor(R.color.chartLine));
                            set1.setCircleColor(getActivity().getColor(R.color.chartLine));
                            //set1.setColor(getActivity().getColor(R.color.colorPrimary2));
                            set1.setColor(getActivity().getColor(R.color.chartLine));

                            List<ILineDataSet> set2 = new ArrayList<>();
                            set2.add(set1);

                            LineData data = new LineData(set2);

                            LineChart mChart = root.findViewById(R.id.chart);
                            mChart.setData(data);

                            mChart.getAxisLeft().setDrawGridLines(false);
                            mChart.getAxisRight().setDrawGridLines(false);
                            mChart.getXAxis().setDrawGridLines(false);
                            mChart.getAxisLeft().setDrawAxisLine(false);


                            mChart.invalidate();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d(TAG, "onErrorResponse: world"+ error.getMessage());

                    }
                });

        queue.add(jsonObjectRequest);

        Log.d(TAG, "initData: final valuelist" + valueList);
    }

}
