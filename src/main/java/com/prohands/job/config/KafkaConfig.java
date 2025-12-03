package com.prohands.job.config;

import com.prohands.job.dto.request.JobRequest;
import com.prohands.job.dto.event.JobUpdateEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {


    @Bean
    public NewTopic jobCreateRequestsTopic() {
        return TopicBuilder.name("job-create-requests").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic jobUpdatesTopic() {
        return TopicBuilder.name("job-updates").partitions(3).replicas(1).build();
    }


    @Bean
    public ProducerFactory<String, Object> producerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }


    @Bean
    public ConsumerFactory<String, JobRequest> jobRequestConsumerFactory(KafkaProperties kafkaProperties) {
        JsonDeserializer<JobRequest> deserializer = new JsonDeserializer<>(JobRequest.class);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(false);

        ErrorHandlingDeserializer<JobRequest> errorHandlingDeserializer =
                new ErrorHandlingDeserializer<>(deserializer);

        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "job-creation-group"); // Dedicated Group
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), errorHandlingDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, JobRequest> jobRequestContainerFactory(
            ConsumerFactory<String, JobRequest> jobRequestConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, JobRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(jobRequestConsumerFactory);
        return factory;
    }


    @Bean
    public ConsumerFactory<String, JobUpdateEvent> jobUpdateConsumerFactory(KafkaProperties kafkaProperties) {
        JsonDeserializer<JobUpdateEvent> deserializer = new JsonDeserializer<>(JobUpdateEvent.class);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(false);

        ErrorHandlingDeserializer<JobUpdateEvent> errorHandlingDeserializer =
                new ErrorHandlingDeserializer<>(deserializer);

        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "job-service-group");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), errorHandlingDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, JobUpdateEvent> jobUpdateContainerFactory(
            ConsumerFactory<String, JobUpdateEvent> jobUpdateConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, JobUpdateEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(jobUpdateConsumerFactory);
        return factory;
    }
}