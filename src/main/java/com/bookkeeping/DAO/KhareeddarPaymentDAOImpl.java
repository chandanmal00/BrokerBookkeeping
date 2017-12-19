package com.bookkeeping.DAO;

import com.bookkeeping.constants.Constants;
import com.bookkeeping.model.Khareeddar;
import com.bookkeeping.model.KhareeddarPayment;
import com.bookkeeping.persistence.MongoConnection;
import com.google.gson.Gson;
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
public class KhareeddarPaymentDAOImpl extends MongoCollectionDAOImpl<KhareeddarPayment>  implements KhareeddarPaymentDAO {
    static final Logger logger = LoggerFactory.getLogger(KhareeddarPaymentDAOImpl.class);

    public KhareeddarPaymentDAOImpl() {
        super();
        mongoCollection = MongoConnection.getInstance().getCollection(Constants.MONGO_PAYMENT_KHAREEDDAR_COLLECTION);
    }

    @Override
    public List<KhareeddarPayment> getBasedOnKhareeddarKey(String uniqueKey) {
        Document doc = new Document();
        doc.append("khareeddar.uniqueKey", uniqueKey);
        List<Document> khareeddarPaymentDocList = mongoCollection.find(doc).into(new ArrayList<Document>());
        return getList(khareeddarPaymentDocList);
    }

    @Override
    public List<KhareeddarPayment> getBasedOnKhareeddarKeyWithLimit(String uniqueKey,int rowCount) {
        Document doc = new Document();
        //doc.append("khareeddar.uniqueKey", uniqueKey);
        doc.append("khareeddar", uniqueKey);
        List<Document> khareeddarPaymentDocList = mongoCollection.find(doc).sort(new Document("creationTime", -1)).limit(10).into(new ArrayList<Document>());
        return getList(khareeddarPaymentDocList);
    }


    @Override
    public List<KhareeddarPayment> getBasedOnKhareeddarKey(String uniqueKey,String targetDate) {
        Document doc = new Document();
        //doc.append("khareeddar.uniqueKey", uniqueKey);
        doc.append("khareeddar", uniqueKey);
        doc.append("eventDate", targetDate);
        List<Document> khareeddarPaymentDocList = mongoCollection.find(doc).into(new ArrayList<Document>());
        return getList(khareeddarPaymentDocList);
    }
    /*
    @Override
    public List<KhareeddarPayment> getBasedOnKhareeddarFirmName(String firmName) {
        Document doc = new Document();
        doc.append("khareeddar.firmName", firmName);
        List<Document> khareeddarPaymentDocList = mongoCollection.find(doc).into(new ArrayList<Document>());
        return getList(khareeddarPaymentDocList);
    }

    @Override
    public List<KhareeddarPayment> getBasedOnKhareeddarFirstName(String firstName) {
        Document doc = new Document();
        doc.append("khareeddar.firstName", firstName);
        List<Document> khareeddarPaymentDocList = mongoCollection.find(doc).into(new ArrayList<Document>());
        return getList(khareeddarPaymentDocList);

    }

    @Override
    public List<KhareeddarPayment> getBasedOnKhareeddarLastName(String lastName) {
        Document doc = new Document();
        doc.append("khareeddar.lastName", lastName);
        List<Document> khareeddarPaymentDocList = mongoCollection.find(doc).into(new ArrayList<Document>());
        return getList(khareeddarPaymentDocList);

    }

    @Override
    public List<KhareeddarPayment> getBasedOnKhareeddarFullName(String firstName, String lastName) {
        Document doc = new Document();
        doc.append("khareeddar.firstName", firstName);
        doc.append("khareeddar.lastName", lastName);
        List<Document> khareeddarPaymentDocList = mongoCollection.find(doc).into(new ArrayList<Document>());
        return getList(khareeddarPaymentDocList);

    }

    @Override
    public List<Document> paymentSummaryBasedOnFirm() {

        List<Bson> bsonList = new ArrayList<Bson>();

        //db.payment_khareeddar.aggregate([{$group:{_id:"$khareeddar.firmName",total:{$sum:"$amount"}}}]);
        String jsonStr = "{$group:{_id:\"$khareeddar.firmName\",total:{$sum:\"$amount\"}}}";
        System.out.println(jsonStr);

        BsonDocument bsonDocument = BsonDocument.parse(jsonStr);

        bsonList.add(bsonDocument);
        List<Document> paymentKhareeddarDocList = mongoCollection.aggregate(bsonList).into(new ArrayList<Document>());

        return paymentKhareeddarDocList;

    }

*/
    @Override
    public double paymentSumBasedOnKhareeddar(String khareeddarUniqueKey) {
        List<Bson> bsonList = new ArrayList<Bson>();
        String docKey = "total";

        //String matchStr = "{$match:{\"khareeddar.uniqueKey\":\""+khareeddarUniqueKey+"\"}}";
        String matchStr = "{$match:{\"khareeddar\":\""+khareeddarUniqueKey+"\"}}";
        //String jsonGroupStr = "{"+docKey+":{$sum:\"$amount\"}}";
        String jsonGroupStr = "{$group:{_id:null,"+docKey+":{$sum:\"$amount\"}}}";

        bsonList.add(BsonDocument.parse(matchStr));
        bsonList.add(BsonDocument.parse(jsonGroupStr));
        Document doc = mongoCollection.aggregate(bsonList).first();

        if(doc!=null)
        return Double.valueOf(doc.getDouble(docKey)+"");

        return 0d;
    }

