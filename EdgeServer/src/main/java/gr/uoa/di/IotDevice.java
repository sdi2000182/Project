package gr.uoa.di;

import java.util.function.Function;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import org.json.JSONObject;
import org.json.JSONException;



public class IotDevice {


    private String deviceId;
    private Data data;
    private String danger = "low";
    private MqttClient client;

    public IotDevice(MqttClient client, String deviceId) {
        this.client = client;
        this.deviceId = deviceId;
    }

    public IotDevice(){


    }

    public void handleMessage(String payload) {
        data = new Data(payload);
        updateDanger();
    }

    private void updateDanger() {
        // Determine danger level based on data
        double smoke = data.getSmoke();
        double gas = data.getGas();
        double temperature = data.getTemperature();
        double uv = data.getUv();

        if (smoke > 0.14 && gas > 9.15) {
            danger = "high";
        } else if (smoke <= 0.14 && gas <= 9.15 && temperature > 50 && uv > 6) {
            danger = "medium";
        } else if (gas > 9.15) {
            danger = "high";
        } else {
            danger = "low";
        }
    }
    public String getDanger() {
        return danger;
    }

    public double calculateDistanceToAndroid(double androidLatitude, double androidLongitude) {
        double latitude = data.getLatitude();
        double longitude = data.getLongitude();
        return Functions.calculateDistance(androidLatitude, androidLongitude, latitude, longitude, "M");
    }

    public void alertDistanceToAndroid(IMqttClient client, double distance,  int androidID, String danger, int iotDeviceID) throws MqttException {
        try {
            JSONObject androidInfoJson = new JSONObject();
            androidInfoJson.put("android_id", androidID);
            androidInfoJson.put("danger", danger);
            androidInfoJson.put("distance_from_iot", distance);


           // String serverIdentifier = "server";
           // String serverPayload = serverIdentifier + ":" + androidInfoJson.toString();
            String serverPayload = androidInfoJson.toString();

            System.out.println("JSON Payload: " + serverPayload);

            MqttMessage androidMessage = new MqttMessage(serverPayload.getBytes());
            client.publish("android", androidMessage);
        } catch (JSONException e) {
            System.err.println("Error creating Android info JSON: " + e.getMessage());
        }
    }


    public static boolean checkHighDanger(String danger1, String danger2) {
        return danger1.equals("high") && danger2.equals("high");
    }

    public static double[] calculateMidpoint(double lat1, double lon1, double lat2, double lon2) {
        double midLat = (lat1 + lat2) / 2;
        double midLon = (lon1 + lon2) / 2;

        return new double[] { midLat, midLon };
    }

    public double getLatitude() {
        return data.getLatitude();
    }

    public double getLongitude() {
        return data.getLongitude();
    }

    public double getBattery() {
        return data.getBattery();
    }

    public double getSmoke() {
        return data.getSmoke();
    }

    public double getGas() {
        return data.getGas();
    }

    public double getTemperature() {
        return data.getTemperature();
    }

    public double getUv() {
        return data.getUv();
    }


}
