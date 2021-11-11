package edu.uci.ics.jiefengw.service.idm.core;

import edu.uci.ics.jiefengw.service.idm.models.RequestModel_register;
import edu.uci.ics.jiefengw.service.idm.IDMService;
import edu.uci.ics.jiefengw.service.idm.logger.ServiceLogger;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class NewUserRecords {
    public static int insertNewStudentRecord(RequestModel_register requestModel) {
        int code = 0;
        try {
            // Construct the query
            String query =  "INSERT INTO user (email, status, plevel, salt, pword)" +
                    " VALUE (?, ?, ?, ?, ?)";

            // Create the prepared statement
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);

            // Set the arguments
            ps.setString(1, requestModel.getEmail());
            ps.setInt(2, 1);      //might change later
            ps.setInt(3, 5);        //might change later
            ps.setString(4, requestModel.getEncodedSalt());
            ps.setString(5, requestModel.getEncodedPassword());


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

    public static String buildUserQuery(ArrayList<String> cols,
                                           RequestModel_register requestModel){

        String SELECT = "SELECT id";
        String FROM = " FROM student";
        String WHERE = " WHERE 1=1";

        for(String c:cols){
            SELECT += (", " + c);
        }

        if (requestModel.getEmail() != null) {
            WHERE += " && email LIKE '%" + requestModel.getEmail() + "%'";
        }

       /*
        if (requestModel.getPassword() != null) {
            WHERE += " && pword LIKE '%" + requestModel.getEncodedPassword() + "%'";
        }
        */

        return SELECT + FROM + WHERE;
    }
}
