package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

@With(Secure.class)
public class CdrTypesController extends JsonController {

    /**
     * Gets all CDR types from DB and returns them to client side in JSON
     * format.
     * 
     */
    public static void getAll() {
	List<CdrTypes> cdrTypes = CdrTypes.all().fetch();
	renderJSON(cdrTypes);
    }

    /**
     * Gets all CDR types and their usage types from DB and returns them to
     * client side in JSON format.
     * 
     */
    public static void getTypes() {
	List<CdrTypes> cdrTypes = CdrTypes.all().fetch();

	List<CdrUsage> emptyCdrUsageList = Collections.emptyList();
	for (CdrTypes type : cdrTypes) {
	    type.setCdrUsages(emptyCdrUsageList);
	}
	renderJSON(cdrTypes);
    }

    /**
     * Gets a CDR type from DB depending on the given usage type and returns it
     * to client side using JSON format.
     * 
     * @param type
     *            usage type
     */
    public static void getUsageTypes(Integer type) {
	List<CdrTypes> cdrType = CdrTypes.find("byCdrType", type).fetch();
	renderJSON(cdrType.get(0));
    }
}
