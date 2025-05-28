package org.minhhn.mqttdemo.service;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Service;

@Service
public class MqttSubscribeService {

    private final IMqttClient mqttClient;

    public MqttSubscribeService(IMqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    public void subscribeToTopic(String topic) {
        try {
            if (!mqttClient.isConnected()) {
                mqttClient.connect();
            }

            mqttClient.subscribe(topic, (s, message) -> {
                String payload = new String(message.getPayload());
                System.out.println("Received message: " + payload);
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
