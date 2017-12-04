package com.cs523team4.iotui.fragment;


import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cs523team4.iotui.DeviceDataDetails;
import com.cs523team4.iotui.R;
import com.cs523team4.iotui.adapter.MyDataListAdapter;
import com.cs523team4.iotui.data_access.AppDatabase;
import com.cs523team4.iotui.data_model.AccessPermission;
import com.cs523team4.iotui.data_model.DataRequest;
import com.cs523team4.iotui.data_model.DataRequester;
import com.cs523team4.iotui.data_model.DataSource;
import com.cs523team4.iotui.data_model.Device;
import com.cs523team4.iotui.data_model.DeviceDataSummary;
import com.cs523team4.iotui.server_util.ServerPullService;

import java.util.Date;

import static com.cs523team4.iotui.server_util.ServerPullService.ACTION_REFRESH_DATA;
import static com.cs523team4.iotui.server_util.ServerPullService.BROADCAST_ACTION;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyDevicesFragment extends Fragment implements
        AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private ListView myDataView;
    private SwipeRefreshLayout myRefreshLayout;
    private MyDataListAdapter myAdapter;
    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            myAdapter.refresh();
            myRefreshLayout.setRefreshing(false);
        }
    };


    public MyDevicesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_my_devices, container, false);
        myDataView = (ListView) root.findViewById(R.id.my_data_list);
        insertDummyData.execute();

        myRefreshLayout = root.findViewById(R.id.main_refresh_layout);
        myRefreshLayout.setOnRefreshListener(this);

        registerBroadcastListener();
        return root;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_my_devices, menu);
        super.onCreateOptionsMenu(menu, inflater);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregisterBroadcastListener();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getContext(), DeviceDataDetails.class);
        intent.putExtra(DeviceDataDetails.DEVICE_ID, ((Device) myAdapter.getItem(position)).deviceId);
        startActivity(intent);
    }

    public AsyncTask<Void, Void, Void> insertDummyData = new AsyncTask<Void, Void, Void>() {

        @Override
        public Void doInBackground(Void... voids) {
            AppDatabase db = Room.databaseBuilder(getContext(), AppDatabase.class, AppDatabase.DB_NAME).build();
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
                    new DataRequester("ABR", "Anti-intruder app", "no one", R.drawable.ic_cloud),
                    new DataRequester("BLA", "Google Photos", "everyone", R.drawable.ic_menu_slideshow),
                    new DataRequester("HAH", "Smarthome app", "someone", R.drawable.ic_menu_camera)
            };
            int[] requesterIds = new int[requesters.length];
            for (int i = 0; i < requesters.length; i++) {
                requesterIds[i] = (int) db.appDao().insertDataRequester(requesters[i]);
            }
            DeviceDataSummary[] summaries = {
                    new DeviceDataSummary(deviceIds[0], "access to realtime data"),
                    new DeviceDataSummary(deviceIds[0], "full access"),
                    new DeviceDataSummary(deviceIds[2], "access to hourly average"),
            };
            for (int i = 0; i < summaries.length; i++) {
                int summaryId = (int) db.appDao().insertDeviceDataSummary(summaries[i]);
                db.appDao().insertAccessPermission(new AccessPermission(requesterIds[i], summaryId, new Date(), new Date()));
                if (i == 2) {
                    DataRequest request = new DataRequest(requesterIds[1], summaryId, new Date(), new Date());
                    db.appDao().insertDataRequest(request);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            myAdapter = new MyDataListAdapter(getContext(),
                    Room.databaseBuilder(getContext(), AppDatabase.class, AppDatabase.DB_NAME).build());
            myDataView.setAdapter(myAdapter);
            myDataView.setOnItemClickListener(MyDevicesFragment.this);
        }
    };

    @Override
    public void onRefresh() {
        getActivity().startService(new Intent(getContext(), ServerPullService.class).setAction(ACTION_REFRESH_DATA));
        // We'll get a broadcast when data is refreshed.
    }

    /**
     * Register broadcast listeners which lets us know when data refresh is complete.
     */
    private void registerBroadcastListener() {
        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
        // Registers the receiver with the new filter
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(myBroadcastReceiver, intentFilter);
    }

    private void unregisterBroadcastListener() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(myBroadcastReceiver);
    }
}
