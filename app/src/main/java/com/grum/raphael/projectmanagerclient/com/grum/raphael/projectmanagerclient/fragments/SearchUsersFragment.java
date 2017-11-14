package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;
import com.grum.raphael.projectmanagerclient.tasks.CheckInternet;
import com.grum.raphael.projectmanagerclient.tasks.GetUsersTask;
import com.grum.raphael.projectmanagerclient.tasks.InviteUserTask;
import com.grum.raphael.projectmanagerclient.tasks.UserTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;


public class SearchUsersFragment extends Fragment {

    private ListView list;
    private ArrayAdapter<String> arrayAdapter;
    private EditText search;
    private String[] userData;

    public SearchUsersFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_search_users, container, false);
        list = (ListView) rootView.findViewById(R.id.list_users);
        search = (EditText) rootView.findViewById(R.id.filter_users);
        if (CheckInternet.isNetworkAvailable(getContext())) {
            GetUsersTask getUsersTask = new GetUsersTask();
            try {
                final JSONObject fetchedData
                        = getUsersTask.execute(new String[]{MainActivity.URL + "users"}).get();
                String success = fetchedData.getString("success");
                if (success.equals("true")) {
                    String fetchedUsers = fetchedData.getString("users");
                    fetchedUsers = fetchedUsers.substring(1, fetchedUsers.length() - 1);
                    userData = fetchedUsers.split(",");
                    userData = removeOwnUsername(userData);
                    if (userData.length != 0) {
                        // Fill list with items
                        arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item,
                                R.id.list_element, userData);
                        arrayAdapter.notifyDataSetChanged();
                        list.setAdapter(arrayAdapter);
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String username = (String) list.getAdapter().getItem(position);
                                String url = MainActivity.URL + "user";
                                String token = MainActivity.userData.getToken();
                                String[] params = new String[]{url, username, token};
                                UserTask userTask = new UserTask();
                                try {
                                    JSONObject fetchedUserData = userTask.execute(params).get();
                                    dealWithResponse(fetchedUserData, rootView);
                                } catch (InterruptedException e) {
                                    // TODO
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    // TODO
                                    e.printStackTrace();
                                }
                            }
                        });
                        search.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                arrayAdapter.getFilter().filter(s);
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                    } else {
                        // TODO
                    }
                } else {
                    // TODO
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
        return rootView;
    }

    private String[] removeOwnUsername(String[] userData) {
        if (userData.length != 0) {
            ArrayList<String> usernames = new ArrayList<>();
            for (String username : userData) {
                if (!username.equals(MainActivity.userData.getUsername())) {
                    usernames.add(username);
                }
            }
            userData = new String[usernames.size()];
            userData = usernames.toArray(userData);
        }
        return userData;
    }

    private void dealWithResponse(JSONObject data, View anchorView) {
        try {
            String success = data.getString("success");
            if (success.equals("true")) {
                JSONObject user = new JSONObject(data.getString("user"));
                HashMap<String, String> userData = getUserData(user);
                View view = this.getLayoutInflater(null).inflate(R.layout.fragment_popup_user, null);
                setValuesToPopupWindow(userData, view);
                final PopupWindow popupWindow = new PopupWindow(view,
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setWidth((int) (anchorView.getWidth() * 0.9));
                popupWindow.setHeight((int) (anchorView.getHeight() * 0.9));
                popupWindow.setBackgroundDrawable(new BitmapDrawable(getContext().getResources(),
                        (Bitmap) null));
                // Set values for popup
                Button closeBtn = (Button) view.findViewById(R.id.close_btn_user_popup);
                Button inviteUser = (Button) view.findViewById(R.id.btn_invite_user);
                final TextView username = (TextView) view.findViewById(R.id.popup_username);
                if (MainActivity.userData.getUserRole().equals(MainActivity.ADMIN)) {
                    inviteUser.setVisibility(View.VISIBLE);
                    inviteUser.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            inviteUser(username);
                        }
                    });
                } else {
                    inviteUser.setVisibility(View.INVISIBLE);
                }
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
            } else {
                // TODO
            }
        } catch (JSONException e) {
            // TODO
            e.printStackTrace();
        }
    }

    private void inviteUser(TextView username) {
        String usernameToInvite = username.getText().toString();
        String token = MainActivity.userData.getToken();
        String teamName = MainActivity.userData.getTeamName();
        String url = MainActivity.URL + "invite";
        if (CheckInternet.isNetworkAvailable(getContext())) {
            if (teamName != null && !(teamName.equals(""))) {
                String[] params = new String[]{url, token, usernameToInvite, teamName};
                InviteUserTask inviteUserTask = new InviteUserTask();
                try {
                    JSONObject response = inviteUserTask.execute(params).get();
                    String success = response.getString("success");
                    if (success.equals("true")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.success)
                                .setMessage("Sie haben den User " + usernameToInvite + " erfolgreich " +
                                        "eingeladen.")
                                .setNegativeButton("OK", null)
                                .create();
                        alertDialog.show();
                    } else {
                        String reason = response.getString("reason");
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.error)
                                .setMessage("Der User " + usernameToInvite + " konnte nicht eingeladen "
                                        + "werden. " + reason)
                                .setNegativeButton("Ok", null)
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
                        .setMessage("Sie sind in keinem Team! Sie können nur als Team-Administrator " +
                                "andere User zu Ihrem Team einladen!")
                        .setNegativeButton("Ok", null)
                        .create();
                alertDialog.show();
            }
        } else {
            AlertDialog alertDialog = CheckInternet.internetNotAvailable(getActivity());
            alertDialog.show();
        }
    }

    private HashMap<String, String> getUserData(JSONObject userData) {
        HashMap<String, String> result = new HashMap<>();
        try {
            result.put("firstName", userData.getString("firstName"));
            result.put("surname", userData.getString("surname"));
            result.put("birthday", userData.getString("birthday"));
            result.put("address", userData.getString("address"));
            result.put("email", userData.getString("email"));
            result.put("phoneNr", userData.getString("phoneNr"));
            result.put("tributes", userData.getString("tributes"));
            result.put("dayOfEntry", userData.getString("dayOfEntry"));
            result.put("register", userData.getString("registerName"));
            result.put("team", userData.getString("team"));
            result.put("username", userData.getString("username"));
        } catch (JSONException e) {
            result = null;
        }
        return result;
    }

    private void setValuesToPopupWindow(HashMap<String, String> values, View view) {
        // Fetch all items within the PopupWindow
        TextView firstName = (TextView) view.findViewById(R.id.popup_firstName);
        TextView surname = (TextView) view.findViewById(R.id.popup_surname);
        TextView email = (TextView) view.findViewById(R.id.popup_email);
        TextView address = (TextView) view.findViewById(R.id.popup_address);
        TextView birthday = (TextView) view.findViewById(R.id.popup_birthday);
        TextView phoneNr = (TextView) view.findViewById(R.id.popup_phoneNr);
        TextView team = (TextView) view.findViewById(R.id.popup_team);
        TextView username = (TextView) view.findViewById(R.id.popup_username);
        TextView dayOfEntry = (TextView) view.findViewById(R.id.popup_dayOfEntry);
        TextView tributes = (TextView) view.findViewById(R.id.popup_tributes);
        // TODO btn to invite users to your team

        // Get all values from HashMap
        String firstNameText = values.get("firstName");
        String surnameText = values.get("surname");
        String emailText = values.get("email");
        String addressText = values.get("address");
        String birthdayText = values.get("birthday");
        String phoneNrText = values.get("phoneNr");
        String teamText = values.get("team");
        String usernameText = values.get("username");
        String dayOfEntryText = values.get("dayOfEntry");
        String tributesText = values.get("tributes");

        // Set values to the items
        firstName.setText(firstNameText);
        surname.setText(surnameText);
        email.setText(emailText);
        address.setText(addressText);
        birthday.setText(birthdayText);
        phoneNr.setText(phoneNrText);
        username.setText(usernameText);
        if (teamText.equals("null")) {
            team.setText(getResources().getString(R.string.blank));
        } else {
            team.setText(teamText);
        }
        if (dayOfEntryText.equals("null")) {
            dayOfEntry.setText(getResources().getString(R.string.blank));
        } else {
            dayOfEntry.setText(dayOfEntryText);
        }
        if (tributesText.equals("null")) {
            tributes.setText(getResources().getString(R.string.blank));
        } else {
            tributes.setText(tributesText);
        }
    }
}
