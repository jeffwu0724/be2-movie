package edu.uci.ics.jiefengw.service.idm.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.jiefengw.service.idm.IDMService;
import edu.uci.ics.jiefengw.service.idm.logger.ServiceLogger;
import edu.uci.ics.jiefengw.service.idm.security.Crypto;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class RequestModel_Daddy_Password extends RequestModel_Daddy_Email{
    @JsonProperty(value = "password", required = true)
    protected char[] password;

    public RequestModel_Daddy_Password(String email, char[] password)
    {
        super(email);
        this.password = password;
    }
    public RequestModel_Daddy_Password() {}

    //Getter and Setter
    public char[] getPassword()
    {
        return password;
    }
    public void setPassword(char[] password)
    {
        this.password = password;
    }

    //check email is valid or not
    public boolean checkPasswordValid() {
        int numOfUpper = 0;
        int numOfLower = 0;
        int numOfNumber = 0;

        for(int i = 0; i < password.length; i++){
            if(Character.isUpperCase(password[i])) {
                numOfUpper++;
            }else if(Character.isLowerCase(password[i])){
                numOfLower++;
            }else if(Character.isDigit(password[i])){
                numOfNumber++;
            }
        }
        ServiceLogger.LOGGER.info(password.toString());
        ServiceLogger.LOGGER.info(numOfUpper + " " + numOfLower + " " + numOfNumber);
        if (numOfLower == 0 || numOfNumber == 0 || numOfUpper == 0){
            ServiceLogger.LOGGER.info("格式错误");
            return false;
        }else{
            ServiceLogger.LOGGER.info("格式正确");
            return true;
        }
    }

    //check whether the email is correct or not
    public boolean checkEmailValid(){
        String emailRegex = "^[a-zA-Z0-9]+@(.+)\\.(.+)$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    // check whether the password match the one in data base
    public boolean checkPasswordMatch() {
        int status = 0;
        String salt;
        String passwordInDB;
        byte[] hashedPassword;
        String encodedPassword;
        byte[] decodedSalt;

        try {
            String query = "SELECT pword, salt from user WHERE email = ?";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, email);

            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next()) {
                salt = resultSet.getString("salt");
                // Use the salt to hash the password
                passwordInDB = resultSet.getString("pword");
                decodedSalt = Hex.decodeHex(salt);
                hashedPassword = Crypto.hashPassword(password, decodedSalt, Crypto.ITERATIONS, Crypto.KEY_LENGTH);
                encodedPassword = Hex.encodeHexString(hashedPassword);
                if(encodedPassword.equals(passwordInDB) ){
                    status = 1;
                }
            }

        } catch (SQLException | DecoderException e) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve student records.");
            e.printStackTrace();
        }
        if (status == 1){
            ServiceLogger.LOGGER.info("password match!!!!");
            return true;
        }else{
            ServiceLogger.LOGGER.info("password not match!!!!");
            return false;
        }
    }
}
