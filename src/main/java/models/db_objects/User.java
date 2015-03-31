package main.java.models.db_objects;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;

@DatabaseTable(tableName = "users") //Define SQL Table
public class User
{
    @DatabaseField(generatedId = true) //auto-increment
    private int id;

    @DatabaseField(dataType = DataType.TIME_STAMP)
    private Timestamp modified_at;

    @DatabaseField
    private String name;

    @DatabaseField
    private String email;

    @DatabaseField
    private String password; //This should be encrypted!!!

    @DatabaseField(foreign = true)
    private President president;



    public User() {} //ORMLite requires an empty constructor to do its magic

    public int getId() {return this.id;}

    public Timestamp getModified() {return this.modified_at;}

    public String getName() {return this.name;}
    public void setName(String name) {this.name = name;}

    public String getEmail() {return this.email;}
    public void setEmail(String email) {this.email = email;}

    public String getPassword() {return this.password;}
    public void setPassword(String password) {this.password = password;}

    public President getPresident() {return this.president;}
    public void setPresident(President president) {this.president = president;}

}