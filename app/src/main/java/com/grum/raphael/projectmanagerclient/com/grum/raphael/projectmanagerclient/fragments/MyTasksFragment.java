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
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;
import com.grum.raphael.projectmanagerclient.tasks.CheckInternet;
import com.grum.raphael.projectmanagerclient.tasks.DeleteTaskTask;
import com.grum.raphael.projectmanagerclient.tasks.EditTaskTask;
import com.grum.raphael.projectmanagerclient.tasks.GetMyTasksTask;
import com.grum.raphael.projectmanagerclient.tasks.GetTaskTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MyTasksFragment extends Fragment {

    private List<String> tasks;
    private ListView tasksList;
    public String deadline;
    private String taskName;
    private String taskDescription;
    private String taskWorker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_tasks, container, false);
        tasksList = (ListView) rootView.findViewById(R.id.list_my_tasks);
        tasks = getMyTasks();
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.list_item,
                R.id.list_element, tasks);
        tasksList.setAdapter(arrayAdapter);
        TextView header = new TextView(getActivity());
        header.setTextColor(Color.BLACK);
        header.setTextSize(MainActivity.DP_TEXT_SIZE);
        header.setText(getResources().getString(R.string.header_tasks));
        header.setGravity(Gravity.CENTER);
        header.setFocusable(false);
        tasksList.addHeaderView(header);
        tasksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tempTaskName = (String) tasksList.getAdapter().getItem(position);
                String url = MainActivity.URL + "task";
                String token = MainActivity.userData.getToken();
                String teamName = MainActivity.userData.getTeamName();
                String[] params = new String[]{url, token, tempTaskName, teamName};
                if (CheckInternet.isNetworkAvailable(getContext())) {
                    GetTaskTask getTaskTask = new GetTaskTask();
                    try {
                        JSONObject result = getTaskTask.execute(params).get();
                        String success = result.getString("success");
                        if (success.equals("true")) {
                            JSONObject task = result.getJSONObject("task");
                            String fetchedTaskName = task.getString("name");
                            String fetchedTaskDescription = task.getString("description");
                            String fetchedTaskDeadline = task.getString("deadline");
                            deadline = fetchedTaskDeadline;
                            taskName = fetchedTaskName;
                            taskDescription = fetchedTaskDescription;
                            String fetchedTaskWorker = task.getString("worker");
                            taskWorker = fetchedTaskWorker;
                            dealWithResponse(fetchedTaskName, fetchedTaskDescription,
                                    fetchedTaskDeadline,
                                    fetchedTaskWorker, view);
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
                = this.getLayoutInflater(null).inflate(R.layout.fragment_edit_own_task_popup,
                null);
        final PopupWindow popupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setValuesToPopup(fetchedTaskName, fetchedTaskDescription, fetchedTaskDeadline,
                fetchedTaskWorker, view,  popupWindow);
        popupWindow.setFocusable(true);
        popupWindow.setWidth((int) anchorView.getWidth());
        //
        popupWindow.setBackgroundDrawable(new BitmapDrawable(getContext().getResources(),
                (Bitmap) null));
        Button closeBtn = (Button) view.findViewById(R.id.close_btn_popup_own_task);
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
                                  String fetchedTaskDeadline, String fetchedTaskWorker, View view,
                                  PopupWindow popupWindow) {
        TextView taskName = (TextView) view.findViewById(R.id.popup_edit_task_name);
        EditText taskDescription = (EditText) view.findViewById(R.id.popup_edit_task_description);
        DatePicker taskDeadline = (DatePicker) view.findViewById(R.id.popup_edit_task_deadline);
        TextView taskWorker = (TextView) view.findViewById(R.id.popup_edit_task_worker);
        Button editTask = (Button) view.findViewById(R.id.popup_btn_edit_task_ready);
        Button deleteTask = (Button) view.findViewById(R.id.popup_btn_delete_task);
        TextView info = (TextView) view.findViewById(R.id.popup_edit_task_info);
        taskName.setText(fetchedTaskName);
        taskDescription.setText(fetchedTaskDescription);
        setUpDatePicker(taskDeadline, fetchedTaskDeadline);
        taskWorker.setText(fetchedTaskWorker);
        setUpTextWatcherTaskDescription(taskDescription);
        setUpEditBtn(editTask, info, popupWindow);
        setUpDeleteBtn(deleteTask, popupWindow);
    }

    private void setUpDeleteBtn(final Button deleteTask, final PopupWindow popupWindow) {
        deleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckInternet.isNetworkAvailable(getContext())) {
                    if (taskWorker.equals(MainActivity.userData.getUsername())) {
                        String[] params = new String[]{MainActivity.URL + "delete/task",
                                MainActivity.userData.getToken(), taskName, MainActivity.userData.getTeamName(),
                                MainActivity.userData.getUsername()};
                        DeleteTaskTask deleteTaskTask = new DeleteTaskTask();
                        try {
                            JSONObject result = deleteTaskTask.execute(params).get();
                            String success = result.getString("success");
                            if (success.equals("true")) {
                                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                        .setTitle(R.string.success)
                                        .setMessage("Die Aufgabe wurde erfolgreich gelöscht!")
                                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Fragment taskFragment = new TaskFragment();
                                                FragmentTransaction transaction
                                                        = getFragmentManager().beginTransaction();
                                                transaction.replace(R.id.containerFrame, taskFragment);
                                                transaction.commit();
                                                popupWindow.dismiss();
                                            }
                                        })
                                        .create();
                                alertDialog.show();
                            } else {
                                String reason = result.getString("reason");
                                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                        .setTitle(R.string.error)
                                        .setMessage("Die Aufgabe konnte nicht gelöscht werden!\n"
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
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.error)
                                .setMessage(getResources().getString(R.string.no_rights))
                                .setNegativeButton("OK", null)
                                .create();
                        alertDialog.show();
                    }
                } else {
                    AlertDialog alertDialog = CheckInternet.internetNotAvailable(getActivity());
                    alertDialog.show();
                }
            }
        });
    }

    private void setUpEditBtn(Button editTask, final TextView info, final PopupWindow popupWindow) {
        editTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info.setText("");
                if (CheckInternet.isNetworkAvailable(getContext())) {
                    if (taskWorker.equals(MainActivity.userData.getUsername())) {
                        if (validateInput(taskDescription, deadline)) {
                            if (validateDeadline(deadline)) {
                                EditTaskTask editTaskTask = new EditTaskTask();
                                String[] params = new String[]{MainActivity.URL + "edit/task",
                                        MainActivity.userData.getToken(), taskName, taskDescription, deadline,
                                        MainActivity.userData.getTeamName(), MainActivity.userData.getUsername()};
                                try {
                                    JSONObject result = editTaskTask.execute(params).get();
                                    String success = result.getString("success");
                                    if (success.equals("true")) {
                                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                                .setTitle(R.string.success)
                                                .setMessage("Die Aufgabe wurde erfolgreich editiert!")
                                                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Fragment taskFragment = new TaskFragment();
                                                        FragmentTransaction transaction
                                                                = getFragmentManager().beginTransaction();
                                                        transaction.replace(R.id.containerFrame, taskFragment);
                                                        transaction.commit();
                                                        popupWindow.dismiss();
                                                    }
                                                })
                                                .create();
                                        alertDialog.show();
                                    } else {
                                        String reason = result.getString("reason");
                                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                                .setTitle(R.string.error)
                                                .setMessage("Die Aufgabe konnte nicht editiert werden!\n"
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
                                info.setText(getResources().getString(R.string.error_deadline));

                            }
                        } else {
                            info.setText(getResources().getString(R.string.error_fields_filled_wrong));
                        }
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.error)
                                .setMessage(getResources().getString(R.string.no_rights))
                                .setNegativeButton("OK", null)
                                .create();
                        alertDialog.show();
                    }
                } else {
                    AlertDialog alertDialog = CheckInternet.internetNotAvailable(getActivity());
                    alertDialog.show();
                }
            }
        });
    }

    private boolean validateDeadline(String deadline) {
        boolean result = false;
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        Calendar currentDate = Calendar.getInstance();
        Calendar chosenDeadline = Calendar.getInstance();
        try {
            chosenDeadline.setTime(formatter.parse(deadline));
            if (chosenDeadline.getTime().after(currentDate.getTime())) {
                result = true;
            }
        } catch (ParseException e) {
            // Won't be reached
            e.printStackTrace();
        }
        return result;
    }

    private boolean validateInput(String taskDescription, String deadline) {
        boolean result = false;
        if (taskDescription != null && !taskDescription.equals("") && deadline != null
                && !deadline.equals("")) {
            result = true;
        }
        return result;
    }

    private void setUpTextWatcherTaskDescription(final EditText taskDescription) {
        taskDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setTaskDescription(taskDescription.getText().toString());
            }
        });
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    private void setUpDatePicker(DatePicker taskDeadline, String fetchedDeadline) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        final Calendar usersBirthday = Calendar.getInstance();
        try {
            usersBirthday.setTime(formatter.parse(deadline));
            taskDeadline.init(usersBirthday.get(Calendar.YEAR), usersBirthday.get(Calendar.MONTH),
                    usersBirthday.get(Calendar.DAY_OF_MONTH),
                    new DatePicker.OnDateChangedListener() {
                        @Override
                        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            String mDay;
                            String mMonth;
                            String mYear;
                            if (dayOfMonth < 10) {
                                mDay = "0" + dayOfMonth;
                            } else {
                                mDay = "" + dayOfMonth;
                            }
                            if (monthOfYear < 9) {
                                mMonth = "0" + (monthOfYear + 1);
                            } else {
                                mMonth = "" + (monthOfYear + 1);
                            }
                            mYear = "" + year;
                            // The 00:00:00 is for the formatter on server side
                            deadline = mDay + "." + mMonth + "." + mYear
                                    + " 00:00:00";
                        }
                    });
        } catch (ParseException e) {
            // TODO
            e.printStackTrace();
        }
    }

    private List<String> getMyTasks() {
        List<String> myTasks = new ArrayList<>();
        if (CheckInternet.isNetworkAvailable(getContext())) {
            GetMyTasksTask getMyTasksTask = new GetMyTasksTask();
            String[] params = new String[]{MainActivity.URL + "user/tasks",
                    MainActivity.userData.getToken(), MainActivity.userData.getUsername()};
            try {
                JSONObject result = getMyTasksTask.execute(params).get();
                String success = result.getString("success");
                if (success.equals("true")) {
                    JSONArray tasks = result.getJSONArray("tasks");
                    for (int i = 0; i < tasks.length(); i++) {
                        String task = tasks.getString(i);
                        myTasks.add(task);
                    }
                } else {
                    String reason = result.getString("reason");
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.error)
                            .setMessage("Ihre Aufgaben konnten nicht geladen werden!\n" + reason)
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
        return myTasks;
    }
}
