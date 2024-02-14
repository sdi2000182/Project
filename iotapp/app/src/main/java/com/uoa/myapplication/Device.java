package com.uoa.myapplication;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "devices")
public class Device {

    @PrimaryKey(autoGenerate = true)
    private int deviceCount; // Autoincrement primary key

    @ColumnInfo(name = "device_id")
    private String deviceId;

    // Constructor
    public Device(String deviceId) {
        this.deviceId = deviceId;
    }

    // Getters and setters
    public int getDeviceCount() {
        System.out.println("db: " + deviceCount);
        return deviceCount;
    }

    public void setDeviceCount(int count) {
        this.deviceCount = count;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
