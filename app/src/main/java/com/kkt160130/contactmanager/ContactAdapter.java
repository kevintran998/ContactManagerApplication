package com.kkt160130.contactmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends ArrayAdapter<Contact> {

    private Context mContext;
    private int mResource;

    public ContactAdapter(Context context, int resource, List<Contact> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String firstName = getItem(position).getFirstName();
        String lastName = getItem(position).getLastName();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);
        TextView tv = convertView.findViewById(R.id.contactTextView);

        tv.setText(lastName + " " + firstName);

        return convertView;
    }
}