package com.algotraider.data;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Neo4jContainer;

@Disabled
@DataNeo4jTest
class AddressAndUserRepositoryTest {

    private static Neo4jContainer<?> neo4jContainer;

    @BeforeAll
    static void initializeNeo4j() {

        neo4jContainer = new Neo4jContainer<>().withAdminPassword("password");
        neo4jContainer.start();
    }

    @AfterAll
    static void stopNeo4j() {

        neo4jContainer.close();
    }

    @DynamicPropertySource
    static void neo4jProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.neo4j.uri", neo4jContainer::getBoltUrl);
        registry.add("spring.neo4j.authentication.username", () -> "neo4j");
        registry.add("spring.neo4j.authentication.password", neo4jContainer::getAdminPassword);
    }
}
