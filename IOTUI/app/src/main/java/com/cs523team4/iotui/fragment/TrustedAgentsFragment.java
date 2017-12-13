package com.cs523team4.iotui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cs523team4.iotui.R;
import com.cs523team4.iotui.data_access.AppDatabase;
import com.cs523team4.iotui.data_model.TrustedAgent;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrustedAgentsFragment extends Fragment {

    private Executor myExecutor = Executors.newSingleThreadExecutor();
    private View myRootView;

    public TrustedAgentsFragment() {
        // Required empty public constructor
    }

    private Runnable fetchData = new Runnable() {
        @Override
        public void run() {
            final TrustedAgent[] trustedAgents = AppDatabase.getInstance(getContext()).appDao().loadAllTrustedAgents();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ListView trustedAgentsView = myRootView.findViewById(R.id.list_trusted_agents);
                    if (trustedAgents.length == 0) {
                        trustedAgentsView.setVisibility(View.GONE);
                        myRootView.findViewById(R.id.text_no_items).setVisibility(View.VISIBLE);
                    } else {
                        trustedAgentsView.setAdapter(new ArrayAdapter<>(
                                getContext(), R.layout.list_item_trusted_agent, trustedAgents));
                    }
                }
            });
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myRootView = inflater.inflate(R.layout.fragment_trusted_agents, container, false);
        myExecutor.execute(fetchData);
        return myRootView;
    }

}
