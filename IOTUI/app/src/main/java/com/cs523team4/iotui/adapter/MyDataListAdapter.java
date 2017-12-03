package com.cs523team4.iotui.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cs523team4.iotui.R;
import com.cs523team4.iotui.data_access.AppDatabase;
import com.cs523team4.iotui.data_model.DataSource;
import com.cs523team4.iotui.data_model.Device;
import com.cs523team4.iotui.data_model.pojo.AccessPermissionDeviceTuple;

import java.util.HashMap;
import java.util.Map;

/**
 * Adapter used to populate 'My Data' list view.
 * Created by aravind on 11/20/17.
 */

public class MyDataListAdapter extends BaseAdapter {

    /**
     * View holder class to avoid repeated findViewById lookups.
     */
    private static class ViewHolder {
        ImageView imageView;
        TextView nameView;
        TextView sharedStatusView;
        TextView sourceView;
        TextView dateView;

        public ViewHolder(ImageView imageView, TextView nameView, TextView sharedStatusView,
                          TextView sourceView, TextView dateView) {
            this.imageView = imageView;
            this.nameView = nameView;
            this.sharedStatusView = sharedStatusView;
            this.sourceView = sourceView;
            this.dateView = dateView;
        }
    }

    private final LayoutInflater myInflater;
    private Device[] myDevices;
    private SparseArray<String> myDataSources;
    private SparseBooleanArray mySharedStatuses;

    public MyDataListAdapter(final Context context, AppDatabase db) {
        super();
        myInflater = LayoutInflater.from(context);
        myDevices = new Device[0];
        new AsyncTask<AppDatabase, Void, Void>() {

            @Override
            protected Void doInBackground(AppDatabase... voids) {
                AppDatabase db = voids[0];
                myDevices = db.appDao().loadAllDevices();
                myDataSources = new SparseArray<>(myDevices.length);
                mySharedStatuses = new SparseBooleanArray(myDevices.length);
                for (DataSource d : db.appDao().loadAllDataSources()) {
                    myDataSources.put(d.dataSourceId, d.name);
                }
                for (AccessPermissionDeviceTuple t : db.appDao().loadAccessPermissionDeviceTuples()) {
                    mySharedStatuses.put(t.deviceId, true);
                };
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                notifyDataSetChanged();
            }
        }.execute(db);
    }

    @Override
    public int getCount() {
        return myDevices.length;
    }

    @Override
    public Object getItem(int position) {
        return myDevices[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Create or reuse the layout objects as required.
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = myInflater.inflate(R.layout.list_item_my_data, parent, false);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (holder == null) {
            holder = new ViewHolder((ImageView) convertView.findViewById(R.id.my_data_icon),
                    (TextView) convertView.findViewById(R.id.device_name_view),
                    (TextView) convertView.findViewById(R.id.share_status_view),
                    (TextView) convertView.findViewById(R.id.data_store_view),
                    (TextView) convertView.findViewById(R.id.date_range_view));
        }
        Device data = (Device) getItem(position);
        holder.imageView.setImageResource(data.drawableResId);
        holder.nameView.setText(data.deviceName);
        holder.sourceView.setText(myDataSources.get(data.deviceId));
        if (mySharedStatuses.get(data.deviceId)) {
            holder.sharedStatusView.setText(R.string.shared);
        } else {
            holder.sharedStatusView.setText(R.string.not_shared);
        }
        return convertView;
    }
}
