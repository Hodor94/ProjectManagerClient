package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;
import com.grum.raphael.projectmanagerclient.tasks.CheckInternet;
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
        myProjectsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String projectName = (String) parent.getItemAtPosition(position);
                openProjectOverviewPage(projectName);
            }
        });
        myProjectsList.setAdapter(arrayAdapter);
        return rootView;
    }

    private void openProjectOverviewPage(String projectName) {
        Bundle bundle = new Bundle();
        bundle.putString("projectName", projectName);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment newFragment = new ProjectOverviewFragment();
        newFragment.setArguments(bundle);
        transaction.replace(R.id.containerFrame, newFragment);
        transaction.commit();
    }

    private List<String> getMyProjects() {
        List<String> myProjects = new ArrayList<>();
        if (CheckInternet.isNetworkAvailable(getContext())) {
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
        } else {
            AlertDialog alertDialog = CheckInternet.internetNotAvailable(getActivity());
            alertDialog.show();
        }
        return myProjects;
    }
}
