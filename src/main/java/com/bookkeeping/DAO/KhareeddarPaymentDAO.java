package com.bookkeeping.DAO;

import com.bookkeeping.model.KhareeddarPayment;
import org.bson.Document;

import java.util.List;

/**
 * Created by chandan on 6/4/16.
 */
public interface KhareeddarPaymentDAO extends MongoCollectionDAO<KhareeddarPayment> {
    /*
    public  void add(KhareeddarPayment khareeddarPayment);
    public  void remove(KhareeddarPayment khareeddarPayment);
    public List<KhareeddarPayment> list();

    public KhareeddarPayment get(String khareeddarId);
    */
    public List<KhareeddarPayment> getBasedOnKhareeddarKey(String khareeddarUniqueKey);
    public List<KhareeddarPayment> getBasedOnKhareeddarKeyWithLimit(String kisaanUniqueKey,int rowCount);
    public List<KhareeddarPayment> getBasedOnKhareeddarKey(String khareeddarUniqueKey,String targetDate);
    public List<Document> paymentSummaryBasedOnDate(String targetDate);
    /*
    public List<Document> dailyPaymentSummaryBasedOnFirm(String firmName);
    public Document paymentSummaryBasedOnFirm(String firmName);
    public List<Document> paymentSummaryBasedOnFirm();
    */
    public List<Document> getSummaryByEntity(String entity);

    public double paymentSumBasedOnKhareeddar(String khareeddarUniqueKey);
    public double paymentSumBasedOnKhareeddar(String khareeddarUniqueKey, String targetDate);
}
