package com.bookkeeping.DAO;

import com.bookkeeping.model.TransactionItem;

/**
 * Created by chandan on 6/4/16.
 */
public interface TransactionItemDAO extends MongoCollectionDAO<TransactionItem>{

    TransactionItem getBasedOnName(String transactionItem);
    /*
    TransactionItem get(String transactionItemId);
    */
}
