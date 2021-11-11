package edu.uci.ics.jiefengw.service.idm.core;

import edu.uci.ics.jiefengw.service.idm.models.RequestModel_login;
import edu.uci.ics.jiefengw.service.idm.IDMService;
import edu.uci.ics.jiefengw.service.idm.logger.ServiceLogger;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class NewSessionRecords {
    public static int insertNewSessionRecord(RequestModel_login requestModel) {
        int code = 0;
        try {
            // Construct the query
            String query =  "INSERT INTO session (session_id, email, status, time_created, last_used, expr_time)" +
                    " VALUE (?, ?, ?, ?, ?, ?)";

            // Create the prepared statement
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);

            // Set the arguments

            ps.setString(1, requestModel.getSessionId());
            ps.setString(2, requestModel.getEmail());
            ps.setInt(3, 1);
            //ps.setInt(3, 5);
            ps.setTimestamp(4, Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setTimestamp(5, Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setTimestamp(6, Timestamp.valueOf(java.time.LocalDateTime.now().plusHours(1)));   //需要修改！！！！


           /*
            // Construct the query
            String query_privilege_level =  "INSERT INTO privilege_level (plevel, pname)" +
                    " VALUE (?, ?)";
            // Create the prepared statement
            PreparedStatement ps_privilege_level = IDMService.getCon().prepareStatement(query_privilege_level);

            // Set the arguments
            ps_privilege_level.setInt(1, 102);
            ps_privilege_level.setString(2, "ROOT" );      //might change later



            // Construct the query
            String query_user_status =  "INSERT INTO user_status (status_id, status)" +
                    " VALUE (?, ?)";
            // Create the prepared statement
            PreparedStatement ps_user_status = IDMService.getCon().prepareStatement(query_user_status);

            // Set the arguments
            ps_user_status.setInt(1, 102);
            ps_user_status.setString(2, "ACTIVE" );      //might change later
            */

            // Save the query result to a ResultSet so records may be retrieved
            ServiceLogger.LOGGER.info("Trying insertion: " + ps.toString());
            // code = ps_user_status.executeUpdate();
            // code = ps_privilege_level.executeUpdate();
            code = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Insertion succeeded.");


        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Insertion failed.");
            e.printStackTrace();
            return -1;
        }
        return code;
    }

    public static String buildSessionQuery(ArrayList<String> cols,
                                        RequestModel_login requestModel){

        String SELECT = "SELECT session_id";
        String FROM = " FROM session";
        String WHERE = " WHERE 1=1";

        for(String c:cols){
            SELECT += (", " + c);
        }

        if (requestModel.getEmail() != null) {
            WHERE += " && email LIKE '%" + requestModel.getEmail() + "%'";
        }

        if (requestModel.getSessionId() != null) {
            WHERE += " && pword LIKE '%" + requestModel.getSessionId() + "%'";
        }

        return SELECT + FROM + WHERE;
    }
}
