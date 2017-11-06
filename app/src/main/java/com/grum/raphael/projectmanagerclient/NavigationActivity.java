package com.grum.raphael.projectmanagerclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments.*;
import com.grum.raphael.projectmanagerclient.tasks.LeaveAppTask;
import com.grum.raphael.projectmanagerclient.tasks.TeamTask;
import com.grum.raphael.projectmanagerclient.tasks.UserTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static JSONObject userTaskData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Initial check and open Pinboard
        navigationView.setCheckedItem(R.id.frg_user_profile);

        UserTask userTask = new UserTask(NavigationActivity.this);
        if (!MainActivity.userData.isEmpty()) {
            try {
                userTaskData = userTask.execute(new String[]{MainActivity.URL + "user",
                        MainActivity.userData.getUsername(), MainActivity.userData.getToken()})
                        .get();
            } catch (InterruptedException e) {
                // TODO
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO
                e.printStackTrace();
            }
        } else {
            Intent main = new Intent(NavigationActivity.this, MainActivity.class);
            startActivity(main);
        }
        Fragment userProfile = new UserProfileFragment();
        Bundle bundle = new Bundle();
        if (userTaskData != null) {
            bundle.putString("userData", userTaskData.toString());
            userProfile.setArguments(bundle);
        }
        FragmentTransaction fragmentTransaction
                = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.containerFrame, userProfile);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            logout();
        } else if (id == R.id.leave_app) {
            leaveApp();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        String[] params;
        Fragment newFragment = null;

        if (!MainActivity.userData.isEmpty()) {
            if (id == R.id.nav_pinboard) {
                newFragment = new PinboardFragment();
            } else if (id == R.id.nav_profile) {
                UserTask userTask = new UserTask(NavigationActivity.this);
                Bundle bundle = new Bundle();
                try {
                    userTaskData = userTask.execute(new String[]{MainActivity.URL + "user",
                            MainActivity.userData.getUsername(), MainActivity.userData.getToken()})
                            .get();
                    bundle.putString("userData", userTaskData.toString());
                } catch (InterruptedException e) {
                    // TODO
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    // TODO
                    e.printStackTrace();
                }
                newFragment = new UserProfileFragment();
                newFragment.setArguments(bundle);
            } else if (id == R.id.nav_team) {
                if (!MainActivity.userData.getTeamName().equals("null")) {
                    params = new String[]
                            {MainActivity.userData.getToken(), MainActivity.URL + "team",
                            MainActivity.userData.getTeamName()};
                    TeamTask teamTask = new TeamTask();
                    Bundle bundle = new Bundle();
                    try {
                        JSONObject fetchedTeamData = teamTask.execute(params).get();
                        bundle.putString("teamData", fetchedTeamData.toString());
                    } catch (InterruptedException e) {
                        // TODO
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        // TODO
                        e.printStackTrace();
                    }
                    newFragment = new TeamProfileFragment();
                    newFragment.setArguments(bundle);
                } else {
                    AlertDialog alertDialog = generateEmptyTeamAlert();
                    alertDialog.show();
                    newFragment = new EmptyDataFragment();
                }
            } else if (id == R.id.nav_projects) {
                if (!MainActivity.userData.getTeamName().equals("null")) {
                    // TODO
                    newFragment = new ProjectsFragment();
                } else {
                    AlertDialog alertDialog = generateEmptyTeamAlert();
                    alertDialog.show();
                    newFragment = new EmptyDataFragment();
                }
            } else if (id == R.id.nav_appointments) {
                if (!MainActivity.userData.getTeamName().equals("null")) {
                    // TODO
                    newFragment = new AppointmentsFragment();
                } else {
                    AlertDialog alertDialog = generateEmptyTeamAlert();
                    alertDialog.show();
                    newFragment = new EmptyDataFragment();
                }
            } else if (id == R.id.nav_tasks) {
                newFragment = new TaskFragment();
            } else if (id == R.id.nav_searchTeams) {
                newFragment = new SearchTeamsFragment();
            } else if (id == R.id.nav_searchUsers) {
                newFragment = new SearchUsersFragment();
            } else if (id == R.id.nav_chats) {
                if (!MainActivity.userData.getTeamName().equals("null")) {
                    // TODO
                    newFragment = new ChatsFragment();
                } else {
                    AlertDialog alertDialog = generateEmptyTeamAlert();
                    alertDialog.show();
                    newFragment = new EmptyDataFragment();
                }
            }

            if (newFragment != null) {
                FragmentTransaction fragmentTransaction
                        = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.containerFrame, newFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else {
            AlertDialog alertDialog = new ErrorAlertExpiredRights(NavigationActivity.this).getAlertDialog();
            alertDialog.show();
            return false;
        }
    }

    private AlertDialog generateEmptyTeamAlert() {
        AlertDialog result = new AlertDialog.Builder(NavigationActivity.this)
                .setTitle(R.string.error_internal)
                .setMessage("Sei sind noch keinem Team zugeordnet! Bitte treten Sie zuerst einem " +
                        "Team bei oder erstellen Sie Ihr eigenes Team.")
                .setNegativeButton("OK", null)
                .create();
        return result;
    }

    private void logout() {
        MainActivity.userData = new DataContainer();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void leaveApp() {
        String[] params = new String[] {MainActivity.URL + "leave",
                MainActivity.userData.getToken(), MainActivity.userData.getUsername()};
        LeaveAppTask leaveAppTask = new LeaveAppTask();
        try {
            JSONObject result = leaveAppTask.execute(params).get();
            String success = result.getString("success");
            if (success.equals("true")) {
                AlertDialog alertDialog = new AlertDialog.Builder(NavigationActivity.this)
                        .setTitle(R.string.success)
                        .setMessage("Sie haben die App erfolgreich verlassen! Auf Wiedersehen!")
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(NavigationActivity.this,
                                        MainActivity.class));
                            }
                        })
                        .create();
                alertDialog.show();
            } else {
                String reason = result.getString("reason");
                AlertDialog alertDialog = new AlertDialog.Builder(NavigationActivity.this)
                        .setTitle(R.string.error)
                        .setMessage("Sie konnten die App nicht verlassen! Ein Fehler ist " +
                                "aufgetreten!\n" + reason)
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
