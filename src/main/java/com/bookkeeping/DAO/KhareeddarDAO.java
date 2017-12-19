package com.bookkeeping.DAO;

import com.bookkeeping.model.Khareeddar;

/**
 * Created by chandan on 6/4/16.
 */
public interface KhareeddarDAO extends MongoCollectionDAO<Khareeddar> {

    public Khareeddar get(String khareeddarId);
    public Khareeddar getBasedOnFirstName(String khareeddar);
    public Khareeddar getBasedOnFirmName(String khareeddar);

}
