package main.java.models;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import main.java.configs.SQLConfig;
import main.java.models.db_objects.Club;
import main.java.models.db_objects.Event;
import main.java.models.db_objects.President;
import main.java.models.db_objects.User;
import main.java.models.db_objects.join_tables.EventClub;
import main.java.models.db_objects.join_tables.UserClub;

import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Database Connection
 */
public class DBC {

    //SQLConfig is a class in main.java.configs package which was NOT committed to GitHub
    //Create your own class and add fields for MySQL url, user, and password
    private static final String databaseURL = SQLConfig.URL;
    private static final String databaseUser = SQLConfig.USER;
    private static final String databasePassword = SQLConfig.PASS;

    private static ConnectionSource connectionSource;
    private static Dao<User, String> userDao;
    private static Dao<Club, String> clubDao;
    private static Dao<Event, String> eventDao;
    private static Dao<President, String> presidentDao;

    private static Dao<UserClub, String> userClubDao;
    private static Dao<EventClub, String> eventClubDao;

    public static List<User> queryUser(String key, Object value)
    {
        try {
            List<User> userList = userDao.queryForEq(key, value);
            return userList;
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Club queryClub(int clubID) {
        try {
            List<Club> clubList = clubDao.queryForEq("id", clubID);
            return clubList.get(0);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Event queryEvent(int eventID) {
        try {
            List<Event> eventList = eventDao.queryForEq("id", eventID);
            return eventList.get(0);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Map<String, Object> getClub(int clubID) {
        try {
            List<Club> clubList = clubDao.queryForEq("id", clubID);
            LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();

            if (clubList.size() > 0) {
                Club club = clubList.get(0);

                String clubIDString = ((Integer) club.getId()).toString();


                map.put("id", club.getId());
                map.put("name", club.getName());
                map.put("description", club.getDescription());
                map.put("image", club.getImage());
                President president = presidentDao.queryForId(((Integer) club.getPresident().getId()).toString());
                map.put("president", president);
                map.put("events", queryEventsForClub(clubIDString, true));
                map.put("members", queryUsersForClub(clubIDString));
            } else {
                map.put("error", "Club ID does not exist!");
            }



            return map;
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }

    }

    public static Map<String, Object> getEvent(int eventID) {
        try {
            List<Event> eventList = eventDao.queryForEq("id", eventID);
            LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();

            if (eventList.size() > 0) {
                Event event = eventList.get(0);

                String eventIDString = ((Integer) event.getId()).toString();


                map.put("id", event.getId());
                map.put("name", event.getName());
                map.put("description", event.getDescription());
                map.put("time", event.getTime());
                map.put("location", event.getLocation());
                map.put("flyer", event.getFlyer());
                map.put("clubs", queryClubsForEvent(eventIDString, true));
            } else {
                map.put("error", "Event ID does not exist!");
            }



            return map;
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }

    }

    public static List<LinkedHashMap<String, Object>> getAllUsers() {
        try {
            List<User> userList = userDao.queryForAll();

            List<LinkedHashMap<String, Object>> results = new ArrayList<LinkedHashMap<String, Object>>();

            for (User user : userList) {
                String userID = ((Integer) user.getId()).toString();

                LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
                map.put("id", user.getId());
                map.put("name", user.getName());
                map.put("email", user.getEmail());
                map.put("clubs", queryClubsForUser(userDao.queryForId(userID)));

                results.add(map);
            }

            return results;
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static List<LinkedHashMap<String, Object>> getAllClubs() {
        try {
            List<Club> clubList = clubDao.queryForAll();

            List<LinkedHashMap<String, Object>> results = new ArrayList<LinkedHashMap<String, Object>>();

            for (Club club : clubList) {
                String clubID = ((Integer) club.getId()).toString();

                LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
                map.put("id", club.getId());
                map.put("name", club.getName());
                map.put("description", club.getDescription());
                map.put("image", club.getImage());
                President president = presidentDao.queryForId(((Integer) club.getPresident().getId()).toString());
                map.put("president", president);
                map.put("events", queryEventsForClub(clubID, false));
                map.put("members", queryUsersForClub(clubID));

                results.add(map);
            }

            return results;
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static List<LinkedHashMap<String, Object>> getAllEvents() {
        try {
            List<Event> eventList = eventDao.queryForAll();

            Calendar cal = Calendar.getInstance();
            cal.roll(Calendar.HOUR, -12);

            eventList = eventList.stream().filter(e -> new Timestamp(cal.getTime().getTime()).compareTo(e.getTime()) >= 0).collect(Collectors.toList());
            eventList.sort(Event::compareTo);
            List<LinkedHashMap<String, Object>> results = new ArrayList<LinkedHashMap<String, Object>>();

            for (Event event : eventList) {

                String eventID = ((Integer) event.getId()).toString();

                LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
                map.put("id", event.getId());
                map.put("name", event.getName());
                map.put("description", event.getDescription());
                map.put("time", event.getTime());
                map.put("location", event.getLocation());
                map.put("flyer", event.getFlyer());
                map.put("clubs", queryClubsForEvent(eventID, true));

                results.add(map);
            }

            return results;
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void addUser(User user)
    {
        try {
            userDao.create(user);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void addPresident(President president) {
        try {
            presidentDao.create(president);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void addClub(Club club) {
        try {
            clubDao.create(club);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void addEvent(Event event) {
        try {
            eventDao.create(event);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void updateUser(User user)
    {
        try {
            userDao.update(user);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }


    //UserClub
    public static void linkUserClub(User user, Club club)
    {
        try {
            UserClub uc = new UserClub(user, club);
            userClubDao.create(uc);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static List<LinkedHashMap<String, Object>> queryUsersForClub(String clubID) {
        try {
            Club club = clubDao.queryForId(clubID);
            List<UserClub> userClubList = userClubDao.queryForEq("club_id", club.getId());

            List<LinkedHashMap<String, Object>> results = new ArrayList<LinkedHashMap<String, Object>>();

            for (UserClub uc : userClubList) {
                Integer id = uc.getUser().getId();
                User user = userDao.queryForId(id.toString());

                LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();

                map.put("id", user.getId());
                map.put("name", user.getName());
                map.put("email", user.getEmail());

                results.add(map);
            }

            return results;
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static List<LinkedHashMap<String, Object>> queryClubsForUser(User user) {
        try {
            List<UserClub> userClubList = userClubDao.queryForEq("user_id", user.getId());

            List<Club> clubList = new ArrayList<Club>();

            for (UserClub uc : userClubList) {
                Integer id = uc.getClub().getId();
                clubList.add(clubDao.queryForId(id.toString()));
            }

            List<LinkedHashMap<String, Object>> results = new ArrayList<LinkedHashMap<String, Object>>();

            for (Club club : clubList) {
                String clubID = ((Integer) club.getId()).toString();

                LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
                map.put("id", club.getId());
                map.put("name", club.getName());
                map.put("description", club.getDescription());
                map.put("image", club.getImage());
                President president = presidentDao.queryForId(((Integer) club.getPresident().getId()).toString());
                map.put("president", president);
                map.put("events", queryEventsForClub(clubID, true));
                map.put("members", queryUsersForClub(clubID));

                results.add(map);
            }

            return results;

        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }


    //EventClub
    public static void linkEventClub(Event event, Club club)
    {
        try {
            EventClub ec = new EventClub(event, club);
            eventClubDao.create(ec);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static List<LinkedHashMap<String, Object>> queryEventsForClub(String clubID, boolean getClubs) {
        try {
            Club club = clubDao.queryForId(clubID);
            List<EventClub> eventClubList = eventClubDao.queryForEq("club_id", club.getId());

            List<LinkedHashMap<String, Object>> results = new ArrayList<LinkedHashMap<String, Object>>();

            for (EventClub ec : eventClubList) {
                Integer id = ec.getEvent().getId();
                Event event = eventDao.queryForId(id.toString());

                LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();

                map.put("id", event.getId());
                map.put("name", event.getName());
                map.put("description", event.getDescription());
                map.put("time", event.getTime());
                map.put("location", event.getLocation());
                map.put("flyer", event.getFlyer());
                if (getClubs) map.put("clubs", queryClubsForEvent(((Integer) event.getId()).toString(), false));

                results.add(map);
            }

            return results;
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static List<LinkedHashMap<String, Object>> queryClubsForEvent(String eventID, boolean getEvents) {
        try {
            Event event = eventDao.queryForId(eventID);
            List<EventClub> eventClubList = eventClubDao.queryForEq("event_id", event.getId());

            List<Club> clubList = new ArrayList<Club>();

            for (EventClub ec : eventClubList) {
                Integer id = ec.getClub().getId();
                clubList.add(clubDao.queryForId(id.toString()));
            }

            List<LinkedHashMap<String, Object>> results = new ArrayList<LinkedHashMap<String, Object>>();

            for (Club club : clubList) {
                String clubID = ((Integer) club.getId()).toString();

                LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
                map.put("id", club.getId());
                map.put("name", club.getName());
                map.put("description", club.getDescription());
                map.put("image", club.getImage());
                President president = presidentDao.queryForId(((Integer) club.getPresident().getId()).toString());
                map.put("president", president);
                if (getEvents) map.put("events", queryEventsForClub(clubID, false));
                map.put("members", queryUsersForClub(clubID));

                results.add(map);
            }

            return results;

        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void initialize()
    {
        try {
            ConnectionSource connectionSource = getConnectionSource();

            userDao = DaoManager.createDao(connectionSource, User.class); //Create Data Access Object
            TableUtils.createTableIfNotExists(connectionSource, User.class); //Create users table iff it doesn't exist

            clubDao = DaoManager.createDao(connectionSource, Club.class);
            TableUtils.createTableIfNotExists(connectionSource, Club.class);

            eventDao = DaoManager.createDao(connectionSource, Event.class);
            TableUtils.createTableIfNotExists(connectionSource, Event.class);

            presidentDao = DaoManager.createDao(connectionSource, President.class);
            TableUtils.createTableIfNotExists(connectionSource, President.class);


            userClubDao = DaoManager.createDao(connectionSource, UserClub.class);
            TableUtils.createTableIfNotExists(connectionSource, UserClub.class);

            eventClubDao = DaoManager.createDao(connectionSource, EventClub.class);
            TableUtils.createTableIfNotExists(connectionSource, EventClub.class);



        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }

    }

    private static ConnectionSource getConnectionSource() throws SQLException {


        connectionSource = new JdbcConnectionSource(databaseURL);
        ((JdbcConnectionSource)connectionSource).setUsername(databaseUser);
        ((JdbcConnectionSource)connectionSource).setPassword(databasePassword);
        return connectionSource;

    }

    public static void terminate()
    {
        try {
            connectionSource.close();
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
