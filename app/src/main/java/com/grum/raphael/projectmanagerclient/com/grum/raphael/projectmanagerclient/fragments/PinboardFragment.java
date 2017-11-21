package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;
import com.grum.raphael.projectmanagerclient.tasks.AnswerInvitationOrRequestTask;
import com.grum.raphael.projectmanagerclient.tasks.CheckInternet;
import com.grum.raphael.projectmanagerclient.tasks.GetMyInvitationsTask;
import com.grum.raphael.projectmanagerclient.tasks.GetTeamRequests;
import com.grum.raphael.projectmanagerclient.tasks.NewsflashTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.grum.raphael.projectmanagerclient.R.id.list_element;

public class PinboardFragment extends Fragment {

    private final String CAT_PROJECT = "Projekt Deadline";
    private final String CAT_TASK = "Aufgabe Deadline";
    private final String CAT_APPOINTMENT = "Meeting";
    private final String CAT_BIRTHDAY = "Geburtstag";
    private ListView invitationsList;
    private String[] invitations;
    private TextView headerInvitations;
    private TextView noInvitations;
    private ListView requestsList;
    private String[] requests;
    private TextView headerRequests;
    private TextView noRequests;
    private TableLayout newsflashTable;
    private JSONArray projects;
    private JSONArray tasks;
    private JSONArray appointments;
    private JSONArray birthdays;
    private SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy dd:mm:ss");

