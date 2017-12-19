package com.bookkeeping.DAO;

import com.bookkeeping.constants.Constants;
import com.bookkeeping.model.KisaanTransaction;
import com.bookkeeping.persistence.MongoConnection;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chandan on 6/4/16.
 */
public class KisaanTransactionDAOImpl extends MongoCollectionDAOImpl<KisaanTransaction>  implements KisaanTransactionDAO {
    static final Logger logger = LoggerFactory.getLogger(KisaanTransactionDAOImpl.class);

    public KisaanTransactionDAOImpl() {
        super();
        mongoCollection = MongoConnection.getInstance().getCollection(Constants.MONGO_TRANSACTION_KISAAN_COLLECTION);

    }


    @Override
    public void add(KisaanTransaction kisaanTransaction) {
        //Make sure we calculate total amount
        kisaanTransaction.calculate();
        logger.info(gson.toJson(kisaanTransaction));
        super.add(kisaanTransaction);
    }

    @Override
    public List<String> listKeys() {
        List<String> keys = new ArrayList<String>();
        List<KisaanTransaction> list = super.list();
        for(KisaanTransaction k : list) {
            keys.add(k.getUniqueKey());
        }
        return keys;
    }
    @Override
    public List<KisaanTransaction> getBasedOnKisaanId(String kisaanId) {
        Document doc = new Document();
        doc.append("kisaan._id",kisaanId);

        return getList(mongoCollection.find(doc).into(new ArrayList<Document>()));

    }

    @Override
    public List<KisaanTransaction> getBasedOnKisaanKey(String kisaanUniqueKey) {
        Document doc = new Document();
        //doc.append("kisaan.uniqueKey",kisaanUniqueKey);
        doc.append("kisaan",kisaanUniqueKey);
        return getList(mongoCollection.find(doc).into(new ArrayList<Document>()));
    }

    @Override
    public List<KisaanTransaction> getBasedOnKisaanKeyWithLimit(String kisaanUniqueKey,int rowCount) {
        Document doc = new Document();
        //doc.append("kisaan.uniqueKey",kisaanUniqueKey);
        doc.append("kisaan",kisaanUniqueKey);
        return getList(mongoCollection.find(doc).sort(new Document("creationTime", -1)).limit(10).into(new ArrayList<Document>()));
    }

    @Override
    public List<KisaanTransaction> getBasedOnKisaanKey(String kisaanUniqueKey, String targetDate) {
        Document doc = new Document();
        //doc.append("kisaan.uniqueKey",kisaanUniqueKey);
        doc.append("kisaan",kisaanUniqueKey);
        doc.append("eventDate",targetDate);
        return getList(mongoCollection.find(doc).into(new ArrayList<Document>()));
    }

    @Override
    public List<KisaanTransaction> getBasedOnKhareeddarKey(String khareeddarUniqueKey) {
        Document doc = new Document();
        //doc.append("khareeddar.uniqueKey",khareeddarUniqueKey);
        doc.append("khareeddar",khareeddarUniqueKey);
        return getList(mongoCollection.find(doc).into(new ArrayList<Document>()));
    }

    @Override
    public List<KisaanTransaction> getBasedOnKhareeddarKeyWithLimit(String khareeddarUniqueKey, int rowCount) {
        Document doc = new Document();
        //doc.append("khareeddar.uniqueKey",khareeddarUniqueKey);
        doc.append("khareeddar",khareeddarUniqueKey);
        return getList(mongoCollection.find(doc).sort(new Document("creationTime", -1)).limit(10).into(new ArrayList<Document>()));
    }

    @Override
    public List<KisaanTransaction> getBasedOnKhareeddarKey(String khareeddarUniqueKey, String targetDate) {
        Document doc = new Document();
        //doc.append("khareeddar.uniqueKey",khareeddarUniqueKey);
        doc.append("khareeddar",khareeddarUniqueKey);
        doc.append("eventDate",targetDate);
        return getList(mongoCollection.find(doc).into(new ArrayList<Document>()));
    }

