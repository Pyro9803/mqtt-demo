package org.minhhn.mqttdemo.config;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class MqttBrokerConfig {
    
    private static final Logger log = LoggerFactory.getLogger(MqttBrokerConfig.class);
    
    @Value("${mqtt.broker.url}")
    private String brokerUrl;
    
    @Value("${mqtt.client.timeout}")
    private int connectionTimeout;

    @Bean
    public IMqttClient mqttClient() throws MqttException {
        String clientId = UUID.randomUUID().toString();

        MemoryPersistence persistence = new MemoryPersistence();
        
        try {
            MqttClient client = new MqttClient(brokerUrl, clientId, persistence);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(connectionTimeout);
            options.setKeepAliveInterval(60);
            options.setMaxInflight(100);
            
            try {
                log.info("Connecting to MQTT broker at {}", brokerUrl);
                client.connect(options);
                log.info("Successfully connected to MQTT broker");
            } catch (MqttException e) {
                log.error("Failed to connect to MQTT broker: {}", e.getMessage());
            }
            
            return client;
        } catch (MqttException e) {
            log.error("Error creating MQTT client: {}", e.getMessage());
            throw e;
        }
    }
}
