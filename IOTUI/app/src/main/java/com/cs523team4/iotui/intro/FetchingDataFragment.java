package com.cs523team4.iotui.intro;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cs523team4.iotui.R;
import com.cs523team4.iotui.server_util.ServerPullService;
import com.github.paolorotolo.appintro.ISlidePolicy;

import static android.content.Context.SHORTCUT_SERVICE;
import static com.cs523team4.iotui.server_util.ServerPullService.ACTION_REFRESH_DATA;
import static com.cs523team4.iotui.server_util.ServerPullService.BROADCAST_ACTION;
import static com.cs523team4.iotui.server_util.ServerPullService.DATA_REFRESH_RESULT;

/**
 * A simple {@link Fragment} subclass, which fetches data from the server during initialization.
 */
public class FetchingDataFragment extends Fragment implements ISlidePolicy {

    private TextView messageText;
    private ProgressBar progressBar;

    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(DATA_REFRESH_RESULT, false)) {
                messageText.setText("Refresh complete!");
            } else {
                messageText.setText("Refresh failed. Will try again later.");
            }

            progressBar.setVisibility(View.GONE);
        }
    };

    public FetchingDataFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_fetching_data, container, false);
        messageText = root.findViewById(R.id.textView);
        progressBar = root.findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
        registerBroadcastListener();
        return root;
    }

    public void startFetching() {
        getActivity().startService(new Intent(getContext(), ServerPullService.class).setAction(ACTION_REFRESH_DATA));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregisterBroadcastListener();
    }

    /**
     * Register broadcast listeners which lets us know when data refresh is complete.
     */
    private void registerBroadcastListener() {
        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
        // Registers the receiver with the new filter
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(myBroadcastReceiver, intentFilter);
    }

    private void unregisterBroadcastListener() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(myBroadcastReceiver);
    }

    @Override
    public boolean isPolicyRespected() {
        return progressBar.getVisibility() == View.GONE;
    }

    @Override
    public void onUserIllegallyRequestedNextPage() {
        Snackbar.make(progressBar, "Please wait, fetching data from server", Snackbar.LENGTH_SHORT).show();
    }
}
