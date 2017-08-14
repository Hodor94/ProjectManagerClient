package com.grum.raphael.projectmanagerclient;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

// TODo create fragments and set Icons
public class NavigationAction extends AppCompatActivity {

    private String[] navigationDrawerItemTitles;
    private DrawerLayout drawerLayout;
    private ListView listView;
    private Toolbar toolbar;
    private CharSequence drawerTitle;
    private CharSequence title;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_action);
        title = drawerTitle = getTitle();
        navigationDrawerItemTitles
                = getResources()
                .getStringArray(R.array.navigation_drawer_items_array);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        listView = (ListView) findViewById(R.id.left_drawer);

        setupToolbar();

        // TODO watch!
        DataModel[] drawerItem = new DataModel[7];

        // TODO set icons
        /*
        drawerItem[0] = new DataModel(R.drawable.pinboard, "Pinnwand");
        drawerItem[1] = new DataModel(R.drawable.profile, "Profil");
        drawerItem[2] = new DataModel(R.drawable.team, "Team");
        drawerItem[3] = new DataModel(R.drawable.projects, "Projekte");
        drawerItem[4] = new DataModel(R.drawable.appointments, "Termine");
        drawerItem[5] = new DataModel(R.drawable.search_teams, "Team finden");
        drawerItem[6] = new DataModel(R.drawable.search_users, "Nutzer finden"); */

        // Used to hide the default back button.
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        DrawerItemCustomAdapter adapter
                = new DrawerItemCustomAdapter(this, R.layout.list_view_item_row, drawerItem);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new DrawerItemClickListener());
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.addDrawerListener(drawerToggle);

        setupDrawerToggle();
    }

    private class DrawerItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        Fragment fragment = null;

        // TODO
        /*
        switch (position) {
            case 0:
                fragment = new PinboardFragment();
                break;
            case 1:
                fragment = new UserProfileFragment();
                break;
            case 2:
                fragment = new TeamProfileFragment();
                break;
            case 3:
                fragment = new ProjectsFragment();
                break;
            case 4:
                fragment = new AppointmentsFragment();
                break;
            case 5:
                fragment = SearchTeamsFragment();
                break;
            case 6:
                fragment = new SearchUsersFragment();
                break;
            default:
                break;
        }
        */

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            listView.setItemChecked(position, true);
            listView.setSelection(position);
            setTitle(navigationDrawerItemTitles[position]);
            drawerLayout.closeDrawer(listView);
        } else {
            // TODO error alert or log!
            Log.e("NavigationAction", "Error creating fragment!");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void setTitle(CharSequence title) {
        this.title = title;
        getSupportActionBar().setTitle(this.title);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    void setupDrawerToggle() {
        drawerToggle
                = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                                            R.string.app_name, R.string.app_name);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        drawerToggle.syncState();
    }
}
