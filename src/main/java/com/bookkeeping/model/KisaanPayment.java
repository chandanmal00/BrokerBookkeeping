package com.bookkeeping.model;

import com.bookkeeping.constants.Constants;
import com.google.gson.Gson;
import org.bson.types.ObjectId;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chandan on 6/4/16.
 */
public class KisaanPayment extends Payment {

    private String transactionId;
    private String kisaan;

    public KisaanPayment(String kisaan, double amount, String dt) {
        super();
        this.kisaan = kisaan;
        this.setAmount(amount);
        this.setPaymentType("Cash");
        this.setEventDate(dt);
        this.setUniqueKey(this.kisaan
                + Constants.UNIQUE_KEY_SEPARATOR
                        + this.getEventDate()
                        + Constants.UNIQUE_KEY_SEPARATOR
                + this.getAmount()
        );
    }

    public String getKisaan() {
        return kisaan;
    }

    public String getTransactionId() {

        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setKisaan(String kisaan) {
        this.kisaan = kisaan;
    }


    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
