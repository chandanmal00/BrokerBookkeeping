package com.bookkeeping.DAO;

import com.bookkeeping.model.KisaanPayment;
import org.bson.Document;

import java.util.List;

/**
 * Created by chandan on 6/4/16.
 */
public interface KisaanPaymentDAO extends MongoCollectionDAO<KisaanPayment>{

    /*
    public void add(KisaanPayment kisaanPayment);
    public void forceAdd(KisaanPayment kisaanPayment);
    public void remove(KisaanPayment kisaanPayment);
    public List<KisaanPayment> list();


    public KisaanPayment get(String kisaanId);
    */

    //public List<KisaanPayment> getBasedOnKisaanId(String kisaanId);
    //public List<KisaanPayment> getBasedOnKisaanNickName(String nickName);
    public List<KisaanPayment> getBasedOnKisaan(String kisaanUniqueKey);
    public List<KisaanPayment> getBasedOnKisaanWithLimit(String kisaanUniqueKey,int rowCount);
    public List<KisaanPayment> getBasedOnKisaan(String kisaanUniqueKey,String targetDate);
    public double paymentSumBasedOnKisaan(String kisaanUniqueKey);
    public double paymentSumBasedOnKisaan(String kisaanUniqueKey, String targetDate);
    //public List<KisaanPayment> paymentSumBasedOnKisaan(String uniqueKey, String targetDate);
    /*
    public List<KisaanPayment> getBasedOnKisaanFirstName(String firstName);
    public List<KisaanPayment> getBasedOnKisaanLastName(String lastName);
    public List<KisaanPayment> getBasedOnKisaanFullName(String firstName, String lastName);
    */
    public List<Document> paymentSummaryBasedOnDate(String targetDate);
    /*
    public List<Document> dailyPaymentSummaryBasedOnNickName(String nickName);
    public Document paymentSummaryBasedOnNickName(String nickName);
    public List<Document> paymentSummaryByNickName();
    */
    public List<Document> getSummaryByEntity(String entity);
}
