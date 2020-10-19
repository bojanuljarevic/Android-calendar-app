package bojan.uljarevic.calendarapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class EventDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "events.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "event";
    public static final String COLUMN_DATE = "Date";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_TIME = "Time";
    public static final String COLUMN_REMINDER = "Reminder";
    public static final String COLUMN_LOCATION  = "Location";
    public static final String COLUMN_TIME2 = "Time2";
    public static final String COLUMN_DURATION = "Duration";


    public EventDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_DATE + " TEXT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_TIME + " TEXT, " +
                COLUMN_REMINDER + " INTEGER, " +
                COLUMN_LOCATION + " TEXT, " +
                COLUMN_TIME2 + " TEXT, " +
                COLUMN_DURATION + " INTEGER, " +
                "PRIMARY KEY (" + COLUMN_DATE + "," + COLUMN_NAME + "));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(Event event) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, event.getDate());
        values.put(COLUMN_NAME, event.getName());
        values.put(COLUMN_TIME, event.getTime());
        values.put(COLUMN_REMINDER, event.getReminder());                     // CHECKBOX
        values.put(COLUMN_LOCATION, event.getLoc());
        values.put(COLUMN_TIME2, event.getTime2());
        values.put(COLUMN_DURATION, event.getmDuration());

        db.insert(TABLE_NAME, null, values);
        close();
    }

    public Event[] readEvents(String date) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_DATE + "=?",
                new String[] {date}, null, null, null, null);

        if (cursor.getCount() <= 0) {
            return null;
        }

        Event[] events = new Event[cursor.getCount()];
        int i = 0;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            events[i++] = createEvent(cursor);
        }

        close();
        return events;
    }


    public Event readEvent(String name, String date) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_NAME + "=?" + " AND " + COLUMN_DATE + "=?",
                new String[] {name, date}, null, null, null);

        if (cursor.getCount() <= 0) {
            return null;
        }

        cursor.moveToFirst();
        Event event = createEvent(cursor);

        close();
        return event;
    }

    public void deleteEvent(String name, String date) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_NAME + "=?"+ " AND " + COLUMN_DATE + "=?", new String[] {name, date});
        close();
    }

    private Event createEvent(Cursor cursor) {

        String Date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
        String Name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
        String Time = cursor.getString(cursor.getColumnIndex(COLUMN_TIME));
        String Loc = cursor.getString(cursor.getColumnIndex(COLUMN_LOCATION));
        int rem = cursor.getInt(cursor.getColumnIndex(COLUMN_REMINDER));
        String Time2 = cursor.getString(cursor.getColumnIndex(COLUMN_TIME2));
        int duration = cursor.getInt(cursor.getColumnIndex(COLUMN_DURATION));
        boolean reminder;
        if (rem == 0) {
            reminder = false;
        } else {
            reminder = true;
        }


        return new Event(Date, Name, Time, Loc, reminder, Time2, duration);
    }

    public void DeleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }
}
