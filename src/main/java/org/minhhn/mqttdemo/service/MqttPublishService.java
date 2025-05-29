package org.minhhn.mqttdemo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.*;
import org.minhhn.mqttdemo.dto.Temperature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class MqttPublishService {

    private static final Logger log = LoggerFactory.getLogger(MqttPublishService.class);
    private static final String TOPIC = "temperature";
    private final ObjectMapper mapper = new ObjectMapper();
    private final AtomicBoolean connected = new AtomicBoolean(false);
    
    private final IMqttClient mqttClient;

    public MqttPublishService(IMqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }
    
    @PostConstruct
    public void init() {
        try {
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    connected.set(false);
                    log.warn("MQTT publisher lost connection: {}", cause.getMessage());
                    reconnect();
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
        } catch (Exception e) {
            log.error("MQTT connection failed: {}", e.getMessage());
            connected.set(false);
            reconnect();
        }
    }

    private void reconnect() {
        new Thread(() -> {
            while (!connected.get()) {
                try {
                    Thread.sleep(3000);
                    mqttClient.connect();
                    connected.set(true);
                    log.info("Publisher reconnected to MQTT broker");
                } catch (MqttException e) {
                    log.warn("Retrying MQTT connection... {}", e.getMessage());
                } catch (InterruptedException e) {
                    log.error("Error when creating Thread: {}", e.getMessage());
                }
            }
        }).start();
    }

    @Scheduled(fixedRate = 5000)
    public void publish() {
        Temperature temperatureData = new Temperature(15 + Math.random() * 15);
        try {
            double formattedTemp = Math.round(temperatureData.getTemp() * 100.0) / 100.0;
            temperatureData = new Temperature(formattedTemp);
            
            String payload = mapper.writeValueAsString(temperatureData);
            log.debug("Generated temperature: {}°C", formattedTemp);
            
            if (!mqttClient.isConnected()) {
                try {
                    log.warn("Publisher not connected. Attempting to reconnect...");
                    mqttClient.connect();
                    connected.set(true);
                    log.info("Publisher reconnected successfully");
                } catch (MqttException e) {
                    connected.set(false);
                    log.error("Publisher failed to connect: {}", e.getMessage());
                    return;
                }
            }
            
            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(1);
            message.setRetained(true);
            
            mqttClient.publish(TOPIC, message);
            log.info("Published temperature: {}°C", formattedTemp);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize temperature data: {}", e.getMessage());
        } catch (MqttException e) {
            connected.set(false);
            log.error("Failed to publish temperature data: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error in publisher: {}", e.getMessage());
        }
    }
}
