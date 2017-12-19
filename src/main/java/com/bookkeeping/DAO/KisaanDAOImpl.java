package com.bookkeeping.DAO;

import com.bookkeeping.constants.Constants;
import com.bookkeeping.model.Kisaan;
import com.bookkeeping.model.Location;
import com.bookkeeping.persistence.MongoConnection;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chandan on 6/4/16.
 */
public class KisaanDAOImpl extends MongoCollectionDAOImpl<Kisaan> implements KisaanDAO {

    //Client client;
    static final Logger logger = LoggerFactory.getLogger(KisaanDAOImpl.class);

    public KisaanDAOImpl() {
        super();
        mongoCollection = MongoConnection.getInstance().getCollection(Constants.MONGO_KISAAN_COLLECTION);
    }


    public List<Kisaan> getBasedOnName(String kisaanName) {
        List<Document> kisaanDocumentList = mongoCollection.find((new Document()).append("firstName",kisaanName)).into(new ArrayList<Document>());
        return this.getList(kisaanDocumentList);

    }

    @Override
    public Kisaan getBasedOnNickName(String kisaanNickName) {
        Document kisaanDoc = mongoCollection.find((new Document()).append("nickName",kisaanNickName)).first();
        return gson.fromJson(kisaanDoc.toJson(),Kisaan.class);
    }

    private void addTestKisaan() {
        Kisaan k = new Kisaan("ch");
        k.setFirstName("malooChanda");
        k.setLastName("yes");
        Location l = new Location("wrr");
        k.setLocation(l);
        this.add(k);
    }




    @Override
    public Kisaan getTargetObjBasedOnUniqueKey(Document doc) {
        return gson.fromJson(doc.toJson(), Kisaan.class);
    }

    @Override
    protected Document uniqueDocument(Kisaan kisaan) {
        return new Document()
                .append(Constants.UNIQUE_KEY,
                        kisaan.getUniqueKey());
    }

    @Override
    protected Document getDocument(Kisaan kisaan) {
        return Document.parse(gson.toJson(kisaan));
    }

    @Override
    protected String getJsonString(Kisaan kisaan) {
        return gson.toJson(kisaan);
    }

    @Override
    protected List<Kisaan> getList(List<Document> documentList) {
        List<Kisaan> kisaanList = new ArrayList<Kisaan>();
        for (Document kisaanDoc : documentList) {
            // System.out.println("HERE you go::" + kisaanDoc.toJson());
            Kisaan kisaan = gson.fromJson(kisaanDoc.toJson(), Kisaan.class);
            kisaanList.add(kisaan);
        }
        return kisaanList;
    }

    @Override
    public List<String> listKeys() {
        List<String> keys = new ArrayList<String>();
        List<Kisaan> list = super.list();
        for(Kisaan k : list) {
            keys.add(k.getUniqueKey());
        }
        return keys;
    }

    /*
            public void add(Kisaan kisaan) {

            String kisanDocument = gson.toJson(kisaan);
            //Add a kisaan only if it does not exists
            //We are assuming nickNames are unique.

            Document document = mongoCollection.find(uniqueDocument(kisaan)).first();
            if(document==null) {
                mongoCollection.insertOne(Document.parse(kisanDocument));
            } else {
                logger.error("Kisaan with nickName:{} already in database:, so not adding",kisaan.getNickName());
            }

        }


        public void remove(Kisaan Kisaan) {

            mongoCollection.deleteOne(Document.parse(gson.toJson(Kisaan)));
        }

        public List<Kisaan> list() {

            List<Document> kisaanDocumentList = mongoCollection.find().into(new ArrayList<Document>());
            return this.getList(kisaanDocumentList);
        }

    public Kisaan get(String kisaanId) {
            Document query = new Document();
            query.append("_id",kisaanId);
            Document kisaanDoc = mongoCollection.find(query).first();
            Kisaan kisaan = gson.fromJson(kisaanDoc.toJson(),Kisaan.class);
            return kisaan;

        }
         */
    public static void main(String[] args) {
        MongoConnection.specialInit();
        KisaanDAO kisaanDAO = new KisaanDAOImpl();

        for(int i=0;i<120;i++) {
            Kisaan kisaan = new Kisaan("kisaan_"+i);
            kisaan.setLocation(new Location("place+"+i));
            kisaanDAO.add(kisaan);
        }
        /*
        KisaanDAO kisaanDAO = new KisaanDAOImpl();

        List<Kisaan> kisaanList = kisaanDAO.list();
        Gson gson = new Gson();
        String id=null;
        for(Kisaan kisaan: kisaanList) {
            System.out.println(gson.toJson(kisaan));
            id = kisaan.get_id();
        }


        System.out.println("ID::"+id);
        Kisaan k = kisaanDAO.get(id);
        System.out.println("HERE**");
        System.out.println(gson.toJson(k));
        */




    }

}
