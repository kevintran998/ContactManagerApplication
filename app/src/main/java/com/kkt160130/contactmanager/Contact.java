package com.kkt160130.contactmanager;

import java.io.Serializable;

public class Contact implements Serializable {

    private int ID;
    private String firstName;
    private String lastName;
    private String phoneNum;
    private int birthDate[];
    private int firstContact[];
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String zipcode;

    Contact()
    {
        ID = -1;
        firstName = "";
        lastName = "";
        phoneNum = "";
        birthDate = new int[] {-1, -1, -1};
        firstContact = new int[] {-1, -1, -1};
        address1 = "";
        address2 = "";
        city = "";
        state = "";
        zipcode = "";
    }

    Contact(int id, String f, String l, String p, int[] b, int[] c, String a1, String a2, String ci, String s, String z)
    {
        ID = id;
        firstName = f;
        lastName = l;
        phoneNum = p;
        birthDate = b;
        firstContact = c;
        address1 = a1;
        address2 = a2;
        city = ci;
        zipcode = z;
    }

    public void setID(int id) { ID = id; }

    public void setFirstName(String name) { firstName = name; }

    public void setLastName(String name){
        lastName = name;
    }

    public void setPhoneNum(String num){
        phoneNum = num;
    }

    public void setBirthDate(int[] date){
        birthDate = date;
    }

    public void setFirstContact(int[] date){
        firstContact = date;
    }

    public int getID() {
        return ID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public int[] getBirthDate() {
        return birthDate;
    }

    public int[] getFirstContact() {
        return firstContact;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
}

