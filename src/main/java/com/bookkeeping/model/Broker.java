package com.bookkeeping.model;

import com.google.gson.Gson;

/**
 * Created by chandan on 6/4/16.
 */
public class Broker {
    private String name;
    private String proprietor;
    private String firmName;
    private Location location;
    private String uniqueKey;

    public Broker(String name) {
        this.name = name;
        this.uniqueKey=name;
    }

    public String getProprietor() {
        return proprietor;
    }

    public void setProprietor(String proprietor) {
        this.proprietor = proprietor;
    }

    public String getFirmName() {
        return firmName;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
