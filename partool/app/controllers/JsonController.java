package controllers;

import com.google.gson.*;

import play.mvc.*;

@With(Secure.class)
class JsonController extends Controller {
    static protected void renderJSON(Object o) {
	Gson gsonBuilder = new GsonBuilder()
		.setExclusionStrategies(new GsonExclusionStrategy(null))
		.setPrettyPrinting().create();
	renderJSON(gsonBuilder.toJson(o));
    }
}
