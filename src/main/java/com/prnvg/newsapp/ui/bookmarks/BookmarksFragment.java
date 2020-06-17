package com.prnvg.newsapp.ui.bookmarks;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prnvg.newsapp.R;
import com.prnvg.newsapp.RecyclerViewAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookmarksFragment extends Fragment {

    private static final String TAG = "Bookmarks Tab";
    private BookmarksViewModel bookmarksViewModel;
    private SharedPreferences pref;
    private View root;
    TextView textView;

    private ArrayList<String> titleList = new ArrayList<>();
    private ArrayList<String> imageList = new ArrayList<>();
    private ArrayList<String> timestampList = new ArrayList<>();
    private ArrayList<String> sectionList = new ArrayList<>();
    private ArrayList<String> idList = new ArrayList<>();
    private ArrayList<String> urlList = new ArrayList<>();

    RecyclerView recyclerView;
    RecyclerViewAdapter adapter = new RecyclerViewAdapter(titleList, imageList, timestampList, sectionList, idList, urlList, getActivity(), 1);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {



        pref = getContext().getSharedPreferences("MyPref", 0);

        root = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        recyclerView = root.findViewById(R.id.bookmark_container);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        textView = root.findViewById(R.id.text_bookmarks);
        Map<String, ?> allEntries = pref.getAll();

        if(allEntries.size() == 0) {
            bookmarksViewModel =
                    ViewModelProviders.of(this).get(BookmarksViewModel.class);

            bookmarksViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
                @Override
                public void onChanged(@Nullable String s) {
                    textView.setText("No Bookmarked Articles");
                }
            });
        }

        else {

            textView.setText("");

            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {

                Log.d(TAG,"map values " + entry.getKey() + ": " + entry.getValue().toString());

                Gson gson = new Gson();
                Type type = new TypeToken<List<String>>() {}.getType();
                List<String> arrayList= gson.fromJson(entry.getValue().toString(), type);

                Log.d(TAG, "onCreateView: " + arrayList.get(0));

                imageList.add(arrayList.get(1));
                titleList.add(arrayList.get(0));
                timestampList.add(arrayList.get(2));
                sectionList.add(arrayList.get(3));
                idList.add(entry.getKey());
                urlList.add(arrayList.get(4));
            }

            adapter = new RecyclerViewAdapter(titleList, imageList, timestampList, sectionList, idList, urlList, getActivity(), 1);
            recyclerView.setAdapter(adapter);

        }



        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        imageList.clear();
        titleList.clear();
        timestampList.clear();
        sectionList.clear();
        idList.clear();
        urlList.clear();


        Map<String, ?> allEntries = pref.getAll();

        if(allEntries.size() == 0) {
            textView.setText("No Bookmarked Articles");
        }

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {

            Log.d(TAG,"map values " + entry.getKey() + ": " + entry.getValue().toString());

            Gson gson = new Gson();
            Type type = new TypeToken<List<String>>() {}.getType();
            List<String> arrayList= gson.fromJson(entry.getValue().toString(), type);

            Log.d(TAG, "onCreateView: " + arrayList.get(0));

            imageList.add(arrayList.get(1));
            titleList.add(arrayList.get(0));
            timestampList.add(arrayList.get(2));
            sectionList.add(arrayList.get(3));
            idList.add(entry.getKey());
            urlList.add(arrayList.get(4));
        }


        adapter.notifyDataSetChanged();
    }
}
