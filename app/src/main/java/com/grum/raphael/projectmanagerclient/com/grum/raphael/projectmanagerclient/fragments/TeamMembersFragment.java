package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;

public class TeamMembersFragment extends Fragment {

    private TableLayout table;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_team_members, container, false);
        table = (TableLayout) rootView.findViewById(R.id.team_memebers_table);

        return rootView;
    }

    private void getTeamMemebers() {
        // TODO
        String teamName = MainActivity.userData.getTeamName();
        String[] params = new String[] {MainActivity.URL + "team/members",
                MainActivity.userData.getToken(), MainActivity.userData.getTeamName()};
    }
}
