package com.cs523team4.iotui.data_model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by aravind on 12/2/17.
 */

@Entity(foreignKeys = @ForeignKey(entity = DataSource.class,
                                  parentColumns = "dataSourceId",
                                  childColumns = "dataSourceId"))
public class Device {

    @PrimaryKey
    public int deviceId;

    public String deviceName;
    public String type;
    public String location;
    public int dataSourceId;
    public String diskSpaceUsage;

    // Temporary
    public int drawableResId;

    public Device(int deviceId, String deviceName, String type, String location, int dataSourceId, String diskSpaceUsage, int drawableResId) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.type = type;
        this.location = location;
        this.dataSourceId = dataSourceId;
        this.diskSpaceUsage = diskSpaceUsage;
        this.drawableResId = drawableResId;
    }
}
