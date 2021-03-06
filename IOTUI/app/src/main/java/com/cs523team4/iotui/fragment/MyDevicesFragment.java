package com.cs523team4.iotui.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;

import com.cs523team4.iotui.DeviceDataDetails;
import com.cs523team4.iotui.R;
import com.cs523team4.iotui.adapter.MyDataListAdapter;
import com.cs523team4.iotui.adapter.RefreshCompleteListener;
import com.cs523team4.iotui.data_access.AppDatabase;
import com.cs523team4.iotui.data_model.Device;
import com.cs523team4.iotui.server_util.ServerPullService;

import static com.cs523team4.iotui.server_util.ServerPullService.ACTION_REFRESH_DATA;
import static com.cs523team4.iotui.server_util.ServerPullService.BROADCAST_ACTION;
import static com.cs523team4.iotui.server_util.ServerPullService.DATA_REFRESH_RESULT;

/**
 * A simple {@link Fragment} subclass for showing user's devices. This is the default fragment
 * populated in the main activity.
 */
public class MyDevicesFragment extends Fragment implements
        AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, RefreshCompleteListener {

    private ListView myDataView;
    private TextView myNoDevicesView;
    private SwipeRefreshLayout myRefreshLayout;
    private MyDataListAdapter myAdapter;
    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(DATA_REFRESH_RESULT, false)) {
                Snackbar.make(myRefreshLayout, "Refresh complete", BaseTransientBottomBar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(myRefreshLayout, "Error fetching data from server", BaseTransientBottomBar.LENGTH_SHORT).show();
            }
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
        myDataView = root.findViewById(R.id.my_data_list);
        myNoDevicesView = root.findViewById(R.id.text_no_items);
        myAdapter = new MyDataListAdapter(getContext(), AppDatabase.getInstance(getContext()), this);
        myDataView.setAdapter(myAdapter);
        myDataView.setOnItemClickListener(MyDevicesFragment.this);

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

    @Override
    public void onRefreshComplete(boolean success) {
        if (success) {
            myDataView.setVisibility(View.VISIBLE);
            myNoDevicesView.setVisibility(View.GONE);
        } else {
            myDataView.setVisibility(View.GONE);
            myNoDevicesView.setVisibility(View.VISIBLE);
        }
    }
}
