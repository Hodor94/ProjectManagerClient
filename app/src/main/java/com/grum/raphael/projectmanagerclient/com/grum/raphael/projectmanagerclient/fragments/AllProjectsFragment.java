package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;
import com.grum.raphael.projectmanagerclient.tasks.CheckInternet;
import com.grum.raphael.projectmanagerclient.tasks.GetAllProjectsTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AllProjectsFragment extends Fragment {

    private ListView allProjectsList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_projects, container, false);
        allProjectsList = (ListView) rootView.findViewById(R.id.all_projects);
        TextView headerOfList = new TextView(getActivity());
        headerOfList.setText(R.string.all_projects);
        headerOfList.setGravity(Gravity.CENTER);
        headerOfList.setTextSize(MainActivity.DP_TEXT_SIZE);
        headerOfList.setTextColor(Color.BLACK);
        allProjectsList.addHeaderView(headerOfList);
        List<String> allProjects = getAllProjects();
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.list_item,
                R.id.list_element, allProjects);
        allProjectsList.setAdapter(arrayAdapter);
        return rootView;
    }

    private List<String> getAllProjects() {
        List<String> allProjects = new ArrayList<>();
        if (CheckInternet.isNetworkAvailable(getContext())) {
            String[] params = new String[]{MainActivity.URL + "team/projects",
                    MainActivity.userData.getToken(), MainActivity.userData.getTeamName()};
            GetAllProjectsTask getAllProjectsTask = new GetAllProjectsTask();
            try {
                JSONObject response = getAllProjectsTask.execute(params).get();
                String success = response.getString("success");
                if (success.equals("true")) {
                    JSONArray projects = response.getJSONArray("projects");
                    for (int i = 0; i < projects.length(); i++) {
                        allProjects.add(projects.getString(i));
                    }
                }
            } catch (InterruptedException e) {
                // TODO
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO
                e.printStackTrace();
            }
        } else {
            AlertDialog alertDialog = CheckInternet.internetNotAvailable(getActivity());
            alertDialog.show();
        }
        return allProjects;
    }

}
