package main.java.routes.post;

import main.java.models.DBC;
import main.java.models.Email;
import main.java.models.PasswordHash;
import main.java.models.db_objects.User;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.modelAndView;

public class SignupPostRoute implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request request, Response response) {
        String name = request.queryParams("fullname").trim();
        String email = request.queryParams("email").trim();
        String password = request.queryParams("password");
        String password2 = request.queryParams("password2");

        if (name.equals("")) {
            response.status(400);
            Map<String, Object> attributes = getAttributes("You forgot your name!", name, email);
            return modelAndView(attributes, "signup.ftl");
        }

        if (email.equals("")) {
            response.status(400);
            Map<String, Object> attributes = getAttributes("You forgot your email!", name, email);
            return modelAndView(attributes, "signup.ftl");
        }

        if (!Email.isValidEmailAddress(email)) {
            response.status(400);
            Map<String, Object> attributes = getAttributes("That email address is not valid!", name, email);
            return modelAndView(attributes, "signup.ftl");
        }

        if (userExists(email)) {
            response.status(400);
            Map<String, Object> attributes = getAttributes("That email is already in our database!", name, email);
            return modelAndView(attributes, "signup.ftl");
        }

        if (password.equals("") || password2.equals("")) {
            response.status(400);
            Map<String, Object> attributes = getAttributes("You forgot passwords!", name, email);
            return modelAndView(attributes, "signup.ftl");
        }

        //Passwords don't match
        if (!password.equals(password2)) {
            response.status(422);
            Map<String, Object> attributes = getAttributes("Passwords don't match!", name, email);
            return modelAndView(attributes, "signup.ftl");
        }

        //Everything is good! Let's create the account

        password = hashPassword(password);

        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(password);

        DBC.addUser(newUser);

        String template = Email.getStringFromTemplate("welcome.html").replace("{{user.name}}", newUser.getName());
        Email.sendEmailHTML(newUser.getEmail(), "Test Email", template);

        //Add user to session and go to /me
        request.session(true).attribute("user", newUser);
        response.status(201);
        response.redirect("/me");
        return new ModelAndView(null, "redirect.ftl");
    }

    private boolean userExists(String email)
    {
        List<User> userList = DBC.queryUser("email", email);
        return (userList.size() > 0);
    }

    private String hashPassword(String password)
    {
        try {
            return PasswordHash.createHash(password);
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private HashMap<String, Object> getAttributes(String message, String name, String email)
    {
        HashMap<String, Object> attributes = new HashMap<String, Object>();

        attributes.put("message", message);
        attributes.put("fullname", name);
        attributes.put("email", email);

        return attributes;
    }
}