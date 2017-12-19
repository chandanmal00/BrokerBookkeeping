package com.bookkeeping.DAO;

import com.bookkeeping.model.KisaanTransaction;
import org.bson.Document;

import java.util.List;

/**
 * Created by chandan on 6/4/16.
 */
public interface KisaanTransactionDAO extends MongoCollectionDAO<KisaanTransaction> {

    /*
    public  void add(KisaanTransaction kisaanTransaction);
    public  void forceAdd(KisaanTransaction kisaanTransaction);
    public  void remove(KisaanTransaction kisaanTransaction);
    public List<KisaanTransaction> list();

    */
    public List<KisaanTransaction> getBasedOnKisaanId(String kisaanId);
    public List<KisaanTransaction> getBasedOnKisaanKey(String kisaanUniqueKey);
    public List<KisaanTransaction> getBasedOnKisaanKeyWithLimit(String kisaanUniqueKey,int rowCount);
    public List<KisaanTransaction> getBasedOnKisaanKey(String kisaanUniqueKey, String targetDate);
    public List<KisaanTransaction> getBasedOnKhareeddarKey(String khareeddarUniqueKey);
    public List<KisaanTransaction> getBasedOnKhareeddarKeyWithLimit(String khareeddarUniqueKey,int rowCount);

    public List<KisaanTransaction> getBasedOnKhareeddarKey(String khareeddarUniqueKey, String targetDate);
    public List<KisaanTransaction> getBasedOnDate(String targetDate);

    public double transactionSumForKisaan(String kisaanUniqueKey);
    public Document transactionSumForKisaanNew(String kisaanUniqueKey);
    public double transactionSumForKisaan(String kisaanUniqueKey, String targetDate);

    public double transactionSumForKhareeddar(String khareeddarUniqueKey,String targetDate);
    public double transactionSumForKhareeddar(String khareeddarUniqueKey);
    public Document transactionSumForKhareeddarNew(String kisaanUniqueKey);

    public double transactionSum(String targetDate);
    public List<Document> getSummaryByEntity(String entity);

    //public double transactionSumForKisaan(String targetDate,String kisaanNickName);



}
