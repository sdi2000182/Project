package gr.uoa.di;

import org.json.JSONException;
import org.json.JSONObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Data {
    private double latitude;
    private double longitude;
    private double battery;
    private double smoke;
    private double gas;
    private double temperature;
    private double uv;

    public Data(double latitude, double longitude, double battery, double smoke, double gas, double temperature, double uv) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.battery = battery;
        this.smoke = smoke;
        this.gas = gas;
        this.temperature = temperature;
        this.uv = uv;
    }

    public Data(String json) {


            JSONObject jsonData = new JSONObject(json);
            try {
                this.latitude = jsonData.getDouble("latitude");
            } catch (JSONException e){
                this.latitude = 0.0;
            }
            try {
                this.longitude = jsonData.getDouble("longitude");
            }catch(JSONException e){
                this.longitude = 0.0;
            }
            try {
                this.battery = jsonData.getDouble("battery");
            }catch (JSONException e){
                this.battery = 0.0;
            }
            try {
                this.smoke = jsonData.getDouble("smoke");
            }catch(JSONException e){
                this.smoke = 0.0;
            }
            try {
                this.gas = jsonData.getDouble("gas");
            }catch (JSONException e){
                this.gas = 0.0;
            }
            try {
                this.temperature = jsonData.getDouble("temp");
            }catch(JSONException e){
                this.temperature = 0.0;
            }
            try {
                this.uv = jsonData.getDouble("uv");
            }catch (JSONException e){
                this.uv = 0.0;
            }


    }



    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getBattery() {
        return battery;
    }

    public double getSmoke() {
        return smoke;
    }

    public double getGas() {
        return gas;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getUv() {
        return uv;
    }

    public void insertData(Connection connection, int iotDevice, double latitude, double longitude, double smoke, double gas, double temperature, double uv, String danger) {
        String insertQuery = "INSERT INTO Events (Timestamp, Iot_Device, Latitude, Longitude, Smoke, Gas, Temperature, UV, Danger) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, getCurrentTimestamp());
            preparedStatement.setInt(2, iotDevice);
            preparedStatement.setDouble(3, latitude);
            preparedStatement.setDouble(4, longitude);
            preparedStatement.setDouble(5, smoke);
            preparedStatement.setDouble(6, gas);
            preparedStatement.setDouble(7, temperature);
            preparedStatement.setDouble(8, uv);
            preparedStatement.setString(9, danger);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert data into the database: " + e.getMessage());
        }
    }


    public String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }


}


