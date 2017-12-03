package com.cs523team4.iotui;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.cs523team4.iotui.adapter.MyDataListAdapter;
import com.cs523team4.iotui.data_access.AppDatabase;
import com.cs523team4.iotui.data_model.AccessPermission;
import com.cs523team4.iotui.data_model.DataRequester;
import com.cs523team4.iotui.data_model.DataSource;
import com.cs523team4.iotui.data_model.Device;
import com.cs523team4.iotui.data_model.DeviceDataSummary;
import com.cs523team4.iotui.intro.IntroActivity;
import com.cs523team4.iotui.util.PreferenceHelper;

import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener {

    private ListView myDataView;
    private MyDataListAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!PreferenceHelper.getBoolean(this, R.string.pref_intro_completed, false)) {
            startActivity(new Intent(this, IntroActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        myDataView = (ListView) findViewById(R.id.my_data_list);
        insertDummyData.execute();

//        Snackbar.make(fab, String.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("example_switch", false)), Snackbar.LENGTH_SHORT).show();

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
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getApplicationContext(), DeviceDataDetails.class);
        intent.putExtra(DeviceDataDetails.DEVICE_ID, ((Device) myAdapter.getItem(position)).deviceId);
        startActivity(intent);
    }

    public AsyncTask<Void, Void, Void> insertDummyData = new AsyncTask<Void, Void, Void>() {

        @Override
        public Void doInBackground(Void... voids) {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, AppDatabase.DB_NAME).build();
            if (db.appDao().loadAllDevices().length > 0) {
                return null;
            }
            DataSource[] dataSources = {new DataSource("Google Drive", "ABC"),
                                        new DataSource("BOX", "XYZ"),
                                        new DataSource("Nest", "DEF")};
            int[] dsIds = new int[dataSources.length];
            for (int i = 0; i < dataSources.length; i++) {
                dsIds[i] = (int) db.appDao().insertDataSource(dataSources[i]);
            }
            Device[] devices = {
                    new Device("Living room camera", "Camera", "Home", dsIds[0], "15 GB", R.drawable.ic_menu_camera),
                    new Device("Phone location", "GPS", "varying", dsIds[1], "120 MB", R.drawable.ic_location_on_black_24dp),
                    new Device("Home thermostat", "temperature", "home", dsIds[2], "25 MB", R.drawable.ic_ac_unit_black_24dp)
            };
            int[] deviceIds = new int[devices.length];
            for (int i = 0; i < devices.length; i++) {
                deviceIds[i] = (int) db.appDao().insertDevice(devices[i]);
            }
            DataRequester[] requesters = {
                    new DataRequester("ABR", "Anti-intruder app"),
                    new DataRequester("BLA", "Google Photos"),
                    new DataRequester("HAH", "Smarthome app")
            };
            int[] requesterIds = new int[requesters.length];
            for (int i = 0; i < requesters.length; i++) {
                requesterIds[i] = (int) db.appDao().insertDataRequester(requesters[i]);
            }
            DeviceDataSummary[] summaries = {
                    new DeviceDataSummary(deviceIds[0], "access to realtime data"),
                    new DeviceDataSummary(deviceIds[0], "full access"),
                    new DeviceDataSummary(deviceIds[2], "accessto hourly average"),
            };
            for (int i = 0; i < summaries.length; i++) {
                int summaryId = (int) db.appDao().insertDeviceDataSummary(summaries[i]);
                db.appDao().insertAccessPermission(new AccessPermission(requesterIds[i], summaryId, new Date(), new Date()));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            myAdapter = new MyDataListAdapter(getApplicationContext(),
                    Room.databaseBuilder(getApplicationContext(), AppDatabase.class, AppDatabase.DB_NAME).build());
            myDataView.setAdapter(myAdapter);
            myDataView.setOnItemClickListener(MainActivity.this);
        }
    };
}
