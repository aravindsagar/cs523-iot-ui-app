package com.cs523team4.iotui.data_model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by aravind on 12/2/17.
 */

@Entity(foreignKeys = @ForeignKey(entity = Device.class,
                                  parentColumns = "deviceId",
                                  childColumns = "deviceId"))
public class DeviceDataSummary {
    @PrimaryKey(autoGenerate = true)
    public int summaryId;
    public int deviceId;
    public String summaryDescription;

    public DeviceDataSummary(int deviceId, String summaryDescription) {
        this.deviceId = deviceId;
        this.summaryDescription = summaryDescription;
    }
}
