package main.java.models.db_objects;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;

@DatabaseTable(tableName = "events") //Define SQL Table
public class Event implements Comparable<Event>
{
    @DatabaseField(generatedId = true) //auto-increment
    private int id;

    @DatabaseField(dataType = DataType.TIME_STAMP)
    private Timestamp modified_at;

    @DatabaseField
    private String name;

    @DatabaseField
    private String description;

    @DatabaseField
    private Timestamp time;

    @DatabaseField
    private String location;

    @DatabaseField
    private String flyer;

    public Event() {} //ORMLite requires an empty constructor to do its magic

    public int getId() {return this.id;}

    public Timestamp getModified() {return this.modified_at;}

    public String getName() {return this.name;}
    public void setName(String name) {this.name = name;}

    public String getDescription() {return this.description;}
    public void setDescription(String description) {this.description = description;}

    public Timestamp getTime() {return this.time;}
    public void setTime(Timestamp time) {this.time = time;}

    public String getLocation() {return this.location;}
    public void setLocation(String location) {this.location = location;}

    public String getFlyer() {return this.flyer;}
    public void setFlyer(String flyer) {this.flyer = flyer;}

    @Override
    public int compareTo(Event o) {
        return this.time.compareTo(o.time);
    }
}