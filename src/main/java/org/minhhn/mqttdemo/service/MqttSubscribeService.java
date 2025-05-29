package org.minhhn.mqttdemo.service;

import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class MqttSubscribeService {

    private static final Logger log = LoggerFactory.getLogger(MqttSubscribeService.class);
    private static final String TOPIC = "temperature";
    private final IMqttClient mqttClient;
    private final AtomicBoolean connected = new AtomicBoolean(false);

    public MqttSubscribeService(IMqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }
    
    @PostConstruct
    public void init() {
        try {
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    connected.set(false);
                    log.warn("MQTT subscriber lost connection: {}", cause.getMessage());
                    reconnectAndSubscribe();
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    String payload = new String(message.getPayload());
                    log.info("Received temperature: {}", payload);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });

            connected.set(true);
            mqttClient.subscribe(TOPIC, 1);
            log.info("Subscriber connected and subscribed to MQTT broker");
        } catch (Exception e) {
            log.error("MQTT connection failed: {}", e.getMessage());
            connected.set(false);
            reconnectAndSubscribe();
        }
    }

    public void reconnectAndSubscribe() {
        new Thread(() -> {
            while (!connected.get()) {
                try {
                    Thread.sleep(3000);
                    mqttClient.connect();
                    connected.set(true);
                    mqttClient.subscribe(TOPIC, 1);
                    log.info("Subscriber reconnected to MQTT broker");
                } catch (MqttException e) {
                    log.warn("Retrying MQTT connection... {}", e.getMessage());
                } catch (InterruptedException e) {
                    log.error("Error when creating Thread: {}", e.getMessage());
                }
            }
        }).start();
    }
}
