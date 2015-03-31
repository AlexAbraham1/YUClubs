package main.java.models.db_objects;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;

@DatabaseTable(tableName = "presidents") //Define SQL Table
public class President
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
    private String phone;

    @DatabaseField
    private String image;

    @DatabaseField(foreign = true)
    private User user;



    public President() {} //ORMLite requires an empty constructor to do its magic

    public int getId() {return this.id;}

    public Timestamp getModified() {return this.modified_at;}

    public String getName() {return this.name;}
    public void setName(String name) {this.name = name;}

    public String getEmail() {return this.email;}
    public void setEmail(String email) {this.email = email;}

    public String getPhone() {return phone;}
    public void setPhone(String phone) {this.phone = phone;}

    public String getImage() {return this.image;}
    public void setImage(String image) {this.image = image;}

    public User getUser() {return user;}
    public void setUser(User user) {this.user = user;}

}