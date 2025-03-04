package io.confluent.developer;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.fail;

public class KafkaTestContainersTest {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(KafkaTestContainersTest.class);
    @ParameterizedTest
    @ValueSource(strings = {
            "apache/kafka:3.8.0",
            "apache/kafka-native:3.8.0",
            "apache/kafka:3.9.0",
            "apache/kafka-native:3.9.0",
            "apache/kafka:4.0.0-rc0",
            "apache/kafka-native:4.0.0-rc0",

    })
    public void testKafkaTestContainers(String fullImageName) {
        try (KafkaContainer kafka = new KafkaContainer(DockerImageName.parse(fullImageName))
                .withLogConsumer(new Slf4jLogConsumer(LOGGER))) {
            kafka.start();
            kafka.stop();
        } catch (Exception e) {
            fail();
        }
    }
}
