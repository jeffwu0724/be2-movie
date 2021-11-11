package edu.uci.ics.jiefengw.service.idm.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.jiefengw.service.idm.IDMService;
import edu.uci.ics.jiefengw.service.idm.logger.ServiceLogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RequestModel_login extends RequestModel_Daddy_Password{
   /*
    @JsonProperty(value = "email", required = true)
    private String email;
    @JsonProperty(value = "password", required = true)
    private char[] password;
    */

    public RequestModel_login( String email, char password[]) { super(email, password);}
    public RequestModel_login(){};

    //update the session id
    public void updateSessionID(String newSessionID){
        try {
            // Construct the query
            String query =  "UPDATE session SET session_id = ?" +
                    " WHERE email = ?";

            // Create the prepared statement
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);

            // Set the arguments
            ps.setString(1, newSessionID);
            ps.setString(2, email);      //might change later

            // Save the query result to a ResultSet so records may be retrieved
            ServiceLogger.LOGGER.info("Trying updating new session id: " + ps.toString());
            // code = ps_user_status.executeUpdate();
            // code = ps_privilege_level.executeUpdate();
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Update new session id succeeded.");

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Insertion failed.");
            e.printStackTrace();

        }
    }

    //check if the session is used before
    public boolean checkExistSession () {
        int statusExist = 0;
        try {
            ServiceLogger.LOGGER.info("在checkExistSession里面！！！！！！！！！");
            String query = "SELECT session_id, status from session WHERE email = ?";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, email);
            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                int status = resultSet.getInt("status");
                String sessionIDInDB = resultSet.getString("session_id");

                ServiceLogger.LOGGER.info(email);
                ServiceLogger.LOGGER.info(session_id);
                ServiceLogger.LOGGER.info(sessionIDInDB);
                System.out.println(status);

                ServiceLogger.LOGGER.info("有该session！！！！！");

                if (status == 1) {
                    ServiceLogger.LOGGER.info("session符合！！！！！");
                    statusExist = 1;
                }
            }

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve student records.");
            e.printStackTrace();
        }
        if (statusExist == 1) {
            ServiceLogger.LOGGER.info("session存在");
            return true;
        } else {
            ServiceLogger.LOGGER.info("session不存在!!!!");
            return false;
        }
    }

    String session_id;
    //在此拿salt
    public String getSessionId(){
        return session_id;
    }

    public void setSessionId(String session_id ){
        this.session_id = session_id;
    }

}