    /*
    @Override
    public List<KisaanTransaction> listBasedOnKisaanNickName(String nickName) {
        Document doc = new Document();
        doc.append("kisaan.nickName",nickName);

        return getList(mongoCollection.find(doc).into(new ArrayList<Document>()));

    }


    @Override
    public List<KisaanTransaction> listBasedOnKhareeddarId(String khareeddarId) {
        Document doc = new Document();
        doc.append("khareeddar._id",khareeddarId);

        return getList(mongoCollection.find(doc).into(new ArrayList<Document>()));
    }


    @Override
    public List<KisaanTransaction> listBasedOnKhareeddarFirmName(String firmName) {
        Document doc = new Document();
        doc.append("khareeddar.firmName",firmName);

        return getList(mongoCollection.find(doc).into(new ArrayList<Document>()));
    }
    */

    @Override
    public List<KisaanTransaction> getBasedOnDate(String targetDate) {
        Document doc = new Document();
        doc.append("eventDate",targetDate);

        return getList(mongoCollection.find(doc).into(new ArrayList<Document>()));
    }

    /*
    @Override
    public List<KisaanTransaction> listBasedOnDateForKhareedar(String targetDate, String khareeddarFirmName) {
        Document doc = new Document();
        doc.append("khareeddar.firmName",khareeddarFirmName);
        doc.append("creationDate",targetDate);

        return getList(mongoCollection.find(doc).into(new ArrayList<Document>()));
    }

    @Override
    public List<KisaanTransaction> listBasedOnDateForKisaan(String targetDate, String kisaanNickName) {
        Document doc = new Document();
        doc.append("kisaan.nickName",kisaanNickName);
        doc.append("creationDate",targetDate);

        return getList(mongoCollection.find(doc).into(new ArrayList<Document>()));
    }
    */

    @Override
    public double transactionSum(String targetDate) {
        List<Bson> bsonList = new ArrayList<Bson>();

        String docKey = "total";
        String brokerageKey = "totalBrokerage";
        String matchStr = "{$match:{\"eventDate\":\""+targetDate+"\"}}";
        bsonList.add(BsonDocument.parse(matchStr));
        String jsonGroupStr = "{"+docKey+":{$sum:\"$amount\"},"+brokerageKey+":{$sum:\"$amountBrokerage\"}}";
        bsonList.add(BsonDocument.parse(jsonGroupStr));
        Document doc = mongoCollection.aggregate(bsonList).first();
        logger.info(gson.toJson(doc));

        if(doc!=null) return Double.valueOf(doc.get(docKey)+"");
        return 0d;
    }

    /*
    @Override
    public double transactionSumForKhareeddar(String targetDate, String khareeddarFirmName) {
        List<Bson> bsonList = new ArrayList<Bson>();

        String matchStr = "{$match:{\"creationDate\":\""+targetDate+"\", \"khareeddar.firmName\":\""+khareeddarFirmName+"\"}}";
        bsonList.add(BsonDocument.parse(matchStr));
        String jsonGroupStr = "{total:{$sum:\"$amount\"}}";
        bsonList.add(BsonDocument.parse(jsonGroupStr));
        Document transactionAmountDoc = mongoCollection.aggregate(bsonList).first();

        return transactionAmountDoc.getDouble("total");
    }
    */

    @Override
    public double transactionSumForKisaan(String kisaanUniqueKey,String targetDate) {
        List<Bson> bsonList = new ArrayList<Bson>();

        String docKey = "total";
        String brokerageKey = "totalBrokerage";
        //String matchStr = "{$match:{\"eventDate\":\""+targetDate+"\", \"kisaan.uniqueKey\":\""+kisaanUniqueKey+"\"}}";
        String matchStr = "{$match:{\"eventDate\":\""+targetDate+"\", \"kisaan\":\""+kisaanUniqueKey+"\"}}";
        bsonList.add(BsonDocument.parse(matchStr));
        //String jsonGroupStr = "{$group:{_id:null,"+docKey+":{$sum:\"$amount\"}}}";
        String jsonGroupStr = "{$group:{_id:null,"+docKey+":{$sum:\"$amount\"},"+brokerageKey+":{$sum:\"$amountBrokerage\"}}}";
       // String jsonGroupStr = "{total:{$sum:\"$amount\"}}";
        //String jsonGroupStr = "{total:{$sum:\"$amount\"}}";
        bsonList.add(BsonDocument.parse(jsonGroupStr));
        Document doc = mongoCollection.aggregate(bsonList).first();

        if(doc!=null) return Double.valueOf(doc.get(docKey)+"");
        return 0d;

    }

