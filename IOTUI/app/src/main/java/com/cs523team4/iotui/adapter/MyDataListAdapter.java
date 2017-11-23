package com.cs523team4.iotui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cs523team4.iotui.R;
import com.cs523team4.iotui.data_model.DeviceData;

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
    private DeviceData[] myDeviceData;

    public MyDataListAdapter(final Context context, DeviceData[] deviceData) {
        super();
        myInflater = LayoutInflater.from(context);
        myDeviceData = deviceData;
    }

    @Override
    public int getCount() {
        return myDeviceData.length;
    }

    @Override
    public Object getItem(int position) {
        return myDeviceData[position];
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
        DeviceData data = (DeviceData) getItem(position);
        holder.imageView.setImageResource(data.getDrawable());
        holder.nameView.setText(data.getName());
        holder.sourceView.setText(data.getSource());
        if (data.isDataShared()) {
            holder.sharedStatusView.setText(R.string.shared);
        } else {
            holder.sharedStatusView.setText(R.string.not_shared);
        }
        return convertView;
    }
}
