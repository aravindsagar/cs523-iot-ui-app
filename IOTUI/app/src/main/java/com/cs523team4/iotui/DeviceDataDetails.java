package com.cs523team4.iotui;

import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
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

import com.cs523team4.iotui.data_access.AppDatabase;
import com.cs523team4.iotui.data_model.AccessPermission;
import com.cs523team4.iotui.data_model.DataSource;
import com.cs523team4.iotui.data_model.Device;
import com.cs523team4.iotui.data_model.pojo.DataRequesterSummaryDescriptionTuple;
import com.cs523team4.iotui.server_util.ServerReader;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Activity which shows users details of a particular device.
 */
public class DeviceDataDetails extends AppCompatActivity {

    public static String DEVICE_ID = "device_id";
    private Executor singleThreadExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_data_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        singleThreadExecutor.execute(loadData);
    }

    private Runnable loadData = new Runnable() {
        @Override
        public void run() {
            final AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            final Device data = db.appDao().loadDevice(getIntent().getIntExtra(DEVICE_ID, 0));
            final DataRequesterSummaryDescriptionTuple[] permissions = db.appDao().loadDataRequesterSummaryDescriptionTuples(data.deviceId);
            final DataSource source = db.appDao().loadDataSource(data.dataSourceId);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
                    ImageView toolbarImg = toolbarLayout.findViewById(R.id.toolbar_layout_img);
                    toolbarLayout.setTitle(data.deviceName);
                    toolbarImg.setImageResource(data.drawableResId);

                    LinearLayout sharingDetails = (LinearLayout) findViewById(R.id.list_view_sharing_details);
                    sharingDetails.removeAllViews();
                    if (permissions.length > 0) {
                        for (final DataRequesterSummaryDescriptionTuple entry : permissions) {
                            TextView item = (TextView) getLayoutInflater().inflate(R.layout.list_item_sharing_details, sharingDetails, false);
                            item.setText(entry.requesterName + " has " + entry.summaryDescription);
                            sharingDetails.addView(item);
                            item.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(DeviceDataDetails.this);
                                    builder.setMessage("Revoke this data access permission?")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    singleThreadExecutor.execute(
                                                            new DeletePermission(db, entry.accessPermissionId)
                                                    );
                                                    singleThreadExecutor.execute(loadData);
                                                }
                                            })
                                            .setNegativeButton("No", null)
                                            .show();
                                    return true;
                                }
                            });
                        }
                    } else {
                        TextView item = (TextView) getLayoutInflater().inflate(R.layout.list_item_sharing_details, sharingDetails, false);
                        item.setText("Not shared");
                        sharingDetails.addView(item);
                    }

                    TextView dataSource = (TextView) findViewById(R.id.view_data_source),
                            dateRange = (TextView) findViewById(R.id.view_date_range),
                            dataUsage = (TextView) findViewById(R.id.view_data_usage);

                    dataSource.setText("Stored in " + source.name);
                    dataUsage.setText("Uses " + data.diskSpaceUsage + " MB of space");
                    dateRange.setVisibility(View.GONE);//dateRange.setText("From 1/1/2017");
                }
            });
        }
    };

    class DeletePermission implements Runnable {
        AppDatabase db;
        int id;

        public DeletePermission(AppDatabase db, int id) {
            this.db = db;
            this.id = id;
        }

        @Override
        public void run() {
            AccessPermission permission = db.appDao().loadAccessPermission(id);
            try {
                ServerReader.postAccessPermissionAction(DeviceDataDetails.this, permission, ServerReader.ACTION_DENY);
            } catch (KeyManagementException | CertificateException | NoSuchAlgorithmException | KeyStoreException | IOException e) {
                e.printStackTrace();
            }
            db.appDao().deleteAccessPermission(permission);
        }
    }


}
