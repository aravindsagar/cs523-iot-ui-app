package com.cs523team4.iotui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cs523team4.iotui.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrustedAgentsFragment extends Fragment {


    public TrustedAgentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trusted_agents, container, false);
    }

}
