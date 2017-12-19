package com.bookkeeping.model;

import com.bookkeeping.constants.Constants;
import com.bookkeeping.utilities.ControllerUtilities;
import com.google.gson.Gson;
import org.bson.types.ObjectId;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chandan on 6/4/16.
 */
public class NationalIdentity extends ModelObj {
    private String pan;
    private String aadhar;

    public NationalIdentity(String pan) {
        super();
        this.pan = pan;
        this.setUniqueKey(ControllerUtilities.formatUniqueKey(pan));
    }


    public NationalIdentity(String pan,String aadhar) {
        this.pan = pan;
        this.aadhar=aadhar;
        this.setUniqueKey(ControllerUtilities.formatUniqueKey(this.pan
                + Constants.UNIQUE_KEY_SEPARATOR
                + this.aadhar));
    }


    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getAadhar() {
        return aadhar;
    }

    public void setAadhar(String aadhar) {
        this.aadhar = aadhar;
    }


    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
