package com.algotraider.data.repo;

import com.algotraider.data.entity.Address;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends Neo4jRepository<Address, String> {

    Address findOneByMeta(String meta);

    Address findOneByIp(String ip);
}
