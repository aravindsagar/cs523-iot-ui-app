package com.cs523team4.iotui.adapter;

/**
 * A listener which activities can use to listen for refresh complete events from the adapters
 * defined in this package.
 *
 * Created by aravind on 12/13/17.
 */
public interface RefreshCompleteListener {
    void onRefreshComplete(boolean success);
}
