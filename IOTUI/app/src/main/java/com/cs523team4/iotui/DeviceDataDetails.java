package com.cs523team4.iotui;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cs523team4.iotui.data_model.DeviceData;

import java.util.Map;

public class DeviceDataDetails extends AppCompatActivity {

    public static String DEVICE_ID = "device_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_data_details);
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DeviceData data = DeviceData.getDeviceData()[getIntent().getIntExtra(DEVICE_ID, 0)];
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        ImageView toolbarImg = toolbarLayout.findViewById(R.id.toolbar_layout_img);
        toolbarLayout.setTitle(data.getName());
        toolbarImg.setImageResource(data.getDrawable());

        LinearLayout sharingDetails = (LinearLayout) findViewById(R.id.list_view_sharing_details);
        if (data.isDataShared()) {
            for (Map.Entry<String, String> entry : data.getAccessDetails().entrySet()) {
                TextView item = (TextView) getLayoutInflater().inflate(R.layout.list_item_sharing_details, sharingDetails, false);
                item.setText(entry.getKey() + " has " + entry.getValue());
                sharingDetails.addView(item);
            }
        } else {
            TextView item = (TextView) getLayoutInflater().inflate(R.layout.list_item_sharing_details, sharingDetails, false);
            item.setText("Not shared");
            sharingDetails.addView(item);
        }

        TextView dataSource = (TextView) findViewById(R.id.view_data_source),
                dateRange = (TextView) findViewById(R.id.view_date_range),
                dataUsage = (TextView) findViewById(R.id.view_data_usage);

        dataSource.setText("Stored in " + data.getSource());
        dataUsage.setText("Uses " + data.getStorageUse() + " of space");
        dateRange.setText("From 1/1/2017");
    }
}
