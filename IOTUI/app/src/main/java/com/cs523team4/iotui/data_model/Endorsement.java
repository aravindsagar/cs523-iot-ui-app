package com.cs523team4.iotui.data_model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by aravind on 12/4/17.
 */

@Entity(primaryKeys = {"dataRequesterId", "trustedAgentId"},
        foreignKeys = {@ForeignKey(entity = DataRequester.class,
                                   parentColumns = "dataRequesterId",
                                   childColumns = "dataRequesterId"),
                       @ForeignKey(entity = TrustedAgent.class,
                                   parentColumns = "trustedAgentId",
                                   childColumns = "trustedAgentId")})
public class Endorsement {
    public int trustedAgentId;
    public int dataRequesterId;
}
