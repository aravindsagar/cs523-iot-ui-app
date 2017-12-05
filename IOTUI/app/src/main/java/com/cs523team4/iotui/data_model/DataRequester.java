package com.cs523team4.iotui.data_model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by aravind on 12/2/17.
 */

@Entity
public class DataRequester {

    @PrimaryKey(autoGenerate = true)
    public int dataRequesterId;

    public String pubicKey;
    public String name;
    public int drawableResId;

    public DataRequester(String pubicKey, String name, int drawableResId) {
        this.pubicKey = pubicKey;
        this.name = name;
        this.drawableResId = drawableResId;
    }

}
