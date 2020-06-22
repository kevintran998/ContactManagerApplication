package com.kkt160130.contactmanager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ContactActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Contact contact;
    private DatabaseHelper database;
    private int position;
    private String firstName = "";
    private String lastName = "";
    private String phoneNum = "";
    private int birthDate[] = new int[] {-1, -1, -1};
    private int firstContact[] = new int[] {-1, -1, -1};
    private String address1 = "";
    private String address2 = "";
    private String city = "";
    private String state = "";
    private String zipcode = "";
    private static String TAG = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        // customized toolbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_layout);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // retrieve intent
        Intent intent = getIntent();
        Bundle contactBundle = intent.getBundleExtra("bundle");
        contact = (Contact) contactBundle.getSerializable("contact");
        position = contactBundle.getInt("position");

        // retrieve database
        database = new DatabaseHelper(this);

        // if the contact already exists, fill in text fields with current information
        if (position > -1 )
        {
            // set edit text fields
            EditText input;
            input = findViewById(R.id.firstName);
            input.setText(contact.getFirstName());
            input = findViewById(R.id.lastName);
            input.setText(contact.getLastName());
            input = findViewById(R.id.phoneNum);
            input.setText(contact.getPhoneNum());
            input = findViewById(R.id.addressLine1);
            input.setText(contact.getAddress1());
            input = findViewById(R.id.addressLine2);
            input.setText(contact.getAddress2());
            input = findViewById(R.id.city);
            input.setText(contact.getCity());
            input = findViewById(R.id.state);
            input.setText(contact.getState());
            input = findViewById(R.id.zipcode);
            input.setText(contact.getZipcode());

            // formats birth date and date of first contact
            int birthdate[] = contact.getBirthDate();
            int firstContact[] = contact.getFirstContact();
            String birthDateString = birthdate[1] + "/" + birthdate[2] + "/" + birthdate[0];
            String firstContactString = firstContact[1] + "/" + firstContact[2] + "/" + firstContact[0];

            // textview for birth date and date of first contact
            TextView textView;

            // if a birth date or date of first contact exists, display it
            // otherwise, set textview to invisible
            if(birthdate[0] != -1)
            {
                textView = findViewById(R.id.birthDate);
                textView.setText(birthDateString);
                textView.setVisibility(View.VISIBLE);
            }
            if(firstContact[0] != -1)
            {
                textView = findViewById(R.id.firstContact);
                textView.setText(firstContactString);
                textView.setVisibility(View.VISIBLE);
            }
        }

        // click listener for birth date calendar fragment
        Button buttonBirth = findViewById(R.id.selectBirthDate);
        buttonBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment calendar = new CalendarFragment.CalendarFragmentBirthDate();
                calendar.show(getSupportFragmentManager(), "calendarBirth");
                TAG = "BirthDate";
            }
        });

        // click listener for date of first contact calendar fragment
        Button buttonContact = findViewById(R.id.selectFirstContact);
        buttonContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment calendar = new CalendarFragment.CalendarFragmentFirstContact();
                calendar.show(getSupportFragmentManager(), "calendarContact");
                TAG = "FirstContact";
            }
        });
    }

    // sets date when selected in the calendar fragment
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
    {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String date = month + "/" + dayOfMonth + "/" + year;

        // if the birth date was set, display textview and set birth date values
        if (TAG.equals("BirthDate"))
        {
            TextView textView = findViewById(R.id.birthDate);
            textView.setText(date);
            textView.setVisibility(View.VISIBLE);
            birthDate[0] = year;
            birthDate[1] = month + 1;
            birthDate[2] = dayOfMonth;
        }

        // else if date of first contact was set, display textview and set first contact date values
        else if (TAG.equals("FirstContact"))
        {
            TextView textView = findViewById(R.id.firstContact);
            textView.setText(date);
            textView.setVisibility(View.VISIBLE);
            firstContact[0] = year;
            firstContact[1] = month + 1;
            firstContact[2] = dayOfMonth;
        }
    }

    // onClick for save contact button
    public void saveClick(View view)
    {
        // find user inputs from edit text, convert inputs to strings, and store them
        EditText input = findViewById(R.id.firstName);
        firstName = input.getText().toString();

        input = findViewById(R.id.lastName);
        lastName = input.getText().toString();

        input = findViewById(R.id.phoneNum);
        phoneNum = input.getText().toString();

        input = findViewById(R.id.addressLine1);
        address1 = input.getText().toString();

        input = findViewById(R.id.addressLine2);
        address2 = input.getText().toString();

        input = findViewById(R.id.city);
        city = input.getText().toString();

        input = findViewById(R.id.state);
        state = input.getText().toString();

        input = findViewById(R.id.zipcode);
        zipcode = input.getText().toString();

        // if contact already exists, update contact
        if (position > -1)
        {
            // check if input was valid
            if (validInput(firstName, lastName, phoneNum, address1, address2, city, state, zipcode))
            {
                int tempBirthDate[] = contact.getBirthDate();
                int tempFirstContact[] = contact.getFirstContact();

                // if new date was not selected, set it to previous date
                if (tempBirthDate[0] != -1)
                {
                    birthDate = tempBirthDate;
                }
                if (tempFirstContact[0] != -1)
                {
                    firstContact = tempFirstContact;
                }

                // update contact
                database.updateContact(contact.getID(), firstName, lastName, phoneNum, birthDate, firstContact, address1, address2, city, state, zipcode);
                returnToMain();
            }

            // else display a toast that indicates invalid input
            else
            {
                Toast.makeText(ContactActivity.this, "Invalid Input", Toast.LENGTH_SHORT).show();
            }
        }

        // else if contact does not exist and input is valid, add new contact
        else if(validInput(firstName, lastName, phoneNum, address1, address2, city, state, zipcode))
        {
            // if date was not selected, set it to current date
            if (firstContact[0] == -1)
            {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                firstContact[0] = year;
                firstContact[1] = month + 1;
                firstContact[2] = day;
            }

            // add new contact
            database.addContact(firstName, lastName, phoneNum, birthDate, firstContact, address1, address2, city, state, zipcode);
            returnToMain();
        }

        // else display a toast that indicates invalid input
        else
        {
            Toast.makeText(ContactActivity.this, "Invalid Input", Toast.LENGTH_SHORT).show();
        }
    }

    // onClick for delete contact button
    public void deleteClick(View view)
    {
        // if contact exists, delete contact from database
        if (position > -1)
        {
            database.deleteContact(contact.getID());
        }

        returnToMain();
    }

    // validates input from user
    private boolean validInput(String fn, String ln, String pn, String a1, String a2, String city, String s, String z)
    {
        if ((fn.length() > 0) && (ln.length() > 0) && (pn.length() == 10) &&
                (pn.matches("[0-9]+")) && (a1.length() > 0) && (city.length() > 0) &&
                (s.length() == 2) && (z.length() == 5) && (z.matches("[0-9]+")))
        {
            if ((fn.length() <= 25) && (ln.length() <= 25) && (a1.length() <= 25) && (a2.length() <= 25) && (city.length() <= 25))
            {
                return true;
            }
        }

        return false;
    }

    // returns to the main activity
    private void returnToMain()
    {
        Intent intent = new Intent();
        setResult(1, intent);
        finish();
    }

    // opens google maps
    private void openMap()
    {
        // string of the address
        String address = contact.getAddress1() + " " +  contact.getAddress2() + " " +
                contact.getCity() + " " + contact.getState() + " " + contact.getZipcode();
        Intent intent = new Intent(ContactActivity.this, MapsFragment.class);
        Bundle bundle = new Bundle();
        bundle.putString("address", address);
        intent.putExtra("bundle", bundle);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // options for action bar menu
        if (id == R.id.mapAddress) {
            openMap();
        }
        else
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
