package com.cs523team4.iotui.data_model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by aravind on 12/4/17.
 */

@Entity
public class TrustedAgent {
    @PrimaryKey
    public int trustedAgentId;

    public String name;

    public TrustedAgent(int trustedAgentId, String name) {
        this.trustedAgentId = trustedAgentId;
        this.name = name;
    }
}
