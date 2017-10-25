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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;
import com.grum.raphael.projectmanagerclient.tasks.GetMyProjectsTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MyProjectsFragment extends Fragment {

    private ListView myProjectsList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_projects, container, false);
        myProjectsList = (ListView) rootView.findViewById(R.id.my_projects);
        TextView headerOfList = new TextView(getActivity());
        headerOfList.setTextColor(Color.BLACK);
        headerOfList.setText(getResources().getString(R.string.my_projects));
        headerOfList.setGravity(Gravity.CENTER);
        headerOfList.setTextSize(MainActivity.DP_TEXT_SIZE);
        myProjectsList.addHeaderView(headerOfList);
        List<String> myProjects = getMyProjects();
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.list_item,
                R.id.list_element, myProjects);
        myProjectsList.setAdapter(arrayAdapter);
        return rootView;
    }

    private List<String> getMyProjects() {
        List<String> myProjects = new ArrayList<>();
        String[] params = new String[]{MainActivity.URL + "user/projects",
                MainActivity.userData.getToken(), MainActivity.userData.getUsername(),
                MainActivity.userData.getTeamName()};
        GetMyProjectsTask getMyProjectsTask = new GetMyProjectsTask();
        try {
            JSONObject result = getMyProjectsTask.execute(params).get();
            String success = result.getString("success");
            if (success.equals("true")) {
                JSONArray projects = result.getJSONArray("projects");
                for (int i = 0; i < projects.length(); i++) {
                    myProjects.add(projects.get(i).toString());
                }
            } else {
                String reason = result.getString("reason");
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.error)
                        .setMessage("Ihre Projekte konnten nicht geladen werden!\n" + reason)
                        .setNegativeButton("OK", null)
                        .create();
                alertDialog.show();
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
        return myProjects;
    }
}
