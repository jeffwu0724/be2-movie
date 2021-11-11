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

import edu.uci.ics.jiefengw.service.idm.models.RequestModel_session;
import edu.uci.ics.jiefengw.service.idm.models.ResponseModel_session;
import java.io.IOException;

//@Path("idm") // Outer path
@Path("session")
public class TestPage_Session {
   // @Path("session")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userRegister(@Context HttpHeaders headers, String jsonText) {
        RequestModel_session requestModel;
        ResponseModel_session responseModel;
        ObjectMapper mapper = new ObjectMapper();


        // Validate model & map JSON to POJO
        try {
            requestModel = mapper.readValue(jsonText, RequestModel_session.class);
        } catch (IOException e) {
            // Catch other exceptions here
            int resultCode;
            e.printStackTrace();

            if (e instanceof JsonParseException) {
                resultCode = -3;
                responseModel = new ResponseModel_session(resultCode, "JSON Parse Exception", null);
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {
                resultCode = -2;
                responseModel = new ResponseModel_session(resultCode, "JSON Mapping Exception", null);
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                resultCode = -1;
                responseModel = new ResponseModel_session(resultCode, "Internal Server Error", null);
                ServiceLogger.LOGGER.severe("Internal Server Error");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }

        }

        //ServiceLogger.LOGGER.info("Received request to hash and salt and password");
       // ServiceLogger.LOGGER.info("Request:\n" + jsonText);


        ServiceLogger.LOGGER.info("email: " + requestModel.getEmail());
        ServiceLogger.LOGGER.info("session id: " + requestModel.getSession_id());

        if (requestModel.getSession_id() == null || requestModel.getSession_id().length() != 128 ) {  //Case -13: Token has invalid length.
            responseModel = new ResponseModel_session(-13, "Token has invalid length.", null);
            ServiceLogger.LOGGER.severe("Token has invalid length.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        } else if (requestModel.getEmail() == null || requestModel.getEmail().length() > 50  || requestModel.getEmail().length() == 0 ) { //Case -10: Email address has invalid length.
            responseModel = new ResponseModel_session(-10, "Email address has invalid length.", null);
            ServiceLogger.LOGGER.severe("Email address has invalid length.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        } else if (!requestModel.checkEmailValid()) { //Case -11: Email address has invalid format.
            responseModel = new ResponseModel_session(-11, "Email address has invalid format.", null);
            ServiceLogger.LOGGER.severe("Email address has invalid format.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }
        else if(!requestModel.checkUserExist()){ //Case 14: User not found.
            responseModel = new ResponseModel_session(14, "User not found.",null);
            ServiceLogger.LOGGER.severe("User not found.");
            return Response.status(Status.OK).entity(responseModel).build();
        }
        else if (requestModel.checkExistSession() == false) {
            responseModel = new ResponseModel_session(134, "Session not found.", null);
            ServiceLogger.LOGGER.severe("Session not found.");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }
        else if (requestModel.checkSessionStatus() == 130) {  //Case 130: Session is active.
            //ServiceLogger.LOGGER.info("进来active了！！！！！");
            responseModel = new ResponseModel_session(130, "Session is active.", requestModel.getSession_id());
            ServiceLogger.LOGGER.severe("Session is active.");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        } else if (requestModel.checkSessionStatus() == 131) { //Case 131:  Session is expired.
            responseModel = new ResponseModel_session(131, "Session is expired.", null);
            ServiceLogger.LOGGER.severe("Session is expired.");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }
        else if (requestModel.checkSessionStatus() == 132) { //Case 132:  Session is closed.
            responseModel = new ResponseModel_session(132, "Session is closed.", null);
            ServiceLogger.LOGGER.severe("Session is closed.");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }
        else if (requestModel.checkSessionStatus() == 133) { //Case 133:  Session is revoked.
            responseModel = new ResponseModel_session(133, "Session is revoked.", null);
            ServiceLogger.LOGGER.severe("Session is revoked.");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }
        else{

            responseModel = new ResponseModel_session(0, "??????.", null);
            ServiceLogger.LOGGER.severe("??????.");
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }


    }
}