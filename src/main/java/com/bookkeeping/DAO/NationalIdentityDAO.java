package com.bookkeeping.DAO;

import com.bookkeeping.model.NationalIdentity;

/**
 * Created by chandan on 6/4/16.
 */
public interface NationalIdentityDAO extends MongoCollectionDAO<NationalIdentity>{
    /*
    public void add(NationalIdentity nationalIdentity);
    public void remove(NationalIdentity nationalIdentity);
    public List<NationalIdentity> list();
    public NationalIdentity get(String nationalIdentityId);
    */
    public NationalIdentity getBasedOnPan(String pan);
    public NationalIdentity getBasedOnAadhar(String aadhar);
}
