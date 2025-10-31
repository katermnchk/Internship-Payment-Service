package com.innowise.internship;

import com.innowise.internship.dto.kafka.OrderCreatedEvent;
import com.innowise.internship.dto.kafka.PaymentCreatedEvent;
import com.innowise.internship.entity.PaymentStatus;
import com.innowise.internship.repository.PaymentRepository;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertAll;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PaymentServiceIntegrationTest extends AbstractIntegrationTest {

    private static final String CREATE_ORDER_TOPIC = "test-create-order-" + UUID.randomUUID();
    private static final String CREATE_PAYMENT_TOPIC = "test-create-payment-" + UUID.randomUUID();

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private PaymentRepository paymentRepository;

    private Consumer<String, PaymentCreatedEvent> paymentEventConsumer;

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("app.kafka.topic.create-order", () -> CREATE_ORDER_TOPIC);
        registry.add("app.kafka.topic.create-payment", () -> CREATE_PAYMENT_TOPIC);
        registry.add("spring.kafka.consumer.group-id", () -> "test-group-" + UUID.randomUUID());
    }

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
        wireMockServer.resetAll();

        Map<String, Object> consumerProps = Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers(),
                ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-" + UUID.randomUUID(),
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
                JsonDeserializer.TRUSTED_PACKAGES, "*",
                JsonDeserializer.VALUE_DEFAULT_TYPE, PaymentCreatedEvent.class.getName()
        );

        paymentEventConsumer = new DefaultKafkaConsumerFactory<String, PaymentCreatedEvent>(consumerProps)
                .createConsumer();
        paymentEventConsumer.subscribe(Collections.singletonList(CREATE_PAYMENT_TOPIC));

        KafkaTestUtils.getRecords(paymentEventConsumer, Duration.ofSeconds(1));
    }

    @AfterEach
    void tearDown() {
        if (paymentEventConsumer != null) {
            paymentEventConsumer.close();
        }
    }

    @Test
    void givenEvenRandomNumber_whenOrderCreated_thenPaymentSuccess() {
        stubRandomNumberApi(100);
        String orderId = "order-even-" + UUID.randomUUID();
        var orderEvent = new OrderCreatedEvent(orderId, "user12", new BigDecimal("100.00"));

        kafkaTemplate.send(CREATE_ORDER_TOPIC, orderId, orderEvent);

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            var paymentOpt = paymentRepository.findByOrderId(orderId).stream().findFirst();
            assertThat(paymentOpt).isPresent();
            var payment = paymentOpt.get();

            assertAll(
                    () -> assertThat(payment.getOrderId()).isEqualTo(orderId),
                    () -> assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS),
                    () -> assertThat(payment.getPaymentAmount()).isEqualByComparingTo("100.00")
            );
        });

        ConsumerRecords<String, PaymentCreatedEvent> records =
                KafkaTestUtils.getRecords(paymentEventConsumer, Duration.ofSeconds(10));
        assertThat(records.count()).isEqualTo(1);

        PaymentCreatedEvent event = records.iterator().next().value();
        assertAll(
                () -> assertThat(event.getOrderId()).isEqualTo(orderId),
                () -> assertThat(event.getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS.toString())
        );
    }

    @Test
    void givenOddRandomNumber_whenOrderCreated_thenPaymentFailed() {
        stubRandomNumberApi(101);
        String orderId = "order-odd-" + UUID.randomUUID();
        var orderEvent = new OrderCreatedEvent(orderId, "user12", new BigDecimal("50.00"));

        kafkaTemplate.send(CREATE_ORDER_TOPIC, orderId, orderEvent);

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            var paymentOpt = paymentRepository.findByOrderId(orderId).stream().findFirst();
            assertThat(paymentOpt).isPresent();
            var payment = paymentOpt.get();

            assertAll(
                    () -> assertThat(payment.getOrderId()).isEqualTo(orderId),
                    () -> assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED),
                    () -> assertThat(payment.getPaymentAmount()).isEqualByComparingTo("50.00")
            );
        });
        ConsumerRecords<String, PaymentCreatedEvent> records =
                KafkaTestUtils.getRecords(paymentEventConsumer, Duration.ofSeconds(10));
        assertThat(records.count()).isEqualTo(1);

        PaymentCreatedEvent event = records.iterator().next().value();
        assertAll(
                () -> assertThat(event.getOrderId()).isEqualTo(orderId),
                () -> assertThat(event.getPaymentStatus()).isEqualTo(PaymentStatus.FAILED.toString())
        );
    }
}
