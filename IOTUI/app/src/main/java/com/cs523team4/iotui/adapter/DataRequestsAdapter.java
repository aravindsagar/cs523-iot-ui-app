package com.cs523team4.iotui.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
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
import com.cs523team4.iotui.server_util.ServerReader;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by aravind on 12/3/17.
 */

public class DataRequestsAdapter extends BaseAdapter {

    @SuppressLint("SimpleDateFormat")
    private static final DateFormat ourDateFormat = new SimpleDateFormat("MMM dd, yyyy");

    private AppDatabase myDb;
    private DataRequest[] myDataRequests;
    private SparseArray<DataRequester> myDataRequesters;
    private SparseArray<String> mySummaryDescriptions;
    private SparseArray<String> mySummaryDeviceNames;
    private SparseArray<String> myEndorsements;
    private RefreshCompleteListener myListener;

    private Executor myExecutor = Executors.newSingleThreadExecutor();
    private Handler myHandler;

    public DataRequestsAdapter(AppDatabase db, RefreshCompleteListener listener) {
        super();
        myDb = db;
        myDataRequests = new DataRequest[0];
        myDataRequesters = new SparseArray<>();
        mySummaryDescriptions = new SparseArray<>();
        mySummaryDeviceNames = new SparseArray<>();
        myEndorsements = new SparseArray<>();
        myHandler = new Handler();
        myListener = listener;
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
        TextView dView = convertView.findViewById(R.id.text_date_range);
        dView.setText(ourDateFormat.format(request.startDate) + " - " + ourDateFormat.format(request.endDate));
        dView.setCompoundDrawablesWithIntrinsicBounds(getDrawable(parent, R.drawable.ic_clock), null, null, null);
        TextView eView = convertView.findViewById(R.id.text_endorsement);
        String endorsements = myEndorsements.get(requester.dataRequesterId);
        if (endorsements != null) {
            eView.setText("Endorsed by " + endorsements);
            eView.setCompoundDrawablesWithIntrinsicBounds(getDrawable(parent, R.drawable.ic_tick), null, null, null);
        } else {
            eView.setText("No endorsements");
            eView.setCompoundDrawablesWithIntrinsicBounds(getDrawable(parent, R.drawable.ic_warning), null, null, null);
        }
        rView.setCompoundDrawablesWithIntrinsicBounds(getDrawable(parent, requester.drawableResId), null, null, null);
        return convertView;
    }

    private Drawable getDrawable(View view, int resId) {
        return view.getContext().getResources().getDrawable(resId);
    }

    private Runnable notifyDatasetChangedRunnable = new Runnable() {
        @Override
        public void run() {
            myListener.onRefreshComplete(myDataRequests.length > 0);
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

            // Dummay data for test
            DataRequest request = new DataRequest(1, 1, 1, new Date(), new Date());
            myHandler.post(notifyDatasetChangedRunnable);
        }
    };

    public void itemClick(int position, final Context context) {
        final DataRequest request = (DataRequest) getItem(position);
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
                        acceptDataRequest(context, request);
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        denyDataRequest(context, request);
                    }
                })
                .setView(checkBoxView)
                .show();
    }

    public void processAllDataRequests(boolean accept, Context context) {
        for (DataRequest r : myDataRequests) {
            if (accept) {
                acceptDataRequest(context, r);
            } else {
                denyDataRequest(context, r);
            }
        }
    }

    private void denyDataRequest(final Context context, final DataRequest request) {
        myHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerReader.postDataRequestAction(context, request, ServerReader.ACTION_DENY);
                } catch (KeyManagementException | CertificateException | NoSuchAlgorithmException | IOException | KeyStoreException e) {
                    e.printStackTrace();
                }
                myDb.appDao().deleteDataRequest(request);
                populateData.run();
            }
        });
    }

    private void acceptDataRequest(final Context context, final DataRequest request) {
        myExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerReader.postDataRequestAction(context, request, ServerReader.ACTION_ACCEPT);
                } catch (KeyManagementException | CertificateException | NoSuchAlgorithmException | IOException | KeyStoreException e) {
                    e.printStackTrace();
                }
                myDb.appDao().deleteDataRequest(request);
                populateData.run();
            }
        });
    }
}
