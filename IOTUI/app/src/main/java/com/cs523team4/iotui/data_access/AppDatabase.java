package com.cs523team4.iotui.data_access;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.cs523team4.iotui.data_model.AccessPermission;
import com.cs523team4.iotui.data_model.DataDateRange;
import com.cs523team4.iotui.data_model.DataRequest;
import com.cs523team4.iotui.data_model.DataRequester;
import com.cs523team4.iotui.data_model.DataSource;
import com.cs523team4.iotui.data_model.Device;
import com.cs523team4.iotui.data_model.DeviceDataSummary;
import com.cs523team4.iotui.data_model.Endorsement;
import com.cs523team4.iotui.data_model.TrustedAgent;

/**
 * Room database class which gives access to our database.
 * Created by aravind on 12/2/17.
 */

@Database(entities = {AccessPermission.class, DataDateRange.class, DataRequester.class,
                      DataSource.class, Device.class, DeviceDataSummary.class, DataRequest.class,
                      TrustedAgent.class, Endorsement.class},
          version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public static final String DB_NAME = "device_data";

    public static AppDatabase getInstance(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, DB_NAME).build();
    }

    public abstract AppDao appDao();
}
