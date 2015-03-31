package main.java.routes.get;


import main.java.models.db_objects.User;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

import java.util.HashMap;
import java.util.Map;

public class ProfileGetRoute implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response response) {
        Map<String, Object> attributes = new HashMap<String, Object>();

        User user = request.session(true).attribute("user");
        attributes.put("user", user);
        return new ModelAndView(attributes, "me.ftl");
    }
}
