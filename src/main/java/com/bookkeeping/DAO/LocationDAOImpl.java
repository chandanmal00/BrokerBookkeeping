package com.bookkeeping.DAO;

import com.bookkeeping.constants.Constants;
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
public class LocationDAOImpl extends MongoCollectionDAOImpl<Location> implements LocationDAO {
    
    //Client client;
    static final Logger logger = LoggerFactory.getLogger(LocationDAOImpl.class);


    public LocationDAOImpl() {
        //this.client = ElasticCacheConnection.getInstance();
        super();
        mongoCollection = MongoConnection.getInstance().getCollection(Constants.MONGO_LOCATION_COLLECTION);
    }


    @Override
    public Location getBasedOnPlace(String place) {
        Document doc = new Document();
        doc.append("place",place);
        Document locationDoc = mongoCollection.find(doc).first();
        return gson.fromJson(locationDoc.toJson(),Location.class);
    }

    @Override
    public List<Location> getBasedOnTaluka(String taluka) {
        Document doc = new Document();
        doc.append("taluka",taluka);
        List<Document> locationDocList = mongoCollection.find(doc).into(new ArrayList<Document>());
        return getList(locationDocList);

    }
    @Override
    public List<String> listKeys() {
        List<String> keys = new ArrayList<String>();
        List<Location> list = super.list();
        for(Location k : list) {
            keys.add(k.getUniqueKey());
        }
        return keys;
    }



    @Override
    public Location getTargetObjBasedOnUniqueKey(Document doc) {
        return gson.fromJson(doc.toJson(), Location.class);
    }

    @Override
    protected Document uniqueDocument(Location location) {
        return new Document()
                .append(Constants.UNIQUE_KEY,
                        location.getUniqueKey());
    }

    @Override
    protected Document getDocument(Location location) {
        return Document.parse(gson.toJson(location));
    }

    @Override
    protected String getJsonString(Location location) {
        return gson.toJson(location);
    }

    @Override
    protected List<Location> getList(List<Document> documentList) {
        List<Location> locationList = new ArrayList<Location>();
        for (Document locationDoc : documentList) {
            // System.out.println("HERE you go::" + locationDoc.toJson());
            Location location = gson.fromJson(locationDoc.toJson(), Location.class);
            locationList.add(location);
        }
        return locationList;
    }
    /*
     public List<Location> list() {
        List<Document> locationDocList = mongoCollection.find().into(new ArrayList<Document>());
        return getList(locationDocList);
    }


    public void add(Location location) {
        //mongoCollection.insertOne(Document.parse(gson.toJson(location)));
        Document document = mongoCollection.find(uniqueDocument(location)).first();
        if(document==null) {
            mongoCollection.insertOne(Document.parse(gson.toJson(location)));
        } else {
            logger.error("Khareeddar with firmName:{} already in database:, so not adding",location.getPlace());
        }

    }

    public void remove(Location location) {

    }

    private List<Location> getList(List<Document> documentList) {
        List<Location> locationList = new ArrayList<Location>();
        for(Document locationDoc : documentList) {
            System.out.println("HERE you go::"+locationDoc.toJson());
            Location location = gson.fromJson(locationDoc.toJson(), Location.class);
            locationList.add(location);
        }
        return locationList;
    }


    private Document uniqueDocument(Location location) {
        return new Document()
                .append(Constants.locationUniqueKey,
                        location.getPlace());
    }
     */

    public static void main(String[] args) {
        Location location = new Location("wrr");
        LocationDAO locationDAO = new LocationDAOImpl();
        locationDAO.add(location);
    }

}
