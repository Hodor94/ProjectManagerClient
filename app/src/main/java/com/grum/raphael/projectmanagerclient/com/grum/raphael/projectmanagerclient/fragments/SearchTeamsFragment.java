package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.grum.raphael.projectmanagerclient.R;

import java.util.ArrayList;
import java.util.List;


public class SearchTeamsFragment extends Fragment {

    private TabLayout tabLayout;
    private FrameLayout frameLayout;

    public SearchTeamsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_teams, container, false);

        // Set up the ViewPager with adapter.
        frameLayout = (FrameLayout) rootView.findViewById(R.id.pager_search_teams);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout_search_teams);
        tabLayout.addTab(tabLayout.newTab().setText("Team Erstellen"));
        tabLayout.addTab(tabLayout.newTab().setText("Team Suchen"));
        TabLayout.Tab startingTab = tabLayout.getTabAt(0);
        startingTab.select();
        Fragment initialFragment = new CreateTeamFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.pager_search_teams, initialFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    FragmentManager manager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = manager.beginTransaction();
                    fragmentTransaction.replace(R.id.pager_search_teams, new CreateTeamFragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else {
                    FragmentManager manager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = manager.beginTransaction();
                    fragmentTransaction.replace(R.id.pager_search_teams,
                            new SearchAllTeamsFragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        return rootView;
    }



}
