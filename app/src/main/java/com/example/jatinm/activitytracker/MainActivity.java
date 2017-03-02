package com.example.jatinm.activitytracker;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ListAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RefreshActivityListener {
    private GridView mUserActivityListView;
    private ListAdapter mUserActivityListAdapter;
    private StorageDatabaseHelper mStorageDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStorageDatabaseHelper = new StorageDatabaseHelper(this);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton createNewActivity =
                (FloatingActionButton) findViewById(R.id.create_activity);
        createNewActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNewActivityDialogFragment fragment = new AddNewActivityDialogFragment();
                fragment.setDatabaseHelper(mStorageDatabaseHelper);
                fragment.setRefreshListener(MainActivity.this);
                fragment.show(getFragmentManager(), "new_activity_tag");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mUserActivityListView = (GridView) findViewById(R.id.user_activity_list);
        new FetchDashboardContentAsyncTask(this, mStorageDatabaseHelper).execute();
    }

    @Override
    public void onRefresh() {
        new FetchDashboardContentAsyncTask(this, mStorageDatabaseHelper).execute();
    }

    public void initializeListView(Cursor cursor) {
        mUserActivityListAdapter = new UserActivityListAdapter(
                this /* context */, cursor, 0, mStorageDatabaseHelper);
        mUserActivityListView.setAdapter(mUserActivityListAdapter);
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
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the MainActivity/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
