package edu.uci.ics.jiefengw.service.idm.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestModel_session extends RequestModel_Daddy_Session{
   /*
    @JsonProperty(value = "email", required = true)
    private String email;
    @JsonProperty(value = "session_id", required = true)
    private String session_id;
    */

    public RequestModel_session(String email, String session_id) { super(email, session_id); }
    public RequestModel_session() {}

}
