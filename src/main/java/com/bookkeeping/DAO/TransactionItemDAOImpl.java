package com.bookkeeping.DAO;

import com.bookkeeping.constants.Constants;
import com.bookkeeping.model.TransactionItem;
import com.bookkeeping.persistence.MongoConnection;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chandan on 6/4/16.
 */
public class TransactionItemDAOImpl extends MongoCollectionDAOImpl<TransactionItem> implements TransactionItemDAO {

    static final Logger logger = LoggerFactory.getLogger(TransactionItemDAOImpl.class);


    public TransactionItemDAOImpl() {
        super();
        mongoCollection = MongoConnection.getInstance().getCollection(Constants.MONGO_TRANSACTION_ITEM_COLLECTION);
    }

    public void removeTransactionWithId(String transactionItemId) {
        Document doc = new Document();
        doc.append("_id",transactionItemId);
        mongoCollection.deleteOne(doc);

    }

    @Override
    public List<String> listKeys() {
        List<String> keys = new ArrayList<String>();
        List<TransactionItem> list = super.list();
        for(TransactionItem k : list) {
            keys.add(k.getUniqueKey());
        }
        return keys;
    }

    public static void main(String[] args) {
        TransactionItem transactionItem = new TransactionItem("jawar");
        TransactionItemDAO transactionItemDAO = new TransactionItemDAOImpl();
        transactionItemDAO.add(transactionItem);
    }



    public TransactionItem getBasedOnName(String transactionItem) {
        Document doc = new Document();
        doc.append("name",transactionItem);
        Document transactionItemDoc = mongoCollection.find(doc).first();
        return gson.fromJson(transactionItemDoc.toJson(),TransactionItem.class);

    }


    public TransactionItem get(String transactionItemId) {
        Document doc = new Document();
        doc.append("_id",transactionItemId);
        Document transactionItemDoc = mongoCollection.find(doc).first();
        return gson.fromJson(transactionItemDoc.toJson(),TransactionItem.class);
    }




    @Override
    public TransactionItem getTargetObjBasedOnUniqueKey(Document doc) {
        return gson.fromJson(doc.toJson(), TransactionItem.class);
    }

    @Override
    protected Document uniqueDocument(TransactionItem transactionItem) {
        return new Document()
                .append(Constants.UNIQUE_KEY,
                        transactionItem.getUniqueKey());
    }

    @Override
    protected Document getDocument(TransactionItem transactionItem) {
        return Document.parse(gson.toJson(transactionItem));
    }

    @Override
    protected String getJsonString(TransactionItem transactionItem) {
        return gson.toJson(transactionItem);
    }

    @Override
    protected List<TransactionItem> getList(List<Document> documentList) {
        List<TransactionItem> transactionItemList = new ArrayList<TransactionItem>();
        for (Document transactionItemDoc : documentList) {
            // System.out.println("HERE you go::" + transactionItemDoc.toJson());
            TransactionItem transactionItem = gson.fromJson(transactionItemDoc.toJson(), TransactionItem.class);
            transactionItemList.add(transactionItem);
        }
        return transactionItemList;
    }
    /*


    public void add(TransactionItem transactionItem) {


        Document document = mongoCollection.find(uniqueDocument(transactionItem)).first();
        if(document==null) {
            mongoCollection.insertOne(Document.parse(gson.toJson(transactionItem)));
        } else {
            logger.error("TransactionITtem with name:{} already in database:, so not adding",transactionItem.getName());
        }
        //mongoCollection.insertOne(Document.parse(gson.toJson(transactionItem)));

    }

    public void remove(TransactionItem transactionItem) {
        mongoCollection.deleteOne(Document.parse(gson.toJson(transactionItem)));

    }

    public List<TransactionItem> list() {

        List<Document> documentList = mongoCollection.find().into(new ArrayList<Document>());
        return getList(documentList);
    }
    private List<TransactionItem> getList(List<Document> documentList) {
        List<TransactionItem> transactionItemList = new ArrayList<TransactionItem>();
        for(Document transactionItemDoc : documentList) {
            System.out.println("HERE you go::"+transactionItemDoc.toJson());
            TransactionItem transactionItem = gson.fromJson(transactionItemDoc.toJson(), TransactionItem.class);
            transactionItemList.add(transactionItem);
        }
        return transactionItemList;
    }
    private Document uniqueDocument(TransactionItem transactionItem) {
        return new Document()
                .append(Constants.transactionItemUniqueKey,
                        transactionItem.getName());
    }

     */

}
