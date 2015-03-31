package main.java.routes.post;

import main.java.models.DBC;
import main.java.models.Email;
import main.java.models.PasswordHash;
import main.java.models.db_objects.Club;
import main.java.models.db_objects.President;
import main.java.models.db_objects.User;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.modelAndView;

/**
 * Created by alexabraham on 3/29/15.
 */
public class CreateClubPostRoute implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request request, Response response) {
        User user = request.session(true).attribute("user");

        String clubName = request.queryParams("clubName");
        String clubDescription = request.queryParams("clubDescription");
        String clubImage = request.queryParams("clubImage");

        String presidentName = request.queryParams("presidentName");
        String presidentEmail = request.queryParams("presidentEmail");
        String presidentPhone = request.queryParams("presidentPhone");
        String presidentImage = request.queryParams("presidentImage");

        if (clubName.equals("")) {
            response.status(400);
            Map<String, Object> attributes = getBadAttributes("You forgot to enter the club name!", user, clubName, clubDescription, clubImage, presidentEmail, presidentImage, presidentName, presidentPhone);
        }

        if (clubDescription.equals("")) {
            response.status(400);
            Map<String, Object> attributes = getBadAttributes("You forgot to enter the club description!", user, clubName, clubDescription, clubImage, presidentEmail, presidentImage, presidentName, presidentPhone);
            return modelAndView(attributes, "me.ftl");
        }

        if (clubImage.equals("")) {
            response.status(400);
            Map<String, Object> attributes = getBadAttributes("You forgot to enter the club image!", user, clubName, clubDescription, clubImage, presidentEmail, presidentImage, presidentName, presidentPhone);
            return modelAndView(attributes, "me.ftl");
        }

        if (user.getPresident() == null && (presidentEmail.equals("") || presidentName.equals(""))) {
            response.status(400);
            Map<String, Object> attributes = getBadAttributes("You forgot to enter president information!", user, clubName, clubDescription, clubImage, presidentEmail, presidentImage, presidentName, presidentPhone);
            return modelAndView(attributes, "me.ftl");
        }

        if (presidentEmail != null && !Email.isValidEmailAddress(presidentEmail)) {
            response.status(400);
            Map<String, Object> attributes = getBadAttributes("The president email address is not valid!!", user, clubName, clubDescription, clubImage, presidentEmail, presidentImage, presidentName, presidentPhone);
            return modelAndView(attributes, "me.ftl");
        }

        //Everything is good! Let's create the new club!

        Club newClub = new Club();
        newClub.setName(clubName);
        newClub.setDescription(clubDescription);
        newClub.setImage(clubImage);

        if (user.getPresident() == null) {
            President president = new President();

            president.setEmail(presidentEmail);
            president.setName(presidentName);
            president.setPhone(presidentPhone);
            president.setUser(user);
            user.setPresident(president);
            DBC.addPresident(president);
            DBC.updateUser(user);
            newClub.setPresident(president);

        } else {
            newClub.setPresident(user.getPresident());
        }

        DBC.addClub(newClub);

        //Add user to session and go to /me
        response.status(201);
        response.redirect("/me");
        return new ModelAndView(null, "redirect.ftl");
    }

    private HashMap<String, Object> getBadAttributes(String message, User user, String clubName, String clubDescription, String clubImage, String presidentEmail, String presidentImage, String presidentName, String presidentPhone)
    {
        HashMap<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("badMessage", message);
        attributes.put("user", user);
        attributes.put("clubName", clubName);
        attributes.put("clubDescription", clubDescription);
        attributes.put("clubImage", clubImage);
        attributes.put("presidentEmail", presidentEmail);
        attributes.put("presidentImage", presidentImage);
        attributes.put("presidentName", presidentName);
        attributes.put("presidentPhone", presidentPhone);
        if (user.getPresident() != null) attributes.put("hasPresident", true);

        return attributes;
    }
}
