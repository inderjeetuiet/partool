package controllers;

import play.mvc.Http.Response;
import models.User;

public class Security extends Secure.Security {

    static boolean authenticate(String username, String password) {
    try{	
	User u = User.find("login", username).first();
	if (u == null) {
	    return false;
	} else if (password.compareTo(u.getPassword()) == 0) {
	    return true;
	} else {
	    return false;
	}
    }
	catch (Exception e) {
		Response.current().status = 404;
		Response.current().print(
				"Internal Server Error");
		return false;
	}

    }

}
