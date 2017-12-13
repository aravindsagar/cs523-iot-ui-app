package com.cs523team4.iotui;

import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cs523team4.iotui.adapter.DataRequestsAdapter;
import com.cs523team4.iotui.data_access.AppDatabase;
import com.cs523team4.iotui.server_util.ServerPullService;
import com.github.clans.fab.FloatingActionMenu;

import static com.cs523team4.iotui.server_util.ServerPullService.ACTION_REFRESH_DATA;
import static com.cs523team4.iotui.server_util.ServerPullService.BROADCAST_ACTION;
import static com.cs523team4.iotui.server_util.ServerPullService.DATA_REFRESH_RESULT;

public class DataRequestsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private FloatingActionMenu myFam;
    private DataRequestsAdapter myDataRequestsAdapter;
    private SwipeRefreshLayout myRefreshLayout;
    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(DATA_REFRESH_RESULT, false)) {
                Snackbar.make(myRefreshLayout, "Refresh complete", BaseTransientBottomBar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(myRefreshLayout, "Error fetching data from server", BaseTransientBottomBar.LENGTH_SHORT).show();
            }
            myDataRequestsAdapter.refresh();
            myRefreshLayout.setRefreshing(false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_requests);
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        myFam = findViewById(R.id.clear_all_menu);
        myFam.setIconAnimated(false);
        myFam.setClosedOnTouchOutside(true);
        ListView dataRequestsList = findViewById(R.id.list_data_requests);
        myDataRequestsAdapter = new DataRequestsAdapter(Room.databaseBuilder(this, AppDatabase.class, AppDatabase.DB_NAME).build());
        dataRequestsList.setAdapter(myDataRequestsAdapter);
        dataRequestsList.setOnItemClickListener(this);

        myRefreshLayout = findViewById(R.id.refresh_data_requests);
        myRefreshLayout.setOnRefreshListener(this);

        registerBroadcastListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcastListener();
    }

    @Override
    public void onBackPressed() {
        if (myFam.isOpened()) {
            myFam.close(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        myDataRequestsAdapter.itemClick(position, this);
    }

    @Override
    public void onRefresh() {
        startService(new Intent(this, ServerPullService.class).setAction(ACTION_REFRESH_DATA));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_data_requests, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            myRefreshLayout.setRefreshing(true);
            onRefresh();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Register broadcast listeners which lets us know when data refresh is complete.
     */
    private void registerBroadcastListener() {
        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
        // Registers the receiver with the new filter
        LocalBroadcastManager.getInstance(this).registerReceiver(myBroadcastReceiver, intentFilter);
    }

    private void unregisterBroadcastListener() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myBroadcastReceiver);
    }
}
