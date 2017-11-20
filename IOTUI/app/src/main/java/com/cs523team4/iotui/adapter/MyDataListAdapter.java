package com.cs523team4.iotui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cs523team4.iotui.R;

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

    // Dummy data for now.
    private static class DataUnit {
        int drawable;
        String name, source;
        boolean shared;

        public DataUnit(int drawable, String name, String source, boolean shared) {
            this.drawable = drawable;
            this.name = name;
            this.source = source;
            this.shared = shared;
        }
    }
    private DataUnit[] myData = new DataUnit[] {
            new DataUnit(R.drawable.ic_menu_camera, "Living room camera", "Google Drive", true),
            new DataUnit(R.drawable.ic_location_on_black_24dp, "Phone GPS", "Box", false),
            new DataUnit(R.drawable.ic_ac_unit_black_24dp, "Home thermostat", "Nest", true),
    };

    private final LayoutInflater inflater;

    public MyDataListAdapter(final Context context) {
        super();
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return myData.length;
    }

    @Override
    public Object getItem(int position) {
        return myData[position];
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
            convertView = inflater.inflate(R.layout.my_data_list_item, parent, false);
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
        DataUnit data = (DataUnit) getItem(position);
        holder.imageView.setImageResource(data.drawable);
        holder.nameView.setText(data.name);
        holder.sourceView.setText(data.source);
        if (data.shared) {
            holder.sharedStatusView.setText(R.string.shared);
        } else {
            holder.sharedStatusView.setText(R.string.not_shared);
        }
        return convertView;
    }
}
