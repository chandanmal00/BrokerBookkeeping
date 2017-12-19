package com.bookkeeping.DAO;

import com.bookkeeping.model.Kisaan;

import java.util.List;

/**
 * Created by chandan on 6/4/16.
 */
public interface KisaanDAO extends MongoCollectionDAO<Kisaan>{

    public List<Kisaan> getBasedOnName(String kisaanName);
    public Kisaan getBasedOnNickName(String kisaanNickName);
    //public Kisaan search(String kisaanNickName);


}
