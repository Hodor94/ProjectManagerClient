package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;
import com.grum.raphael.projectmanagerclient.tasks.CheckInternet;
import com.grum.raphael.projectmanagerclient.tasks.GetTeamMembersTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class TeamMembersFragment extends Fragment {

    private TableLayout table;
    private Spinner dropDownFilter;
    private ArrayList<JSONObject> members;
    private final Comparator<String> ALPHABETICAL_ORDER = setUpComparator();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_team_members, container, false);
        members = new ArrayList<>();
        table = (TableLayout) rootView.findViewById(R.id.team_memebers_table);
        dropDownFilter = (Spinner) rootView.findViewById(R.id.dropdown_sort_table);
        String[] dropDownElements = getResources().getStringArray(R.array.sorting_table);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(),
                R.layout.drop_down, R.id.drop_down_element, dropDownElements);
        dropDownFilter.setAdapter(arrayAdapter);
        dropDownFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortTable(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (CheckInternet.isNetworkAvailable(getContext())) {
            GetTeamMembersTask getTeamMembersTask = new GetTeamMembersTask();
            String[] params = new String[]{MainActivity.URL + "team/members",
                    MainActivity.userData.getToken(), MainActivity.userData.getTeamName()};
            try {
                JSONObject result = getTeamMembersTask.execute(params).get();
                String success = result.getString("success");
                if (success.equals("true")) {
                    JSONArray fetchedMembers = result.getJSONArray("members");
                    members = extractMembers(fetchedMembers);
                    setUpTable(members);
                } else {
                    String reason = result.getString("reason");
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.error)
                            .setMessage("Die Teammitglieder konnten nicht abgefragt werden!\n" + reason)
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
                e.printStackTrace();
            }
        } else {
            AlertDialog alertDialog = CheckInternet.internetNotAvailable(getActivity());
            alertDialog.show();
        }
        return rootView;
    }

    private void setUpTable(ArrayList<JSONObject> members) throws JSONException {
        TableRow.LayoutParams layoutParamsLeftElement
                = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsLeftElement.setMargins(0, 10, 5, 0);
        TableRow.LayoutParams layoutParamsRightElement
                = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsRightElement.setMargins(5, 10 , 0, 0);
        addHeaderToTable();
        for (int i = 0; i < members.size(); i++) {
            View separatorHorizontal = new View(getActivity());
            separatorHorizontal.setLayoutParams(
                    new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
            separatorHorizontal.setBackgroundColor(Color.rgb(51, 51, 51));
            JSONObject member = members.get(i);
            String tempUsername = member.getString("username");
            String tempRegister = member.getString("register");
            String color = member.getString("color");
            final TextView usernameText = new TextView(getContext());
            final TextView registerText = new TextView(getContext());
            usernameText.setBackgroundColor(Integer.parseInt(color));
            registerText.setBackgroundColor(Integer.parseInt(color));
            usernameText.setText(tempUsername);
            if (!tempRegister.equals("null")) {
                registerText.setText(tempRegister);
            } else {
                registerText.setText(R.string.blank);
            }
            usernameText.setGravity(Gravity.CENTER);
            registerText.setGravity(Gravity.CENTER);
            registerText.setTextSize(MainActivity.DP_TEXT_SIZE);
            usernameText.setTextSize(MainActivity.DP_TEXT_SIZE);
            usernameText.setTextColor(Color.BLACK);
            registerText.setTextColor(Color.BLACK);
            usernameText.setLayoutParams(layoutParamsLeftElement);
            registerText.setLayoutParams(layoutParamsRightElement);
            TableRow tableRow = new TableRow(getContext());
            tableRow.addView(usernameText);
            tableRow.addView(registerText);
            tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectTableRow(usernameText.getText().toString(),
                            registerText.getText().toString());
                }
            });

            table.addView(tableRow);
            if (i != members.size() - 1) {
                table.addView(separatorHorizontal);
            }
        }
    }

    private void addHeaderToTable() {
        TableRow header = new TableRow(getContext());
        TextView usernameHeader = new TextView(getContext());
        TextView registerHeader = new TextView(getContext());
        usernameHeader.setGravity(Gravity.CENTER);
        registerHeader.setGravity(Gravity.CENTER);
        usernameHeader.setText(R.string.label_username);
        registerHeader.setText(R.string.label_registers);
        usernameHeader.setTextSize(MainActivity.DP_TEXT_SIZE);
        registerHeader.setTextSize(MainActivity.DP_TEXT_SIZE);
        usernameHeader.setTextColor(Color.BLACK);
        registerHeader.setTextColor(Color.BLACK);
        header.addView(usernameHeader);
        header.addView(registerHeader);
        table.addView(header);
    }

    private ArrayList<JSONObject> extractMembers(JSONArray fetchedMembers) throws JSONException {
        ArrayList<JSONObject> result = new ArrayList<>();
        for (int i = 0; i < fetchedMembers.length(); i++) {
            result.add(fetchedMembers.getJSONObject(i));
        }
        return result;
    }

    private void sortTable(int position) {
        try {
            if (members != null && members.size() != 0) {
                if (position == 0 || position == 1) {
                    sortTableDueToUsername(position);
                } else if (position == 2 || position == 3) {
                    sortTableDueToRegsiter(position);
                }
            }
        } catch (JSONException e) {
            // TODO
        }
    }

    private void sortTableDueToUsername(int position) throws JSONException {
        List<String> usernames = new ArrayList<>();
        for (JSONObject jsonObject : members) {
            usernames.add(jsonObject.getString("username"));
        }
        Collections.sort(usernames, ALPHABETICAL_ORDER);
        for (int i = 0; i < usernames.size(); i++) {
            String username = usernames.get(i);
            for (JSONObject jsonObject : members) {
                if (jsonObject.getString("username").equals(username)) {
                    members.remove(jsonObject);
                    members.add(i, jsonObject);
                    break;
                }
            }
        }
        if (position == 1) {
            Collections.reverse(members);
        }
        table.removeAllViews();
        setUpTable(members);
    }

    private void sortTableDueToRegsiter(int position) throws JSONException {
        List<String> registers = new ArrayList<String>();
        for (JSONObject jsonObject : members) {
            registers.add(jsonObject.getString("register"));
        }
        Collections.sort(registers, ALPHABETICAL_ORDER);
        for (int i = 0; i < registers.size(); i++) {
            String register = registers.get(i);
            for (JSONObject jsonObject : members) {
                if (jsonObject.getString("register").equals(register)) {
                    members.remove(jsonObject);
                    members.add(i, jsonObject);
                    break;
                }
            }
        }
        if (position == 3) {
            Collections.reverse(members);
        }
        table.removeAllViews();;
        setUpTable(members);
    }

    private Comparator<String> setUpComparator() {
        return new Comparator<String>() {
            public int compare(String str1, String str2) {
                int res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
                if (res == 0) {
                    res = str1.compareTo(str2);
                }
                return res;
            }
        };
    }

    private void selectTableRow(String username, String registerName) {
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("register", registerName);
        EditTeamMemberFragment newFragment = new EditTeamMemberFragment();
        newFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.pager_team_profile, newFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
