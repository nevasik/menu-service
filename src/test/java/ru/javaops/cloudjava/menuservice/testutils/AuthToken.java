package ru.javaops.cloudjava.menuservice.testutils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthToken {

    private final String accessToken;

    @JsonCreator
    public AuthToken(@JsonProperty("access_token") String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
}