    @Override
    public double transactionSumForKisaan(String kisaanUniqueKey) {

        String docKey = "total";
        String brokerageKey = "totalBrokerage";
        //String matchStr = "{$match:{\"kisaan.uniqueKey\":\""+kisaanUniqueKey+"\"}}";
        String matchStr = "{$match:{\"kisaan\":\""+kisaanUniqueKey+"\"}}";
        //String jsonGroupStr = "{"+docKey+":{$sum:\"$amount\"}}";
        //String jsonGroupStr = "{$group:{_id:null,"+docKey+":{$sum:\"$amount\"}}}";
        String jsonGroupStr = "{$group:{_id:null,"+docKey+":{$sum:\"$amount\"},"+brokerageKey+":{$sum:\"$amountBrokerage\"}}}";
        //String jsonGroupStr = "{total:{$sum:\"$amount\"}}";

        List<Bson> bsonList = new ArrayList<Bson>();
        bsonList.add(BsonDocument.parse(matchStr));
        bsonList.add(BsonDocument.parse(jsonGroupStr));

        logger.info(matchStr);
        logger.info(jsonGroupStr);
        Document doc = mongoCollection.aggregate(bsonList).first();
        logger.info(gson.toJson(doc));
        if(doc!=null) return Double.valueOf(doc.get(docKey)+"");
        return 0d;

    }

    @Override
    public Document transactionSumForKisaanNew(String kisaanUniqueKey) {

        String docKey = "total";
        String brokerageKey = "totalBrokerage";
        String matchStr = "{$match:{\"kisaan\":\""+kisaanUniqueKey+"\"}}";
        String jsonGroupStr = "{$group:{_id:null,"+docKey+":{$sum:\"$amount\"},"+brokerageKey+":{$sum:\"$amountBrokerage\"}}}";

        List<Bson> bsonList = new ArrayList<Bson>();
        bsonList.add(BsonDocument.parse(matchStr));
        bsonList.add(BsonDocument.parse(jsonGroupStr));

        Document emptyDoc = new Document(docKey,0).append(brokerageKey,0);
        Document doc = mongoCollection.aggregate(bsonList).first();
        logger.info(gson.toJson(doc));
        if(doc==null) {
            return emptyDoc;
        }
        return doc;
    }

    @Override
    public double transactionSumForKhareeddar(String khareeddarUniqueKey,String targetDate) {
        List<Bson> bsonList = new ArrayList<Bson>();

        String docKey = "total";
        String brokerageKey = "totalBrokerage";
        //String matchStr = "{$match:{\"eventDate\":\""+targetDate+"\", \"khareeddar.uniqueKey\":\""+khareeddarUniqueKey+"\"}}";
        String matchStr = "{$match:{\"eventDate\":\""+targetDate+"\", \"khareeddar\":\""+khareeddarUniqueKey+"\"}}";
        bsonList.add(BsonDocument.parse(matchStr));
        //String jsonGroupStr = "{$group:{_id:null,"+docKey+":{$sum:\"$amount\"}}}";
        String jsonGroupStr = "{$group:{_id:null,"+docKey+":{$sum:\"$amountKhareeddar\"},"+brokerageKey+":{$sum:\"$amountBrokerage\"}}}";
        bsonList.add(BsonDocument.parse(jsonGroupStr));
        Document doc = mongoCollection.aggregate(bsonList).first();
        logger.info(gson.toJson(doc));
        if(doc!=null) return Double.valueOf(doc.get(docKey)+"");

        return 0d;
    }

