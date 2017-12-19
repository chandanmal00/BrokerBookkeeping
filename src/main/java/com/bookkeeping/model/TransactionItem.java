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
public class TransactionItem extends ModelObj {
    private String name;

    public TransactionItem(String name) {
        super();
        this.name = name;
        this.setUniqueKey(ControllerUtilities.formatUniqueKey(name));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
