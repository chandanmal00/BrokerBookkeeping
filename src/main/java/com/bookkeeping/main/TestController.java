package com.bookkeeping.main;

import com.bookkeeping.DAO.*;
import com.bookkeeping.model.*;
import com.bookkeeping.utilities.ControllerUtilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chandan on 6/12/16.
 */
public class TestController {

    public static void main(String[] args) {
        Kisaan kisaan = new Kisaan("chand");
        kisaan.setFirstName("chandan");
        kisaan.setLastName("maloo");

        Location location = new Location("mountain view");

        location.setState("California");
        location.setTaluka("Santa Clara County");
        kisaan.setLocation(location);
        kisaan.setAge(31);


        KisaanDAO kisaanDAO = new KisaanDAOImpl();
        kisaanDAO.add(kisaan);

        TransactionItem transactionItem = new TransactionItem("toovar");


        TransactionItemDAO transactionItemDAO = new TransactionItemDAOImpl();
        transactionItemDAO.add(transactionItem);

        Khareeddar khareeddar = new Khareeddar("Gothi Sons");
        khareeddar.setFirstName("Neeraj");
        khareeddar.setLastName("Gothi");
        Location locationKhareeddar = new Location("warora");

        locationKhareeddar.setTaluka("warora");
        locationKhareeddar.setState("maharashtra");
        khareeddar.setLocation(locationKhareeddar);
        KhareeddarDAO khareeddarDAO = new KhareeddarDAOImpl();
        khareeddarDAO.add(khareeddar);

        List<ItemSell> itemSells = new ArrayList<ItemSell>();
        ItemSell itemSell = new ItemSell(transactionItem.getUniqueKey(),0.9f,1.1f,100.1f, ControllerUtilities.getCurrentDateStrInYYYY_MM_DD());
        itemSells.add(itemSell);
        String dt="2016-09-01";
        KisaanTransaction kisaanTransaction = new KisaanTransaction(kisaan.getUniqueKey(),khareeddar.getUniqueKey(),dt,itemSells);
        /*
        kisaanTransaction.setKisaan(kisaan);
        kisaanTransaction.setTransactionItem(transactionItem);
        kisaanTransaction.setKhareeddar(khareeddar);
        kisaanTransaction.setPrice(100.1f);
        kisaanTransaction.setQuantity(20.1f);
        */

        KisaanTransactionDAO kisaanTransactionDAO = new KisaanTransactionDAOImpl();
        kisaanTransactionDAO.add(kisaanTransaction);


        KisaanPayment kisaanPayment = new KisaanPayment(kisaan.getUniqueKey(),3000d,dt);
        kisaanPayment.setTransactionId(kisaanTransaction.getUniqueKey());

        KisaanPaymentDAO kisaanPaymentDAO = new KisaanPaymentDAOImpl();
        kisaanPaymentDAO.add(kisaanPayment);

        KhareeddarPayment khareeddarPayment = new KhareeddarPayment(khareeddar.getUniqueKey(),3000d,dt);

        //khareeddarPayment.se(kisaanTransaction);

        KhareeddarPaymentDAO khareeddarPaymentDAO = new KhareeddarPaymentDAOImpl();
        khareeddarPaymentDAO.add(khareeddarPayment);
    }
}
