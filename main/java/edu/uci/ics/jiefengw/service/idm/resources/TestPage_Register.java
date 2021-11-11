package edu.uci.ics.jiefengw.service.idm.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.jiefengw.service.idm.logger.ServiceLogger;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import edu.uci.ics.jiefengw.service.idm.models.RequestModel_register;
import edu.uci.ics.jiefengw.service.idm.models.ResponseModel_register;
import edu.uci.ics.jiefengw.service.idm.core.NewUserRecords;
import edu.uci.ics.jiefengw.service.idm.core.UserRecords;
import edu.uci.ics.jiefengw.service.idm.security.Crypto;
import org.apache.commons.codec.binary.Hex;

import java.io.IOException;


//@Path("idm") // Outer path
@Path("register")
public class TestPage_Register {
   // @Path("register")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userRegister(@Context HttpHeaders headers, String jsonText){
        RequestModel_register requestModel;
        ResponseModel_register responseModel;
        ObjectMapper mapper = new ObjectMapper();


        // Validate model & map JSON to POJO
        try {
            requestModel = mapper.readValue(jsonText, RequestModel_register.class);


        } catch (IOException e) {
            // Catch other exceptions here
            int resultCode;
            e.printStackTrace();

            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new ResponseModel_register(resultCode, "JSON Parse Exception");
                ServiceLogger.LOGGER.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {

                resultCode = -2;
                responseModel = new ResponseModel_register(resultCode, "JSON Mapping Exception");
                ServiceLogger.LOGGER.info("*****************************" );
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                resultCode = -1;
                responseModel = new ResponseModel_register(resultCode, "Internal Server Error");
                ServiceLogger.LOGGER.severe("Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }

        ServiceLogger.LOGGER.info("Received request to hash and salt and password");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);

        // Salt & Hash
        // Generate a random salt
        byte[] salt = Crypto.genSalt();

        // Use the salt to hash the password
        char[] password = requestModel.getPassword();
        byte[] hashedPassword = Crypto.hashPassword(password, salt, Crypto.ITERATIONS, Crypto.KEY_LENGTH);

        //Encode salt & password
        String encodedSalt = Hex.encodeHexString(salt);
        String encodedPassword = Hex.encodeHexString(hashedPassword);

        requestModel.setEncodedSalt(encodedSalt);
        // requestModel.setSalt(salt.toString());
        requestModel.setEncodedPassword(encodedPassword);

        String email = requestModel.getEmail();


        if(email == null || email.length() == 0 || email.length() > 50 ){ //Case -10: Email address has invalid length.
            responseModel = new ResponseModel_register(-10, "Email address has invalid length.");
            ServiceLogger.LOGGER.severe("Email address has invalid length.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

        }else if(!requestModel.checkEmailValid()){ //Case -11: Email address has invalid format.
            System.out.println(requestModel.getEmail());
            responseModel = new ResponseModel_register(-11, "Email address has invalid format.");
            ServiceLogger.LOGGER.severe("Email address has invalid format.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        else if (password == null || password.length == 0 || password.length > 16 ) {  //Case -12: Password has invalid length.
            responseModel = new ResponseModel_register(-12, "Password has invalid length.");
            ServiceLogger.LOGGER.severe("Password has invalid length.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();

        }
        else if(requestModel.getPassword().length < 7 ){ //Case 12: Password does not meet length requirements.
            responseModel = new ResponseModel_register(12, "Password does not meet length requirements.");
            ServiceLogger.LOGGER.severe("Password does not meet length requirements.");
            return Response.status(Status.OK).entity(responseModel).build();
        }
        else if(requestModel.checkPasswordValid() == false){ //Case 13: Password does not meet character requirements.
            responseModel = new ResponseModel_register(13, "Password does not meet character requirements.");
            ServiceLogger.LOGGER.severe("Password does not meet character requirements.");
            return Response.status(Status.OK).entity(responseModel).build();
        }
        else if(requestModel.checkDuplicatedEmail() == false){ //Case 16: Email already in use    !!!!!!!!!!need to change
            responseModel = new ResponseModel_register(16, "Email already in use.");
            ServiceLogger.LOGGER.severe("Email already in use.");
            return Response.status(Status.OK).entity(responseModel).build();
        }else{
            //.out.println(requestModel.checkDuplicatedEmail());
            //if everything is good, then we add to the database user
            NewUserRecords.insertNewStudentRecord(requestModel);        // save to database
            UserRecords.retrieveStudentsFromDB(requestModel.getEmail());    // get from database

            responseModel = new ResponseModel_register(110, " User registered successfully.");
            ServiceLogger.LOGGER.severe("User registered successfully..");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }


    }

}
