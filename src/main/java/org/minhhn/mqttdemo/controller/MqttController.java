package org.minhhn.mqttdemo.controller;

import org.minhhn.mqttdemo.service.MqttPublishService;
import org.minhhn.mqttdemo.service.MqttSubscribeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sensor/temperature")
public class MqttController {

    private final MqttPublishService publisher;
    private final MqttSubscribeService subscriber;

    public MqttController(MqttPublishService publisher, MqttSubscribeService subscriber) {
        this.publisher = publisher;
        this.subscriber = subscriber;
    }

    @PostMapping("/publish")
    public String publish(String topic, String message) {
        try {
            publisher.publish(topic, message);
            return "Message successfully publish";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to publish message: " + e.getMessage();
        }
    }

    @PostMapping("/subscribe")
    public String subscribe(@RequestParam String topic) {
        try {
            subscriber.subscribeToTopic(topic);
            return "Subscriber received message";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to subscribe message: " + e.getMessage();
        }
    }
}
