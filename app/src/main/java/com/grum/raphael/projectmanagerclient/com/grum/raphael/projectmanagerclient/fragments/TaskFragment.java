package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;


public class TaskFragment extends Fragment {

    private TabLayout tab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_task, container, false);
        tab = (TabLayout) rootView.findViewById(R.id.tab_tasks);
        if (!MainActivity.userData.getUserRole().equals(MainActivity.ADMIN)) {
            tab.setVisibility(View.GONE);
        } else {
            tab.addTab(tab.newTab().setText("Task Erstellen"));
            tab.addTab(tab.newTab().setText("Meine Tasks"));
            tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    if (tab.getPosition() == 0) {
                        // TODO
                    } else {
                        // TODO
                    }
                    // TODO
                    transaction.commit();
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }
        return rootView;
    }
}
