package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;
import com.grum.raphael.projectmanagerclient.tasks.CheckInternet;
import com.grum.raphael.projectmanagerclient.tasks.CreateChatTask;
import com.grum.raphael.projectmanagerclient.tasks.GetTeamMembersTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

public class CreateChatFragment extends Fragment {

    private ListView users;
    private EditText setChatName;
    private ArrayList<JSONObject> existingChats;
    private ArrayList<String> teamMembers;
    private ArrayList<String> usersOfChat;
    private String chatName;
    private Button createChat;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_chat, container, false);
        Bundle bundle = getArguments();
        existingChats = (ArrayList<JSONObject>) bundle.getSerializable("chats");
        usersOfChat = new ArrayList<>();
        usersOfChat.add(MainActivity.userData.getUsername());
        teamMembers = getTeamMembers();
        setChatName = (EditText) rootView.findViewById(R.id.set_chat_name);
        setChatName.setFilters(new InputFilter[] {MainActivity.EMOJI_FILTER});
        setChatName.addTextChangedListener(setChatName());
        users = (ListView) rootView.findViewById(R.id.choose_chat_team_members);
        users.setOnItemClickListener(new CheckBoxClick());
        ArrayAdapter arrayAdapterMembers = new ArrayAdapter(getActivity(),
                R.layout.list_item_checkbox, teamMembers);
        users.setAdapter(arrayAdapterMembers);
        createChat = (Button) rootView.findViewById(R.id.create_chat);
        createChat.setOnClickListener(createChat());
        return rootView;
    }

    private View.OnClickListener createChat() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!chatConstellationAlreadyExists(usersOfChat)) {
                    runCreateChatTask();
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                            .setTitle(R.string.attention)
                            .setMessage("Es existiert bereits ein Chat mit den ausgewählten Usern!\n" +
                                    "Soll der Chat trotzdem erstellt werden?")
                            .setPositiveButton("Trotzdem erstellen!",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            runCreateChatTask();
                                        }
                                    })
                            .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FragmentTransaction transaction = getFragmentManager()
                                            .beginTransaction();
                                    transaction.replace(R.id.containerFrame,
                                            new ChooseChatFragment());
                                    transaction.commit();
                                }
                            }).create();
                    alertDialog.show();
                }
            }
        };
    }

    private boolean chatConstellationAlreadyExists(ArrayList<String> usersOfChat) {
        boolean result = false;
        for (JSONObject chat : existingChats) {
            try {
                JSONArray users = chat.getJSONArray("users");
                ArrayList<String> userSet = new ArrayList<>();
                for (int i = 0; i < users.length(); i++) {
                    userSet.add(users.getString(i));
                }
                Collections.sort(usersOfChat);
                Collections.sort(userSet);
                if (usersOfChat.equals(userSet)) {
                    result = true;
                    break;
                }
            } catch (JSONException e) {

            }
        }
        return result;
    }

    private void runCreateChatTask() {
        if (CheckInternet.isNetworkAvailable(getContext())) {
            if (usersOfChat.size() >= 2) {
                if (chatName != null) {
                    CreateChatTask createChatTask;
                    if (proofIfSoloChat()) {
                        createChatTask = new CreateChatTask("", "true");
                        Toast.makeText(getContext(), "Sie erstellen einen Solo-Chat!\n" +
                                "Der Name wurde freigelassen!", Toast.LENGTH_LONG).show();
                    } else {
                        createChatTask = new CreateChatTask(chatName, "false");
                    }
                    try {
                            JSONObject result = createChatTask.execute(usersOfChat).get();
                            String success = result.getString("success");
                            if (success.equals("true")) {
                                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                                        .setTitle(R.string.success)
                                        .setMessage("Der Chat wurde erfolgreich erstellt!")
                                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                FragmentTransaction transaction = getFragmentManager()
                                                        .beginTransaction();
                                                transaction.replace(R.id.containerFrame,
                                                        new ChooseChatFragment());
                                                transaction.commit();
                                            }
                                        }).create();
                                alertDialog.show();
                            } else {
                                String reason = result.getString("reason");
                                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                                        .setTitle(R.string.error)
                                        .setMessage("Der Chat konnte nicht erstellt werden!\n" + reason)
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
                    Toast.makeText(getContext(), "Bitte benennen Sie den Chat!", Toast.LENGTH_LONG)
                            .show();
                }
            } else {
                Toast.makeText(getContext(), "Bitte wählen Sie User aus, mit denen Sie " +
                        "chatten wollen!", Toast.LENGTH_LONG).show();
            }
        } else {
            android.app.AlertDialog alertDialog
                    = CheckInternet.internetNotAvailable(getActivity());
            alertDialog.show();
        }
    }

    private boolean proofIfSoloChat() {
        boolean result = false;
        if (usersOfChat.size() == 2) {
            result = true;
        }
        return result;
    }

    private ArrayList<String> getTeamMembers() {
        ArrayList<String> teamMembers = new ArrayList<>();
        if (CheckInternet.isNetworkAvailable(getContext())) {
            String[] params = new String[]{MainActivity.URL + "team/members",
                    MainActivity.userData.getToken(), MainActivity.userData.getTeamName()};
            GetTeamMembersTask getTeamMembersTask = new GetTeamMembersTask();
            try {
                JSONObject result = getTeamMembersTask.execute(params).get();
                String success = result.getString("success");
                if (success.equals("true")) {
                    JSONArray members = result.getJSONArray("members");
                    for (int i = 0; i < members.length(); i++) {
                        JSONObject member = members.getJSONObject(i);
                        String username = member.getString("username");
                        if (!username.equals(MainActivity.userData.getUsername())) {
                            teamMembers.add(username);
                        }
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
            android.app.AlertDialog alertDialog = CheckInternet.internetNotAvailable(getActivity());
            alertDialog.show();
        }
        return teamMembers;
    }

    private TextWatcher setChatName() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                chatName = setChatName.getText().toString();
            }
        };
    }

    public class CheckBoxClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            final CheckedTextView ctv = (CheckedTextView) arg1;
            ctv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String checkedUsername = ctv.getText().toString();
                    if (ctv.isChecked()) {
                        ctv.setChecked(false);
                        usersOfChat.remove(checkedUsername);
                    } else {
                        ctv.setChecked(true);
                        usersOfChat.add(checkedUsername);
                    }
                }
            });
        }
    }
}
