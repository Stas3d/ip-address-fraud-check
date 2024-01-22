package com.algotraider.data.repo;

import com.algotraider.data.entity.Address;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;

@DataNeo4jTest
class AddressRepositoryTests {

    @Autowired
    private AddressRepository repository;

    @Test
    @Disabled
    void testExample() {

        repository.save(new Address("123.156.189.1", "test-meta"));
        var address = this.repository.findOneByMeta("test-meta");
        Assertions.assertTrue(address.isPresent());
        Assertions.assertEquals("123.156.189.1", address.get().getIp());
    }
}
