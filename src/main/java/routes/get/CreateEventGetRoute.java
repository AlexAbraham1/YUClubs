package main.java.routes.get;

import main.java.models.db_objects.User;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

import java.util.HashMap;
import java.util.Map;

public class CreateEventGetRoute implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request request, Response response) {
        User user = request.session(true).attribute("user");

        return new ModelAndView(null, "createEvent.ftl");
    }
}