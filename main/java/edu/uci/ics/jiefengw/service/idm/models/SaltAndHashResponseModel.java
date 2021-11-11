package edu.uci.ics.jiefengw.service.idm.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaltAndHashResponseModel {

    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;

    @JsonProperty(value = "message", required = true)
    private String message;

    @JsonProperty(value = "salt")
    private String salt;

    @JsonProperty(value = "hashedPassword")
    private String hashedPassword;

    @JsonCreator
    public SaltAndHashResponseModel(int resultCode, String message, String salt, String hashedPassword) {
        this.resultCode = resultCode;
        this.message = message;
        this.salt = salt;
        this.hashedPassword = hashedPassword;
    }

    @JsonProperty("resultCode")
    public int getResultCode() {
        return resultCode;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("salt")
    public String getSalt() {
        return salt;
    }

    @JsonProperty("hashedPassword")
    public String getHashedPassword() {
        return hashedPassword;
    }
}