package com.uoa.sensor2;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

//Implement the required callback methods from MqttCallbackExtended
public class MQTTCallbackHandler extends Service implements MqttCallbackExtended {

    public interface MQTTCallbackListener {
        void onMessageReceived(String topic, MqttMessage message);
        void onConnectionLost();
        void onReconnected();
    }

    private final MQTTCallbackListener callbackListener;

    public MQTTCallbackHandler(MQTTCallbackListener listener) {
        this.callbackListener = listener;
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        if (reconnect) {
            callbackListener.onReconnected();
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("Connection lost");
        //cause.printStackTrace();
        callbackListener.onConnectionLost();
    }

    @Override
    //whenever a message is received on the topic for which your client has subscribed
    public void messageArrived(String topic, MqttMessage message) {
        System.out.println("Message arrived " + message);
        callbackListener.onMessageReceived(topic, message);
    }

    @Override
    // handle publish notification events
    public void deliveryComplete(IMqttDeliveryToken token) {
        System.out.println("Message delivered " + token);
        // Optional: handle message delivery completion if needed
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("onBind(Intent) never called");
        return null;
    }
}