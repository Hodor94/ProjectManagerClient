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
            Fragment newFragment = new MyTasksFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.containerFrame, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            tab.addTab(tab.newTab().setText("Task Erstellen"));
            tab.addTab(tab.newTab().setText("Meine Tasks"));
            tab.addTab(tab.newTab().setText("Alle Aufgaben"));
            TabLayout.Tab startingTab = tab.getTabAt(0);
            startingTab.select();
            Fragment initialFragment = new CreateTaskFragment();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.pager_tasks, initialFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    Fragment newFragment;
                    if (tab.getPosition() == 0) {
                        newFragment = new CreateTaskFragment();
                    } else if (tab.getPosition() == 1) {
                        newFragment = new MyTasksFragment();
                    } else {
                        // TODO
                        newFragment = new TeamsTasksFragment();
                    }
                    transaction.replace(R.id.pager_tasks, newFragment);
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
