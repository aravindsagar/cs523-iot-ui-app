package com.cs523team4.iotui.data_access;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by aravind on 12/2/17.
 */

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
