package main.java.models.db_objects.join_tables;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import main.java.models.db_objects.Club;
import main.java.models.db_objects.Event;
import main.java.models.db_objects.User;

import java.sql.Timestamp;

@DatabaseTable(tableName = "events_clubs") //Define SQL Table
public class EventClub
{
    public final static String EVENT_ID_FIELD_NAME = "event_id";
    public final static String CLUB_ID_FIELD_NAME = "club_id";

    @DatabaseField(generatedId = true) //auto-increment
    private int id;

    @DatabaseField(dataType = DataType.TIME_STAMP)
    private Timestamp modified_at;

    @DatabaseField(foreign = true, columnName = EVENT_ID_FIELD_NAME)
    Event event;

    @DatabaseField(foreign = true, columnName = CLUB_ID_FIELD_NAME)
    Club club;



    public EventClub() {} //ORMLite requires an empty constructor to do its magic

    public EventClub(Event event, Club club) {
        this.event = event;
        this.club = club;
    }

    public int getId() {return this.id;}

    public Timestamp getModified() {return this.modified_at;}

    public Event getEvent() {return this.event;}
    public void setEvent(Event event) {this.event = event;}

    public Club getClub() {return this.club;}
    public void setClub(Club user) {this.club = club;}

}