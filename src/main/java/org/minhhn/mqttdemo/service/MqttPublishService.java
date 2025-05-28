package org.minhhn.mqttdemo.service;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class MqttPublishService {

    private final IMqttClient mqttClient;

    public MqttPublishService(IMqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    public void publish(String topic, String payload) throws MqttException {
        if (!mqttClient.isConnected()) {
            throw new IllegalStateException("MQTT is not connected");
        }

        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(1);
        message.setRetained(false);

        mqttClient.publish(topic, message);
    }
}
