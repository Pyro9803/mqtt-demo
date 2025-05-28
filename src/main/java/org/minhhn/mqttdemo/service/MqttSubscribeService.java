package org.minhhn.mqttdemo.service;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

@Service
public class MqttSubscribeService {

    private final IMqttClient mqttClient;

    public MqttSubscribeService(IMqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    public void subscribe() {
        try {
            if (!mqttClient.isConnected()) {
                mqttClient.connect();
            }

            mqttClient.subscribe("temperature", (s, message) -> {
                String payload = new String(message.getPayload());
                System.out.println("Received message: " + payload);
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/unsubscribe")
    public void unsubscribe() {
        try {
            if (mqttClient.isConnected()) {
                mqttClient.disconnect();
            }
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }
}
