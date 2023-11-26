package gr.uoa.di;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.sql.Connection;




public class main {



    // Connect to the database

    public static void main(String[] args) throws Exception {



        System.out.println("Hello and welcome!\n");
        Connection dbConnection;
        dbConnection = DbConnection.connect();


        String broker = "tcp://localhost:1883";
        String[] topics =  {"android", "iot_device1", "iot_device2"};
        String clientId = "subscribe_client";



        MemoryPersistence persistence=new MemoryPersistence();
        try {
            MqttClient client = new MqttClient(broker, clientId, persistence);  //MQTT client connection
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            System.out.println("Connecting to MQTT broker: " + broker);
            client.connect(connOpts);
            System.out.println("Connected to MQTT broker: " + broker);


            for (String topic : topics) {
                client.subscribe(topic);
                System.out.println("Subscribed to topic: " + topic);
            }

            IotDevice iotDevice1 = new IotDevice(client, "iot_device1");
            IotDevice iotDevice2 = new IotDevice(client, "iot_device2");

            MessageProcessor messageProcessor = new MessageProcessor(iotDevice1, iotDevice2, dbConnection);


            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection lost: " + cause.getMessage());

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    messageProcessor.processMessage(topic,message,client);

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });

            while(true) {

            }
        }catch (MqttException e){
            throw new RuntimeException(e);
        }finally {
            DbConnection.close(dbConnection);
        }


    }





}



