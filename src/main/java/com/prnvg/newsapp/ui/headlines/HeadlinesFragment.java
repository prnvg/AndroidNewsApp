package com.prnvg.newsapp.ui.headlines;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.prnvg.newsapp.R;

public class HeadlinesFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabItem worldTab, businessTab, politicsTab, sportsTab, technologyTab, scienceTab;
    public PagerAdapter pagerAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_headlines, container, false);

        tabLayout = (TabLayout) root.findViewById(R.id.tablayout);
        worldTab = (TabItem) root.findViewById(R.id.world_tab);
        businessTab = (TabItem) root.findViewById(R.id.business_tab);
        politicsTab = (TabItem) root.findViewById(R.id.politics_tab);
        sportsTab = (TabItem) root.findViewById(R.id.sports_tab);
        technologyTab = (TabItem) root.findViewById(R.id.technology_tab);
        scienceTab = (TabItem) root.findViewById(R.id.science_tab);
        viewPager = (ViewPager) root.findViewById(R.id.headlines_viewpager);

        pagerAdapter = new PageAdapter(getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if(tab.getPosition() == 0) {
                    pagerAdapter.notifyDataSetChanged();
                }
                else if(tab.getPosition() == 1) {
                    pagerAdapter.notifyDataSetChanged();
                }
                else if(tab.getPosition() == 2) {
                    pagerAdapter.notifyDataSetChanged();
                }
                else if(tab.getPosition() == 3) {
                    pagerAdapter.notifyDataSetChanged();
                }
                else if(tab.getPosition() == 4) {
                    pagerAdapter.notifyDataSetChanged();
                }
                else if(tab.getPosition() == 5) {
                    pagerAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        pagerAdapter.notifyDataSetChanged();
    }
}
