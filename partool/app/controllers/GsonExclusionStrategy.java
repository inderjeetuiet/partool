package controllers;

import com.google.gson.*;

class GsonExclusionStrategy implements ExclusionStrategy {
    private final Class<?> typeToExclude;

    public GsonExclusionStrategy(Class<?> clazz) {
	this.typeToExclude = clazz;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
	return (this.typeToExclude != null && this.typeToExclude == clazz)
		|| clazz.getAnnotation(JsonExclude.class) != null;
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
	return f.getAnnotation(JsonExclude.class) != null;
    }
}
