package gr.uoa.di;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.sql.Connection;




public class main {




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


//            for (String topic : topics) {
//                client.subscribe(topic);
//                System.out.println("Subscribed to topic: " + topic);
//            }
            client.subscribe("#");

            IotDevice iotDevice1 = new IotDevice(client, "iot_device1");
            IotDevice iotDevice2 = new IotDevice(client, "iot_device2");
            String json = "{ \"android_id\": 1, \"latitude\": 37.7749, \"longitude\": -122.4194 }";
            AndroidData androidData = new AndroidData(json);

            MessageProcessor messageProcessor = new MessageProcessor(iotDevice1, iotDevice2, dbConnection,androidData);


            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection lost: " + cause.getMessage());
                    if (cause instanceof MqttException) {
                        MqttException mqttException = (MqttException) cause;
                        System.out.println("Detailed reason: ");
                        mqttException.printStackTrace();
                    }


                }

                @Override

                public void messageArrived(String topic, MqttMessage message) throws Exception {


                    //System.out.println(topic);
                    if (topic.startsWith("iot_device") ) {

                        messageProcessor.processMessage(topic, message, client);
                    }


                    if(topic.startsWith("android")){

                        String payload = new String(message.getPayload());
                        String[] parts = payload.split(":", 2);

                        if (parts.length == 2 && parts[0].equals("server")) {
                            // This is a message from the server, do not process
                         // System.out.println("Received a message from the server: " + parts[1]);
                        } else {
                            // This is a message from an external source, proceed with regular processing
                            messageProcessor.processAndroidMessage(topic, message, client);
                        }
                    }

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



