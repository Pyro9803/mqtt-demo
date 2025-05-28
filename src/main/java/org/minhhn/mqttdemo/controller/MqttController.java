package org.minhhn.mqttdemo.controller;

import org.minhhn.mqttdemo.service.MqttPublishService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sensor")
public class MqttController {

    private final MqttPublishService mqttPublishService;

    public MqttController(MqttPublishService mqttPublishService) {
        this.mqttPublishService = mqttPublishService;
    }

    @PostMapping("/temperature")
    public String publish(String topic, String message) {
        try {
            mqttPublishService.publish(topic, message);
            return "Message successfully publish";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to publish message" + e.getMessage();
        }
    }
}
