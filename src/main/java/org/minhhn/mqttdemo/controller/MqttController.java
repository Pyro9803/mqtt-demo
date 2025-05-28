package org.minhhn.mqttdemo.controller;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.minhhn.mqttdemo.service.MqttPublishService;
import org.minhhn.mqttdemo.service.MqttSubscribeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sensor/temperature")
public class MqttController {

    private static final Logger log = LoggerFactory.getLogger(MqttController.class);

    private final MqttPublishService publisher;
    private final MqttSubscribeService subscriber;

    public MqttController(MqttPublishService publisher, MqttSubscribeService subscriber) {
        this.publisher = publisher;
        this.subscriber = subscriber;
    }

    @PostMapping("/publish")
    public ResponseEntity<String> publish() {
        try {
            publisher.publish();
            return ResponseEntity.ok("Temperature message published successfully");
        } catch (Exception e) {
            log.error("Failed to publish temperature: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to publish temperature: " + e.getMessage());
        }
    }

    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe() {
        try {
            subscriber.subscribe();
            return ResponseEntity.ok("Successfully subscribed to temperature topic");
        } catch (Exception e) {
            log.error("Failed to subscribe: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to subscribe: " + e.getMessage());
        }
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<String> unsubscribe() {
        try {
            subscriber.unsubscribe();
            return ResponseEntity.ok("Successfully unsubscribed from temperature topic");
        } catch (Exception e) {
            log.error("Failed to unsubscribe: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to unsubscribe: " + e.getMessage());
        }
    }
    
    @ExceptionHandler(MqttException.class)
    public ResponseEntity<String> handleMqttException(MqttException e) {
        log.error("MQTT Error: {} (Error code: {})", e.getMessage(), e.getReasonCode(), e);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body("MQTT broker communication error: " + e.getMessage());
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("An unexpected error occurred: " + e.getMessage());
    }
}
