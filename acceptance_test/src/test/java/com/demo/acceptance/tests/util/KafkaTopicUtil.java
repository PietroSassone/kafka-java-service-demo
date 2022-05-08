package com.demo.acceptance.tests.util;

import java.util.Map;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.RecordsToDelete;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Util class for Kafka topic interaction.
 */
@Slf4j
@Component
public class KafkaTopicUtil {

    @Autowired
    private AdminClient adminClient;

    public void purgeKafkaTopic(final TopicPartition topicPartitionToPurge) {
        log.info("Purging Kafka topic in the tests: {}", topicPartitionToPurge.topic());
        final Map<TopicPartition, RecordsToDelete> deleteMap = Map.of(topicPartitionToPurge, RecordsToDelete.beforeOffset(-1));

        adminClient.deleteRecords(deleteMap);
    }
}
