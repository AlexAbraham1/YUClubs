package main.java.models.db_objects;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;

@DatabaseTable(tableName = "clubs") //Define SQL Table
public class Club
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
    private String image; //path to club image file

    @DatabaseField(foreign = true)
    private President president;

    public Club() {} //ORMLite requires an empty constructor to do its magic

    public int getId() {return this.id;}

    public Timestamp getModified() {return this.modified_at;}

    public String getName() {return this.name;}
    public void setName(String name) {this.name = name;}

    public String getDescription() {return this.description;}
    public void setDescription(String description) {this.description = description;}

    public String getImage() {return this.image;}
    public void setImage(String image) {this.image = image;}

    public President getPresident() {return this.president;}
    public void setPresident(President president) {this.president = president;}

}