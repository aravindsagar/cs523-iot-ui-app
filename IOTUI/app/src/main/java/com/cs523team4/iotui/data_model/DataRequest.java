package com.cs523team4.iotui.data_model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * Created by aravind on 12/3/17.
 */

@Entity
public class DataRequest {
    @PrimaryKey
    public int requestId;

    public int dataRequesterId;
    public int summaryId;
    public Date startDate;
    public Date endDate;

    public DataRequest(int dataRequesterId, int summaryId, Date startDate, Date endDate) {
        this.dataRequesterId = dataRequesterId;
        this.summaryId = summaryId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public AccessPermission toAccessPermission() {
        return new AccessPermission(dataRequesterId, summaryId, startDate, endDate);
    }
}