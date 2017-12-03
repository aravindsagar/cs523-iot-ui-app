package com.cs523team4.iotui.intro;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.cs523team4.iotui.R;
import com.github.paolorotolo.appintro.ISlidePolicy;

/**
 * Log in fragment.
 */
public class LogInFragment extends Fragment implements ISlidePolicy {
    private static final int SHAKE_X = 25;

    private EditText myUsernameView;
    private EditText myPasswordView;

    public LogInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_log_in, container, false);
        myUsernameView = layout.findViewById(R.id.username);
        myPasswordView = layout.findViewById(R.id.password);
        return layout;
    }

    @Override
    public boolean isPolicyRespected() {
        String username = myUsernameView.getText().toString();
        String password = myPasswordView.getText().toString();

        // TODO Authenticate using our server.
        boolean success = !username.isEmpty();

        if (success) {
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
                    .putString(getString(R.string.pref_key_username), username).apply();
        }

        return success;
    }

    @Override
    public void onUserIllegallyRequestedNextPage() {
        myPasswordView.setText("");
        Snackbar.make(myPasswordView, R.string.incorrect_cred, Snackbar.LENGTH_SHORT).show();
        ObjectAnimator.ofFloat(myPasswordView, "translationX",
                -SHAKE_X, SHAKE_X, -SHAKE_X, SHAKE_X, -SHAKE_X, SHAKE_X, -SHAKE_X, 0)
                .setDuration(1000)
                .start();
    }
}
