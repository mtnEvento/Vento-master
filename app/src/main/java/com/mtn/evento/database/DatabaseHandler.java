package com.mtn.evento.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mtn.evento.data.Event;
import com.mtn.evento.data.Location;
import com.mtn.evento.data.Ticket;
import com.mtn.evento.data.Ticket_Type;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Summy on 8/8/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "ReservedManager";

    // Contacts table name
    private static final String TABLE_RESERVED_EVENT = "Reserved_Event";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_EVENT_TITLE = "evt_title";
    private static final String KEY_EVENT_ID = "evt_id";
    private static final String KEY_EVENT = "evt_event";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RESERVED_EVENT_TABLE = "CREATE TABLE " + TABLE_RESERVED_EVENT + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_EVENT_ID + " TEXT,"
                + KEY_EVENT_TITLE + " TEXT,"
                + KEY_EVENT + " TEXT" + ")";
        db.execSQL(CREATE_RESERVED_EVENT_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVED_EVENT);

        // Create tables again
        onCreate(db);
    }


    // [Adding new event]
    public void addEvent(Event event) {

        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            String json = "";

            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            json = ow.writeValueAsString(event);

            ContentValues values = new ContentValues();
            values.put(KEY_EVENT_ID, event.getEvent_id());
            values.put(KEY_EVENT_TITLE,event.getTitle());
            values.put(KEY_EVENT,json);

            // Inserting Row
            db.insert(TABLE_RESERVED_EVENT, null, values);
            db.close(); // Closing database connection

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    // [Adding new event]

    // [Getting All reserved Events]
    public List<Event> getAllreservedEvents() {

        try
        {
            List<Event> eventList = new ArrayList<Event>();
            // Select All Query
            String selectQuery = "SELECT  * FROM " + TABLE_RESERVED_EVENT;
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Event event = new Event();
                    String str_event =  cursor.getString(3);
                    JSONObject object = new JSONObject(str_event);
                    event.setEvent_id(object.getString("event_id"));
                    event.setEvent_type(object.getString("event_type"));
                    event.setRegion(object.getString("region"));
                    event.setBanner(object.getString("banner"));
                    event.setTotal_seats(object.getString("total_seats"));
                    event.setDescription(object.getString("description"));
                    event.setDate_published(object.getString("date_published"));
                    event.setVenue(object.getString("venue"));
                    event.setEvent_name(object.getString("event_name"));
                    event.setTitle(object.getString("title"));
                    event.setEvent_date(object.getString("event_date"));
                    JSONObject locationObject =  object.getJSONObject("location");
                    String latitude = locationObject.getString("latitude");
                    String longitude = locationObject.getString("longitude");
                    event.setLocation(new Location(Double.parseDouble(latitude),Double.parseDouble(longitude)));
                    ArrayList<Ticket> tickets = new ArrayList<>();
                    JSONArray ticketTypeArr =  object.getJSONArray("ticket_type");

                    for (int i = 0; i < ticketTypeArr.length(); i++) {
                        JSONObject mTicket =  ticketTypeArr.getJSONObject(i);
                        String ticket_name = mTicket.getString("name");
                        String ticket_amount = mTicket.getString("amount");
                        Ticket ticket = new Ticket(ticket_name,ticket_amount);
                        tickets.add(ticket);
                    }
                    event.setTicket_type(new Ticket_Type(tickets));
                    // Adding event to list
                    eventList.add(event);
                } while (cursor.moveToNext());
            }
            // return event list
            return eventList;
        } catch (JSONException e) {
            e.printStackTrace();
            return  null;
        }

    }
    // [Getting All reserved Events]

    // [Getting events Count]
    public int getEventsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_RESERVED_EVENT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        // return count
        return cursor.getCount();
    }
    // [Getting events Count]

    // [Updating single event]
    public int updateContact(Event event) {
        // updating row
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            String json = "";

            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            json = ow.writeValueAsString(event);

            ContentValues values = new ContentValues();
            values.put(KEY_EVENT_ID, event.getEvent_id());
            values.put(KEY_EVENT_TITLE,event.getTitle());
            values.put(KEY_EVENT,json);

            // Inserting Row
            int i = db.update(TABLE_RESERVED_EVENT, values, KEY_EVENT_ID + " = ?",
                    new String[] { String.valueOf(event.getEvent_id()) });
            db.close();
            return i;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
    // [Updating single event]

    // [Deleting single event]
    public void deleteEvent(Event event){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RESERVED_EVENT, KEY_EVENT_ID + " = ?",
                new String[] { String.valueOf(event.getEvent_id()) });
        db.close();
    }
}