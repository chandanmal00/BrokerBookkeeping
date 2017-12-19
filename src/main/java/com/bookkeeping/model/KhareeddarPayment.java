package com.bookkeeping.model;

import com.bookkeeping.constants.Constants;
import com.google.gson.Gson;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.bson.types.ObjectId;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chandan on 6/4/16.
 */
public class KhareeddarPayment extends Payment{
    private String khareeddar;

    public KhareeddarPayment(String khareeddar,
                             double amount, String date) {
        super();
        this.khareeddar=khareeddar;
        this.setAmount(amount);
        this.setPaymentType("Cash");
        this.setEventDate(date);
        this.setUniqueKey(this.khareeddar
                + Constants.UNIQUE_KEY_SEPARATOR
                + this.getEventDate()
                + Constants.UNIQUE_KEY_SEPARATOR
                + this.getAmount());

    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String getKhareeddar() {
        return khareeddar;
    }

    public void setKhareeddar(String khareeddar) {
        this.khareeddar = khareeddar;
    }
}
