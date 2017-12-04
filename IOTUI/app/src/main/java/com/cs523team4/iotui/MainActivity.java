package com.cs523team4.iotui;

import android.app.job.JobScheduler;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cs523team4.iotui.fragment.MyDevicesFragment;
import com.cs523team4.iotui.fragment.TrustedAgentsFragment;
import com.cs523team4.iotui.intro.IntroActivity;
import com.cs523team4.iotui.util.PreferenceHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private DrawerLayout myDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!PreferenceHelper.getBoolean(this, R.string.pref_intro_completed, false)) {
            startActivity(new Intent(this, IntroActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_main, new MyDevicesFragment())
                    .commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, myDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        myDrawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        TextView username = navigationView.getHeaderView(0).findViewById(R.id.text_username);
        username.setText(PreferenceHelper.getString(this, R.string.pref_key_username, "Unknown"));
        TextView logOutBtn = navigationView.findViewById(R.id.logout);
        logOutBtn.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if (myDrawer.isDrawerOpen(GravityCompat.START)) {
            myDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_notifications) {
            startActivity(new Intent(this, DataRequestsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_my_devices) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_main, new MyDevicesFragment())
                    .commit();
        } else if (id == R.id.nav_trusted_agents) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_main, new TrustedAgentsFragment())
                    .commit();
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        myDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.logout) {
            PreferenceHelper.remove(this, R.string.pref_key_username);
            PreferenceHelper.remove(this, R.string.pref_intro_completed);
            startActivity(new Intent(this, IntroActivity.class));
            finish();
        }
        JobScheduler j;
    }
}
