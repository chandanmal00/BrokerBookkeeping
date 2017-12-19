package com.bookkeeping.DAO;

import com.bookkeeping.constants.Constants;
import com.bookkeeping.model.Kisaan;
import com.bookkeeping.model.KisaanPayment;
import com.bookkeeping.persistence.MongoConnection;
import com.google.gson.Gson;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.bookkeeping.constants.Constants.MONGO_KISAAN_PAYMENT_COLLECTION;

/**
 * Created by chandan on 6/4/16.
 */
public class KisaanPaymentDAOImpl extends MongoCollectionDAOImpl<KisaanPayment> implements KisaanPaymentDAO {
    //Client client;
    static final Logger logger = LoggerFactory.getLogger(KisaanPaymentDAOImpl.class);


    public KisaanPaymentDAOImpl() {
        //this.client = ElasticCacheConnection.getInstance();
        super();
        mongoCollection = MongoConnection.getInstance().getCollection(MONGO_KISAAN_PAYMENT_COLLECTION);


    }

    @Override
    public List<String> listKeys() {
        List<String> keys = new ArrayList<String>();
        List<KisaanPayment> list = super.list();
        for(KisaanPayment k : list) {
            keys.add(k.getUniqueKey());
        }
        return keys;
    }


    /*

    @Override
    public List<KisaanPayment> getBasedOnKisaanId(String kisaanId) {
        Document doc = new Document();
        doc.append("kisaan._id", kisaanId);
        List<Document> kisaanPaymentDocList = mongoCollection.find(doc).into(new ArrayList<Document>());
        return getList(kisaanPaymentDocList);


    }


    @Override
    public List<KisaanPayment> getBasedOnKisaanNickName(String nickName) {
        Document doc = new Document();
        doc.append("kisaan.nickName", nickName);
        List<Document> kisaanPaymentDocList = mongoCollection.find(doc).into(new ArrayList<Document>());
        return getList(kisaanPaymentDocList);


    }
    */

    @Override
    public List<KisaanPayment> getBasedOnKisaan(String uniqueKey) {
        Document doc = new Document();
        //doc.append("kisaan.uniqueKey", uniqueKey);
        doc.append("kisaan", uniqueKey);
        List<Document> kisaanPaymentDocList = mongoCollection.find(doc).into(new ArrayList<Document>());
        return getList(kisaanPaymentDocList);
    }

    @Override
    public List<KisaanPayment> getBasedOnKisaanWithLimit(String uniqueKey, int rowCount) {
        Document doc = new Document();
        //doc.append("kisaan.uniqueKey", uniqueKey);
        doc.append("kisaan", uniqueKey);
        List<Document> kisaanPaymentDocList = mongoCollection.find(doc).sort(new Document("creationTime", -1)).limit(10).into(new ArrayList<Document>());
        return getList(kisaanPaymentDocList);
    }


    @Override
    public List<KisaanPayment> getBasedOnKisaan(String uniqueKey,String targetDate) {
        Document doc = new Document();
        //doc.append("kisaan.uniqueKey", uniqueKey);
        doc.append("kisaan", uniqueKey);
        doc.append("eventDate", targetDate);
        List<Document> kisaanPaymentDocList = mongoCollection.find(doc).into(new ArrayList<Document>());
        return getList(kisaanPaymentDocList);
    }

    /*
    @Override
    public List<KisaanPayment> getBasedOnKisaanFirstName(String firstName) {
        Document doc = new Document();
        doc.append("kisaan.firstName", firstName);
        List<Document> kisaanPaymentDocList = mongoCollection.find(doc).into(new ArrayList<Document>());
        return getList(kisaanPaymentDocList);
    }

    @Override
    public List<KisaanPayment> getBasedOnKisaanLastName(String lastName) {
        Document doc = new Document();
        doc.append("kisaan.lastName", lastName);
        List<Document> kisaanPaymentDocList = mongoCollection.find(doc).into(new ArrayList<Document>());
        return getList(kisaanPaymentDocList);
    }

    @Override
    public List<KisaanPayment> getBasedOnKisaanFullName(String firstName, String lastName) {
        Document doc = new Document();
        doc.append("kisaan.firstName", firstName);
        doc.append("kisaan.lastName", lastName);
        List<Document> kisaanPaymentDocList = mongoCollection.find(doc).into(new ArrayList<Document>());
        return getList(kisaanPaymentDocList);
    }
    */

