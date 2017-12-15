package com.cs523team4.iotui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cs523team4.iotui.server_util.ServerReader;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * This Activity can be used by third parties to get access to the data owner's public key.
 * For example:
 *  Intent ti = new Intent().setAction("com.cs523team4.iotui.action.GET_OWNER_PUBLIC_KEY");
 *  startActivityForResult(ti.putExtra(GetOwnerKeyActivity.EXTRA_REQUESTER_NAME, "Self"), 1);
 *
 * Read the results in onActivityResult().
 */
public class GetOwnerKeyActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_REQUESTER_NAME = "com.cs523team4.iotui.REQUESTER_NAME";
    public static final String EXTRA_OWNER_PUBLIC_KEY = "com.cs523team4.iotui.OWNER_PUBLIC_KEY";

    private Executor myExecutor = Executors.newSingleThreadExecutor();

    private Runnable fetchMyKey = new Runnable() {
        @Override
        public void run() {
            try {
                final String publicKey = ServerReader.fetchPublicKey(GetOwnerKeyActivity.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent result = new Intent();
                        result.putExtra(EXTRA_OWNER_PUBLIC_KEY, publicKey);
                        setResult(RESULT_OK, result);
                        finish();
                    }
                });
            } catch (KeyManagementException | CertificateException | KeyStoreException | NoSuchAlgorithmException | IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                });
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_owner_key);
        String requesterName = getIntent().getStringExtra(EXTRA_REQUESTER_NAME);
        if (requesterName == null) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }
        TextView messageView = findViewById(R.id.textView_permissionMessage);
        messageView.setText(requesterName + " is requesting access to your identity. This will allow them to " +
                "request access to data that you own. Do you want to provide your identity to " + requesterName + "?");
        Button yes_button = findViewById(R.id.button_yes),
                no_button = findViewById(R.id.button_no);
        yes_button.setOnClickListener(this);
        no_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_yes:
                v.setEnabled(false);
                findViewById(R.id.button_no).setEnabled(false);
                myExecutor.execute(fetchMyKey);
                break;
            case R.id.button_no:
                v.setEnabled(false);
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
