package com.prnvg.newsapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DetailedCard extends AppCompatActivity {

    private static final String TAG = "DetailedCard";

    String id, image, heading, section, date, webURL;
    StringBuilder description = new StringBuilder();
    ImageView twitterBtn, bookmarkBtn;
    Toolbar toolbar;

    private RequestQueue queue;

    private SharedPreferences pref;

    ProgressBar progressBar;
    TextView progressText;


    Context mContext = this;



    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_card);


        toolbar = findViewById(R.id.detailed_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);

        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);


        Intent myIntent = getIntent();
        id = myIntent.getStringExtra("id");
        Log.d(TAG, "onCreate: " + id);




        twitterBtn = findViewById(R.id.detailed_toolbar_twitter);
        bookmarkBtn = findViewById(R.id.detailed_toolbar_bookmark);

        pref = getSharedPreferences("MyPref", 0);
        Log.d(TAG, "onCreateOptionsMenu: ");
        if(pref.contains(id)) {
            bookmarkBtn.setImageResource(R.drawable.ic_bookmark_24px);
            //bookmarkBtn.setIcon(getResources().getDrawable( R.drawable.ic_bookmark_24px));
        }

        else {
            bookmarkBtn.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
            //bookmarkBtn.setIcon(getResources().getDrawable( R.drawable.ic_bookmark_border_black_24dp));
        }

        twitterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                String url = "https://twitter.com/intent/tweet?text=Check+out+this+Link:+" + webURL + "&hashtags=CSCI571NewsSearch";
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onOptionsItemSelected:  bookmark fired!");
                SharedPreferences.Editor editor = pref.edit();

                if(pref.contains(id)) {
                    editor.remove(id);
                    editor.commit();
                    Toast.makeText(mContext, "'" + heading +"' was removed from bookmarks", Toast.LENGTH_SHORT).show();
                    //bookmarkBtn.setIcon(getResources().getDrawable( R.drawable.ic_bookmark_border_black_24dp));
                    bookmarkBtn.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                }

                else {
                    ArrayList<String> newEntry = new ArrayList<>();
                    newEntry.add(heading);
                    newEntry.add(image);
                    newEntry.add(getTime(date).substring(0,6));
                    newEntry.add(section);
                    newEntry.add(webURL);

                    Gson gson = new Gson();

                    String json = gson.toJson(newEntry);

                    editor.putString(id, json);
                    editor.commit();
                    Toast.makeText(mContext, "'" + heading +"' was added to bookmarks", Toast.LENGTH_SHORT).show();
                    //bookmarkBtn.setIcon(getResources().getDrawable( R.drawable.ic_bookmark_24px));
                    bookmarkBtn.setImageResource(R.drawable.ic_bookmark_24px);
                }
            }
        });


        queue = Volley.newRequestQueue(this);
        initData(id);




    }

