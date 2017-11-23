package com.cs523team4.iotui.data_model;

import com.cs523team4.iotui.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aravind on 11/23/17.
 */

public class DeviceData {
    private int myDrawable;
    private String myName, mySource, myStorageUse;
    private Map<String, String> myAccessDetails = new HashMap<>();

    public DeviceData(int drawable, String name, String source, String storageUse) {
        this.myDrawable = drawable;
        this.myName = name;
        this.mySource = source;
        this.myStorageUse = storageUse;
    }

    public void addAccessDetails(String requesterName, String accessLevel) {
        myAccessDetails.put(requesterName, accessLevel);
    }

    public int getDrawable() {
        return myDrawable;
    }

    public String getName() {
        return myName;
    }

    public String getSource() {
        return mySource;
    }

    public String getStorageUse() {
        return myStorageUse;
    }

    public Map<String, String> getAccessDetails() {
        return myAccessDetails;
    }

    public boolean isDataShared() {
        return !myAccessDetails.isEmpty();
    }

    public static DeviceData[] getDeviceData() {
        //Dummy data for now.
        DeviceData[] data = new DeviceData[] {
                new DeviceData(R.drawable.ic_menu_camera, "Living room camera", "Google Drive", "15 GB"),
                new DeviceData(R.drawable.ic_location_on_black_24dp, "Phone GPS", "Box", "150 MB"),
                new DeviceData(R.drawable.ic_ac_unit_black_24dp, "Home thermostat", "Nest", "50 MB"),
        };
        data[0].addAccessDetails("Anti-intruder app", "access to realtime data");
        data[0].addAccessDetails("Google photos", "full access");
        data[2].addAccessDetails("Smarthome app", "access to hourly average");
        return data;
    }
}
