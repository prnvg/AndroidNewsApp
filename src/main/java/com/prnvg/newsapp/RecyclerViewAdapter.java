package com.prnvg.newsapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.prnvg.newsapp.ui.home.HomeFragment;
import com.squareup.picasso.Picasso;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    private ArrayList<String> titleList = new ArrayList<>();
    private ArrayList<String> imageList = new ArrayList<>();
    private ArrayList<String> timestampList = new ArrayList<>();
    private ArrayList<String> sectionList = new ArrayList<>();
    private ArrayList<String> idList = new ArrayList<>();
    private ArrayList<String> urlList = new ArrayList<>();
    private Context mContext;
    private int flag;
    private SharedPreferences pref;

    public RecyclerViewAdapter(ArrayList<String> titleList, ArrayList<String> imageList, ArrayList<String> timestampList, ArrayList<String> sectionList, ArrayList<String> idList, ArrayList<String> urlList, Context mContext, int flag) {
        this.titleList = titleList;
        this.imageList = imageList;
        this.timestampList = timestampList;
        this.sectionList = sectionList;
        this.idList = idList;
        this.urlList = urlList;
        this.mContext = mContext;
        this.flag = flag;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(flag == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_card, parent, false);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_card, parent, false);
        }

        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");

        Glide.with(mContext)
                .asBitmap()
                .load(imageList.get(position))
                .into(holder.image);
        holder.title.setText(titleList.get(position));
        holder.timestamp.setText(getTime(timestampList.get(position)));
        holder.section.setText(sectionList.get(position));


        pref = mContext.getApplicationContext().getSharedPreferences("MyPref", 0);
        if(pref.contains(idList.get(position))) {
            holder.bookmark.setImageResource(R.drawable.ic_bookmark_24px);
        }

        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SharedPreferences.Editor editor = pref.edit();
                if(pref.contains(idList.get(position))) {
                    editor.remove(idList.get(position));
                    holder.bookmark.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                    editor.commit();
                    Toast.makeText(mContext, "'" + titleList.get(position) +"' was removed from bookmarks", Toast.LENGTH_SHORT).show();
                    if(flag == 1) {
                        titleList.remove(position);
                        imageList.remove(position);
                        timestampList.remove(position);
                        sectionList.remove(position);
                        idList.remove(position);
                        urlList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, idList.size());

                        if(idList.size() == 0) {
                            TextView textView = ((Activity)mContext).findViewById(R.id.text_bookmarks);
                            textView.setText("No Bookmarked Articles");
                        }
                    }

                }
                else {
                    ArrayList<String> newEntry = new ArrayList<>();
                    newEntry.add(titleList.get(position));
                    newEntry.add(imageList.get(position));
                    newEntry.add(getBookMarkTime(timestampList.get(position)));
                    newEntry.add(sectionList.get(position));
                    newEntry.add(urlList.get(position));

                    Gson gson = new Gson();

                    String json = gson.toJson(newEntry);

                    editor.putString(idList.get(position), json);
                    editor.commit();
                    Toast.makeText(mContext, "'" + titleList.get(position) +"' was added to bookmarks", Toast.LENGTH_SHORT).show();
                    holder.bookmark.setImageResource(R.drawable.ic_bookmark_24px);

                }
            }
        });


        holder.newsCardParent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Dialog dialog = new Dialog(mContext);
                dialog.setContentView(R.layout.dialog);

                TextView dTitle = (TextView) dialog.findViewById(R.id.dialog_title);
                dTitle.setText(titleList.get(position));
                ImageView dImage = (ImageView) dialog.findViewById(R.id.dialog_image);
                Picasso.get().load(imageList.get(position)).into(dImage);

                ImageView dTwitter = (ImageView) dialog.findViewById(R.id.dialog_twitter);
                dTwitter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        String url = "https://twitter.com/intent/tweet?text=Check+out+this+Link:+" + urlList.get(position) + "&hashtags=CSCI571NewsSearch";
                        i.setData(Uri.parse(url));
                        mContext.startActivity(i);
                    }
                });

                final ImageView dBookmark = (ImageView) dialog.findViewById(R.id.dialog_bookmark);

                //pref = mContext.getApplicationContext().getSharedPreferences("MyPref", 0);
                if(pref.contains(idList.get(position))) {
                    dBookmark.setImageResource(R.drawable.ic_bookmark_24px);
                }

                dBookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pref = mContext.getApplicationContext().getSharedPreferences("MyPref", 0);
                        Log.d(TAG, "onClick: bookmark fired: ");
                        SharedPreferences.Editor editor = pref.edit();
                        if(pref.contains(idList.get(position))) {
                            editor.remove(idList.get(position));
                            dBookmark.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                            holder.bookmark.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                            editor.commit();
                            Toast.makeText(mContext, "'" + titleList.get(position) +"' was removed from bookmarks", Toast.LENGTH_SHORT).show();
                            if(flag == 1) {
                                dialog.dismiss();
                                titleList.remove(position);
                                imageList.remove(position);
                                timestampList.remove(position);
                                sectionList.remove(position);
                                idList.remove(position);
                                urlList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, idList.size());

                                if(idList.size() == 0) {
                                    TextView textView = ((Activity)mContext).findViewById(R.id.text_bookmarks);
                                    textView.setText("No Bookmarked Articles");
                                }
                            }
                        }
                        else {

                            ArrayList<String> newEntry = new ArrayList<>();
                            newEntry.add(titleList.get(position));
                            newEntry.add(imageList.get(position));
                            newEntry.add(getBookMarkTime(timestampList.get(position)));
                            newEntry.add(sectionList.get(position));
                            newEntry.add(urlList.get(position));

                            Gson gson = new Gson();

                            String json = gson.toJson(newEntry);
                            editor.putString(idList.get(position), json);
                            editor.commit();

                            Toast.makeText(mContext, "'" + titleList.get(position) +"' was added bookmarks", Toast.LENGTH_SHORT).show();

                            dBookmark.setImageResource(R.drawable.ic_bookmark_24px);
                            holder.bookmark.setImageResource(R.drawable.ic_bookmark_24px);

                        }

                    }
                });

                dialog.show();
                return true;
            }
        });

        holder.newsCardParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on: " + titleList.get(position));

                //Toast.makeText(mContext, titleList.get(position), Toast.LENGTH_SHORT).show();

                Intent myIntent = new Intent(mContext, DetailedCard.class);
                Log.d(TAG, "onClick: "+ position);
                myIntent.putExtra("id", idList.get(position));
                mContext.startActivity(myIntent);

            }
        });

    }



    @Override
    public int getItemCount() {
        return titleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title;
        TextView timestamp;
        TextView section;
        CardView newsCardParent;
        ImageView bookmark;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            if(flag == 0) {
                image = itemView.findViewById(R.id.news_card_image);
                title = itemView.findViewById(R.id.news_card_title);
                timestamp = itemView.findViewById(R.id.news_card_timestamp);
                section = itemView.findViewById(R.id.news_card_section);
                newsCardParent = itemView.findViewById(R.id.news_card_parent);
                bookmark = itemView.findViewById(R.id.bookmark_image);
            }

            else {
                image = itemView.findViewById(R.id.bookmark_card_image);
                title = itemView.findViewById(R.id.bookmark_card_title);
                timestamp = itemView.findViewById(R.id.bookmark_card_timestamp);
                section = itemView.findViewById(R.id.bookmark_card_section);
                newsCardParent = itemView.findViewById(R.id.bookmark_card_parent);
                bookmark = itemView.findViewById(R.id.bookmark_image);
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getTime(String webTime) {

        if(webTime.length() < 10) {
            return webTime;
        }

        Log.d(TAG, "getTime: " + webTime);

        ZonedDateTime publishTime = Instant.parse(webTime).atZone(ZoneId.of("America/Los_Angeles"));
        ZonedDateTime currentTime = LocalDateTime.now().atZone( ZoneId.of( "America/Los_Angeles" ) );

        long secondsDiff = Duration.between( publishTime , currentTime ).toMillis() / 1000;
        long minutesDiff = secondsDiff / 60;
        long hoursDiff = minutesDiff / 60;

        if(secondsDiff < 0) {
            return("0 s ago");
        }

        else if(hoursDiff > 0){
            return(hoursDiff + " h ago");
        }
        else if (minutesDiff > 0) {
            return(minutesDiff + " m ago");
        }
        else {
            return(secondsDiff + " s ago");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getBookMarkTime(String webTime) {

        if(webTime.length() < 10) {
            return webTime;
        }

        Log.d(TAG, "getBookMarkTime: " + webTime);

        ZonedDateTime publishTime = Instant.parse(webTime).atZone(ZoneId.of("America/Los_Angeles"));
        Log.d(TAG, "getTime: LA publish time: world"+ publishTime);
        return(publishTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy")).substring(0, 6));
    }

}
