package gr.uoa.di;


import org.eclipse.paho.client.mqttv3.*;
import java.sql.Connection;
import org.json.JSONObject;
import org.json.JSONException;


public class MessageProcessor {
    private IotDevice iotDevice1;
    private IotDevice iotDevice2;

    private AndroidData androidData;
    private double androidLatitude = 0.0;
    private double androidLongitude = 0.0;
    private Connection dbConnection;

    public MessageProcessor(IotDevice iotDevice1, IotDevice iotDevice2, Connection dbConnection) {
        this.iotDevice1 = iotDevice1;
        this.iotDevice2 = iotDevice2;
        this.dbConnection = dbConnection; // Initialize dbConnection
    }

    public void processMessage(String topic, MqttMessage message, MqttClient client) {
        String payload = new String(message.getPayload());
        System.out.println("Received message on topic '" + topic + "': " + payload);

        int iotDeviceNumber = 0;

        if (topic.startsWith("iot_device1")) {
            iotDeviceNumber = 1;
        } else if (topic.startsWith("iot_device2")) {
            iotDeviceNumber = 2;
        }

        if (topic.startsWith("iot_device1") || topic.startsWith("iot_device2")) {
            IotDevice currentIotDevice = topic.startsWith("iot_device1") ? iotDevice1 : iotDevice2;
            currentIotDevice.handleMessage(payload);
            Functions.alertAndroid(topic, currentIotDevice.getDanger(), client);
            double distanceToCurrentIot = currentIotDevice.calculateDistanceToAndroid(androidLatitude, androidLongitude);

            // Use the getter methods to retrieve the data values
            double latitudeValue = currentIotDevice.getLatitude();
            double longitudeValue = currentIotDevice.getLongitude();
            double smokeValue = currentIotDevice.getSmoke();
            double batteryValue = currentIotDevice.getBattery();
            double gasValue = currentIotDevice.getGas();
            double temperatureValue = currentIotDevice.getTemperature();
            double uvValue = currentIotDevice.getUv();

            // Create a Data object
            Data eventData = new Data(latitudeValue, longitudeValue, batteryValue,smokeValue, gasValue, temperatureValue, uvValue);
            // Insert the data into the database
            eventData.insertData(dbConnection, iotDeviceNumber, latitudeValue, longitudeValue, smokeValue, gasValue, temperatureValue, uvValue, currentIotDevice.getDanger());

            try {
                currentIotDevice.alertDistanceToAndroid(client, distanceToCurrentIot, "android", topic);
                checkAndPrintHighDanger(iotDevice1.getDanger(), iotDevice2.getDanger(), client);
            } catch (MqttException e) {

                e.printStackTrace();
            }
        } else if (topic.startsWith("android")) {
            try {

                androidData = new AndroidData(payload);  // Parse Android data from the payload
                androidData.insertAndroidData(dbConnection);






            }  catch (JSONException e) {
            System.err.println("Error parsing JSON payload on 'android' topic: " + e.getMessage());
            System.err.println("Received JSON payload: " + payload);
            e.printStackTrace();  // Print the stack trace for detailed information
        }

    }

    }

    private void checkAndPrintHighDanger(String danger1, String danger2, MqttClient client) {
        if (IotDevice.checkHighDanger(danger1, danger2)) {
            // Calculate the midpoint
            double[] midpoint = IotDevice.calculateMidpoint(iotDevice1.getLatitude(), iotDevice1.getLongitude(), iotDevice2.getLatitude(), iotDevice2.getLongitude());

            String message = "Both IoT devices have a high danger level. Midpoint distance: " + calculateDistanceToMidpoint(midpoint) + " meters";

            System.out.println(message);

            String androidTopic = "android";
            MqttMessage androidMessage = new MqttMessage(message.getBytes());

            try {
                client.publish(androidTopic, androidMessage);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    private double calculateDistanceToMidpoint(double[] midpoint) {
        // Calculate the distance from the Android to the midpoint
        return Functions.calculateDistance(androidLatitude, androidLongitude, midpoint[0], midpoint[1], "M");
    }




}
