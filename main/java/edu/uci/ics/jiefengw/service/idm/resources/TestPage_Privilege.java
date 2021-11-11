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

import edu.uci.ics.jiefengw.service.idm.models.RequestModel_privilege;
import edu.uci.ics.jiefengw.service.idm.models.ResponseModel_privilege;
import edu.uci.ics.jiefengw.service.idm.core.NewUserRecords;
import edu.uci.ics.jiefengw.service.idm.core.UserRecords;
import edu.uci.ics.jiefengw.service.idm.models.ResponseModel_session;
import edu.uci.ics.jiefengw.service.idm.security.Crypto;
import org.apache.commons.codec.binary.Hex;

import java.io.IOException;

//@Path("idm")
@Path("privilege")
public class TestPage_Privilege {
   // @Path("privilege")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userRegister(@Context HttpHeaders headers, String jsonText){
        RequestModel_privilege requestModel;
        ResponseModel_privilege responseModel;
        ObjectMapper mapper = new ObjectMapper();

        // Validate model & map JSON to POJO
        try {
            requestModel = mapper.readValue(jsonText, RequestModel_privilege.class);
        } catch (IOException e) {
            // Catch other exceptions here
            int resultCode;
            e.printStackTrace();

            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new ResponseModel_privilege(resultCode, "JSON Parse Exception");
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {
                resultCode = -2;
                responseModel = new ResponseModel_privilege(resultCode, "JSON Mapping Exception");
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                resultCode = -1;
                responseModel = new ResponseModel_privilege(resultCode, "Internal Server Error");
                ServiceLogger.LOGGER.severe("Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }

        ServiceLogger.LOGGER.info("Received request to hash and salt and password");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);



        if (requestModel.getPlevel() > 5 || requestModel.getPlevel() < 1) {  //Case -14: Privilege level out of valid range.
            responseModel = new ResponseModel_privilege(-14, "Privilege level out of valid range.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }else if(requestModel.getEmail() == null || requestModel.getEmail().length() == 0 || requestModel.getEmail().length() > 50 ){ //Case -10: Email address has invalid length.
            responseModel = new ResponseModel_privilege(-10, "Email address has invalid length.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }else if(!requestModel.checkEmailValid()){ //Case -11: Email address has invalid format.
            responseModel = new ResponseModel_privilege(-11, "Email address has invalid format.\n");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }
        else if(!requestModel.checkUserExist()){ //Case 14: User not found.
            responseModel = new ResponseModel_privilege(14, "User not found.");
            ServiceLogger.LOGGER.severe("User not found.");
            return Response.status(Status.OK).entity(responseModel).build();
        }

        else if(requestModel.checkSufficientPLever() == true){ //Case 140: User has sufficient privilege level.
            responseModel = new ResponseModel_privilege(140, "User has sufficient privilege level.");
            ServiceLogger.LOGGER.severe("User has sufficient privilege level.");
            return Response.status(Status.OK).entity(responseModel).build();
        }
        else if(requestModel.checkSufficientPLever() == false){ //Case 141:  User has insufficient privilege level.
            responseModel = new ResponseModel_privilege(141, "User has insufficient privilege level.");
            ServiceLogger.LOGGER.severe("User has insufficient privilege level.");
            return Response.status(Status.OK).entity(responseModel).build();
        }

        else{

            responseModel = new ResponseModel_privilege(0, "？？？？？？？");
            ServiceLogger.LOGGER.severe("？？？？？？？");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }


    }
}
