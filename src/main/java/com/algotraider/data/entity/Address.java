package com.algotraider.data.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.Instant;

@Node("Address")
@Getter
@Setter
public class Address {

    public Address(String ip, String meta) {
        this.ip = ip;
        this.meta = meta;
        this.registered = Instant.now().toEpochMilli();
    }

    @Id
    private String ip;

    @Property("city")
    private String city;

    @Property("region")
    private String region;

    @Property("country")
    private String country;

    @Property("loc")
    private String loc;

    @Property("org")
    private String org;

    @Property("postal")
    private String postal;

    @Property("timezone")
    private String timezone;

    @Property("meta")
    private String meta;

    @Property("success")
    private long successfulAttemptsNumber;

    @Property("failed")
    private long failedAttemptsNumber;

    @Property("banned")
    private boolean banned;

    @Property("registered")
    private long registered;

    @Property("lastSeen")
    private long lastSeen;

    @Relationship(type = "LOGGED_FROM", direction = Relationship.Direction.OUTGOING)
    private User user;

    public void updateSuccessfulAttempts() {
        successfulAttemptsNumber++;
    }

    public void updateFailedAttempts() {
        failedAttemptsNumber++;
    }
}