    @Override
    public double paymentSumBasedOnKhareeddar(String khareeddarUniqueKey, String targetDate) {
        List<Bson> bsonList = new ArrayList<Bson>();
        String docKey = "total";

       // String matchStr = "{$match:{\"eventDate\":\""+targetDate+"\", \"khareeddar.uniqueKey\":\""+khareeddarUniqueKey+"\"}}";
        String matchStr = "{$match:{\"eventDate\":\""+targetDate+"\", \"khareeddar\":\""+khareeddarUniqueKey+"\"}}";
        String jsonGroupStr = "{$group:{_id:null,"+docKey+":{$sum:\"$amount\"}}}";
        //String jsonGroupStr = "{"+docKey+":{$sum:\"$amount\"}}}";

        bsonList.add(BsonDocument.parse(matchStr));
        bsonList.add(BsonDocument.parse(jsonGroupStr));
        Document doc = mongoCollection.aggregate(bsonList).first();

        if(doc!=null)
            return Double.valueOf(doc.getDouble(docKey)+"");

        return 0d;

    }


    /*
    @Override
    public Document paymentSummaryBasedOnFirm(String firmName) {

        List<Bson> bsonList = new ArrayList<Bson>();


        //db.payment_khareeddar.aggregate([{$match:{"khareeddar.firmName":"Gothi Sons"}},{$group:{_id:"$khareeddar.firmName",total:{$sum:"$amount"}}}])

        String matchStr = "{$match:{\"khareeddar.firmName\":\""+firmName+"\"}}";
        //db.payment_khareeddar.aggregate([{$group:{_id:"$khareeddar.firmName",total:{$sum:"$amount"}}}]);
        String jsonGroupStr = "{$group:{_id:\"$khareeddar.firmName\",total:{$sum:\"$amount\"}}}";
        System.out.println(matchStr);

        BsonDocument bsonDocument = BsonDocument.parse(jsonGroupStr);

        bsonList.add(BsonDocument.parse(matchStr));
        bsonList.add(bsonDocument);
        Document paymentKhareeddarDoc = mongoCollection.aggregate(bsonList).first();

        return paymentKhareeddarDoc;

    }

    @Override
    public List<Document> dailyPaymentSummaryBasedOnFirm(String firmName) {

        List<Bson> bsonList = new ArrayList<Bson>();


        //db.payment_khareeddar.aggregate([{$match:{"khareeddar.firmName":"Gothi Sons"}},{$group:{_id:"$khareeddar.firmName",total:{$sum:"$amount"}}}])

        String matchStr = "{$match:{\"khareeddar.firmName\":\""+firmName+"\"}}";
        //db.payment_khareeddar.aggregate([{$group:{_id:"$khareeddar.firmName",total:{$sum:"$amount"}}}]);
        String jsonGroupStr = "{$group:{_id:\"eventDate\",total:{$sum:\"$amount\"}}}";
        System.out.println(matchStr);

        BsonDocument bsonDocument = BsonDocument.parse(jsonGroupStr);

        bsonList.add(BsonDocument.parse(matchStr));
        bsonList.add(bsonDocument);
        List<Document> paymentKhareeddarDocList = mongoCollection.aggregate(bsonList).into(new ArrayList<Document>());

        return paymentKhareeddarDocList;

    }

*/
    @Override
    public List<Document> paymentSummaryBasedOnDate(String targetDate) {

        List<Bson> bsonList = new ArrayList<Bson>();

        String matchStr = "{$match:{\"eventDate\":\""+targetDate+"\"}}";
        //db.payment_khareeddar.aggregate([{$group:{_id:"$khareeddar.firmName",total:{$sum:"$amount"}}}]);
        //String jsonGroupStr = "{$group:{_id:\"$khareeddar.firmName\",total:{$sum:\"$amount\"}}}";
        String jsonGroupStr = "{$group:{_id:\"$khareeddar\",total:{$sum:\"$amount\"}}}";
        System.out.println(matchStr);

        BsonDocument bsonDocument = BsonDocument.parse(jsonGroupStr);

        bsonList.add(BsonDocument.parse(matchStr));
        bsonList.add(bsonDocument);
        List<Document> paymentKhareeddarDocList = mongoCollection.aggregate(bsonList).into(new ArrayList<Document>());

        return paymentKhareeddarDocList;

    }

