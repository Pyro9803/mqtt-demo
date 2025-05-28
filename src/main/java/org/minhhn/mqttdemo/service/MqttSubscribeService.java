package org.minhhn.mqttdemo.service;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MqttSubscribeService {

    private static final Logger log = LoggerFactory.getLogger(MqttSubscribeService.class);
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
            log.error(e.toString());
        }
    }

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
