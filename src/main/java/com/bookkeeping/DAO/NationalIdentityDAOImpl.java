package com.bookkeeping.DAO;

import com.bookkeeping.constants.Constants;
import com.bookkeeping.model.NationalIdentity;
import com.bookkeeping.persistence.MongoConnection;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chandan on 6/4/16.
 */
public class NationalIdentityDAOImpl extends MongoCollectionDAOImpl<NationalIdentity>  implements NationalIdentityDAO {
    static final Logger logger = LoggerFactory.getLogger(NationalIdentityDAOImpl.class);


    public NationalIdentityDAOImpl() {
        super();
        mongoCollection = MongoConnection.getInstance().getCollection(Constants.MONGO_NATIONAL_IDENTITY_COLLECTION);
    }


    public NationalIdentity get(String nationalId) {


        Document query = new Document();
        query.append("_id", nationalId);
        Document kisaanDoc = mongoCollection.find(query).first();
        NationalIdentity nationalIdentity = gson.fromJson(kisaanDoc.toJson(), NationalIdentity.class);
        return nationalIdentity;

    }

    @Override
    public List<String> listKeys() {
        List<String> keys = new ArrayList<String>();
        List<NationalIdentity> list = super.list();
        for(NationalIdentity k : list) {
            keys.add(k.getUniqueKey());
        }
        return keys;
    }

    public NationalIdentity getBasedOnPan(String pan) {

        Document query = new Document();
        query.append("pan", pan);
        Document kisaanDoc = mongoCollection.find(query).first();
        NationalIdentity nationalIdentity = gson.fromJson(kisaanDoc.toJson(), NationalIdentity.class);
        return nationalIdentity;

    }

    public NationalIdentity getBasedOnAadhar(String aadhar) {
        Document query = new Document();
        query.append("aadhar", aadhar);
        Document kisaanDoc = mongoCollection.find(query).first();
        NationalIdentity nationalIdentity = gson.fromJson(kisaanDoc.toJson(), NationalIdentity.class);
        return nationalIdentity;

    }

    @Override
    public NationalIdentity getTargetObjBasedOnUniqueKey(Document doc) {
        return gson.fromJson(doc.toJson(), NationalIdentity.class);
    }

    @Override
    protected Document uniqueDocument(NationalIdentity nationalIdentity) {
        return new Document()
                .append(Constants.UNIQUE_KEY,
                        nationalIdentity.getUniqueKey());
    }

    @Override
    protected Document getDocument(NationalIdentity nationalIdentity) {
        return Document.parse(gson.toJson(nationalIdentity));
    }

    @Override
    protected String getJsonString(NationalIdentity nationalIdentity) {
        return gson.toJson(nationalIdentity);
    }

    @Override
    protected List<NationalIdentity> getList(List<Document> documentList) {
        List<NationalIdentity> nationalIdentityList = new ArrayList<NationalIdentity>();
        for (Document nationalIdentityDoc : documentList) {
            // System.out.println("HERE you go::" + nationalIdentityDoc.toJson());
            NationalIdentity nationalIdentity = gson.fromJson(nationalIdentityDoc.toJson(), NationalIdentity.class);
            nationalIdentityList.add(nationalIdentity);
        }
        return nationalIdentityList;
    }

    /*

    @Override
    public void add(NationalIdentity nationalIdentity) {
        Document document = mongoCollection.find(uniqueDocument(nationalIdentity)).first();
        if (document == null) {
            document = mongoCollection.find(uniqueDocumentPan(nationalIdentity)).first();
            if (document == null) {
                mongoCollection.insertOne(Document.parse(gson.toJson(nationalIdentity)));
            } else {
                logger.error("NationalIdentity with pan:{} already in database:, so not adding", nationalIdentity.getPan());
            }
        } else {
            logger.error("NationalIdentity with aadhar:{} already in database:, so not adding", nationalIdentity.getAadhar());
        }
        //mongoCollection.insertOne(Document.parse(gson.toJson(nationalIdentity)));
    }

    @Override
    public void remove(NationalIdentity nationalIdentity) {
        mongoCollection.deleteOne(Document.parse(gson.toJson(nationalIdentity)));

    }
    @Override
    public List<NationalIdentity> list() {
        List<Document> documentList = mongoCollection.find().into(new ArrayList<Document>());
        return getList(documentList);
    }


    private Document uniqueDocument(NationalIdentity nationalIdentity) {
        return new Document()
                .append(Constants.nationalIdentityUniqueKey,
                        nationalIdentity.getAadhar());
    }

    private Document uniqueDocumentPan(NationalIdentity nationalIdentity) {
        return new Document()
                .append(Constants.nationalIdentityPanUniqueKey,
                        nationalIdentity.getPan());
    }

private List<NationalIdentity> getList(List<Document> documentList) {
        List<NationalIdentity> nationalIdentityList = new ArrayList<NationalIdentity>();
        for (Document nationalIdentityDoc : documentList) {
            System.out.println("HERE you go::" + nationalIdentityDoc.toJson());
            NationalIdentity nationalIdentity = gson.fromJson(nationalIdentityDoc.toJson(), NationalIdentity.class);
            nationalIdentityList.add(nationalIdentity);
        }
        return nationalIdentityList;
    }
     */
}
