package com.e.noloss.utils;

import dalvik.annotation.TestTargetClass;

public class Test {
    public static void main(String[] args) {
        MqttManager mqttManager=new MqttManager();
        mqttManager.connect();
        mqttManager.subscribe("chat",1);
    }
}
