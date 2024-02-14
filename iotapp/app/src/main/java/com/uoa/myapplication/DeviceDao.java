package com.uoa.myapplication;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DeviceDao {

    @Insert
    void insert(Device device);

    @Query("SELECT * FROM devices WHERE device_id = :androidId LIMIT 1")
    Device findByAndroidId(String androidId);

    @Query("SELECT * FROM devices")
    List<Device> getAllDevices();

    // You can add more methods here as needed
}
