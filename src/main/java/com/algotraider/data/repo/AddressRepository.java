package com.algotraider.data.repo;

import com.algotraider.data.entity.Address;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends Neo4jRepository<Address, String> {

    Optional<Address> findOneByMeta(String meta);

    Optional<Address> findOneByIp(String ip);
}
