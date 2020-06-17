package com.prnvg.newsapp.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prnvg.newsapp.MainActivity;
import com.prnvg.newsapp.R;
import com.prnvg.newsapp.RecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements LocationListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    LocationManager locationManager;
    String provider;
    String city = "";
    String state = "";
    String temperature;
    String weatherType;
    RequestQueue queue;

    ProgressBar progressBar;
    TextView progressText;

    private SwipeRefreshLayout mSwipeRefreshLayout;


    private static final String TAG = "HomeFragment";
    private ArrayList<String> titleList = new ArrayList<>();
    private ArrayList<String> imageList = new ArrayList<>();
    private ArrayList<String> timestampList = new ArrayList<>();
    private ArrayList<String> sectionList = new ArrayList<>();
    private ArrayList<String> idList = new ArrayList<>();
    private ArrayList<String> urlList = new ArrayList<>();

    private View root;


    private RequestQueue weatherQueue;

    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle("R.string.title_location_permission")
                        .setMessage("R.string.text_location_permission")
                        .setPositiveButton("R.string.ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        locationManager.requestLocationUpdates(provider, 400, 1, this);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        Log.d(TAG, "onCreateView: started.");
        
        root = inflater.inflate(R.layout.fragment_home, container, false);


        progressBar = root.findViewById(R.id.progressBar);
        progressText = root.findViewById(R.id.progressText);

        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);




        queue = Volley.newRequestQueue(getActivity());
        recyclerView = root.findViewById(R.id.news_container);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //spinner = (ProgressBar) root.findViewById(R.id.progressBar1);

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



        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        checkLocationPermission();


        TextView city = root.findViewById(R.id.weather_city);
        TextView state = root.findViewById(R.id.weather_state);
        TextView temperature = root.findViewById(R.id.weather_temperature);
        TextView weatherType = root.findViewById(R.id.weather_type);

        MainActivity activity = (MainActivity) getActivity();
        String[] myDataFromActivity = activity.getMyData();

        Log.d(TAG, "onCreateView: " + myDataFromActivity);

        city.setText(myDataFromActivity[0]);
        state.setText(myDataFromActivity[1]);
        temperature.setText(myDataFromActivity[2]);
        weatherType.setText(myDataFromActivity[3]);

        initData();

        return root;
    }

    private void initData() {
        Log.d(TAG, "initData: preparing data.");


        //String url = "https://pranav-newsapp-backend.appspot.com/guardian/home";
        String url = "https://pranav-newsapp-backend.appspot.com/app/home";
        //RequestQueue queue = Volley.newRequestQueue(getActivity());

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
                                    image = jsonObject.getJSONObject("fields").getString("thumbnail");
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

                            Log.d(TAG, "onResponse: " + imageList.size() + "    " + titleList.size() + "    " + timestampList.size() + "    " + sectionList.size());
                            Log.d(TAG, "onResponse: " + titleList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //for(int i = 0; i < response["response"].results.size())

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d(TAG, "onErrorResponse: "+ error.getMessage());

                    }
                });

        queue.add(jsonObjectRequest);
    }


    @Override
    public void onResume() {
        super.onResume();

        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }


        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(provider, 400, 1, this);
        }


    }

    @Override
    public void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Log.d("MYNEWSAPP", String.valueOf(location.getLongitude()));
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            city = addresses.get(0).getLocality();
            Log.d(TAG, "onLocationChanged: " + city);
            state = addresses.get(0).getAdminArea();
            Log.d(TAG, "onLocationChanged: " + state);
            weatherAPICall(city);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void weatherAPICall(final String cityName) {

        weatherQueue = Volley.newRequestQueue(getActivity());
        Log.d(TAG, "weatherAPICall: called");
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&appid=52f262f607edabb54ad624d8323cbff5";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject main = response.getJSONObject("main");
                            temperature = main.getString("temp");
                            weatherType = response.getJSONArray("weather").getJSONObject(0).getString("main");

                            Log.d(TAG, "onResponse: weatherAPICall" + city + state + temperature + weatherType);

                            setWeatherParameters(temperature, weatherType);


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

        weatherQueue.add(jsonObjectRequest);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void setWeatherParameters(String temperature, String weatherType) {
        TextView cityView = root.findViewById(R.id.weather_city);
        TextView stateView = root.findViewById(R.id.weather_state);
        TextView temperatureView = root.findViewById(R.id.weather_temperature);
        TextView weatherView = root.findViewById(R.id.weather_type);
        RelativeLayout weatherImage = root.findViewById(R.id.weather_background);
        cityView.setText(city);
        stateView.setText(state);
        temperatureView.setText(Integer.toString(Math.round(Float.parseFloat(temperature)))   + "°C");
        weatherView.setText(weatherType);

        switch (weatherType) {
            case "Clouds":
                weatherImage.setBackgroundResource(R.drawable.cloudy_weather);
                break;
            case "Clear":
                weatherImage.setBackgroundResource(R.drawable.clear_weather);
                break;
            case "Snow":
                weatherImage.setBackgroundResource(R.drawable.snowy_weather);
                break;
            case "Rain":
            case "Drizzle":
                weatherImage.setBackgroundResource(R.drawable.rainy_weather);
                break;
            case "Thunderstorm":
                weatherImage.setBackgroundResource(R.drawable.thunder_weather);
                break;
            default:
                weatherImage.setBackgroundResource(R.drawable.sunny_weather);
                break;
        }
    }


}
