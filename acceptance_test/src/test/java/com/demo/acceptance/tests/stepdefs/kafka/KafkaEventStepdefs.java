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
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.ValueMatcher;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import com.demo.acceptance.tests.repository.TestDataRepository;
import com.demo.acceptance.tests.stepdefs.BaseSteps;
import com.demo.acceptance.tests.util.FileReaderUtil;
import com.demo.acceptance.tests.util.KafkaEventDeserializer;
import com.demo.acceptance.tests.util.KafkaTopicUtil;
import com.demo.service.events.PurchaseEvent;
import com.demo.service.events.UserOperationNotificationEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class KafkaEventStepdefs extends BaseSteps {
    private static final Duration TWENTY_SECONDS = Duration.ofSeconds(20);
    private static final String TEST_DATA_FOLDER = "kafka";
    private static final String EXPECTED_USER_EVENT_TEMPLATE = "userChangeEventTemplate.json";
    private static final String BALANCE_NODE_NAME = "balance";
    private static final String USER_CHANGE_REASON_NODE_NAME = "userChangeReason";
    private static final String USER_ID_NODE_NAME = "userId";

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
    private TestDataRepository testDataRepository;

    @Autowired
    private KafkaConsumer<Long, UserOperationNotificationEvent> userEventKafkaConsumer;

    @Autowired
    private KafkaTemplate<Long, PurchaseEvent> purchaseEventKafkaTemplate;

    @Autowired
    private KafkaTopicUtil kafkaTopicUtil;

    @Autowired
    private KafkaEventDeserializer eventDeserializer;

    @Before("@KafkaUserEvent")
    public void beforeTest() {
        userEventTopicPartition = new TopicPartition(userTopicName, 0);
        userEventKafkaConsumer.assign(Collections.singletonList(userEventTopicPartition));
    }

    @After("@KafkaUserEvent")
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
    public void theEventShouldBeCorrect(final String userChangeReason) throws JSONException, JsonProcessingException {
        final ValueMatcher<Object> jsonIgnoreMatcher = (firstJsonNode, secondJsonNode) -> true;
        final ObjectNode expectedEventJson = fileReader.readFileToJsonNode(EXPECTED_USER_EVENT_TEMPLATE, TEST_DATA_FOLDER);

        expectedEventJson.put(USER_ID_NODE_NAME, testDataRepository.getUserId());
        expectedEventJson.put(USER_NAME_NODE_NAME, testDataRepository.getUserName());
        expectedEventJson.put(BALANCE_NODE_NAME, testDataRepository.getUserBalance());
        expectedEventJson.put(USER_CHANGE_REASON_NODE_NAME, userChangeReason);

        System.out.println(userEventsFromKafka);

        JSONAssert.assertEquals(
            expectedEventJson.toString(),
            //            String.valueOf(new ObjectMapper().readTree(userEventsFromKafka.get(0).toString())),
            String.valueOf(userEventsFromKafka),
            new CustomComparator(JSONCompareMode.LENIENT, new Customization("eventId", jsonIgnoreMatcher))
        );
    }

    private PurchaseEvent deserializePurchaseEvents(final String eventAsJson) {
        return eventDeserializer.deserializeJsonToPurchaseEvent(eventAsJson);
    }

    private <T> List<T> getKafkaEventsWitPolling(final KafkaConsumer<Long, T> consumer, final TopicPartition topicPartitionToPoll) {
        consumer.seekToBeginning(List.of(topicPartitionToPoll));

        return consumer.poll(TWENTY_SECONDS)
            .records(topicPartitionToPoll)
            .stream()
            .map(ConsumerRecord::value)
            .collect(Collectors.toList());
    }

}
