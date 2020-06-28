package org.users.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;

@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode
public class User {

    private long id;

    public User() {
    }

    public User(final long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
