package com.demo.acceptance.tests.stepdefs.kafka;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import com.demo.acceptance.tests.util.FileReaderUtil;
import com.demo.acceptance.tests.util.KafkaEventDeserializer;
import com.demo.acceptance.tests.util.KafkaTopicUtil;
import com.demo.service.events.PurchaseEvent;
import com.demo.service.events.UserOperationNotificationEvent;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class KafkaEventStepdefs {
    private static final Duration TWENTY_SECONDS = Duration.ofSeconds(20);
    private static final String TEST_DATA_FOLDER = "kafka";
    private static final String EXPECTED_USER_EVENT_TEMPLATE = "userChangeEventTemplate.json";

    private TopicPartition userEventTopicPartition;
    private String purchaseEventJsonAsString;
    private List<UserOperationNotificationEvent> userEventsFromKafka = new ArrayList<>();

    @Value("${user.topic.name}")
    private String userTopicName;

    @Value("${purchase.topic.name}")
    private String purchaseTopicName;

    @Autowired
    private FileReaderUtil fileReader;

    @Autowired
    private KafkaConsumer<String, UserOperationNotificationEvent> userEventKafkaConsumer;

    @Autowired
    private KafkaTemplate<String, PurchaseEvent> purchaseEventKafkaTemplate;

    @Autowired
    private KafkaTopicUtil kafkaTopicUtil;

    @Autowired
    private KafkaEventDeserializer eventDeserializer;

    @Before("@Kafka")
    public void beforeTest() {
        userEventTopicPartition = new TopicPartition(userTopicName, 0);
        userEventKafkaConsumer.assign(Collections.singletonList(userEventTopicPartition));
    }

    @After("@Kafka")
    public void afterTest() {
        kafkaTopicUtil.purgeKafkaTopic(userEventTopicPartition);
    }

    @When("the purchase event is sent to Kafka")
    public void thePurchaseEventIsSentToKafka() {
        purchaseEventKafkaTemplate.send(purchaseTopicName, deserializePurchaseEvents(purchaseEventJsonAsString));
    }

    @Then("^a user notification event should be present on Kafka$")
    public void aNotificationEventShouldBePresentOnKafka() {
        userEventsFromKafka = getKafkaEventsWitPolling(userEventKafkaConsumer, userEventTopicPartition);
        assertThat("A user event should be available on Kafka.", userEventsFromKafka.size(), equalTo(1));
    }

    @Then("^the event should be correct with (USER_CREATED|USER_NAME_CHANGE|BALANCE_REDUCED|BALANCE_INCREASED|USER_DELETED) reason$")
    public void theEventShouldBeCorrect(final String userChangeReason) throws JSONException {
        final ObjectNode expectedEventJson = fileReader.readFileToJsonNode(EXPECTED_USER_EVENT_TEMPLATE, TEST_DATA_FOLDER);

        expectedEventJson.put("userChangeReason", userChangeReason);
        System.out.println(userEventsFromKafka);
        //TODO
        //        JSONAssert.assertEquals(expectedEventJson.toString(), String.valueOf(userEventsFromKafka.get(0)), JSONCompareMode.LENIENT);
    }

    private PurchaseEvent deserializePurchaseEvents(final String eventAsJson) {
        return eventDeserializer.deserializeJsonToPurchaseEvent(eventAsJson);
    }

    private <T> List<T> getKafkaEventsWitPolling(final KafkaConsumer<String, T> consumer, final TopicPartition topicPartitionToPoll) {
        consumer.seekToBeginning(List.of(topicPartitionToPoll));

        return consumer.poll(TWENTY_SECONDS)
            .records(topicPartitionToPoll)
            .stream()
            .map(ConsumerRecord::value)
            .collect(Collectors.toList());
    }

}