    @Override
    public double transactionSumForKhareeddar(String khareeddarUniqueKey) {

        String docKey = "total";
        String brokerageKey = "totalBrokerage";
        //String matchStr = "{$match:{\"khareeddar.uniqueKey\":\""+khareeddarUniqueKey+"\"}}";
        String matchStr = "{$match:{\"khareeddar\":\""+khareeddarUniqueKey+"\"}}";
        //String jsonGroupStr = "{$group:{_id:null,"+docKey+":{$sum:\"$amountKhareeddar\"}}}";
        String jsonGroupStr = "{$group:{_id:null,"+docKey+":{$sum:\"$amountKhareeddar\"},"+brokerageKey+":{$sum:\"$amountBrokerage\"}}}";

        List<Bson> bsonList = new ArrayList<Bson>();
        bsonList.add(BsonDocument.parse(matchStr));
        bsonList.add(BsonDocument.parse(jsonGroupStr));

        Document doc = mongoCollection.aggregate(bsonList).first();
        logger.info(gson.toJson(doc));
        if(doc!=null) return Double.valueOf(doc.get(docKey)+"");

        return 0d;
    }



    @Override
    public Document transactionSumForKhareeddarNew(String khareeddarUniqueKey) {

        String docKey = "total";
        String brokerageKey = "totalBrokerage";
        //String matchStr = "{$match:{\"khareeddar.uniqueKey\":\""+khareeddarUniqueKey+"\"}}";
        String matchStr = "{$match:{\"khareeddar\":\""+khareeddarUniqueKey+"\"}}";
        //String jsonGroupStr = "{$group:{_id:null,"+docKey+":{$sum:\"$amountKhareeddar\"}}}";
        String jsonGroupStr = "{$group:{_id:null,"+docKey+":{$sum:\"$amountKhareeddar\"},"+brokerageKey+":{$sum:\"$amountBrokerage\"}}}";

        List<Bson> bsonList = new ArrayList<Bson>();
        bsonList.add(BsonDocument.parse(matchStr));
        bsonList.add(BsonDocument.parse(jsonGroupStr));

        Document emptyDoc = new Document(docKey,0).append(brokerageKey,0);
        Document doc = mongoCollection.aggregate(bsonList).first();
        logger.info(gson.toJson(doc));
        if(doc==null) {
            return emptyDoc;
        }
        return doc;
    }

    public static void main(String[] args) {
        String nickName = "chandu";
        /*
        TransactionItem transactionItem = new TransactionItem("toovar");
        TransactionItemDAO transactionItemDAO = new TransactionItemDAOImpl();
        transactionItemDAO.add(transactionItem);
        Gson gson =new Gson();

        KisaanDAO kisaanDAO = new KisaanDAOImpl();


        Kisaan kisaan = new Kisaan(nickName);


        kisaanDAO.remove(kisaan);
        kisaanDAO.add(kisaan);

        kisaan = kisaanDAO.getBasedOnUniqueKey(nickName);




        Khareeddar khareeddar = new Khareeddar("Soni");
        KhareeddarDAO khareeddarDAO = new KhareeddarDAOImpl();
        khareeddarDAO.add(khareeddar);

        KisaanTransaction kisaanTransaction = new KisaanTransaction(kisaan,khareeddar,transactionItem,100,1000);
        kisaanTransaction.setBrokerCommission(Constants.BROKERAGE_RATE);




        kisaanTransactionDAO.add(kisaanTransaction);

*/      KisaanTransactionDAO kisaanTransactionDAO = new KisaanTransactionDAOImpl();
        System.out.println("HERE**********");
        System.out.println(kisaanTransactionDAO.transactionSumForKisaan(nickName));
        System.out.println(kisaanTransactionDAO.transactionSumForKhareeddar("Soni"));
        //System.out.println(gson.toJson(kisaan));
    }




    @Override
    public KisaanTransaction getTargetObjBasedOnUniqueKey(Document doc) {
        return gson.fromJson(doc.toJson(), KisaanTransaction.class);
    }

    @Override
    protected Document uniqueDocument(KisaanTransaction kisaanTransaction) {
        return new Document()
                .append(Constants.UNIQUE_KEY,
                        kisaanTransaction.getUniqueKey());
    }