    @Override
    public double paymentSumBasedOnKisaan(String kisaanUniqueKey) {
        List<Bson> bsonList = new ArrayList<Bson>();
        String docKey = "total";

        //String matchStr = "{$match:{\"kisaan.uniqueKey\":\""+kisaanUniqueKey+"\"}}";
        String matchStr = "{$match:{\"kisaan\":\""+kisaanUniqueKey+"\"}}";
        //String jsonGroupStr = "{$group:{_id:\"$creationDate\",total:{$sum:\"$amount\"}}}";
       // String jsonGroupStr = "{"+docKey+":{$sum:\"$amount\"}}}";
        String jsonGroupStr = "{$group:{_id:null,"+docKey+":{$sum:\"$amount\"}}}";
        //BsonDocument bsonDocument = BsonDocument.parse(jsonGroupStr);


        bsonList.add(BsonDocument.parse(matchStr));
        bsonList.add(BsonDocument.parse(jsonGroupStr));
        Document doc = mongoCollection.aggregate(bsonList).first();
        if(doc!=null) {
            return Double.valueOf(doc.getDouble(docKey)+"");
        }
        return 0d;
    }

    @Override
    public double paymentSumBasedOnKisaan(String kisaanUniqueKey, String targetDate) {
        List<Bson> bsonList = new ArrayList<Bson>();
        String docKey = "total";

        //String matchStr = "{$match:{\"eventDate\":\""+targetDate+"\", \"kisaan.uniqueKey\":\""+kisaanUniqueKey+"\"}}";
        String matchStr = "{$match:{\"eventDate\":\""+targetDate+"\", \"kisaan\":\""+kisaanUniqueKey+"\"}}";
        //String jsonGroupStr = "{"+docKey+":{$sum:\"$amount\"}}}";
        String jsonGroupStr = "{$group:{_id:null,"+docKey+":{$sum:\"$amount\"}}}";

        bsonList.add(BsonDocument.parse(matchStr));
        bsonList.add(BsonDocument.parse(jsonGroupStr));
        Document doc = mongoCollection.aggregate(bsonList).first();
        if(doc !=null) return Double.valueOf(doc.getDouble(docKey)+"");
        return 0d;

    }

    @Override
    public List<Document> paymentSummaryBasedOnDate(String targetDate) {
        List<Bson> bsonList = new ArrayList<Bson>();

        String matchStr = "{$match:{\"eventDate\":\""+targetDate+"\"}}";
        //String jsonGroupStr = "{$group:{_id:\"$kisaan.nickName\",total:{$sum:\"$amount\"}}}";
        String jsonGroupStr = "{$group:{_id:\"$kisaan\",total:{$sum:\"$amount\"}}}";
        BsonDocument bsonDocument = BsonDocument.parse(jsonGroupStr);

        bsonList.add(BsonDocument.parse(matchStr));
        bsonList.add(bsonDocument);
        List<Document> paymentKhareeddarDocList = mongoCollection.aggregate(bsonList).into(new ArrayList<Document>());

        return paymentKhareeddarDocList;
    }

