package edu.uci.ics.jiefengw.service.idm.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.jiefengw.service.idm.core.NewSessionRecords;
import edu.uci.ics.jiefengw.service.idm.core.SessionRecords;
import edu.uci.ics.jiefengw.service.idm.logger.ServiceLogger;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import edu.uci.ics.jiefengw.service.idm.models.*;
import edu.uci.ics.jiefengw.service.idm.security.Session;

import java.io.IOException;


//@Path("idm") // Outer path
@Path("login")
public class TestPage_Login {
   // @Path("login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userRegister(@Context HttpHeaders headers, String jsonText){
        RequestModel_login requestModel;
        ResponseModel_login responseModel;
        ObjectMapper mapper = new ObjectMapper();


        // Validate model & map JSON to POJO
        try {
            requestModel = mapper.readValue(jsonText, RequestModel_login.class);
        } catch (IOException e) {
            // Catch other exceptions here
            int resultCode;
            e.printStackTrace();

            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new ResponseModel_login(resultCode, "JSON Parse Exception",null);
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {
                resultCode = -2;
                responseModel = new ResponseModel_login(resultCode, "JSON Mapping Exception",null);
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                resultCode = -1;
                responseModel = new ResponseModel_login(resultCode, "Internal Server Error",null);
                ServiceLogger.LOGGER.severe("Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }

        ServiceLogger.LOGGER.info("Received request to hash and salt and password");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);


        /*
        // Salt & Hash
        // Generate a random salt
        byte[] salt = Crypto.genSalt();


        // Use the salt to hash the password
        char[] password = requestModel.getPassword();
        byte[] hashedPassword = Crypto.hashPassword(password, salt, Crypto.ITERATIONS, Crypto.KEY_LENGTH);

        // Encode salt & password
        //String encodedSalt = Hex.encodeHexString(salt);
        //String encodedPassword = Hex.encodeHexString(hashedPassword);

        // requestModel.setEncodedSalt(encodedSalt);
        //requestModel.setEncodedPassword(encodedPassword);
         */

        if (requestModel.getPassword() == null || requestModel.getPassword().length == 0 ) {  //Case -12: Password has invalid length.
            responseModel = new ResponseModel_login(-12, "Password has invalid length.", null);
            ServiceLogger.LOGGER.severe("Password has invalid length.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }else if(requestModel.getEmail() == null || requestModel.getEmail().length() == 0 || requestModel.getEmail().length() > 50 ){ //Case -10: Email address has invalid length.
            responseModel = new ResponseModel_login(-10, "Email address has invalid length.", null);
            ServiceLogger.LOGGER.severe("Email address has invalid length.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }else if(!requestModel.checkEmailValid()){ //Case -11: Email address has invalid format.
            responseModel = new ResponseModel_login(-11, "Email address has invalid format.",null);
            ServiceLogger.LOGGER.severe("Email address has invalid format.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }
        else if(!requestModel.checkUserExist()){ //Case 14: User not found.
            responseModel = new ResponseModel_login(14, "User not found.",null);
           ServiceLogger.LOGGER.severe("User not found.");
            return Response.status(Status.OK).entity(responseModel).build();
        }
        else if(!requestModel.checkPasswordMatch()){ //Case 11: Passwords do not match.
            responseModel = new ResponseModel_login(11, " Passwords do not match.",null);
           ServiceLogger.LOGGER.severe("Passwords do not match.");
            return Response.status(Status.OK).entity(responseModel).build();
        }
        else{
            //if everything is good, then we add to the database user
            // NewUserRecords.insertNewStudentRecord(requestModel);        // save to database
            // UserRecords.retrieveStudentsFromDB(requestModel.getEmail());    // get from database

            // Create session
            Session session = Session.createSession(requestModel.getEmail());
            requestModel.setSessionId(session.getSessionID().toString());

            if(requestModel.checkExistSession() == true){
                ServiceLogger.LOGGER.severe("来了老弟");
               // if(session.getLastUsed().before(session.getExprTime())){
                    requestModel.updateSessionID(session.getSessionID().toString());
               // }
            }
            else{
                NewSessionRecords.insertNewSessionRecord(requestModel);        // save to database
                SessionRecords.retrieveSessionFromDB(requestModel.getEmail());    // get from database
            }
            responseModel = new ResponseModel_login(120, "User logged in successfully.",session.getSessionID().toString());
            ServiceLogger.LOGGER.severe("User logged in successfully.");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }


    }

}
