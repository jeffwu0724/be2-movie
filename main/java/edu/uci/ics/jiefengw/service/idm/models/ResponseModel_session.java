package edu.uci.ics.jiefengw.service.idm.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseModel_session {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;

    @JsonProperty(value = "message", required = true)
    private String message;

    @JsonProperty(value = "session_id")
    private String session_id;

    @JsonCreator
    public ResponseModel_session(@JsonProperty(value = "resultCode", required = true)  int resultCode,
                               @JsonProperty(value = "message", required = true) String message,
                               @JsonProperty(value = "session_id") String session_id) {
        this.resultCode = resultCode;
        this.message = message;
        this.session_id = session_id;
    }

    @JsonProperty("resultCode")
    public int getResultCode() {
        return resultCode;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("session_id")
    public String getSession_id() {
        return session_id;
    }
}