    public PinboardFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_pinboard, container, false);
        newsflashTable = (TableLayout) rootView.findViewById(R.id.newsflash);
        headerInvitations = (TextView) rootView.findViewById(R.id.header_invitations);
        invitationsList = (ListView) rootView.findViewById(R.id.invitations_list);
        noInvitations = (TextView) rootView.findViewById(R.id.pinnboard_no_invitations);
        noRequests = (TextView) rootView.findViewById(R.id.pinnboard_no_requests);
        if (MainActivity.userData.getTeamName().equals("null")) {
            invitations = getInvitations();
            if (invitations != null) {
                noInvitations.setVisibility(View.GONE);
                ArrayAdapter arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item,
                        list_element, invitations);
                invitationsList.setAdapter(arrayAdapter);
                invitationsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String teamName = invitations[position];
                        openPopup(teamName, rootView);
                    }
                });
            } else {
                invitationsList.setVisibility(View.GONE);
            }
        } else {
            noInvitations.setVisibility(View.GONE);
            headerInvitations.setVisibility(View.GONE);
            invitationsList.setVisibility(View.GONE);
        }
        headerRequests = (TextView) rootView.findViewById(R.id.header_requests);
        requestsList = (ListView) rootView.findViewById(R.id.requests_list);
        if (MainActivity.userData.getUserRole().equals(MainActivity.ADMIN)) {
            requests = getRequests();
            if (requests != null) {
                noRequests.setVisibility(View.GONE);
                ArrayAdapter arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item,
                        list_element, requests);
                requestsList.setAdapter(arrayAdapter);
                requestsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String userName = requests[position];
                        openPopup(userName, rootView);
                    }
                });
            } else {
                requestsList.setVisibility(View.GONE);
            }
        } else {
            noRequests.setVisibility(View.GONE);
            headerRequests.setVisibility(View.GONE);
            requestsList.setVisibility(View.GONE);
        }
        getNewsflashData();
        setUpTable();
        return rootView;
    }

    private void setUpTable() {
        TableRow.LayoutParams layoutParamsLeftElement
                = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsLeftElement.setMargins(0, 0, 10, 0);
        TableRow.LayoutParams layoutParamsRightElement
                = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsRightElement.setMargins(10, 0, 0, 0);
        TableRow.LayoutParams layoutParamsMidElement
                = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsMidElement.setMargins(5, 0 , 5, 0);
        setUpHeaderRow(layoutParamsLeftElement, layoutParamsMidElement, layoutParamsRightElement);
        ArrayList<JSONObject> projectsList = editProjects();
        ArrayList<JSONObject> tasksList = editTasks();
        ArrayList<JSONObject> appointmentsList = editAppointments();
        ArrayList<JSONObject> birthdaysList = editBirthdays();
        addToTable(projectsList, "project", layoutParamsLeftElement, layoutParamsMidElement,
                layoutParamsRightElement);
        addToTable(tasksList, "task", layoutParamsLeftElement, layoutParamsMidElement,
                layoutParamsRightElement);
        addToTable(appointmentsList, "appointment", layoutParamsLeftElement, layoutParamsMidElement,
                layoutParamsRightElement);
        addToTable(birthdaysList, "user", layoutParamsLeftElement, layoutParamsMidElement,
                layoutParamsRightElement);
    }

    private void addToTable(ArrayList<JSONObject> list, String objectName,
                            TableRow.LayoutParams paramsLeft,
                            TableRow.LayoutParams paramsMid,
                            TableRow.LayoutParams paramsRight) {
        View separatorHorizontal = new View(getActivity());
        separatorHorizontal.setLayoutParams(
                new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
        separatorHorizontal.setBackgroundColor(Color.rgb(51, 51, 51));
        try {
            for (JSONObject jsonObject : list) {
                String name = jsonObject.getString(objectName);
                String time = jsonObject.getString("deadline").split(" ")[1];
                String category = jsonObject.getString("category");
                TableRow row = new TableRow(getContext());
                TextView textName = new TextView(getContext());
                textName.setTextColor(getResources().getColor(R.color.text));
                textName.setTextSize(MainActivity.DP_TEXT_SIZE);
                textName.setLayoutParams(paramsLeft);
                textName.setText(name);
                TextView textTime = new TextView(getContext());
                textTime.setTextColor(getResources().getColor(R.color.text));
                textTime.setTextSize(MainActivity.DP_TEXT_SIZE);
                textTime.setLayoutParams(paramsMid);
                textTime.setText(time);
                TextView textCategory = new TextView(getContext());
                textCategory.setTextColor(getResources().getColor(R.color.text));
                textCategory.setTextSize(MainActivity.DP_TEXT_SIZE);
                textCategory.setLayoutParams(paramsRight);
                textCategory.setText(category);
                row.addView(textName);
                row.addView(textTime);
                row.addView(textCategory);
                row.setGravity(Gravity.CENTER);
                newsflashTable.addView(row);
                newsflashTable.addView(separatorHorizontal);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<JSONObject> editBirthdays() {
        ArrayList<JSONObject> result = new ArrayList<>();
        try {
            for (int i = 0; i < birthdays.length(); i++) {
                JSONObject temp = birthdays.getJSONObject(i);
                temp.put("category", CAT_BIRTHDAY);
                result.add(temp);
            }
        } catch (JSONException e) {
            AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                    .setTitle(R.string.error)
                    .setMessage("Die Geburtstage konnten nicht verarbeitet werden!\n " +
                            "Fehlerhafte Daten wurden geschickt!")
                    .setNegativeButton("OK", null)
                    .create();
            alertDialog.show();
        }
        return result;
    }

    private ArrayList<JSONObject> editAppointments() {
        ArrayList<JSONObject> result = new ArrayList<>();
        try {
            for (int i = 0; i < appointments.length(); i++) {
                JSONObject temp = appointments.getJSONObject(i);
                temp.put("category", CAT_APPOINTMENT);
                result.add(temp);
            }
        } catch (JSONException e) {
            AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                    .setTitle(R.string.error)
                    .setMessage("Die Meetings konnten nicht verarbeitet werden!\n " +
                            "Fehlerhafte Daten wurden geschickt!")
                    .setNegativeButton("OK", null)
                    .create();
            alertDialog.show();
        }
        return result;
    }

    private ArrayList<JSONObject> editTasks() {
        ArrayList<JSONObject> result = new ArrayList<>();
        try {
            for (int i = 0; i < tasks.length(); i++) {
                JSONObject temp = tasks.getJSONObject(i);
                temp.put("category", CAT_TASK);
                result.add(temp);
            }
        } catch (JSONException e) {
            AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                    .setTitle(R.string.error)
                    .setMessage("Die Aufgaben konnten nicht verarbeitet werden!\n " +
                            "Fehlerhafte Daten wurden geschickt!")
                    .setNegativeButton("OK", null)
                    .create();
            alertDialog.show();
        }
        return result;
    }

    private ArrayList<JSONObject> editProjects() {
        ArrayList<JSONObject> result = new ArrayList<>();
        try {
            for (int i = 0; i < projects.length(); i++) {
                JSONObject temp = projects.getJSONObject(i);
                temp.put("category", CAT_PROJECT);
                result.add(temp);
            }
        } catch (JSONException e) {
            AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                    .setTitle(R.string.error)
                    .setMessage("Die Projekte konnten nicht verarbeitet werden!\n" +
                            "Fehlerhafte Daten wurden geschickt!")
                    .setNegativeButton("OK", null)
                    .create();
            alertDialog.show();
        }
        return result;
    }

    private void setUpHeaderRow(TableRow.LayoutParams paramsLeft,
                                TableRow.LayoutParams paramsMid,
                                TableRow.LayoutParams paramsRight) {
        TableRow headerRow = new TableRow(getContext());
        TextView headerName = new TextView(getContext());
        headerName.setTextColor(getResources().getColor(R.color.text));
        headerName.setTextSize(MainActivity.DP_TEXT_SIZE);
        headerName.setLayoutParams(paramsLeft);
        headerName.setText(getResources().getString(R.string.label_name));
        TextView headerTime = new TextView(getContext());
        headerTime.setTextColor(getResources().getColor(R.color.text));
        headerTime.setTextSize(MainActivity.DP_TEXT_SIZE);
        headerTime.setLayoutParams(paramsMid);
        headerTime.setText(getResources().getString(R.string.label_time));
        TextView headerCategories = new TextView(getContext());
        headerCategories.setTextColor(getResources().getColor(R.color.text));
        headerCategories.setTextSize(MainActivity.DP_TEXT_SIZE);
        headerCategories.setLayoutParams(paramsRight);
        headerCategories.setText(getResources().getString(R.string.label_category));
        headerRow.addView(headerName);
        headerRow.addView(headerTime);
        headerRow.addView(headerCategories);
        headerRow.setGravity(Gravity.CENTER);
        newsflashTable.addView(headerRow);
    }

    private String[] getRequests() {
        String[] result;
        GetTeamRequests getTeamRequests = new GetTeamRequests();
        String[] params = new String[]{MainActivity.URL + "requests",
                MainActivity.userData.getToken(), MainActivity.userData.getTeamName(),
                MainActivity.userData.getUsername()};
        try {
            JSONObject response = getTeamRequests.execute(params).get();
            String success = response.getString("success");
            if (success.equals("true")) {
                String requests = response.getString("requests");
                result = requests.split(",");
            } else {
                result = null;
            }
        } catch (InterruptedException e) {
            // TODO
            result = null;
        } catch (ExecutionException e) {
            // TODO
            result = null;
        } catch (JSONException e) {
            // TODO
            result = null;
            e.printStackTrace();
        }
        if (result != null && result[0].equals("null")) {
            result = null;
        }
        return result;
    }

    private String[] getInvitations() {
        String[] result = null;
        if (CheckInternet.isNetworkAvailable(getContext())) {
            GetMyInvitationsTask getMyInvitationsTask = new GetMyInvitationsTask();
            String[] params = new String[]{MainActivity.URL + "invitations",
                    MainActivity.userData.getToken(), MainActivity.userData.getUsername()};
            try {
                JSONObject response = getMyInvitationsTask.execute(params).get();
                String success = response.getString("success");
                if (success.equals("true")) {
                    String invitations = response.getString("invitations");
                    result = invitations.split(",");
                } else {
                    result = null;
                }
            } catch (InterruptedException e) {
                // TODO
                e.printStackTrace();
                result = null;
            } catch (ExecutionException e) {
                // TODO
                e.printStackTrace();
                result = null;
            } catch (JSONException e) {
                // TODO
                result = null;
                e.printStackTrace();
            }
            if (result != null && result[0].equals("null")) {
                result = null;
            }
        } else {
            AlertDialog alertDialog = CheckInternet.internetNotAvailable(getActivity());
            alertDialog.show();
        }
        return result;
    }

    private void openPopup(final String text, View anchorView) {
        String teamName = text;
        if (MainActivity.userData.getUserRole().equals(MainActivity.ADMIN)) {
            teamName = MainActivity.userData.getTeamName();
        }
        // Set up popup window
        final View popupView = this.getLayoutInflater(null)
                .inflate(R.layout.fragment_popup_agree, null);
        final PopupWindow popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth((int) (anchorView.getWidth() * 0.9));
        popupWindow.setHeight((int) (anchorView.getHeight() * 0.2));
        popupWindow.setBackgroundDrawable(new BitmapDrawable(getContext().getResources(),
                (Bitmap) null));
        final TextView teamTextField = (TextView) popupView.findViewById(R.id.popup_agree_text_info);
        Button closeBtn = (Button) popupView.findViewById(R.id.close_btn_popup_agree);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        ImageView agree = (ImageView) popupView.findViewById(R.id.agree);
        final String finalTeamName = teamName;
        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String agree = getResources().getString(R.string.agree);
                String invitationOrRequest;
                if (invitationsList.getVisibility() == View.VISIBLE) {
                    invitationOrRequest = getResources().getString(R.string.invitation);
                } else {
                    invitationOrRequest = getResources().getString(R.string.request);
                }
                if (MainActivity.userData.getUserRole().equals(MainActivity.ADMIN)) {
                    answerInvitationOrRequest(agree, finalTeamName, popupWindow, invitationOrRequest);
                } else {
                    answerInvitationOrRequest(agree, text, popupWindow, invitationOrRequest);
                }
            }
        });
        ImageView disagree = (ImageView) popupView.findViewById(R.id.disagree);
        disagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String disagree = getResources().getString(R.string.disagree);
                String teamName = teamTextField.getText().toString();
                String invitationOrRequest;
                if (invitationsList.getVisibility() == View.VISIBLE) {
                    invitationOrRequest = getResources().getString(R.string.invitation);
                } else {
                    invitationOrRequest = getResources().getString(R.string.request);
                }
                if (MainActivity.userData.getUserRole().equals(MainActivity.ADMIN)) {
                    answerInvitationOrRequest(
                            disagree, finalTeamName, popupWindow, invitationOrRequest);
                } else {
                    answerInvitationOrRequest(disagree, text, popupWindow, invitationOrRequest);
                }
            }
        });
        TextView info = (TextView) popupView.findViewById(R.id.popup_agree_text_info);
        info.setText(text);

        // Show popup
        int[] location = new int[2];
        anchorView.getLocationOnScreen(location);
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);
    }

    private void answerInvitationOrRequest(String agreeOrDisagree, String teamName,
                                           final PopupWindow popupWindow, String invitationOrRequest) {
        String[] params;
        if (CheckInternet.isNetworkAvailable(getContext())) {
            if (invitationOrRequest.equals(getResources().getString(R.string.invitation))) {
                params = new String[]{MainActivity.URL + "answer",
                        MainActivity.userData.getToken(), MainActivity.userData.getUsername(),
                        teamName, agreeOrDisagree, invitationOrRequest};
            } else {
                TextView textView = (TextView) popupWindow.getContentView()
                        .findViewById(R.id.popup_agree_text_info);
                String usernameOfRequest = textView.getText().toString();
                params = new String[]{MainActivity.URL + "answer", MainActivity.userData.getToken(),
                        usernameOfRequest, teamName, agreeOrDisagree, invitationOrRequest};
            }
            AnswerInvitationOrRequestTask answerInvitationTask = new AnswerInvitationOrRequestTask();
            try {
                JSONObject response = answerInvitationTask.execute(params).get();
                String success = response.getString("success");
                if (success.equals("true")) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.success)
                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    PinboardFragment newFragment = new PinboardFragment();
                                    fragmentTransaction.replace(R.id.containerFrame, newFragment);
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();
                                    popupWindow.dismiss();
                                }
                            })
                            .create();
                    String type = response.getString("type");
                    if (agreeOrDisagree.equals(getResources().getString(R.string.agree))) {
                        MainActivity.userData.setTeamName(teamName);
                        if (type.equals(getResources().getString(R.string.request))) {
                            alertDialog.setMessage("Der User wurde erfolgreich zum Team hinzugefügt.");
                        } else {
                            alertDialog.setMessage("Sie sind dem Team erfolgreich beigetreten!");
                        }
                    } else {
                        if (type.equals(getResources().getString(R.string.request))) {
                            alertDialog.setMessage("Die Anfrage wurde erfolgreich abgelehnt.");
                        } else {
                            alertDialog.setMessage("Sie haben die Einladung erfolreich abgelehnt!");
                        }
                    }
                    alertDialog.show();
                } else {
                    String reason = response.getString("reason");
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.error)
                            .setMessage("Die Funktion konnte nicht ausgeführt werden!\n" +
                                    reason)
                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FragmentTransaction transaction
                                            = getFragmentManager().beginTransaction();
                                    transaction.replace(R.id.containerFrame, new PinboardFragment());
                                    transaction.commit();
                                }
                            })
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

    private void getNewsflashData() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        Calendar calendar = Calendar.getInstance();
        String currentDate = formatter.format(calendar.getTime());
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        String mondayOfWeek = formatter.format(calendar.getTime());
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        String sundayOfWeek = formatter.format(calendar.getTime());
        if (CheckInternet.isNetworkAvailable(getContext())) {
            String[] params = new String[]{MainActivity.URL + "newsflash",
                    MainActivity.userData.getToken(), MainActivity.userData.getTeamName(),
                    MainActivity.userData.getUsername(), currentDate, mondayOfWeek, sundayOfWeek};
            NewsflashTask newsflashTask = new NewsflashTask();
            try {
                JSONObject result = newsflashTask.execute(params).get();
                String success = result.getString("success");
                if (success.equals("true")) {
                    JSONObject relevantDates = result.getJSONObject("dates");
                    projects = relevantDates.getJSONArray("projects");
                    appointments = relevantDates.getJSONArray("appointments");
                    tasks = relevantDates.getJSONArray("tasks");
                    birthdays = relevantDates.getJSONArray("birthdays");
                } else {
                    String reason = result.getString("reason");
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                            .setTitle(R.string.error)
                            .setMessage("Die anstehenden Ereignisse für die Woche konnten nicht " +
                                    "geladen werden!\n" + reason)
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
}
