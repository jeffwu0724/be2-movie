package edu.uci.ics.jiefengw.service.idm.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.jiefengw.service.idm.IDMService;
import edu.uci.ics.jiefengw.service.idm.logger.ServiceLogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class RequestModel_Daddy_Session extends RequestModel_Daddy_Email{
    @JsonProperty(value = "session_id", required = true)
    String session_id;

    public RequestModel_Daddy_Session(String email, String session_id) {
        super(email);
        this.session_id = session_id;
    }
    public RequestModel_Daddy_Session() {}

    //Getter and Setter
    public String getSession_id() { return session_id;}
    public void setSession_id() { this.session_id = session_id; }

    // check which the status session is
    public int checkSessionStatus (){
        ServiceLogger.LOGGER.info("在SessionStatus里面！！！！！！！！！！！！！！！！！！！");
        int returnValue = 0;
        updateLastUsedTime();
        try {
            String query = "SELECT session_id, last_used, expr_time, status from session WHERE email = ?";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, email);
            ResultSet resultSet = ps.executeQuery();
            ServiceLogger.LOGGER.info(email);
            ServiceLogger.LOGGER.info(session_id);
            if(resultSet.next()) {
                int status = resultSet.getInt("status");
                Timestamp lastUsedTime = resultSet.getTimestamp("last_used");
                Timestamp expiredTime = resultSet.getTimestamp("expr_time");
                Timestamp currentTime = new Timestamp(System.currentTimeMillis());

                ServiceLogger.LOGGER.info("current: " + currentTime);
                ServiceLogger.LOGGER.info("after update, last: " + lastUsedTime);
                ServiceLogger.LOGGER.info("expire: " + expiredTime);
                ServiceLogger.LOGGER.info(currentTime.getTime() + "-" + lastUsedTime.getTime() + "=" + (currentTime.getTime() - lastUsedTime.getTime()) );

                if(status == 1){
                    /*
                    if(currentTime.before(expiredTime)) {           //active
                        ServiceLogger.LOGGER.info("before");
                        returnValue = 130;
                    }
                    else if(currentTime.after(expiredTime) ){        //expired
                        ServiceLogger.LOGGER.info("after");
                        returnValue = 131;
                    }
                    else if((currentTime.getTime() - lastUsedTime.getTime()) > 600000){  //revoke and need to login again
                        returnValue = 133;
                    }
                    else if((currentTime.getTime() - expiredTime.getTime()) < 600000){  //revoke make a new session
                        returnValue = 133;
                    }else{
                        returnValue = 132;
                    }
                     */
                    returnValue = 130;
                }else if (status == 2){
                    returnValue = 132;
                }else if (status == 3){
                    returnValue = 131;
                }else if (status == 4){
                    returnValue = 133;
                }

            }

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve student records.");
            e.printStackTrace();
        }

        ServiceLogger.LOGGER.info("result: " + returnValue);
        return returnValue;
    }

    //update last used time in database
    public void updateLastUsedTime(){
        try {
            // Construct the query
            String query =  "UPDATE session SET last_used = ?" +
                    " WHERE email = ?";

            // Create the prepared statement
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);

            // Set the arguments
            ps.setTimestamp(1, Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setString(2, email);      //might change later

            // Save the query result to a ResultSet so records may be retrieved
            ServiceLogger.LOGGER.info("Trying updating last used time: " + ps.toString());
            // code = ps_user_status.executeUpdate();
            // code = ps_privilege_level.executeUpdate();
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Update the last used time succeeded.");


        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Insertion failed.");
            e.printStackTrace();

        }
    }

    //check whether the session exist or not
    public boolean checkExistSession () {
        int status = 0;
        try {
            ServiceLogger.LOGGER.info("在checkExistSession里面！！！！！！！！！");
            String query = "SELECT session_id from session WHERE email = ?";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, email);
            ResultSet resultSet = ps.executeQuery();
            ServiceLogger.LOGGER.info(email);
            ServiceLogger.LOGGER.info(session_id);
            if (resultSet.next()) {
                String sessionIDInDB = resultSet.getString("session_id");
                ServiceLogger.LOGGER.info("有该session！！！！！");

                if (session_id.equals(sessionIDInDB)) {
                    ServiceLogger.LOGGER.info("session符合！！！！！");
                    status = 1;
                }
            }

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve student records.");
            e.printStackTrace();
        }
        if (status == 1) {
            ServiceLogger.LOGGER.info("session存在");
            return true;
        } else {
            ServiceLogger.LOGGER.info("session不存在!!!!");
            return false;
        }
    }
}
