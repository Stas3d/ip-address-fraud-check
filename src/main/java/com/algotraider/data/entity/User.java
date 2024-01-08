package com.algotraider.data.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Node("User")
public class User {

    public User(String id, String email, Long registered) {
        this.id = id;
        this.email = email;
        this.registeredFrom = registered;
    }

    @Id
    private String id;

    @Property("email")
    private String email;

    @Property("registered")
    private Long registeredFrom;

    @Property("lastSeen")
    private Long lastSeen;

    @Property("banned")
    private boolean isBanned;

    @Relationship(type = "LOGGED_FROM", direction = Relationship.Direction.INCOMING)
    private List<Address> addressList;

    //TODO: refactor/test helper method
    public void associateNewAddress(final Address address) {

        if (addressList.isEmpty()) {
            addressList = new ArrayList<>();
        }
        addressList.add(address);
    }
}
