package com.project.controllers.kafka;

import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("!integrationTest")
public class ListenerController {


    @KafkaListener(topics = "${running-wishes.topic-name}", containerFactory = "kafkaListenerContainerFactory")
    public void listenForCompetitionIds(ConsumerRecord<String, List<Long>> message) {
        System.out.println("Listening to kafka, received competition ids: " + message.value());
    }
}
