package com.cs523team4.iotui.data_model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * Created by aravind on 12/3/17.
 */

@Entity(foreignKeys = {
        @ForeignKey(entity = DataRequester.class,
                parentColumns = "dataRequesterId",
                childColumns = "dataRequesterId"),
        @ForeignKey(entity = DeviceDataSummary.class,
                parentColumns = "summaryId",
                childColumns = "summaryId")})
public class DataRequest {
    @PrimaryKey
    public int requestId;

    public int dataRequesterId;
    public int summaryId;
    public Date startDate;
    public Date endDate;

    public DataRequest(int requestId, int dataRequesterId, int summaryId, Date startDate, Date endDate) {
        this.requestId = requestId;
        this.dataRequesterId = dataRequesterId;
        this.summaryId = summaryId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public AccessPermission toAccessPermission() {
        return new AccessPermission(requestId, dataRequesterId, summaryId, startDate, endDate);
    }
}
