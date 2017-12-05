package com.cs523team4.iotui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.cs523team4.iotui.R;
import com.cs523team4.iotui.data_access.AppDatabase;
import com.cs523team4.iotui.data_model.DataRequest;
import com.cs523team4.iotui.data_model.DataRequester;
import com.cs523team4.iotui.data_model.DeviceDataSummary;
import com.cs523team4.iotui.data_model.TrustedAgent;
import com.cs523team4.iotui.data_model.pojo.DeviceNameSummaryIdTuple;

import java.security.KeyStore;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by aravind on 12/3/17.
 */

public class DataRequestsAdapter extends BaseAdapter {
    private AppDatabase myDb;
    private DataRequest[] myDataRequests;
    private SparseArray<DataRequester> myDataRequesters;
    private SparseArray<String> mySummaryDescriptions;
    private SparseArray<String> mySummaryDeviceNames;
    private SparseArray<String> myEndorsements;

    private Executor myExecutor = Executors.newSingleThreadExecutor();
    private Handler myHandler;

    public DataRequestsAdapter(AppDatabase db) {
        super();
        myDb = db;
        myDataRequests = new DataRequest[0];
        myDataRequesters = new SparseArray<>();
        mySummaryDescriptions = new SparseArray<>();
        mySummaryDeviceNames = new SparseArray<>();
        myEndorsements = new SparseArray<>();
        myHandler = new Handler();
        refresh();
    }

    public void refresh() {
        myExecutor.execute(populateData);
    }

    @Override
    public int getCount() {
        return myDataRequests.length;
    }

    @Override
    public Object getItem(int position) {
        return myDataRequests[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_data_request, parent, false);
        }
        DataRequest request = (DataRequest) getItem(position);
        DataRequester requester = myDataRequesters.get(request.dataRequesterId);
        TextView rView = convertView.findViewById(R.id.text_request);
        rView.setText(requester.name
                + " is requesting "
                + mySummaryDescriptions.get(request.summaryId)
                + " from "
                + mySummaryDeviceNames.get(request.summaryId)
        );
        TextView eView = convertView.findViewById(R.id.text_endorsement);
        String endorsements = myEndorsements.get(requester.dataRequesterId);
        if (endorsements != null) {
            eView.setText("Endorsed by " + endorsements);
            eView.setCompoundDrawables(parent.getContext().getResources().getDrawable(R.drawable.ic_tick), null, null, null);
        } else {
            eView.setText("No endorsements");
            eView.setCompoundDrawables(parent.getContext().getResources().getDrawable(R.drawable.ic_warning), null, null, null);
        }
        ImageView imgView = convertView.findViewById(R.id.img_requester_icon);
        imgView.setImageResource(requester.drawableResId);
        return convertView;
    }

    private Runnable notifyDatasetChangedRunnable = new Runnable() {
        @Override
        public void run() {
            notifyDataSetChanged();
        }
    };

    private Runnable populateData = new Runnable() {
        @Override
        public void run() {
            myDataRequests = myDb.appDao().loadAllDataRequests();
            myDataRequesters.clear();
            mySummaryDescriptions.clear();
            mySummaryDescriptions.clear();
            myEndorsements.clear();
            for (DataRequester r : myDb.appDao().loadAllDataRequesters()) {
                myDataRequesters.put(r.dataRequesterId, r);
                String endorsements = null;
                TrustedAgent[] agents = myDb.appDao().loadEndorsers(r.dataRequesterId);
                if (agents.length > 0) {
                    StringBuilder eNames = new StringBuilder();
                    for (int i = 0; i < agents.length; i++) {
                        eNames.append(agents[i].name);
                        if (i < agents.length-1) {
                            eNames.append(", ");
                        }
                    }
                    endorsements = eNames.toString();
                }
                myEndorsements.put(r.dataRequesterId, endorsements);
            }
            for (DeviceDataSummary s : myDb.appDao().loadAllDeviceDataSummaries()) {
                mySummaryDescriptions.put(s.summaryId, s.summaryDescription);
            }
            for (DeviceNameSummaryIdTuple t : myDb.appDao().loadDeviceNameSummaryIdTuples()) {
                mySummaryDeviceNames.put(t.summaryId, t.deviceName);
            }
            myHandler.post(notifyDatasetChangedRunnable);
        }
    };

    public void itemClick(int position, Context context) {
        DataRequest request = (DataRequest) getItem(position);
        DataRequester requester = myDataRequesters.get(request.dataRequesterId);
        String message = "Allow " + requester.name
                + " " + mySummaryDescriptions.get(request.summaryId)
                + " from " + mySummaryDeviceNames.get(request.summaryId) + "?";
        View checkBoxView = View.inflate(context, R.layout.data_request_dialog_checkbox, null);
        CheckBox checkBox = checkBoxView.findViewById(R.id.checkbox_always_do);
        String allowAllMessage = "Always do this for requests from " + requester.name;
        checkBox.setText(allowAllMessage);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setMessage(message)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setView(checkBoxView)
                .show();
    }
}