    /*
    @Override
    public List<Document> dailyPaymentSummaryBasedOnNickName(String nickName) {
        List<Bson> bsonList = new ArrayList<Bson>();

        //String matchStr = "{$match:{\"kisaan.nickName\":\""+nickName+"\"}}";
        String matchStr = "{$match:{\"kisaan\":\""+nickName+"\"}}";
        String jsonGroupStr = "{$group:{_id:\"$eventDate\",total:{$sum:\"$amount\"}}}";
        BsonDocument bsonDocument = BsonDocument.parse(jsonGroupStr);

        bsonList.add(BsonDocument.parse(matchStr));
        bsonList.add(bsonDocument);
        List<Document> paymentKhareeddarDocList = mongoCollection.aggregate(bsonList).into(new ArrayList<Document>());

        return paymentKhareeddarDocList;

    }

    @Override
    public Document paymentSummaryBasedOnNickName(String nickName) {
        List<Bson> bsonList = new ArrayList<Bson>();

        //String matchStr = "{$match:{\"kisaan.nickName\":\""+nickName+"\"}}";

        String matchStr = "{$match:{\"kisaan\":\""+nickName+"\"}}";
        //String jsonGroupStr = "{$group:{_id:\"$kisaan.nickName\",total:{$sum:\"$amount\"}}}";
        String jsonGroupStr = "{$group:{_id:\"$kisaan\",total:{$sum:\"$amount\"}}}";
        System.out.println(matchStr);

        BsonDocument bsonDocument = BsonDocument.parse(jsonGroupStr);

        bsonList.add(BsonDocument.parse(matchStr));
        bsonList.add(bsonDocument);
        Document paymentKhareeddarDoc = mongoCollection.aggregate(bsonList).first();

        return paymentKhareeddarDoc;
    }
    */

    /*
    @Override
    public List<Document> paymentSummaryByNickName() {
        List<Bson> bsonList = new ArrayList<Bson>();

        String jsonStr = "{$group:{_id:\"$kisaan.nickName\",total:{$sum:\"$amount\"}}}";
        BsonDocument bsonDocument = BsonDocument.parse(jsonStr);

        bsonList.add(bsonDocument);
        List<Document> docList = mongoCollection.aggregate(bsonList).into(new ArrayList<Document>());

        return docList;
    }
    */


    @Override
    public KisaanPayment getTargetObjBasedOnUniqueKey(Document doc) {
        return gson.fromJson(doc.toJson(), KisaanPayment.class);
    }

    @Override
    protected Document uniqueDocument(KisaanPayment kisaanPayment) {
        return new Document()
                .append(Constants.UNIQUE_KEY,
                        kisaanPayment.getUniqueKey());
    }

    @Override
    protected Document getDocument(KisaanPayment kisaanPayment) {
        return Document.parse(gson.toJson(kisaanPayment));
    }

    @Override
    protected String getJsonString(KisaanPayment kisaanPayment) {
        return gson.toJson(kisaanPayment);
    }

    @Override
    protected List<KisaanPayment> getList(List<Document> documentList) {
        List<KisaanPayment> kisaanPaymentList = new ArrayList<KisaanPayment>();
        for (Document kisaanPaymentDoc : documentList) {
            // System.out.println("HERE you go::" + kisaanPaymentDoc.toJson());
            KisaanPayment kisaanPayment = gson.fromJson(kisaanPaymentDoc.toJson(), KisaanPayment.class);
            kisaanPaymentList.add(kisaanPayment);
        }
        return kisaanPaymentList;
    }

