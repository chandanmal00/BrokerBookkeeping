package com.bookkeeping.model;

import com.bookkeeping.constants.Constants;
import com.bookkeeping.utilities.ControllerUtilities;
import com.google.gson.Gson;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.bson.types.ObjectId;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chandan on 6/4/16.
 */
public class Kisaan extends ModelObj {

    private String firstName;
    private String lastName;
    private String nickName;
    private Location location;
    private NationalIdentity nationalIdentity;

    //Default value for age if not provided
    private int age=-1;


    public Kisaan(String nickName) {
        super();
        Date date = new Date();
        this.nickName = nickName;
        this.setUniqueKey(ControllerUtilities.formatUniqueKey(nickName));
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public NationalIdentity getNationalIdentity() {
        return nationalIdentity;
    }

    public void setNationalIdentity(NationalIdentity nationalIdentity) {
        this.nationalIdentity = nationalIdentity;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public static void main(String[] args) {
        Kisaan k = new Kisaan("chandu");
        k.setAge(23);
        Gson gson = new Gson();
        System.out.println(gson.toJson(k));
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}


