package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

@With(Secure.class)
public class ListCDR extends JsonController {   

    public static void getUsages() {
	List<CdrUsage> usages = CdrUsage.findAll();
	renderJSON(usages);
    }

    public static void getTypes() {
	List<CdrTypes> types = CdrTypes.findAll();
	renderJSON(types);
    }  
    
}
