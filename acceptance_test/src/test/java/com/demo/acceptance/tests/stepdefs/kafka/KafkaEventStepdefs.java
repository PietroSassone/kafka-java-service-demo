package com.demo.acceptance.tests.stepdefs.kafka;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import com.demo.acceptance.tests.dao.ProductDao;
import com.demo.acceptance.tests.repository.TestDataRepository;
import com.demo.acceptance.tests.stepdefs.BaseSteps;
import com.demo.acceptance.tests.util.FileReaderUtil;
import com.demo.acceptance.tests.util.KafkaEventDeserializer;
import com.demo.acceptance.tests.util.KafkaTopicUtil;
import com.demo.service.enums.UserChangeReason;
import com.demo.service.events.PurchaseEvent;
import com.demo.service.events.UserOperationNotificationEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;

/**
 * Step definitions for the Kafka event sending and consuming cucumber tests.
 */
@Slf4j
public class KafkaEventStepdefs extends BaseSteps {
    private static final String TEST_DATA_FOLDER = "kafka";
    private static final String PURCHASE_EVENT_JSON = "purchaseEvent.json";
    private static final String USER_ID_NODE_NAME = "userId";
    private static final String PURCHASE_DETAILS_NODE_NAME = "purchaseDetails";
    private static final String PRODUCT_NODE_NAME = "product";

    private TopicPartition userEventTopicPartition;
    private TopicPartition purchaseEventTopicPartition;
    private ObjectNode purchaseEventJson;
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

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductDao productDao;

    @Before("@KafkaUserEvent")
    public void beforeKafkaUserEventTest() {
        userEventTopicPartition = new TopicPartition(userTopicName, 0);
        userEventKafkaConsumer.assign(Collections.singletonList(userEventTopicPartition));
    }

    @Before("@KafkaPurchaseEvent")
    public void beforeKafkaPurchaseEventTest() {
        purchaseEventTopicPartition = new TopicPartition(purchaseTopicName, 0);
    }

    @After("@KafkaUserEvent")
    public void afterKafkaUserEventTest() {
        kafkaTopicUtil.purgeKafkaTopic(userEventTopicPartition);
    }

    @After("@KafkaPurchaseEvent")
    public void afterKafkaPurchaseEventTest() {
        kafkaTopicUtil.purgeKafkaTopic(purchaseEventTopicPartition);
    }

    @Given("a purchase event is prepared")
    public void aPurchaseEventIsPrepared() {
        purchaseEventJson = fileReader.readFileToJsonNode(PURCHASE_EVENT_JSON, TEST_DATA_FOLDER);
    }

    @Given("the existing user id is added to the purchase event")
    public void theUserIdIsAddedToTheEvent() {
        purchaseEventJson.put(USER_ID_NODE_NAME, testDataRepository.getUserId());
    }

    @Given("^the existing product(?:s)? (?:is|are) added to the purchase event$")
    public void theProductsAreAddedToTheEvent() {
        final List<String> productIds = testDataRepository.getProductIds();
        IntStream.range(0, productIds.size()).forEach(
            listIndex -> {
                final Long id = Long.parseLong(productIds.get(listIndex));
                populateProductNodeFromDb(id, listIndex).apply(purchaseEventJson);
            });
    }

    @When("the purchase event is sent to Kafka")
    public void thePurchaseEventIsSentToKafka() {
        log.info("attempting to send event to kafka: {}", purchaseEventJson);
        purchaseEventKafkaTemplate.send(purchaseTopicName, deserializePurchaseEvents(purchaseEventJson.toString()));
    }

    @Then("^a user notification event should( not)? be present on Kafka$")
    public void aNotificationEventShouldBePresentOnKafka(final String notWord) {
        userEventsFromKafka = getKafkaEventsWitPolling(userEventKafkaConsumer, userEventTopicPartition);

        final int expectedNumberOfEvents = Objects.isNull(notWord) ? 1 : 0;
        assertThat(String.format("%s user event(s) should be available on Kafka.", expectedNumberOfEvents), userEventsFromKafka.size(), equalTo(expectedNumberOfEvents));
    }

    @Then("^the event should be correct with (USER_CREATED|USER_NAME_CHANGE|BALANCE_REDUCED|BALANCE_INCREASED|USER_DELETED) reason$")
    public void theEventShouldBeCorrect(final UserChangeReason userChangeReason) throws JSONException, JsonProcessingException {
        final UserOperationNotificationEvent expectedEvent = UserOperationNotificationEvent.builder()
            .userId(Long.parseLong(testDataRepository.getUserId()))
            .userName(testDataRepository.getUserName())
            .balance(testDataRepository.getUserBalance())
            .changeReason(userChangeReason)
            .build();

        assertThat("The event received from Kafka should be correct!", userEventsFromKafka.get(0), equalTo(expectedEvent));
    }

    private PurchaseEvent deserializePurchaseEvents(final String eventAsJson) {
        return eventDeserializer.deserializeJsonToPurchaseEvent(eventAsJson);
    }

    private <T> List<T> getKafkaEventsWitPolling(final KafkaConsumer<Long, T> consumer, final TopicPartition topicPartitionToPoll) {
        consumer.seekToBeginning(List.of(topicPartitionToPoll));

        return consumer.poll(TEN_SECONDS)
            .records(topicPartitionToPoll)
            .stream()
            .map(ConsumerRecord::value)
            .collect(Collectors.toList());
    }

    private Function<ObjectNode, ObjectNode> populateProductNodeFromDb(final Long id, final int nodeIndex) {
        return eventJson -> ((ObjectNode) (eventJson.get(PURCHASE_DETAILS_NODE_NAME).get(nodeIndex)))
            .set(
                PRODUCT_NODE_NAME,
                objectMapper.convertValue(productDao.findResourceById(id).orElse(null), JsonNode.class)
            );
    }
}