//    @SuppressLint("RestrictedApi")
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu)
//    {
//        getMenuInflater().inflate(R.menu.detailed_menu, menu);
//        twitterBtn = findViewById(R.id.detailed_toolbar_twitter);
//        bookmarkBtn = findViewById(R.id.detailed_toolbar_bookmark);
//
//        pref = getSharedPreferences("MyPref", 0);
//        Log.d(TAG, "onCreateOptionsMenu: ");
//        if(pref.contains(id)) {
//            bookmarkBtn.setIcon(getResources().getDrawable( R.drawable.ic_bookmark_24px));
//        }
//
//        else {
//            bookmarkBtn.setIcon(getResources().getDrawable( R.drawable.ic_bookmark_border_black_24dp));
//        }
//
//        return true;
//    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;

            case R.id.detailed_toolbar_bookmark:
                Log.d(TAG, "onOptionsItemSelected:  bookmark fired!");
                SharedPreferences.Editor editor = pref.edit();

                if(pref.contains(id)) {
                    editor.remove(id);
                    editor.commit();
                    Toast.makeText(this, "'" + heading +"' was removed from bookmarks", Toast.LENGTH_SHORT).show();
                    //bookmarkBtn.setIcon(getResources().getDrawable( R.drawable.ic_bookmark_border_black_24dp));
                    bookmarkBtn.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                }

                else {
                    ArrayList<String> newEntry = new ArrayList<>();
                    newEntry.add(heading);
                    newEntry.add(image);
                    newEntry.add(getTime(date).substring(0,6));
                    newEntry.add(section);
                    newEntry.add(webURL);

                    Gson gson = new Gson();

                    String json = gson.toJson(newEntry);

                    editor.putString(id, json);
                    editor.commit();
                    Toast.makeText(this, "'" + heading +"' was added to bookmarks", Toast.LENGTH_SHORT).show();
                    //bookmarkBtn.setIcon(getResources().getDrawable( R.drawable.ic_bookmark_24px));
                    bookmarkBtn.setImageResource(R.drawable.ic_bookmark_24px);
                }



                return true;



            case R.id.detailed_toolbar_twitter:
                Intent i = new Intent(Intent.ACTION_VIEW);
                String url = "https://twitter.com/intent/tweet?text=Check+out+this+Link:+" + webURL + "&hashtags=CSCI571NewsSearch";
                i.setData(Uri.parse(url));
                this.startActivity(i);
                return true;
            default:
                Log.d(TAG, "onOptionsItemSelected: " + item.getItemId());
                return super.onOptionsItemSelected(item);
        }
    }


    private void initData(String id) {


        Log.d(TAG, "initData DetailedCard: preparing data.");

        //String url = "https://content.guardianapis.com/search?order-by=newest&show-fields=starRating,headline,thumbnail,short-url&api-key=495997ca-b766-4942-b0cc-4cf922a47369";
        String url = "https://content.guardianapis.com/" + id + "?api-key=495997ca-b766-4942-b0cc-4cf922a47369&show-blocks=all";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject("response");
                            heading = jsonObject.getJSONObject("content").getString("webTitle");


                            //setTitle(heading);
                            TextView toolbar_heading = findViewById(R.id.detailed_toolbar_title);
                            //toolbar_heading.setText(heading);
                            toolbar.setTitle(heading);

                            section = jsonObject.getJSONObject("content").getString("sectionName");
                            date = jsonObject.getJSONObject("content").getString("webPublicationDate");
                            webURL = jsonObject.getJSONObject("content").getString("webUrl");
                            JSONArray bodyArray = jsonObject.getJSONObject("content").getJSONObject("blocks").getJSONArray("body");


                            for(int i = 0; i < bodyArray.length(); i++) {


                                JSONObject bodyObject = bodyArray.getJSONObject(i);
                                description.append(bodyObject.getString("bodyHtml"));

                                try {
                                    image = jsonObject.getJSONObject("content").getJSONObject("blocks").getJSONObject("main").getJSONArray("elements").getJSONObject(0).getJSONArray("assets").getJSONObject(0).getString("file");
                                }
                                catch (JSONException e) {
                                    image = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png";
                                }

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ImageView imageHolder;
                        TextView headingHolder, sectionHolder, dateHolder, descriptionHolder, webURLHolder;
                        headingHolder = findViewById(R.id.detailed_heading);
                        sectionHolder = findViewById(R.id.detailed_section);
                        dateHolder = findViewById(R.id.detailed_date);
                        descriptionHolder = findViewById(R.id.detailed_description);
                        webURLHolder = findViewById(R.id.detailed_url);
                        imageHolder = findViewById(R.id.detailed_image);

                        Picasso.get().load(image).into(imageHolder);

                        headingHolder.setText(heading);
                        sectionHolder.setText(section);
                        dateHolder.setText(getTime(date));
                        descriptionHolder.setText(Html.fromHtml(description.toString()));

                        webURLHolder.setClickable(true);
                        webURLHolder.setMovementMethod(LinkMovementMethod.getInstance());
                        webURLHolder.setText(Html.fromHtml("<a href='" + webURL + "'>View Full Article</a>"));

                        progressBar.setVisibility(View.GONE);
                        progressText.setVisibility(View.GONE);

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getTime(String webTime) {

        ZonedDateTime publishTime = Instant.parse(webTime).atZone(ZoneId.of("America/Los_Angeles"));
        Log.d(TAG, "getTime: LA publish time: world"+ publishTime);
        return(publishTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
    }


}