    @Override
    protected Document getDocument(KisaanTransaction kisaanTransaction) {
        return Document.parse(gson.toJson(kisaanTransaction));
    }

    @Override
    protected String getJsonString(KisaanTransaction kisaanTransaction) {
        return gson.toJson(kisaanTransaction);
    }

    @Override
    protected List<KisaanTransaction> getList(List<Document> documentList) {
        List<KisaanTransaction> kisaanTransactionList = new ArrayList<KisaanTransaction>();
        for (Document kisaanTransactionDoc : documentList) {
            // System.out.println("HERE you go::" + kisaanTransactionDoc.toJson());
            KisaanTransaction kisaanTransaction = gson.fromJson(kisaanTransactionDoc.toJson(), KisaanTransaction.class);
            kisaanTransactionList.add(kisaanTransaction);
        }
        return kisaanTransactionList;
    }

    public List<Document> getSummaryByEntity(String entity) {

        String docKey = "total";
        //String jsonGroupStr = "{"+docKey+":{$sum:\"$amount\"}}";
        //String jsonGroupStr = "{$group:{_id:\"$"+entity+"\", "+docKey+":{$sum:\"$amount\"}}}";
        //String jsonGroupStr = "{$group:{_id:\"$"+entity+".uniqueKey\", "+docKey+":{$sum:\"$amount\"}}}";
        String jsonGroupStr = "{$group:{_id:\"$"+entity+"\", "+docKey+":{$sum:\"$amount\"}}}";
        if(entity.equals("khareeddar")) {
            jsonGroupStr = "{$group:{_id:\"$"+entity+"\", "+docKey+":{$sum:\"$amountKhareeddar\"}}}";
        }

        //String jsonGroupStr = "{total:{$sum:\"$amount\"}}";

        logger.debug(jsonGroupStr);
        List<Bson> bsonList = new ArrayList<Bson>();
        bsonList.add(BsonDocument.parse(jsonGroupStr));


        List<Document> documentList = mongoCollection.aggregate(bsonList).into(new ArrayList<Document>());
        //logger.info(gson.toJson(documentList));
        return documentList;
    }
    /*

    private List<KisaanTransaction> getList(List<Document> documentList) {
        List<KisaanTransaction> kisaanTransactionList = new ArrayList<KisaanTransaction>();
        for(Document kisaanTransactionDoc : documentList) {
            KisaanTransaction kisaanTransaction = gson.fromJson(kisaanTransactionDoc.toJson(), KisaanTransaction.class);
            kisaanTransactionList.add(kisaanTransaction);
        }
        return kisaanTransactionList;
    }
    private Document uniqueDocument(KisaanTransaction kisaanTransaction) {
        return new Document()
                    .append("quantity", kisaanTransaction.getQuantity())
                .append("price", kisaanTransaction.getPrice())
                .append("kisaan.nickName",kisaanTransaction.getKisaan().getNickName())
                .append("khareeddar.firmName",kisaanTransaction.getKhareeddar().getFirmName())
                .append("creationDate",kisaanTransaction.getCreationDate());

    }

    @Override
    public void add(KisaanTransaction kisaanTransaction) {

        Document doc = mongoCollection.find(uniqueDocument(kisaanTransaction)).first();
        if(doc==null) {
            mongoCollection.insertOne(Document.parse(gson.toJson(kisaanTransaction)));
        } else {
            logger.error("It seems we are inserting same details again for the kisaanTransaction: {}",gson.toJson(kisaanTransaction));
            logger.error("Use forceAdd method incase you feel this is fine");
        }

    }

    @Override
    public void forceAdd(KisaanTransaction kisaanTransaction) {
        logger.info("Adding the transaction without any check for kisaanTransaction:{}",gson.toJson(kisaanTransaction));
        mongoCollection.insertOne(Document.parse(gson.toJson(kisaanTransaction)));
    }

    @Override
    public void remove(KisaanTransaction kisaanTransaction) {
        mongoCollection.deleteOne(Document.parse(gson.toJson(kisaanTransaction)));

    }

    @Override
    public List<KisaanTransaction> list() {

        return getList(mongoCollection.find().into(new ArrayList<Document>()));
    }



     */



}
