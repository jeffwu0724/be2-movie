package edu.uci.ics.jiefengw.service.idm.core;

import edu.uci.ics.jiefengw.service.idm.IDMService;
import edu.uci.ics.jiefengw.service.idm.logger.ServiceLogger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SessionRecords {
    public static void retrieveSessionFromDB(String name) {
        try {
            // Construct the query
            String query =  "SELECT session_id , email" +
                    " FROM session" +
                    " WHERE email LIKE ?;";

            // Create the prepared statement
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);

            // Set the arguments
            ps.setString( 1, name);
            // ps.setString(2, name);

            // Save the query result to a ResultSet so records may be retrieved
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            // Use executeQuery() for queries that RETRIEVE from DB (returns ResultSet)
            // Use executeUpdate() for queries that CHANGE the DB (returns # of rows modified as int)
            // Use execute() for general purpose queries
            ServiceLogger.LOGGER.info("Query succeeded.");

            // Retrieve the students from the Result Set
            // ResultSets are like iterators (they start from BEFORE the first result)
            while (rs.next()) {
                //Integer id = rs.getInt("id");
                String email = rs.getString("email");
                String session_id = rs.getString("session_id");
                //String firstName = rs.getString("firstName");
                // String lastName = rs.getString("lastName");
                //Float gpa = rs.getFloat("GPA");
                ServiceLogger.LOGGER.info("Retrieved User: (" + email+ " " + session_id + " )");
            }

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve student records.");
            e.printStackTrace();
        }
    }
}
