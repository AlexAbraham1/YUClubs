package main.java.services;


import main.java.models.DBC;
import main.java.models.Email;
import main.java.models.db_objects.Club;
import main.java.models.db_objects.Event;
import main.java.models.db_objects.User;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

public class EventService {

    public static Object createEvent(String clubID, String name, String description, String time, String location, String flyer) {

        if (clubID == null || clubID.equals("")) {
            return getErrorMap("You forgot the club ID!");
        }

        if (name == null || name.equals("")) {
            return getErrorMap("You forgot the event name!");
        }

        if (description == null || description.equals("")) {
            return getErrorMap("You forgot the event description!");
        }

        if (time == null) {
            return getErrorMap("You forgot the event time!");
        }

        if (location == null || location.equals("")) {
            return getErrorMap("You forgot the event location!");
        }

        //Everything is good! Let's create the account

        Timestamp timestamp = null;

        try {
          timestamp = Timestamp.valueOf(time);
        } catch (IllegalArgumentException e) {
            return getErrorMap("The timestamp is not in the right format!");
        }

        Event newEvent = new Event();
        newEvent.setName(name);
        newEvent.setDescription(description);
        newEvent.setTime(timestamp);
        newEvent.setLocation(location);
        newEvent.setFlyer(flyer);

        DBC.addEvent(newEvent);

        DBC.linkEventClub(newEvent, DBC.queryClub(Integer.parseInt(clubID)));

        //Add user to session and go to /me
        return newEvent;
    }

    public static Object linkEventToClub(String eventIDString, String clubIDString) {
        Integer clubID = Integer.parseInt(clubIDString);
        Integer eventID = Integer.parseInt(eventIDString);
        Club club = DBC.queryClub(clubID);
        Event event = DBC.queryEvent(eventID);
        DBC.linkEventClub(event, club);
        return "Link Successful";
    }

    public static Object getEventsOnDay(String day) {

        List<LinkedHashMap<String, Object>> events = DBC.getAllEvents();

        List<LinkedHashMap<String, Object>> results = new ArrayList<LinkedHashMap<String, Object>>();

        for (LinkedHashMap<String, Object> map : events) {
            String time = map.get("time").toString().substring(0, 10);
            if (time.equals(day.substring(0, 10))) results.add(map);
        }

        return results;
    }

    private static LinkedHashMap<String, Object> getErrorMap(String message)
    {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();

        map.put("error", message);

        return map;
    }
}
