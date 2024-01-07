package com.algotraider.data;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Neo4jContainer;

import java.util.Optional;

@DataNeo4jTest
public class AddressAndUserRepositoryTest {

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
//        registry.add("spring.neo4j.authentication.username", () -> "neo");
//        registry.add("spring.neo4j.authentication.password", () -> "password");
        registry.add("spring.neo4j.authentication.password", neo4jContainer::getAdminPassword);
    }

    @Test
    void findSomethingShouldWork(@Autowired Neo4jClient client) {
//        client.query("CREATE (b:Book {isbn: '978-0547928210', name: 'The Fellowship of the Ring', year: 1954})-[:WRITTEN_BY]->(a:Author {id: 1, name: 'J. R. R. Tolkien'})");
        client.query("CREATE (b:Book {isbn: '978-0547928210', name: 'The Fellowship of the Ring', year: 1954})-[:WRITTEN_BY]->(a:Author {id: '1', name: 'J. R. R. Tolkien'})");
        client.query("CREATE (b2:Book {isbn: '978-0547928203', name: 'The Two Towers', year: 1956})-[:WRITTEN_BY]->(a)");


//        Optional<Long> result = client.query("MATCH (a) RETURN COUNT(a)")
//        Optional<String> result = client.query("MATCH (n:Book {name: 'The Two Towers'}) RETURN n")
        Optional<String> result = client.query("MATCH (n:Author {id: '1'}) RETURN n")
                .fetchAs(String.class)
                .one();

        Assertions.assertEquals(Boolean.FALSE, result.isPresent());
//        Assertions.assertEquals("0L", result.get());
    }

//    @Test
//    public void findSomethingShouldWork1(@Autowired Neo4jClient client) {
//        client.query("CREATE (b2:Book {isbn: '978-0547928203', title: 'The Two Towers', year: 1956})");
//        Optional<Long> result = client.query("MATCH (n) RETURN COUNT(n)")
//                .fetchAs(Long.class)
//                .one();
//        assertThat(result).hasValue(0L);
//    }
}
