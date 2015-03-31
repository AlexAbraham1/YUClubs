package main.java.routes.get;

import main.java.models.db_objects.User;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

import java.util.HashMap;
import java.util.Map;

public class CreateClubGetRoute implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request request, Response response) {
        User user = request.session(true).attribute("user");

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("user", user);
        if (user.getPresident() != null) map.put("hasPresident", true);

        return new ModelAndView(map, "createClub.ftl");
    }
}