package com.bookkeeping.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Created by chandanmaloo on 7/16/17.
 */
public class LoginUtils {
    // validates that the registration form has been filled out right and username conforms
    static final Logger logger = LoggerFactory.getLogger(LoginUtils.class);

    public static boolean validateSignupV2(String username, String password, String verify, String email,
                                           HashMap<String, String> errors) {
        //String USER_RE = "^[a-zA-Z0-9_-]{3,20}$";
        String PASS_RE = "^.{3,20}$";
        String EMAIL_RE = "^[\\S]+@[\\S]+\\.[\\S]+$";

        //errors.put("username_error", "");
        errors.put("password_error", "");
        errors.put("verify_error", "");
        errors.put("email_error", "");

        /*
        if (!username.matches(USER_RE)) {
            errors.put("username_error", "invalid username. try just letters and numbers");
            return false;
        }
        */

        if (!password.matches(PASS_RE)) {
            errors.put("password_error", "Invalid password length!!");
            return false;
        }


        if (!password.equals(verify)) {
            errors.put("verify_error", "Password must match!!");
            return false;
        }

        if (!email.equals("")) {
            if (!email.matches(EMAIL_RE)) {
                errors.put("email_error", "Invalid Email Address!!");
                return false;
            }
        }

        return true;
    }

    public static Boolean emailVerify(String email) {
        String EMAIL_RE = "^[\\S]+@[\\S]+\\.[\\S]+$";
        if (!email.equals("")) {
            if (!email.matches(EMAIL_RE)) {
                return false;
            }
        }
        return true;
    }

    public static boolean validateSignup(String username, String password, String verify, String email,
                                         HashMap<String, String> errors) {
        String USER_RE = "^[a-zA-Z0-9_-]{3,20}$";
        String PASS_RE = "^.{3,20}$";
        String EMAIL_RE = "^[\\S]+@[\\S]+\\.[\\S]+$";

        errors.put("username_error", "");
        errors.put("password_error", "");
        errors.put("verify_error", "");
        errors.put("email_error", "");


        if (!username.matches(USER_RE)) {
            errors.put("username_error", "invalid username. try just letters and numbers");
            return false;
        }


        if (!password.matches(PASS_RE)) {
            errors.put("password_error", "invalid password.");
            return false;
        }


        if (!password.equals(verify)) {
            errors.put("verify_error", "password must match");
            return false;
        }

        if (!email.equals("")) {
            if (!email.matches(EMAIL_RE)) {
                errors.put("email_error", "Invalid Email Address");
                return false;
            }
        }

        return true;
    }


    public static Boolean verifyAdmin(String username) {

        if( username != null
                && username.equals("master@test.com")) {
            return true;
        }
        return  false;
    }

}

