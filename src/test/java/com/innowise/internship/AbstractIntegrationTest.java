package com.innowise.internship;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static com.github.tomakehurst.wiremock.client.WireMock.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    @Container
    @ServiceConnection
    static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @Container
    @ServiceConnection
    static final KafkaContainer kafkaContainer = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.4.0")
    );

    protected static final WireMockServer wireMockServer =
            new WireMockServer(WireMockConfiguration.options().dynamicPort());

    static {
        wireMockServer.start();
    }

    @AfterAll
    static void stopServers() {
        wireMockServer.stop();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("services.api-gateway.url", () -> "http://localhost:" + wireMockServer.port());
        registry.add("services.random-number-api.path", () -> "/api/test-random");
        registry.add("spring.kafka.consumer.group-id", () -> "test-payment-group");
        registry.add("spring.kafka.consumer.value-deserializer",
                () -> "org.springframework.kafka.support.serializer.JsonDeserializer");
        registry.add("spring.kafka.consumer.properties.spring.json.trusted.packages", () -> "*");
        registry.add("app.liquibase.enabled", () -> "false");
    }

    protected void stubRandomNumberApi(int numberToReturn) {
        wireMockServer.stubFor(get(urlEqualTo("/api/test-random"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(String.valueOf(numberToReturn))));
        wireMockServer.verify(0, getRequestedFor(urlEqualTo("/api/test-random")));
    }
}
