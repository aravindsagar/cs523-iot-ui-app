package com.cs523team4.iotui.data_model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by aravind on 12/2/17.
 */

@Entity
public class DataSource {
    @PrimaryKey(autoGenerate = true)
    public int dataSourceId;

    public String name;
    public String publicKey;

    public DataSource(String name, String publicKey) {
        this.name = name;
        this.publicKey = publicKey;
    }
}
