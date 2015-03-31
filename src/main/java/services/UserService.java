package main.java.services;


import main.java.models.DBC;
import main.java.models.Email;
import main.java.models.PasswordHash;
import main.java.models.db_objects.Club;
import main.java.models.db_objects.President;
import main.java.models.db_objects.User;
import spark.ModelAndView;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.modelAndView;

public class UserService {

    public static Object getUserData(User user) {
        return getUserMap(user);
    }

    public static Object createUser(String name, String email, String password, String password2) {

        if (name == null || name.equals("")) {
            return getErrorMap("You forgot your name!");
        }

        if (email == null || email.equals("")) {
            return getErrorMap("You forgot your email!");
        }

        if (!Email.isValidEmailAddress(email)) {
            return getErrorMap("That email address is not valid!");
        }

        if (userExists(email)) {
            return getErrorMap("That email is already in our database!");
        }

        if (password == null || password2 == null || password.equals("") || password2.equals("")) {
            return getErrorMap("You forgot passwords!");
        }

        //Passwords don't match
        if (!password.equals(password2)) {
            return getErrorMap("Passwords don't match!");
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
        return newUser;
    }

    public static Object login(String email, String password) {

        if (email == null || password == null || email.equals("") || password.equals("")) return getErrorMap("You didn't enter an email and password!");

        User user = getUser(email, password);

        if (user != null) return user;

        return getErrorMap("Wrong email or password");
    }

    public static Object changePassword(User user, String oldPassword, String newPassword, String newPassword2) {
        if (oldPassword.equals("")) {
            return getErrorMap("You forgot the old password!");
        }

        if (newPassword.equals("") || newPassword2.equals("")) {
            return getErrorMap("You forgot the new passwords!");
        }

        if (!validatePassword(oldPassword, user.getPassword())) {
            return getErrorMap("Old password didn't match!");
        }

        if (!newPassword.equals(newPassword2)) {
            return getErrorMap("New passwords don't match!");
        }

        //Everything is good! Let's change the account password
        newPassword = hashPassword(newPassword);
        user.setPassword(newPassword);
        DBC.updateUser(user);

        return user;
    }

    public static Object createClub(User user, String clubName, String clubDescription, String clubImage,
                                    String presidentName, String presidentEmail, String presidentPhone, String presidentImage) {

        if (clubName == null || clubName.equals("")) {
            return getErrorMap("You forgot to enter the club name!");
        }

        if (clubDescription == null || clubDescription.equals("")) {
            return getErrorMap("You forgot to enter the club description!");
        }

        if (clubImage == null || clubImage.equals("")) {
            clubImage = "images/clubs/2/banner.jpg";
        }

        Club newClub = new Club();
        newClub.setName(clubName);
        newClub.setDescription(clubDescription);
        newClub.setImage(clubImage);

        if (user.getPresident() != null) {
            newClub.setPresident(user.getPresident());
        } else {
            if (presidentEmail == null || presidentName == null || presidentEmail.equals("") || presidentName.equals("")) {
                return getErrorMap("You forgot to enter president information!");
            }

            if (!Email.isValidEmailAddress(presidentEmail)) {
                return getErrorMap("The president email address is not valid!!");
            }

            President president = new President();

            president.setEmail(presidentEmail);
            president.setName(presidentName);
            president.setPhone(presidentPhone);
            president.setImage(presidentImage);
            president.setUser(user);
            user.setPresident(president);
            DBC.addPresident(president);
            DBC.updateUser(user);
            newClub.setPresident(president);

        }

        DBC.addClub(newClub);

        DBC.linkUserClub(user, newClub);

        return newClub;
    }

    public static Object linkUserToClub(User user, String clubIDString) {
        Integer clubID = Integer.parseInt(clubIDString);
        Club club = DBC.queryClub(clubID);
        DBC.linkUserClub(user, club);
        return "Link Successful";
    }

    private static boolean userExists(String email)
    {
        List<User> userList = DBC.queryUser("email", email);
        return (userList.size() > 0);
    }

    private static String hashPassword(String password)
    {
        try {
            return PasswordHash.createHash(password);
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static boolean validatePassword(String password, String hash)
    {
        try {
            return PasswordHash.validatePassword(password, hash);
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }

    }

    private static User getUser(String email, String password)
    {

        List<User> userList = DBC.queryUser("email", email);

        if (userList.size() > 0) {
            User user = userList.get(0);

            if (validatePassword(password, user.getPassword())) {
                return user;
            } else {
                return null;
            }
        } else {
            return null;
        }

    }

    private static LinkedHashMap<String, Object> getUserMap(User user)
    {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();

        map.put("id", user.getId());
        map.put("name", user.getName());
        map.put("email", user.getEmail());
        map.put("clubs", DBC.queryClubsForUser(user));

        return map;
    }

    private static LinkedHashMap<String, Object> getErrorMap(String message)
    {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();

        map.put("error", message);

        return map;
    }
}
