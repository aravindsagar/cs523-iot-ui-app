package com.cs523team4.iotui.data_model.pojo;

import android.arch.persistence.room.ColumnInfo;

/**
 * Created by aravind on 12/3/17.
 */

public class DataRequesterSummaryDescriptionTuple {

    // Data requester name
    @ColumnInfo(name = "name")
    public String requesterName;

    public String summaryDescription;
}
