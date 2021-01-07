package com.e.noloss.utils;

import android.content.Context;
import android.util.Log;
import android.webkit.WebMessagePort;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttManager {
    public static final String TAG = MqttManager.class.getSimpleName();

    private String host = "tcp://192.168.0.101:18306";
    private String clientId = "";

    private static MqttManager mqttManager = null;
    private MqttClient client;
    private MqttConnectOptions connectOptions;

    public MqttManager(Context context){
        clientId = MqttClient.generateClientId();
    }

    public MqttManager() {
    }

    public MqttManager getInstance(Context context){
        if(mqttManager == null){
            mqttManager = new MqttManager(context);
        }else{
            return mqttManager;
        }
        return null;
    }

    public void connect(){
        try{
            client = new MqttClient(host,clientId,new MemoryPersistence());
            connectOptions = new MqttConnectOptions();
            client.setCallback(mqttCallback);
            client.connect(connectOptions);
        }catch (MqttException e){
            e.printStackTrace();
        }
    }

    public void subscribe(String topic,int qos){
        if(client != null){
            int[] Qos = {qos};
            String[] topic1 = {topic};
            try {
                client.subscribe(topic1, Qos);
                Log.d(TAG,"订阅topic : "+topic);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public void publish(String topic,String msg,boolean isRetained,int qos) {
        try {
            if (client!=null) {
                MqttMessage message = new MqttMessage();
                message.setQos(qos);
                message.setRetained(isRetained);
                message.setPayload(msg.getBytes());
                client.publish(topic, message);
            }
        } catch (MqttPersistenceException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private MqttCallback mqttCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
            System.out.println("connectionLost");
            Log.i(TAG,"connection lost");
        }

        @Override
        public void messageArrived(String topic, MqttMessage message){
            System.out.println("messageArrived");
            Log.i(TAG,"received topic : " + topic);
            String payload = new String(message.getPayload());
            Log.i(TAG,"received msg : " + payload);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            System.out.println("deliveryComplete");
            Log.i(TAG,"deliveryComplete");
        }
    };

}