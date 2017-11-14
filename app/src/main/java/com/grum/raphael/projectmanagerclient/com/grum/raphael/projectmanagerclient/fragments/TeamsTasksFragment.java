package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;
import com.grum.raphael.projectmanagerclient.tasks.CheckInternet;
import com.grum.raphael.projectmanagerclient.tasks.GetTaskTask;
import com.grum.raphael.projectmanagerclient.tasks.GetTeamsTask;
import com.grum.raphael.projectmanagerclient.tasks.GetTeamsTasksTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class TeamsTasksFragment extends Fragment {

    private ListView teamsTasks;
    private List<String> tasks;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_teams_tasks, container, false);
        tasks = getTasks();
        teamsTasks = (ListView) rootView.findViewById(R.id.list_teams_tasks);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.list_item,
                R.id.list_element, tasks);
        teamsTasks.setAdapter(arrayAdapter);
        TextView header = new TextView(getActivity());
        header.setText(getResources().getString(R.string.teams_tasks));
        header.setTextColor(Color.BLACK);
        header.setTextSize(MainActivity.DP_TEXT_SIZE);
        header.setGravity(Gravity.CENTER);
        header.setFocusable(false);
        teamsTasks.addHeaderView(header);
        teamsTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String taskName = (String) teamsTasks.getAdapter().getItem(position);
                String teamName = MainActivity.userData.getTeamName();
                String url = MainActivity.URL + "task";
                String token = MainActivity.userData.getToken();
                String[] params = new String[]{url, token, taskName, teamName};
                if (CheckInternet.isNetworkAvailable(getContext())) {
                    GetTaskTask getTaskTask = new GetTaskTask();
                    try {
                        JSONObject result = getTaskTask.execute(params).get();
                        String success = result.getString("success");
                        if (success.equals("true")) {
                            JSONObject task = result.getJSONObject("task");
                            String fetchedTaskName = task.getString("name" +
                                    "");
                            String fetchedTaskDescription = task.getString("description");
                            String fetchedTaskDeadline = task.getString("deadline");
                            String fetchedTaskWorker = task.getString("worker");
                            dealWithResponse(fetchedTaskName, fetchedTaskDescription,
                                    fetchedTaskDeadline, fetchedTaskWorker, rootView);
                        } else {
                            String reason = result.getString("reason");
                            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                    .setTitle(R.string.error)
                                    .setMessage("Die Aufgabe konnte nicht geladen werden!\n" + reason)
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
            }
        });
        return rootView;
    }

    private void dealWithResponse(String fetchedTaskName, String fetchedTaskDescription,
                                  String fetchedTaskDeadline, String fetchedTaskWorker,
                                  View anchorView) {
        View view
                = this.getLayoutInflater(null).inflate(R.layout.fragment_popup_task_information,
                null);
        setValuesToPopup(fetchedTaskName, fetchedTaskDescription, fetchedTaskDeadline,
                fetchedTaskWorker, view);
        final PopupWindow popupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth((int) anchorView.getWidth());
        //
        popupWindow.setBackgroundDrawable(new BitmapDrawable(getContext().getResources(),
                (Bitmap) null));
        Button closeBtn = (Button) view.findViewById(R.id.close_btn_popup_task);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        // Show popup
        int[] location = new int[2];
        anchorView.getLocationOnScreen(location);
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);
    }

    private void setValuesToPopup(String fetchedTaskName, String fetchedTaskDescription,
                                  String fetchedTaskDeadline, String fetchedTaskWorker, View view) {
        TextView taskName = (TextView) view.findViewById(R.id.popup_task_name);
        TextView taskDescription = (TextView) view.findViewById(R.id.popup_task_description);
        TextView taskDeadline = (TextView) view.findViewById(R.id.popup_task_deadline);
        TextView taskWorker = (TextView) view.findViewById(R.id.popup_task_worker);
        taskName.setText(fetchedTaskName);
        taskDescription.setText(fetchedTaskDescription);
        taskDeadline.setText(fetchedTaskDeadline);
        taskWorker.setText(fetchedTaskWorker);
    }

    private List<String> getTasks() {
        List<String> tasks = new ArrayList<>();
        if (CheckInternet.isNetworkAvailable(getContext())) {
            GetTeamsTasksTask getTeamsTask = new GetTeamsTasksTask();
            String[] params = new String[]{MainActivity.URL + "team/tasks",
                    MainActivity.userData.getToken(), MainActivity.userData.getTeamName(),
                    MainActivity.userData.getUsername()};
            try {
                JSONObject result = getTeamsTask.execute(params).get();
                String success = result.getString("success");
                if (success.equals("true")) {
                    JSONArray fetchedTasks = result.getJSONArray("tasks");
                    for (int i = 0; i < fetchedTasks.length(); i++) {
                        tasks.add(fetchedTasks.getString(i));
                    }
                } else {
                    String reason = result.getString("reason");
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.error)
                            .setMessage("Die Aufgaben des Teams konnten nicht geladen werden!\n"
                                    + reason)
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
        return tasks;
    }
}
