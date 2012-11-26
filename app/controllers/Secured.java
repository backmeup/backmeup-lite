package controllers;

import controllers.filter.ErrorMessage;
import play.libs.Json;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

public class Secured extends Security.Authenticator {
	
	@Override
    public String getUsername(Context ctx) {
        return ctx.session().get("username");
    }
    
    @Override
    public Result onUnauthorized(Context ctx) {
        return ok(Json.toJson(new ErrorMessage("You are not logged in")));
    }

}
