package com.algotraider.data.repo;

import com.algotraider.data.entity.User;
import com.algotraider.data.entity.Address;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends Neo4jRepository<User, String> {

    @Query("MATCH (a:Address)-[:LOGGED_FROM]->(u:User) WHERE u.email = email RETURN a")
    List<Address> findAddresses(@Param("email") String email);

    User findByEmail(@Param("email") String email);
}
