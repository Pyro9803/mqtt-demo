package org.minhhn.mqttdemo.service;

import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class MqttSubscribeService {

    private static final Logger log = LoggerFactory.getLogger(MqttSubscribeService.class);
    private static final String TOPIC = "temperature";
    private final IMqttClient mqttClient;
    private final ScheduledExecutorService reconnectExecutor = Executors.newSingleThreadScheduledExecutor();
    private boolean isSubscribed = false;

    public MqttSubscribeService(IMqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }
    
    @PostConstruct
    public void init() {
        subscribe();
        reconnectExecutor.scheduleAtFixedRate(this::checkConnectionAndResubscribe, 5, 5, TimeUnit.SECONDS);
    }

    public void subscribe() {
        try {
            if (!mqttClient.isConnected()) {
                log.warn("MQTT client not connected. Attempting to reconnect..");
                try {
                    mqttClient.connect();
                } catch (MqttException e) {
                    log.error("Failed to reconnect to MQTT broker: {}", e.getMessage());
                    return;
                }
            }

            mqttClient.subscribe(TOPIC, (s, message) -> {
                String payload = new String(message.getPayload());
                log.info("Received temperature: {}", payload);
            });
            isSubscribed = true;
            log.info("Successfully subscribed to temperature topic");
        } catch (MqttException e) {
            log.error("Failed to subscribe: {}", e.getMessage());
            isSubscribed = false;
        }
    }

    public void unsubscribe() {
        try {
            if (!mqttClient.isConnected()) {
                mqttClient.connect();
            }

            mqttClient.unsubscribe(TOPIC);
            isSubscribed = false;
            log.info("Successfully unsubscribed from temperature topic");
        } catch (MqttException e) {
            log.error("Failed to unsubscribe: {}", e.getMessage());
        }
    }
    
    private void checkConnectionAndResubscribe() {
        try {
            if (!mqttClient.isConnected()) {
                log.warn("Connection to MQTT broker lost. Attempting to reconnect...");
                try {
                    mqttClient.connect();
                    log.info("Reconnected to MQTT broker");

                    if (isSubscribed) {
                        subscribe();
                    }
                } catch (MqttException e) {
                    log.error("Failed to reconnect to MQTT broker: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Error in connection monitor: {}", e.getMessage());
        }
    }
}
