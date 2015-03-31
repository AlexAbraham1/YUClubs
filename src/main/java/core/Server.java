package main.java.core;

import main.java.models.*;

import main.java.models.db_objects.Club;
import main.java.models.db_objects.User;
import main.java.routes.get.*;
import main.java.services.EventService;
import main.java.services.UserService;
import org.apache.commons.io.FileUtils;
import spark.Filter;
import spark.Request;
import spark.Response;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;

import static spark.Spark.*;

public class Server {

    private static final String[] protectedRoutes = {"/me", "/passwordReset", "/createClub", "/api/user", "/api/user/*"};

    public static void main(String[] args) {

        port(80);

        FreeMarkerTemplateEngine ftl = new FreeMarkerTemplateEngine();
        JSONTemplateEngine jtl = new JSONTemplateEngine();

        DBC.initialize(); //Initialize Database Connection

        //Set public folder as HTML file location
        staticFileLocation("/main/resources/public");

        //Filter for authentication
        setupProtectedFilters();

        //Enable CORS
        enableCORS("*", "*", "*");

        //HTML Endpoints
        get("/", new HomeGetRoute(), ftl);

        get("/login", new LoginGetRoute(), ftl);

        get("/createClub", new CreateClubGetRoute(), ftl);

        get("/me", new ProfileGetRoute(), ftl);

        get("/logout", new LogoutGetRoute(), ftl);

        get("/signup", new SignupGetRoute(), ftl);


        //REST Endpoints
        get("/api/clubs", (request, response) -> {

            response.type("application/json");

            return DBC.getAllClubs();
        }, jtl);

        get("/api/club/:clubID",  (request, response) -> {

            response.type("application/json");

            return DBC.getClub(Integer.parseInt(request.params(":clubID")));
        }, jtl);

        get("/api/events", (request, response) -> {

            response.type("application/json");

            return DBC.getAllEvents();
        }, jtl);

        get("/api/event/:eventID", (request, response) -> {

            response.type("application/json");

            return DBC.getEvent(Integer.parseInt(request.params(":eventID")));
        }, jtl);

        get("/api/users", "application/json", (request, response) -> {

            response.type("application/json");

            return DBC.getAllUsers();
        }, jtl);

        get("/api/club/:clubID/users", (request, response) -> {

            response.type("application/json");

            return DBC.queryUsersForClub(request.params(":clubID"));
        }, jtl);

        post("/api/new/user", (request, response) -> {

            response.type("application/json");

            String name = request.queryParams("fullName");
            String email = request.queryParams("email");
            String password = request.queryParams("password1");
            String password2 = request.queryParams("password2");
            Object result = UserService.createUser(name, email, password, password2);

            if (!(result instanceof User)) {
                response.status(400);
            } else {
                request.session(true).attribute("user", result);
            }

            return result;

        }, jtl);

        get("/api/rss", (request, response) -> {

            response.type("application/json");

            return RSS.getFeed();
        }, jtl);

        //Protected Routes (Must be logged in)
        post("/api/login", (request, response) -> {

            response.type("application/json");

            String email = request.queryParams("email");
            String password = request.queryParams("password");

            Object result = UserService.login(email, password);

            if (result instanceof User) {
                request.session(true).attribute("user", result);
            } else {
                response.status(401);
            }

            return result;
        }, jtl);


        post("/api/user/changePassword", (request, response) -> {

            response.type("application/json");

            User user = request.session(true).attribute("user");

            String oldPassword = request.queryParams("oldPassword");
            String newPassword = request.queryParams("newPassword");
            String newPassword2 = request.queryParams("newPassword2");

            Object result = UserService.changePassword(user, oldPassword, newPassword, newPassword2);

            if (!(result instanceof User)) {
                response.status(400);
            }

            return result;
        }, jtl);

        get("/api/user/clubs", (request, response) -> {

            response.type("application/json");

            User user = request.session(true).attribute("user");
            return DBC.queryClubsForUser(user);
        }, jtl);

        post("/api/user/logout", (request, response) -> {

            response.type("application/json");

            request.session(true).attribute("user", null);
            response.status(200);
            return "Logout Successful";
        }, jtl);

        get("/api/user", (request, response) -> {

            response.type("application/json");

            User user = request.session(true).attribute("user");

            Object result = UserService.getUserData(user);

            return result;
        }, jtl);

        post("/api/user/new/club", (request, response) -> {

            response.type("application/json");

            User user = request.session(true).attribute("user");

            String clubName = request.queryParams("clubName");
            String clubDescription = request.queryParams("clubDescription");
            String clubImage = request.queryParams("clubImage");
            String presidentName = request.queryParams("presidentName");
            String presidentEmail = request.queryParams("presidentEmail");
            String presidentPhone = request.queryParams("presidentPhone");
            String presidentImage = request.queryParams("presidentImage");

            Object result = UserService.createClub(user, clubName, clubDescription, clubImage, presidentName, presidentEmail, presidentPhone, presidentImage);

            if (!(result instanceof Club)) {
                response.status(400);
            }

            return result;
        }, jtl);

        post("/api/user/new/event", (request, response) -> {

            response.type("application/json");

            User user = request.session(true).attribute("user");

            String clubID = request.queryParams("clubID");
            String eventName = request.queryParams("eventName");
            String eventDescription = request.queryParams("eventDescription");
            String eventTime = request.queryParams("eventTime");
            String eventLocation = request.queryParams("eventLocation");
            String eventFlyer = request.queryParams("eventFlyer");

            Object result = EventService.createEvent(clubID, eventName, eventDescription, eventTime, eventLocation, eventFlyer);

            if (!(result instanceof Club)) {
                response.status(400);
            }

            return result;
        }, jtl);

        post("/api/user/linkUserClub/:club_id", (request, response) -> {

            response.type("application/json");

            User user = request.session(true).attribute("user");

            Object result = UserService.linkUserToClub(user, request.params(":club_id"));

            return result;
        }, jtl);

        post("/api/user/linkEventClub/:event_id/:club_id", (request, response) -> {

            response.type("application/json");

            User user = request.session(true).attribute("user");

            Object result = EventService.linkEventToClub(request.params(":event_id"), request.params(":club_id"));

            return result;
        }, jtl);

        get("/api/calendar/day/:day", (request, response) -> {

            response.type("application/json");

            Object result = EventService.getEventsOnDay(request.params(":day"));

            return result;
        }, jtl);

        post("/testFileUpload", (request, response) -> {

            MultipartConfigElement multipartConfigElement = new MultipartConfigElement("/tmp");
            request.raw().setAttribute("org.eclipse.multipartConfig", multipartConfigElement);

            String fileName = request.raw().getPart("file").getHeader("content-disposition");
            fileName = fileName.substring(34, fileName.length()-1);


            InputStream is = request.raw().getPart("file").getInputStream();
            File destination = new File("src/main/resources/public/uploads/" + fileName);
            FileUtils.copyInputStreamToFile(is, destination);

            return null;
        });


        //Make sure all requests have app/json
//        after((req, res) -> {
//            if (!req.uri().equals("/login") && !req.uri().equals("/createClub")) res.type("application/json");
//        });

    }

    private static void setupProtectedFilters()
    {
        Filter f = (request, response) -> {

            if (request.session(true).attribute("user") == null) { //No user in session
                halt(401, "Not Logged In!");
            }

        };

        for (String route : protectedRoutes) {
            before(route, f);
        }
    }

    private static void enableCORS(final String origin, final String methods, final String headers) {
        before(new Filter() {
            @Override
            public void handle(Request request, Response response) {
                response.header("Access-Control-Allow-Origin", origin);
                response.header("Access-Control-Request-Method", methods);
                response.header("Access-Control-Allow-Headers", headers);
            }
        });
    }
}
