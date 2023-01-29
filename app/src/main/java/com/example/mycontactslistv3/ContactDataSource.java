package com.example.mycontactslistv3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
/*
The Data Access class it the class that opens and closes the database and
contains the queries used to store and retrieve data from the database.
 */
public class ContactDataSource {
    private SQLiteDatabase database;
    private ContactDBHelper dbHelper;
//the helper and data source classes are instantiated
    public ContactDataSource(Context context){
        dbHelper = new ContactDBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }
    /*
    public void close() {
        dbHelper.close();
    }

     */
    //Insert and Update Contact Methods
    public boolean insertContact(Contact c) {

        boolean didSucceed = false;
        try {
            database = dbHelper.getWritableDatabase();
            ContentValues initialValues = new ContentValues();  //obj used to store key/value pairs

            //Values are retrieved from the contact object and inserted into the ContentValues obj
            initialValues.put("contactname", c.getContactName());
            initialValues.put("streetaddress", c.getStreetAddress());
            initialValues.put("city", c.getCity());
            initialValues.put("state", c.getState());
            initialValues.put("zipcode", c.getZipCode());
            initialValues.put("cellnumber", c.getCellNumber());
            initialValues.put("email", c.geteMail());
            initialValues.put("birthday", String.valueOf(c.getBirthday().getTimeInMillis())); //set to millis, SQLite does not support stored dates direclty

            /*
          The didSucceed.insert called and passed to the name of the table and values to insert
          returning the number of rows updated. If it's greater than 0 the operation succeeded and the
          return value is set to true.
            */
           // didSucceed = database.insert("contact", null, initialValues) > 0;


            didSucceed = database.insert("contact",null,initialValues) > 0;

        }
        catch (Exception e) {
            //if an exception is thrown the value remains false because the 0 is not greater than 0
        }
        return didSucceed;

    }

    public boolean updateContact(Contact c) {
        boolean didSucceed = false;
        try {
            Long rowID = (long) c.getContactID();
            /*
            Update Procedure need the contactID to correctly update the table. Value retreived
            from contact object and assigned to the var
            */
            ContentValues updateValues = new ContentValues();

            updateValues.put("contactname", c.getContactName());
            updateValues.put("streetaddress", c.getStreetAddress());
            updateValues.put("city", c.getCity());
            updateValues.put("state", c.getState());
            updateValues.put("zipcode", c.getZipCode());
            updateValues.put("cellnumber", c.getCellNumber());
            updateValues.put("email", c.geteMail());
            updateValues.put("birthday", String.valueOf(c.getBirthday().getTimeInMillis()));

            didSucceed = database.update("contact", updateValues, "_id=" + rowID, null) > 0;
        }
        catch (Exception e){

        }
        return didSucceed;
    }

    /*
    If the user adds a new contact, presses the Save button, and then edits the data and presses Save again,
    another contact will be added, rather than updating the contact just entered.
    This is because the currentContact object still has an ID of –1
    This gets the new ID and set the currentContact contactID attribute to that value.
     */
    public int getLastContactID() {
        int lastID;
        try {
            String query = "Select MAX(_id) from contact";
            Cursor cursor = database.rawQuery(query, null); //query that returns the last (max) _id
//A cursor is declared and assigned to hold the results of the execution of the query. A cursor is an object that is used to hold and move through the results of a query.
            cursor.moveToFirst(); //Cursor is told to move to the first record in the returned data.
            lastID = cursor.getInt(0);  //The maximum ID is retrieved from the record set. Fields in the record set are indexed starting at 0.
            cursor.close();  // Dont forget to close dp's and cursors!!!
        }
        catch (Exception e) {
            lastID = -1;
        }
        return lastID;
    }
    public void close() {
        dbHelper.close();
    }




}
