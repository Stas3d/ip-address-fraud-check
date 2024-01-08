package com.algotraider.data.repo;

import com.algotraider.data.entity.Address;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataNeo4jTest
class AddressRepositoryTests {

    @Autowired
    private AddressRepository repository;

    @Test
    void testExample() {

        repository.save(new Address("123.156.189.1", "test-meta"));
        Address address = this.repository.findOneByMeta("test-meta");
        assertThat(address.getIp()).isEqualTo("123.156.189.1");
    }
}
