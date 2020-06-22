// Contact Manager App
// Written By: Kevin Tran
//
// Included Files:
// MainActivity.java - scrollable contact list
// ContactActivity.java - contact viewing/editing activity
// MapsFragment.java - google maps activity that displays contact's location
// CalendarFragment.java - date picker fragment
// ContactAdapter.java - custom adapter that creates format of contact list
// Contact.java - Contact class
// DatabaseHelper.java - Database class
// activity_main.xml - contains list view and floating action button
// activity_contact.xml - contains format for ContactActivity.java
// activity_maps_fragment - contains map view
// adapter_layout.xml - contains format for ContactAdapter.java
//
// About:
// This is a contact manager app that allows users to add, edit, and delete contacts. The app consists
// of two activities: main and the contact editing activity. The main activity contains a scrollable list
// of contacts that are stored in a SQLite database. The user can click on any existing contact to
// edit or delete them, or the user can add a new contact by pressing on the floating action button.
// The database can also be reinitialized in the menu options. The contact editing activity consists
// of 10 fields: first name, last name, phone number, birth date, date of first contact, 2 address lines,
// city, state, and zip code. The save button will update existing contacts or create new contacts.
// The delete button will remove the contact. If the user shakes the device, their contact list
// will display in reverse. The "Map Address" option in the menu will display the contact's address
// in google maps.
//


package com.kkt160130.contactmanager;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private ArrayList<Contact> contactList = new ArrayList<>();
    private ContactAdapter adapter;
    private DatabaseHelper database;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // customized toolbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_layout);

        // create database
        database = new DatabaseHelper(this);

        // initialize sensor
        SensorManager sm;
        Sensor acc;

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acc = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL);

        // click listener for fab; creates a new contact
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (MainActivity.this, ContactActivity.class); // new intent
                Bundle bundle = new Bundle(); // new bundle
                int position = -1; // new contact does not have a current position, so set to -1
                bundle.putInt("position", position); // add position integer to bundle
                intent.putExtra("bundle", bundle); // add bundle to intent
                startActivityForResult(intent, 1); // start activity
            }
        });

        // initialize contacts that already exist in the database
        redrawScreen();

        // click listener for listview; edits existing contacts
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent (MainActivity.this, ContactActivity.class); // new intent
                Bundle bundle = new Bundle(); // new bundle
                bundle.putSerializable("contact", contactList.get(position)); // add Contact clicked to bundle
                bundle.putInt("position", position); // add position integer to bundle
                intent.putExtra("bundle", bundle); // add bundle to intent
                startActivityForResult(intent, 1); // start activity
            }
        });
    }

    // result for ContactActivity
    // redraws the contact list with any changes made
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        redrawScreen();
    }

    // redraws the contact list
    private void redrawScreen()
    {
        contactList = database.getContactList();
        listView = findViewById(R.id.listView);
        adapter = new ContactAdapter(MainActivity.this, R.layout.adapter_layout, contactList);
        listView.setAdapter(adapter);
    }

    // Variables for Sensor
    private static final int MIN_SHAKE_INTERVAL = 2000; // minimum 2-second interval between each shake
    private static final int SHAKE_THRESHOLD = 700;     // threshold for shake sensitivity
    float curVals [] = new float[3];                    // current accelerometer values
    float prevVals [] = new float[3];                   // previous accelerometer values
    long prevTime;                                      // previous time of shake

    // Function to handle sensor changes
    @Override
    public void onSensorChanged(SensorEvent event) {

        long curTime = System.currentTimeMillis();  // current system time
        long diffTime = curTime - prevTime;         // difference between current time and previous shake
        float shake;                                // value of shake intensity

        // if it has been longer than the minimum shake interval
        if ((diffTime) > MIN_SHAKE_INTERVAL)
        {
            // retrieve previous and current values from accelerometer
            prevVals[0] = curVals[0];
            prevVals[1] = curVals[1];
            prevVals[2] = curVals[2];
            curVals[0] = event.values[0];
            curVals[1] = event.values[1];
            curVals[2] = event.values[2];

            // shake intensity formula
            shake = Math.abs(curVals[0] + curVals[1] + curVals[2] - prevVals[0] - prevVals[1] - prevVals[2])
                    * 100;
            // if shake intensity is above threshold and there are more than 1 contact in the list
            if ((shake > SHAKE_THRESHOLD) && (contactList.size() > 1)) {

                // reverse the list of contacts
                this.reverseList();

                // update the view
                listView = findViewById(R.id.listView);
                adapter = new ContactAdapter(MainActivity.this, R.layout.adapter_layout, contactList);
                listView.setAdapter(adapter);

                // update previous shake time
                prevTime = curTime;
            }
        }
    }

    // function not used
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    // reverse the list of contacts
    public void reverseList()
    {
        int end = contactList.size() -  1;
        Contact temp = new Contact();

        for (int x = 0; x < (end + 1) / 2; x++)
        {
            temp = contactList.get(x);
            contactList.set(x, contactList.get(end));
            contactList.set(end, temp);
            end--;
        }
    }

    // deletes the database, creates a new one, and updates the list view
    private void reinitializeContacts()
    {
        database.deleteDatabase(MainActivity.this);
        database = new DatabaseHelper(MainActivity.this);
        redrawScreen();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // options for action bar menu
        if (id == R.id.reinitializeDatabase) {
            reinitializeContacts();
        }

        return super.onOptionsItemSelected(item);
    }
}
