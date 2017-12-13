package com.cs523team4.iotui.server_util;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.json.JSONException;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.ParseException;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class ServerPullService extends IntentService {
    // Actions that this IntentService can perform.
    public static final String ACTION_REFRESH_DATA = "com.cs523team4.iotui.server_util.action.REFRESH_DATA";
    public static final String BROADCAST_ACTION = "com.cs523team4.iotui.server_util.action.BROADCAST";
    public static final String DATA_REFRESH_RESULT = "com.cs523team4.iotui.server_util.action.REFRESH_RESULT";

    public ServerPullService() {
        super("ServerPullService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_REFRESH_DATA.equals(action)) {
                handleRefreshData();
            }
        }
    }

    /**
     * Handle action refresh data in the provided background thread.
     */
    private void handleRefreshData() {
        try {
            ServerReader.readServerData(this);
            reportResult(true);
        } catch (Exception e) {
            e.printStackTrace();
            reportResult(false);
        }
    }

    /**
     * Broadcasts the result of our operations, that is, whether we succeeded or not.
     */
    private void reportResult(boolean result) {
        Intent localIntent =
                new Intent(BROADCAST_ACTION)
                        // Puts the status into the Intent
                        .putExtra(DATA_REFRESH_RESULT, result);
        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }
}
