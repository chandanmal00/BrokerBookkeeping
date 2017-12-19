package com.bookkeeping.controller;

import com.bookkeeping.DAO.*;

/**
 * Created by chandan on 6/21/16.
 */
public class SingletonManagerDAO {

    private static volatile SingletonManagerDAO instance;
    private SessionDAO sessionDAO;
    private KisaanDAO kisaanDAO;
    private KisaanPaymentDAO kisaanPaymentDAO;
    private KisaanTransactionDAO kisaanTransactionDAO;
    private KhareeddarDAO khareeddarDAO;
    private KhareeddarPaymentDAO khareeddarPaymentDAO;
    private TransactionItemDAO transactionItemDAO;
    private UserDAO userDAO;
    private ItemSellDAO itemSellDAO;

    private SingletonManagerDAO() {

        sessionDAO = new SessionDAO();
        kisaanDAO = new KisaanDAOImpl();
        kisaanPaymentDAO = new KisaanPaymentDAOImpl();
        kisaanTransactionDAO = new KisaanTransactionDAOImpl();
        khareeddarDAO = new KhareeddarDAOImpl();
        khareeddarPaymentDAO = new KhareeddarPaymentDAOImpl();
        transactionItemDAO = new TransactionItemDAOImpl();
        userDAO = new UserDAO();
        itemSellDAO = new ItemSellDAOImpl();
    }

    public static SingletonManagerDAO getInstance() {
        if(instance==null) {
            synchronized (SingletonManagerDAO.class) {
                if(instance==null) {
                    instance = new SingletonManagerDAO();
                }
            }
        }
        return instance;

    }

    public KisaanDAO getKisaanDAO() {
        return kisaanDAO;
    }

    public KisaanPaymentDAO getKisaanPaymentDAO() {
        return kisaanPaymentDAO;
    }

    public KisaanTransactionDAO getKisaanTransactionDAO() {
        return kisaanTransactionDAO;
    }

    public KhareeddarDAO getKhareeddarDAO() {
        return khareeddarDAO;
    }

    public KhareeddarPaymentDAO getKhareeddarPaymentDAO() {
        return khareeddarPaymentDAO;
    }

    public TransactionItemDAO getTransactionItemDAO() {
        return transactionItemDAO;
    }

    public SessionDAO getSessionDAO() {
        return sessionDAO;
    }

    public ItemSellDAO getItemSellDAO() {
        return itemSellDAO;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }
}
