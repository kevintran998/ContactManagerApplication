package com.kkt160130.contactmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final  String DATABASE_NAME = "Contacts.db";
    private static final int DATA_BASE_VERSION = 2;

    // columns for database
    private static final String TABLE_NAME = "contactsTable";
    private static final String PRIMARY_KEY = "contactID";
    private static final String ATTRIBUTE_1 = "firstName";
    private static final String ATTRIBUTE_2 = "lastName";
    private static final String ATTRIBUTE_3 = "phoneNum";
    private static final String ATTRIBUTE_4 = "birthDate_Year";
    private static final String ATTRIBUTE_5 = "birthDate_Month";
    private static final String ATTRIBUTE_6 = "birthDate_Day";
    private static final String ATTRIBUTE_7 = "firstContact_Year";
    private static final String ATTRIBUTE_8 = "firstContact_Month";
    private static final String ATTRIBUTE_9 = "firstContact_Date";
    private static final String ATTRIBUTE_10 = "addressLine1";
    private static final String ATTRIBUTE_11 = "addressLine2";
    private static final String ATTRIBUTE_12 = "city";
    private static final String ATTRIBUTE_13 = "state";
    private static final String ATTRIBUTE_14 = "zipcode";

    // first version of database
    private static final String CREATE_TABLE_1 = "CREATE TABLE " + TABLE_NAME + " ( " + PRIMARY_KEY +
            " INTEGER PRIMARY KEY AUTOINCREMENT, " + ATTRIBUTE_1 + " TEXT, " + ATTRIBUTE_2 +
            " TEXT, " + ATTRIBUTE_3 + " TEXT, " + ATTRIBUTE_4 + " INTEGER, " + ATTRIBUTE_5 +
            " INTEGER, " + ATTRIBUTE_6 + " INTEGER, " + ATTRIBUTE_7 + " INTEGER, " + ATTRIBUTE_8 +
            " INTEGER, " + ATTRIBUTE_9 + " INTEGER)";

    // second version of database
    private static final String CREATE_TABLE_2 = "CREATE TABLE " + TABLE_NAME + " ( " + PRIMARY_KEY +
            " INTEGER PRIMARY KEY AUTOINCREMENT, " + ATTRIBUTE_1 + " TEXT, " + ATTRIBUTE_2 +
            " TEXT, " + ATTRIBUTE_3 + " TEXT, " + ATTRIBUTE_4 + " INTEGER, " + ATTRIBUTE_5 +
            " INTEGER, " + ATTRIBUTE_6 + " INTEGER, " + ATTRIBUTE_7 + " INTEGER, " + ATTRIBUTE_8 +
            " INTEGER, " + ATTRIBUTE_9 + " INTEGER, " + ATTRIBUTE_10 + " TEXT, " + ATTRIBUTE_11 +
            " TEXT, " + ATTRIBUTE_12 + " TEXT, " + ATTRIBUTE_13 + " TEXT, " + ATTRIBUTE_14 + " TEXT)";

    // database
    private SQLiteDatabase database;

    // constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATA_BASE_VERSION);
    }

    // deletes database
    public void deleteDatabase(Context context)
    {
        context.deleteDatabase(DATABASE_NAME);
    }

    // creates database in onCreate
    @Override
    public void onCreate(SQLiteDatabase db) {
        this.database = db;
        db.execSQL(CREATE_TABLE_2);
    }

    // upgrades database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        switch(oldVersion)
        {
            // adds new columns to first database
            case 1:
                db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + ATTRIBUTE_10 + "INTEGER");
                db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + ATTRIBUTE_11 + "INTEGER");
                db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + ATTRIBUTE_12 + "INTEGER");
                db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + ATTRIBUTE_13 + "INTEGER");
                db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + ATTRIBUTE_14 + "INTEGER");
            default:
                break;
        }
    }

    // gets a single contact
    // *****NOT USED IN THE PROGRAM*****
    public Contact getContact(int id)
    {
        Contact contact = new Contact();
        database = getReadableDatabase();

        Cursor c = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + PRIMARY_KEY + " = " + id,null);

        if (c.moveToFirst())
        {
            int birthDate[] = new int[] {c.getInt(c.getColumnIndex(ATTRIBUTE_4)),
                    c.getInt(c.getColumnIndex(ATTRIBUTE_5)), c.getInt(c.getColumnIndex(ATTRIBUTE_6))};
            int firstContact[] = new int[] {c.getInt(c.getColumnIndex(ATTRIBUTE_7)),
                    c.getInt(c.getColumnIndex(ATTRIBUTE_8)), c.getInt(c.getColumnIndex(ATTRIBUTE_9))};

            contact.setID(c.getInt(c.getColumnIndex(PRIMARY_KEY)));
            contact.setFirstName(c.getString(c.getColumnIndex(ATTRIBUTE_1)));
            contact.setLastName(c.getString(c.getColumnIndex(ATTRIBUTE_2)));
            contact.setPhoneNum(c.getString(c.getColumnIndex(ATTRIBUTE_3)));
            contact.setBirthDate(birthDate);
            contact.setFirstContact(firstContact);
            contact.setFirstName(c.getString(c.getColumnIndex(ATTRIBUTE_10)));
            contact.setFirstName(c.getString(c.getColumnIndex(ATTRIBUTE_11)));
            contact.setFirstName(c.getString(c.getColumnIndex(ATTRIBUTE_12)));
            contact.setFirstName(c.getString(c.getColumnIndex(ATTRIBUTE_13)));
            contact.setFirstName(c.getString(c.getColumnIndex(ATTRIBUTE_14)));
        }

        c.close();
        return contact;
    }

    // gets entire contact list
    public ArrayList<Contact> getContactList()
    {
        ArrayList<Contact> contactList = new ArrayList<Contact>();
        database = getReadableDatabase();

        // cursor returns all entries from table
        Cursor c = database.rawQuery("SELECT * FROM "  + TABLE_NAME, null);

        // if there is at least one contact in the table
        if (c.moveToFirst())
        {
            do
            {
                // contact object
                Contact contact = new Contact();

                // creates birth date and date of first contact int arrays
                int birthDate[] = new int[] {c.getInt(c.getColumnIndex(ATTRIBUTE_4)),
                        c.getInt(c.getColumnIndex(ATTRIBUTE_5)), c.getInt(c.getColumnIndex(ATTRIBUTE_6))};
                int firstContact[] = new int[] {c.getInt(c.getColumnIndex(ATTRIBUTE_7)),
                        c.getInt(c.getColumnIndex(ATTRIBUTE_8)), c.getInt(c.getColumnIndex(ATTRIBUTE_9))};

                // set values of contact object from table
                contact.setID(c.getInt(c.getColumnIndex(PRIMARY_KEY)));
                contact.setFirstName(c.getString(c.getColumnIndex(ATTRIBUTE_1)));
                contact.setLastName(c.getString(c.getColumnIndex(ATTRIBUTE_2)));
                contact.setPhoneNum(c.getString(c.getColumnIndex(ATTRIBUTE_3)));
                contact.setBirthDate(birthDate);
                contact.setFirstContact(firstContact);
                contact.setAddress1(c.getString(c.getColumnIndex(ATTRIBUTE_10)));
                contact.setAddress2(c.getString(c.getColumnIndex(ATTRIBUTE_11)));
                contact.setCity(c.getString(c.getColumnIndex(ATTRIBUTE_12)));
                contact.setState(c.getString(c.getColumnIndex(ATTRIBUTE_13)));
                contact.setZipcode(c.getString(c.getColumnIndex(ATTRIBUTE_14)));
                contactList.add(contact);
            } while (c.moveToNext()); // while there is another contact in the list
        }

        c.close();
        return contactList;
    }

    // adds a new contact to the database
    public void addContact(String fn, String ln, String pn, int[] bd, int[] fc, String a1, String a2, String city, String s, String z)
    {
        database = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ATTRIBUTE_1, fn);
        cv.put(ATTRIBUTE_2, ln);
        cv.put(ATTRIBUTE_3, pn);
        cv.put(ATTRIBUTE_4, bd[0]);
        cv.put(ATTRIBUTE_5, bd[1]);
        cv.put(ATTRIBUTE_6, bd[2]);
        cv.put(ATTRIBUTE_7, fc[0]);
        cv.put(ATTRIBUTE_8, fc[1]);
        cv.put(ATTRIBUTE_9, fc[2]);
        cv.put(ATTRIBUTE_10, a1);
        cv.put(ATTRIBUTE_11, a2);
        cv.put(ATTRIBUTE_12, city);
        cv.put(ATTRIBUTE_13, s);
        cv.put(ATTRIBUTE_14, z);
        database.insert(TABLE_NAME, null, cv);
    }

    // updates contact values in the database
    public void updateContact(int id, String fn, String ln, String pn, int[] bd, int[] fc, String a1, String a2, String city, String s, String z)
    {
        database = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ATTRIBUTE_1, fn);
        cv.put(ATTRIBUTE_2, ln);
        cv.put(ATTRIBUTE_3, pn);
        cv.put(ATTRIBUTE_4, bd[0]);
        cv.put(ATTRIBUTE_5, bd[1]);
        cv.put(ATTRIBUTE_6, bd[2]);
        cv.put(ATTRIBUTE_7, fc[0]);
        cv.put(ATTRIBUTE_8, fc[1]);
        cv.put(ATTRIBUTE_9, fc[2]);
        cv.put(ATTRIBUTE_10, a1);
        cv.put(ATTRIBUTE_11, a2);
        cv.put(ATTRIBUTE_12, city);
        cv.put(ATTRIBUTE_13, s);
        cv.put(ATTRIBUTE_14, z);
        database.update(TABLE_NAME, cv, PRIMARY_KEY + " = " + id, null);
    }

    // deletes contact from the database
    public void deleteContact(int id) {
        database = getWritableDatabase();
        database.delete(TABLE_NAME, PRIMARY_KEY + " = " + id, null);
    }

    // checks if a contact exists in the database
    // *****NOT USED IN THE PROGRAM*****
    public int contactExists(int id)
    {
        int count = 0;
        database = getReadableDatabase();

        Cursor c = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + PRIMARY_KEY + " = " + id,null);
        count = c.getCount();

        c.close();
        return count;
    }
}
