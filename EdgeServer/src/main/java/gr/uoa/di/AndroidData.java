package gr.uoa.di;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.eclipse.paho.client.mqttv3.MqttException;


public class AndroidData {
    private int androidId;
    private double latitude;
    private double longitude;

    public AndroidData(String json) {
        try {
            JSONObject jsonData = new JSONObject(json);
            this.androidId = jsonData.getInt("android_id");
            this.latitude = jsonData.getDouble("latitude");
            this.longitude = jsonData.getDouble("longitude");
        } catch (JSONException e) {
            throw new RuntimeException("Error parsing Android JSON data: " + e.getMessage());
        }
    }

    public int getAndroidId() {
        return androidId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void insertAndroidData(Connection connection) {
        String insertQuery = "INSERT INTO Android_Data (Timestamp, Android_ID, Longitude, Latitude) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, this.getcurrentTimestamp());
            preparedStatement.setInt(2, androidId);
            preparedStatement.setDouble(3, longitude);
            preparedStatement.setDouble(4, latitude);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert Android data into the database: " + e.getMessage());
        }
    }

    private String getcurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }




    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }
}
