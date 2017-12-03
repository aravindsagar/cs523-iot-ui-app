package com.cs523team4.iotui.data_model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Created by aravind on 12/2/17.
 */

@Entity(primaryKeys = {"deviceId", "startDate"},
        foreignKeys = @ForeignKey(entity = Device.class,
                                  parentColumns = "deviceId",
                                  childColumns = "deviceId"))
public class DataDateRange {
    @NonNull
    public int deviceId;
    @NonNull
    public Date startDate;
    public Date endDate;

    public DataDateRange(int deviceId, Date startDate, Date endDate) {
        this.deviceId = deviceId;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
