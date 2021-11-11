package edu.uci.ics.jiefengw.service.idm.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestModel_register extends RequestModel_Daddy_Password{
   /*
    @JsonProperty(value = "email", required = true)
    private String email;
    @JsonProperty(value = "password", required = true)
    private char[] password;
    */

    public RequestModel_register(String email, char[] password) { super(email,password); }
    public RequestModel_register() {}

    //Set up the function of getting salt and encoded password
    String EncodedSalt;
    String encodedPassword;
    //在此拿salt
    public String getEncodedSalt(){
        return EncodedSalt;
    }
    public void setEncodedSalt(String EncodedSalt ){
        this.EncodedSalt = EncodedSalt;
    }

    //在此加密
    public String getEncodedPassword(){
        return encodedPassword;
    }
    public void setEncodedPassword(String encodedPassword){
        this.encodedPassword = encodedPassword;
    }

}

