package org.minhhn.mqttdemo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.minhhn.mqttdemo.dto.Temperature;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class MqttPublishService {
    private final ObjectMapper mapper = new ObjectMapper();

    private final IMqttClient mqttClient;

    public MqttPublishService(IMqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    @Scheduled(fixedRate = 5000)
    public void publish() throws MqttException, JsonProcessingException {
        if (!mqttClient.isConnected()) {
            throw new IllegalStateException("MQTT is not connected");
        }

        Temperature temperatureData = new Temperature(15 + Math.random() * 15);
        String payload = mapper.writeValueAsString(temperatureData);

        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(1);
        message.setRetained(false);

        mqttClient.publish("temperature", message);
    }
}
