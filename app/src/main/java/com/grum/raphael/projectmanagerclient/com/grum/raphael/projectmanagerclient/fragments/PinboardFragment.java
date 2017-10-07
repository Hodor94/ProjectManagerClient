package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.TextView;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;
import com.grum.raphael.projectmanagerclient.tasks.AnswerInvitationOrRequestTask;
import com.grum.raphael.projectmanagerclient.tasks.GetMyInvitationsTask;
import com.grum.raphael.projectmanagerclient.tasks.GetTeamRequests;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class PinboardFragment extends Fragment {

    private ListView invitationsList;
    private String[] invitations;
    private TextView headerInvitations;
    private TextView noInvitations;
    private ListView requestsList;
    private String[] requests;
    private TextView headerRequests;
    private TextView noRequests;

    public PinboardFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_pinboard, container, false);
        headerInvitations = (TextView) rootView.findViewById(R.id.header_invitations);
        invitationsList = (ListView) rootView.findViewById(R.id.invitations_list);
        noInvitations = (TextView) rootView.findViewById(R.id.pinnboard_no_invitations);
        noRequests = (TextView) rootView.findViewById(R.id.pinnboard_no_requests);
        if (MainActivity.userData.getTeamName().equals("null")) {
            invitations = getInvitations();
            if (invitations != null) {
                // TODO set on Click Listener
                noInvitations.setVisibility(View.GONE);
                ArrayAdapter arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item,
                        R.id.list_element, invitations);
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
                        R.id.list_element, requests);
                requestsList.setAdapter(arrayAdapter);
                requestsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String userName = requests[0];
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
        return rootView;
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
        if (result != null && result[0].equals("[]")) {
            result = null;
        }
        return result;
    }

    private String[] getInvitations() {
        String[] result;
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
        if (result != null && result[0].equals("[]")) {
            result = null;
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
                answerInvitationOrRequest(disagree, teamName, popupWindow, invitationOrRequest);
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
                    if (type.equals(getResources().getString(R.string.request)) ) {
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
    }
}
