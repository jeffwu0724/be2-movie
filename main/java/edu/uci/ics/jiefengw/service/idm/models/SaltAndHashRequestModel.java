package edu.uci.ics.jiefengw.service.idm.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SaltAndHashRequestModel {
    @JsonProperty(value = "password", required = true)
    private char[] password;

    @JsonCreator
    public SaltAndHashRequestModel(@JsonProperty(value = "password", required = true)
                                            char[] password) {
        this.password = password;
    }

    @JsonProperty(value = "password", required = true)
    public char[] getPassword() {
        return password;
    }
}