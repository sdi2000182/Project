package gr.uoa.di;


import org.eclipse.paho.client.mqttv3.*;
import java.sql.Connection;

import org.json.JSONException;


public class MessageProcessor {
    private IotDevice iotDevice1;
    private IotDevice iotDevice2;

    private double androidLatitude = 0;

    private double androidLongitude = 0;

    private AndroidData androidData;

    private Connection dbConnection;

    public MessageProcessor(IotDevice iotDevice1, IotDevice iotDevice2, Connection dbConnection,AndroidData androidData) {
        this.iotDevice1 = iotDevice1;
        this.iotDevice2 = iotDevice2;
        this.androidData = androidData;
        this.dbConnection = dbConnection; // Initialize dbConnection
    }

    public void processMessage(String topic, MqttMessage message, MqttClient client) {

        String payload = new String(message.getPayload());
        System.out.println("Received message on topic '" + topic + "': " + payload);

        int iotDeviceNumber = 0;



        if (topic.startsWith("iot_device1/4f1a9607fbabf94f")) {
            iotDeviceNumber = 1;

        } else if (topic.startsWith("iot_device1/d55d31a3469e73af")) {
            iotDeviceNumber = 2;
        }



        if (topic.startsWith("iot_device1") || topic.startsWith("iot_device2")) {


            IotDevice currentIotDevice = topic.startsWith("iot_device1") ? iotDevice1 : iotDevice2;
            currentIotDevice.handleMessage(payload);

            double distanceToCurrentIot = currentIotDevice.calculateDistanceToAndroid(androidLatitude,androidLongitude);


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
                double[] midpoint = checkAndPrintHighDanger(iotDevice1.getDanger(), iotDevice2.getDanger(), client);

                if (midpoint != null) {
                    currentIotDevice.alertDistanceToAndroid(client, calculateDistanceToMidpoint(midpoint), androidData.getAndroidId(), currentIotDevice.getDanger(), iotDeviceNumber);
                } else {
                    currentIotDevice.alertDistanceToAndroid(client, distanceToCurrentIot, androidData.getAndroidId(), currentIotDevice.getDanger(), iotDeviceNumber);
                }
            } catch (MqttException e) {
                e.printStackTrace();
            }

        }

    }


    public void processAndroidMessage(String topic, MqttMessage message, MqttClient client){
        String payload = new String(message.getPayload());
        System.out.println("Received message on topic '" + topic + "': " + payload);
        if (topic.startsWith("android") ) {

            try {

                try{
                    androidData = new AndroidData(payload);  // Parse Android data from the payload
                }catch (Exception ignored){
                    return;
                }
                androidData.insertAndroidData(dbConnection);

            }  catch (JSONException e) {
                System.err.println("Error parsing JSON payload on 'android' topic: " + e.getMessage());
                System.err.println("Received JSON payload: " + payload);
                e.printStackTrace();  // Print the stack trace for detailed information
            }

        }
    }

    private double[] checkAndPrintHighDanger(String danger1, String danger2, MqttClient client) {
        double[] midpoint = null;

        if (IotDevice.checkHighDanger(danger1, danger2)) {
            // Calculate the midpoint
            midpoint = IotDevice.calculateMidpoint(iotDevice1.getLatitude(), iotDevice1.getLongitude(), iotDevice2.getLatitude(), iotDevice2.getLongitude());

            String message = "Both IoT devices have a high danger level. Midpoint distance: " + calculateDistanceToMidpoint(midpoint) + " meters";

            System.out.println(message);

            // Prepend the server identifier to the payload
            String serverIdentifier = "server";
            String serverPayload = serverIdentifier + ":" + message;

            String androidTopic = "android";
            MqttMessage androidMessage = new MqttMessage(serverPayload.getBytes());

            try {
                client.publish(androidTopic, androidMessage);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        return midpoint;
    }


    private double calculateDistanceToMidpoint(double[] midpoint) {
        // Calculate the distance from the Android to the midpoint
        double androidLatitude = androidData.getLatitude();
        double androidLongitude = androidData.getLongitude();
        return Functions.calculateDistance(androidLatitude, androidLongitude, midpoint[0], midpoint[1], "M");
    }




}