    public List<KisaanPayment> list(String kisaanId) {
        Document kisaan = new Document();
        kisaan.append("kisaanId", kisaanId);
        List<Document> kisaanPaymentDocList = mongoCollection.find(kisaan).into(new ArrayList<Document>());
        return getList(kisaanPaymentDocList);
    }
    /*
    private Document uniqueDocument(KisaanPayment kisaanPayment) {
        return new Document()
                .append("amount", kisaanPayment.getAmount())
                .append("kisaan.nickName",kisaanPayment.getKisaan().getNickName())
                .append("creationDate",kisaanPayment.getCreationDate());

    }
    private List<KisaanPayment> getList(List<Document> documentList) {
        List<KisaanPayment> kisaanPaymentList = new ArrayList<KisaanPayment>();
        for(Document kisaanPaymentDoc : documentList) {
            System.out.println("HERE you go::"+kisaanPaymentDoc.toJson());
            KisaanPayment kisaanPayment = gson.fromJson(kisaanPaymentDoc.toJson(), KisaanPayment.class);
            kisaanPaymentList.add(kisaanPayment);
        }
        return kisaanPaymentList;
    }

    @Override
    public List<KisaanPayment> list(String kisaanId) {
        Document kisaan = new Document();
        kisaan.append("kisaanId",kisaanId);
        List<Document> kisaanPaymentDocList = mongoCollection.find(kisaan).into(new ArrayList<Document>());
        return getList(kisaanPaymentDocList);
    }

    @Override
    public void add(KisaanPayment kisaanPayment) {

        Document doc = mongoCollection.find(uniqueDocument(kisaanPayment)).first();
        if(doc==null) {
            mongoCollection.insertOne(Document.parse(gson.toJson(kisaanPayment)));
        } else {
            logger.error("It seems we are inserting same details again for the KisaanPayment: {}",gson.toJson(kisaanPayment));
            logger.error("Use forceAdd method incase you feel this is fine");
        }

    }

    @Override
    public void forceAdd(KisaanPayment kisaanPayment) {
        logger.info("Adding the transaction without any check for KisaanPayment:{}",gson.toJson(kisaanPayment));
        mongoCollection.insertOne(Document.parse(gson.toJson(kisaanPayment)));
    }

    @Override
    public void remove(KisaanPayment kisaanPayment) {
        mongoCollection.deleteOne(Document.parse(gson.toJson(kisaanPayment)));

    }

        @Override
    public List<KisaanPayment> list() {
        List<Document> kisaanPaymentDocList = mongoCollection.find().into(new ArrayList<Document>());
        return getList(kisaanPaymentDocList);
    }



*/

    public static void main(String[] args) {
        String nickName = "maloo1";


        KisaanDAO kisaanDAO = new KisaanDAOImpl();
        Kisaan kisaan = kisaanDAO.getBasedOnUniqueKey(nickName);

        if(kisaan==null) {
            logger.error("Kisaan with {} does not exist, creating one",nickName);
            kisaan = new Kisaan(nickName);
            kisaanDAO.add(kisaan);

        }
        KisaanPaymentDAO kisaanPaymentDAO = new KisaanPaymentDAOImpl();
        String targetDate="2016-06-18";
        KisaanPayment kisaanPayment = new KisaanPayment(kisaan.getUniqueKey(),2000d,targetDate);

        kisaanPaymentDAO.add(kisaanPayment);

        kisaanPayment = new KisaanPayment(kisaan.getUniqueKey(),5000d,targetDate);


        kisaanPaymentDAO.add(kisaanPayment);


        Gson gson = new Gson();
        System.out.println("Summ by Date:");
        System.out.println(gson.toJson(kisaanPaymentDAO.paymentSummaryBasedOnDate(targetDate)));


    }

    public List<Document> getSummaryByEntity(String entity) {

        String docKey = "total";
        //String jsonGroupStr = "{"+docKey+":{$sum:\"$amount\"}}";
        //String jsonGroupStr = "{$group:{_id:\"$"+entity+".uniqueKey\", "+docKey+":{$sum:\"$amount\"}}}";
        String jsonGroupStr = "{$group:{_id:\"$"+entity+"\", "+docKey+":{$sum:\"$amount\"}}}";
        //String jsonGroupStr = "{$group:{_id:\"$"+entity+"\", "+docKey+":{$sum:\"$amount\"}}}";
        //String jsonGroupStr = "{total:{$sum:\"$amount\"}}";

        logger.debug(jsonGroupStr);
        List<Bson> bsonList = new ArrayList<Bson>();
        bsonList.add(BsonDocument.parse(jsonGroupStr));


        List<Document> documentList = mongoCollection.aggregate(bsonList).into(new ArrayList<Document>());
        logger.info(gson.toJson(documentList));
        return documentList;
    }





}
