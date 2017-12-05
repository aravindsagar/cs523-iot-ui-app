package com.cs523team4.iotui;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;

import com.cs523team4.iotui.server_util.ServerPullService;

import static com.cs523team4.iotui.server_util.ServerPullService.ACTION_REFRESH_DATA;
import static com.cs523team4.iotui.server_util.ServerPullService.BROADCAST_ACTION;
import static com.cs523team4.iotui.server_util.ServerPullService.DATA_REFRESH_RESULT;

/**
 * Created by aravind on 12/4/17.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PeriodicSyncJobService extends JobService {

    private JobParameters myParams;
    private boolean myJobFinished;

    @Override
    public boolean onStartJob(JobParameters params) {
        myParams = params;
        myJobFinished = false;

        registerBroadcastListener();
        startService(new Intent(this, ServerPullService.class).setAction(ACTION_REFRESH_DATA));
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        unregisterBroadcastListener();
        return !myJobFinished;
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(DATA_REFRESH_RESULT, false)) {
                jobFinished(myParams, false);
                myJobFinished = true;
            } else {
                jobFinished(myParams, true);
            }
        }
    };

    private void registerBroadcastListener() {
        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
        // Registers the receiver with the new filter
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, intentFilter);
    }

    private void unregisterBroadcastListener() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
    }
}
