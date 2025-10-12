package com.empuje.messaging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class MessagingServiceKafkaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessagingServiceKafkaApplication.class, args);
    }
}
