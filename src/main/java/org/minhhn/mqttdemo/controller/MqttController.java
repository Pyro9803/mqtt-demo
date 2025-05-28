package org.minhhn.mqttdemo.controller;

import org.minhhn.mqttdemo.service.MqttPublishService;
import org.minhhn.mqttdemo.service.MqttSubscribeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public String publish() {
        try {
            publisher.publish();
            return "Message successfully publish";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to publish message: " + e.getMessage();
        }
    }

    @PostMapping("/subscribe")
    public String subscribe() {
        try {
            subscriber.subscribe();
            return "Subscriber receiving message from publisher...";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to subscribe message: " + e.getMessage();
        }
    }

    @PostMapping("/unsubscribe")
    public String unsubscribe() {
        try {
            subscriber.unsubscribe();
            return "Subscriber unsubscribe successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to unsubscribe message..." +
                    "Please check again!";
        }
    }
}
