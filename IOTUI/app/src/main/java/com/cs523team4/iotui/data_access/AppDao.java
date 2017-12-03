package com.cs523team4.iotui.data_access;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.cs523team4.iotui.data_model.AccessPermission;
import com.cs523team4.iotui.data_model.DataDateRange;
import com.cs523team4.iotui.data_model.DataRequester;
import com.cs523team4.iotui.data_model.DataSource;
import com.cs523team4.iotui.data_model.Device;
import com.cs523team4.iotui.data_model.DeviceDataSummary;
import com.cs523team4.iotui.data_model.pojo.AccessPermissionDeviceTuple;
import com.cs523team4.iotui.data_model.pojo.DataRequesterSummaryDescriptionTuple;

/**
 * Created by aravind on 12/2/17.
 */

@Dao
public abstract class AppDao {
    @Insert
    public abstract long insertDevice(Device device);

    @Insert
    public abstract long insertDataSource(DataSource dataSource);

    @Insert
    public abstract long insertDeviceDataSummary(DeviceDataSummary deviceDataSummary);

    @Insert
    public abstract long insertDataRequester(DataRequester dataRequester);

    @Insert
    public abstract long insertDataDateRange(DataDateRange dataDateRange);

    @Insert
    public abstract long insertAccessPermission(AccessPermission accessPermission);

    @Query("SELECT * FROM Device")
    public abstract Device[] loadAllDevices();

    @Query("SELECT * FROM Device WHERE deviceId = (:deviceId) LIMIT 1")
    public abstract Device loadDevice(int deviceId);

    @Query("SELECT * FROM DataDateRange "
            + "WHERE DataDateRange.deviceId = (:deviceId)")
    public abstract DataDateRange[] loadDataDateRangesForDevice(int deviceId);

    @Query("SELECT * FROM DataSource WHERE dataSourceId = (:id) LIMIT 1")
    public abstract DataSource loadDataSource(int id);

    @Query("SELECT * FROM DataSource")
    public abstract DataSource[] loadAllDataSources();

    @Query("SELECT * FROM DeviceDataSummary WHERE DeviceDataSummary.deviceId = (:deviceId)")
    public abstract DeviceDataSummary[] loadDeviceDataSummaries(int deviceId);

    @Query("SELECT * FROM AccessPermission "
            + "WHERE AccessPermission.summaryId IN (:deviceDataSummaryIds)")
    public abstract AccessPermission[] loadAccessPermissions(int[] deviceDataSummaryIds);

    @Query("SELECT AccessPermission.accessPermissionId, DeviceDataSummary.deviceId FROM AccessPermission "
            + "INNER JOIN DeviceDataSummary ON DeviceDataSummary.summaryId = AccessPermission.summaryId")
    public abstract AccessPermissionDeviceTuple[] loadAccessPermissionDeviceTuples();

    @Query("SELECT * FROM DataRequester WHERE dataRequesterId = (:dataRequesterId) LIMIT 1")
    public abstract DataRequester loadDataRequesters(int dataRequesterId);

    @Query("SELECT DataRequester.name, DeviceDataSummary.summaryDescription FROM AccessPermission "
            + "INNER JOIN DeviceDataSummary ON AccessPermission.summaryId = DeviceDataSummary.summaryId "
            + "INNER JOIN DataRequester ON AccessPermission.dataRequesterId = DataRequester.dataRequesterId "
            + "WHERE DeviceDataSummary.deviceId = (:deviceId)")
    public abstract DataRequesterSummaryDescriptionTuple[] loadDataRequesterSummaryDescriptionTuples(int deviceId);
}