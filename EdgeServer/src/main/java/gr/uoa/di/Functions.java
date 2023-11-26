package gr.uoa.di;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import org.json.JSONObject;
import org.json.JSONException;
import com.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;

public class Functions {
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2, String unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2))
                    + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;

            if (unit.equals("K")) {
                dist = dist * 1.609344;
            } else if (unit.equals("N")) {
                dist = dist * 0.8684;
            }
            return (dist);
        }
    }

    public static void alertAndroid(String topic, String danger, MqttClient client) {
        if (danger.equals("high") || danger.equals("medium")) {
            String androidTopic = "android";
            String iotDeviceId = topic.replace("iot_device", "");
            String notificationMessage = "Danger level: " + danger + " (From IoT Device " + iotDeviceId + ")";

            MqttMessage notificationMqttMessage = new MqttMessage(notificationMessage.getBytes());

            try {
                client.publish(androidTopic, notificationMqttMessage);
            } catch (MqttException e) {
                System.err.println("Error publishing Android alert message: " + e.getMessage());
            }
        }
    }


}
