package com.bookkeeping.DAO;

import com.bookkeeping.constants.Constants;
import com.bookkeeping.model.Khareeddar;
import com.bookkeeping.persistence.MongoConnection;
import com.google.gson.Gson;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chandan on 6/4/16.
 */
public class KhareeddarDAOImpl extends MongoCollectionDAOImpl<Khareeddar> implements KhareeddarDAO {

    //Client client;
    static final Logger logger = LoggerFactory.getLogger(KhareeddarDAOImpl.class);


    public KhareeddarDAOImpl() {
        //this.client = ElasticCacheConnection.getInstance();
        super();
        mongoCollection = MongoConnection.getInstance().getCollection(Constants.MONGO_KHAREEDDAR_COLLECTION);
        //logger.info("Count:"+this.mongoCollection.count()+", nameL:"+this.mongoCollection.toString());
    }


    @Override
    public Khareeddar get(String khareeddarId) {

        Document query = new Document();
        query.append("_id",khareeddarId);
        Document khareeddarDocument = mongoCollection.find(query).first();
        Khareeddar khareeddar = gson.fromJson(khareeddarDocument.toJson(),Khareeddar.class);
        return khareeddar;
    }

    @Override
    public Khareeddar getBasedOnUniqueKey(String uniqueKey) {
        Document doc = mongoCollection.find(this.getUniqueDocument(uniqueKey)).first();
        if(doc!=null) {
            return gson.fromJson(doc.toJson(),Khareeddar.class);
        }
        return null;
    }

    @Override
    public Khareeddar getBasedOnFirstName(String khareeddarName) {
        Document query = new Document();
        query.append("firstName",khareeddarName);
        Document khareeddarDocument = mongoCollection.find(query).first();
        return gson.fromJson(khareeddarDocument.toJson(), Khareeddar.class);
    }
/*
    public List<Khareeddar> getBasedOnKhareeddarFirstName(String khareeddar) {
        Document query = new Document();
        query.append("firstName",khareeddar);
        List<Document> khareeddarDocumentList = mongoCollection.find(query).into(new ArrayList<Document>());
        return getList(khareeddarDocumentList);
    }

    */
    @Override
    public Khareeddar getBasedOnFirmName(String firmName) {
        return getBasedOnUniqueKey(firmName);
        /*
        Document query = new Document();
        query.append("firmName",firmName);
        Document khareeddarDocument = mongoCollection.find(query).first();
        return gson.fromJson(khareeddarDocument.toJson(), Khareeddar.class);
        */


    }

    @Override
    public List<String> listKeys() {
        List<String> keys = new ArrayList<String>();
        List<Khareeddar> list = super.list();
        for(Khareeddar k : list) {
            keys.add(k.getUniqueKey());
        }
        return keys;
    }

    protected List<Khareeddar> getList(List<Document> documentList) {
        List<Khareeddar> KhareeddarList = new ArrayList<Khareeddar>();
        for(Document khareeddarDoc : documentList) {
            Khareeddar khareeddar = gson.fromJson(khareeddarDoc.toJson(), Khareeddar.class);
            KhareeddarList.add(khareeddar);
        }
        return KhareeddarList;
    }

    @Override
    protected Document uniqueDocument(Khareeddar khareeddar) {
        return new Document()
                .append(Constants.UNIQUE_KEY,
                        khareeddar.getUniqueKey());
    }

    @Override
    protected Document getDocument(Khareeddar khareeddar) {
        return Document.parse(gson.toJson(khareeddar));
    }

    @Override
    protected String getJsonString(Khareeddar khareeddar) {
        return gson.toJson(khareeddar);
    }



    @Override
    public Khareeddar getTargetObjBasedOnUniqueKey(Document doc) {
        return gson.fromJson(doc.toJson(), Khareeddar.class);
    }

    public static void main(String[] args) {
        Khareeddar khareeddar = new Khareeddar("maloo sons");
        KhareeddarDAO khareeddarDAO = new KhareeddarDAOImpl();

        MongoCollectionDAO<Khareeddar> mongoCollectionDAO = new KhareeddarDAOImpl();
        Gson gson = new Gson();
        System.out.println("Khareeddar:"+gson.toJson(khareeddar));
        System.out.println("Exists Status: "+khareeddarDAO.exists(khareeddar));
        khareeddarDAO.add(khareeddar);

        String uniqueKey = "maloo sons";
        Khareeddar k1 = khareeddarDAO.getBasedOnUniqueKey(uniqueKey);
        System.out.println("New Re: "+gson.toJson(k1));

        Khareeddar k2 = mongoCollectionDAO.getBasedOnUniqueKey(uniqueKey);

        System.out.println("YO: "+gson.toJson(k2));
    }
}