    @Override
    public KhareeddarPayment getTargetObjBasedOnUniqueKey(Document doc) {
        return gson.fromJson(doc.toJson(), KhareeddarPayment.class);
    }

    @Override
    public List<String> listKeys() {
        List<String> keys = new ArrayList<String>();
        List<KhareeddarPayment> list = super.list();
        for(KhareeddarPayment k : list) {
            keys.add(k.getUniqueKey());
        }
        return keys;
    }
    @Override
    protected Document uniqueDocument(KhareeddarPayment khareeddarPayment) {
        return new Document()
                .append(Constants.UNIQUE_KEY,
                        khareeddarPayment.getUniqueKey());
    }

    @Override
    protected Document getDocument(KhareeddarPayment khareeddarPayment) {
        return Document.parse(gson.toJson(khareeddarPayment));
    }

    @Override
    protected String getJsonString(KhareeddarPayment khareeddarPayment) {
        return gson.toJson(khareeddarPayment);
    }

    @Override
    protected List<KhareeddarPayment> getList(List<Document> documentList) {
        List<KhareeddarPayment> khareeddarPaymentList = new ArrayList<KhareeddarPayment>();
        for (Document khareeddarPaymentDoc : documentList) {
            // System.out.println("HERE you go::" + khareeddarPaymentDoc.toJson());
            KhareeddarPayment khareeddarPayment = gson.fromJson(khareeddarPaymentDoc.toJson(), KhareeddarPayment.class);
            khareeddarPaymentList.add(khareeddarPayment);
        }
        return khareeddarPaymentList;
    }

      /*
    private Document uniqueDocument(KhareeddarPayment khareeddarPayment) {
        return new Document()
                .append("amount", khareeddarPayment.getAmount())
                .append("khareeddar.firmName",khareeddarPayment.getKhareeddar().getFirmName())
                .append("creationDate",khareeddarPayment.getCreationDate());

    }
        @Override
    public List<KhareeddarPayment> list() {
        List<Document> kisaanPaymentDocList = mongoCollection.find().into(new ArrayList<Document>());
        return getList(kisaanPaymentDocList);
    }

    @Override
    public KhareeddarPayment get(String khareeddarId) {
        Document doc = new Document();
        doc.append("khareeddarId", khareeddarId);
        Document khareeddarPaymentDoc = mongoCollection.find(doc).first();
        return gson.fromJson(khareeddarPaymentDoc.toJson(), KhareeddarPayment.class);

    }

    */

    public static void main(String[] args) {

        Khareeddar k = new Khareeddar("Gothi sons");

        String dt="2016-09-01";
        // KhareeddarPaymentDAOImpl khareeddarPaymentDAO = new KhareeddarPaymentDAOImpl();
        KhareeddarPayment kp = new KhareeddarPayment(k.getUniqueKey(),3000d,dt);



        KhareeddarPaymentDAO khareeddarPaymentDAO = new KhareeddarPaymentDAOImpl();
        //khareeddarPaymentDAO.add(kp);
        // khareeddarPaymentDAO.add(kp);

        KhareeddarPaymentDAOImpl khareeddarPaymentDAO1 = new KhareeddarPaymentDAOImpl();
        //Test by date
        List<Document> list = khareeddarPaymentDAO1.paymentSummaryBasedOnDate("2016-06-18");

        Gson gson = new Gson();
        System.out.println(gson.toJson(list));

        System.out.println("********All firms**********");

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
        //logger.info(gson.toJson(documentList));
        return documentList;
    }

}
