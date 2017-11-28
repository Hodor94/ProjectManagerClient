package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;
import com.grum.raphael.projectmanagerclient.tasks.CheckInternet;
import com.grum.raphael.projectmanagerclient.tasks.GetTeamMembersTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Raphael on 27.11.2017.
 */

public class ChooseChatFragment extends Fragment {

    private List<String> usernames;
    private ListView chats;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.choose_chat_fragment, container, false);
        getChats();
        chats = (ListView) rootView.findViewById(R.id.chats_list);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.list_item,
                R.id.list_element, usernames);
        chats.setAdapter(adapter);
        chats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                Fragment newFragment = new ChatFragment();
                String receiver = chats.getAdapter().getItem(position).toString();
                bundle.putString("team", MainActivity.userData.getTeamName());
                bundle.putString("receiver", receiver);
                newFragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.containerFrame, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return rootView;
    }

    private void getChats() {
        usernames = new ArrayList<>();
        String[] params = new String[] {MainActivity.URL + "team/members",
                MainActivity.userData.getToken(), MainActivity.userData.getTeamName()};
        GetTeamMembersTask membersTask = new GetTeamMembersTask();
        if (CheckInternet.isNetworkAvailable(getContext())) {
            try {
                JSONObject result = membersTask.execute(params).get();
                String success = result.getString("success");
                if (success.equals("true")) {
                    JSONArray members = result.getJSONArray("members");
                    for (int i = 0; i < members.length(); i++) {
                        JSONObject member = members.getJSONObject(i);
                        String username = member.getString("username");
                        if (!username.equals(MainActivity.userData.getUsername())) {
                            usernames.add(username);
                        }
                    }
                    usernames.add(MainActivity.userData.getTeamName());
                } else {
                    String reason = result.getString("reason");
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                            .setTitle(R.string.error)
                            .setMessage("Chats konnten nicht geladen werden!\n" + reason)
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
