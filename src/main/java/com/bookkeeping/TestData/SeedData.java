package com.bookkeeping.TestData;

import com.bookkeeping.DAO.*;
import com.bookkeeping.constants.Constants;
import com.bookkeeping.model.*;
import com.bookkeeping.persistence.MongoConnection;
import com.bookkeeping.utilities.ControllerUtilities;

import java.util.*;

/**
 * Created by chandanm on 9/22/16.
 */
public class SeedData {

    public static void main(String[] args) {
        int cntKissaan = 500;
        int cntKhareeddar = 100;
        int cntTransactionItems = 30;
        int start=2001;


        int transactions =100;
        int payments = 50;
        int dateLookup = 60;
        /*
        cntKissaan=1;
        cntKhareeddar=10;
        cntTransactionItems =1;

        transactions =2;
        payments =2;
        */
        Map<Integer,Khareeddar> khareeddarSet =  new HashMap<Integer,Khareeddar>();
        Map<Integer,TransactionItem> itemSet =  new HashMap<Integer,TransactionItem>();

        MongoConnection.specialInit();

        for(int i=0+start;i<cntKissaan+start;i++) {

            Kisaan kisaan = new Kisaan("kisaan_"+i);
            KisaanDAO kisaanDAO = new KisaanDAOImpl();
            KhareeddarDAO khareeddarDAO = new KhareeddarDAOImpl();
            KisaanPaymentDAO kisaanPaymentDAO = new KisaanPaymentDAOImpl();
            KhareeddarPaymentDAO khareeddarPaymentDAO = new KhareeddarPaymentDAOImpl();
            KisaanTransactionDAO kisaanTransactionDAO = new KisaanTransactionDAOImpl();
            TransactionItemDAO transactionItemDAO = new TransactionItemDAOImpl();
            ItemSellDAO itemSellDAO = new ItemSellDAOImpl();


            kisaanDAO.forceAdd(kisaan);
            Random random = new Random();
            int khareeddarId = random.nextInt(cntKhareeddar);

            Khareeddar khareeddar = khareeddarSet.get(khareeddarId);
            if(khareeddar==null) {
                khareeddar = new Khareeddar("Khareeddar_" + khareeddarId);
                khareeddarDAO.forceAdd(khareeddar);
                khareeddarSet.put(khareeddarId,khareeddar);
            }


            //Randomize the no. of transactions from the limit
            int transactionsRandom = random.nextInt(transactions)+1;

            for(int i1=0;i1<transactionsRandom;i1++) {
                int dtBack = random.nextInt(dateLookup);
                int itemSize = random.nextInt(3) + 1;
                String dt = ControllerUtilities.formatDateInYYYY_MM_DD(ControllerUtilities.getNDaysDateFromCurrentDate(-dtBack));
                List<ItemSell> itemSells = new ArrayList<ItemSell>();
                float price = random.nextInt(3000);
                float quantity = random.nextInt(20);
                for(int j=0;j<itemSize;j++) {
                    int transactionItemId = random.nextInt(100);
                    TransactionItem transactionItem = itemSet.get(transactionItemId);

                    if(transactionItem==null) {
                        transactionItem = new TransactionItem("Item_"+transactionItemId);
                        transactionItemDAO.forceAdd(transactionItem);
                        itemSet.put(transactionItemId,transactionItem);
                    }
                    ItemSell itemSell = new ItemSell(transactionItem.getUniqueKey(), 0.9f, quantity, price, dt);
                    itemSellDAO.forceAdd(itemSell);
                    itemSells.add(itemSell);
                }
                KisaanTransaction kisaanTransaction = new KisaanTransaction(kisaan.getUniqueKey(),khareeddar.getUniqueKey(), dt,itemSells);
                //kisaanTransaction.setEventDate(ControllerUtilities.formatDateInYYYY_MM_DD(ControllerUtilities.getNDaysDateFromCurrentDate(-dtBack)));

                kisaanTransaction.setEventDate(dt);
                kisaanTransactionDAO.forceAdd(kisaanTransaction);
            }


            int paymentsRandom = random.nextInt(payments)+1;
            for(int i1=0;i1<paymentsRandom;i1++) {
                float price = random.nextInt(15000);
                int dtBack = random.nextInt(dateLookup);
                String dt = ControllerUtilities.formatDateInYYYY_MM_DD(ControllerUtilities.getNDaysDateFromCurrentDate(-dtBack));
                KisaanPayment kisaanPayment = new KisaanPayment(kisaan.getUniqueKey(),price,dt);
                kisaanPaymentDAO.forceAdd(kisaanPayment);

                KhareeddarPayment khareeddarPayment = new KhareeddarPayment(khareeddar.getUniqueKey(),price,dt);
                khareeddarPaymentDAO.forceAdd(khareeddarPayment);
            }
            System.out.println("Done with Kisaan:"+i);



        }
    }
}
