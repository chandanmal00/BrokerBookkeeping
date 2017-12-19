package com.bookkeeping.DAO;

import com.bookkeeping.constants.Constants;
import com.bookkeeping.model.Kisaan;
import com.bookkeeping.model.ModelObj;
import com.bookkeeping.persistence.MongoConnection;
import com.bookkeeping.utilities.ControllerUtilities;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chandan on 6/18/16.
 */
public abstract class MongoCollectionDAOImpl<T extends ModelObj> {
    MongoCollection<Document> mongoCollection;
    Gson gson;
    URLCodec urlCodec;
    static final Logger logger = LoggerFactory.getLogger(MongoCollectionDAOImpl.class);

    public MongoCollectionDAOImpl() {
        urlCodec = new URLCodec("UTF-8");
        gson = new Gson();
    }

    public long count() {
        return mongoCollection.count();
    }

    public void update(T o) {
        if(!exists(o)) {
            mongoCollection.insertOne(getDocument(o));
            logger.info("upsert-ed document:{}",getJsonString(o));
        } else {

            Bson condition = new Document("uniqueKey",o.getUniqueKey());
            mongoCollection.replaceOne(condition,getDocument(o));
            //mongoCollection.updateOne(getUniqueDocument(o.getUniqueKey()),getDocument(o));
            logger.error("Document updated, doc:{}",getJsonString(o));

        }
    }


    public void add(T o) {

        if(!exists(o)) {
            mongoCollection.insertOne(getDocument(o));
            logger.info("Added document:{}",getJsonString(o));
        } else {
            logger.error("Document already exist, doc:{}",getJsonString(o));

        }
    }

    public void add(List<T> oList) {

        for(T o : oList) {
            if (!exists(o)) {
                mongoCollection.insertOne(getDocument(o));
                logger.info("Added document:{}", getJsonString(o));
            } else {
                logger.error("Document already exist, doc:{}", getJsonString(o));

            }
        }
    }


    //@Override
    public void forceAdd(T o) {
        mongoCollection.insertOne(getDocument(o));
        logger.debug("Added document forcefully:{}", getJsonString(o));

    }


    //@Override
    public void forceAdd(List<T> oList) {
        for(T o : oList) {
            mongoCollection.insertOne(getDocument(o));
            logger.debug("Document already exist, doc:{}", getJsonString(o));
        }
    }
    public boolean exists(T o) {
        logger.info("Checking: {}", uniqueDocument(o).toJson());
        Document doc = mongoCollection.find(uniqueDocument(o)).first();
        return (doc!=null);
    }



    public void remove(T o) {
        mongoCollection.deleteOne(uniqueDocument(o));
        logger.info("Removed document:{}",getJsonString(o));
    }

    public void remove(String uniqueKey) {
        mongoCollection.deleteOne(this.getUniqueDocument(uniqueKey));
        logger.info("Removed document if exists with uniqueKey:{}",uniqueKey);
    }

    public void remove(List<T> oList) {
        for(T o: oList) {
            mongoCollection.deleteOne(uniqueDocument(o));
            logger.info("Removed document:{}", getJsonString(o));
        }
    }

    protected Document getUniqueDocument(String uniqyeKey) {
        try {
            String decodedKey = urlCodec.decode(uniqyeKey);
            return new Document().append(Constants.UNIQUE_KEY, decodedKey);
        } catch (Exception e) {
            logger.error("Problem in decoding key:{}",uniqyeKey);
            return new Document().append(Constants.UNIQUE_KEY, uniqyeKey);
        }
    }

    protected Document getIdDocument(String id) {
        return new Document().append(Constants.ID_KEY,id);
    }


    public  T getBasedOnUniqueKey(String uniqueKey) {
        Document doc = mongoCollection.find(this.getUniqueDocument(uniqueKey)).first();
        if(doc!=null) {
            return getTargetObjBasedOnUniqueKey(doc);
        }
        return null;
    }

    public  T getBasedOnId(String id) {
        Document doc = mongoCollection.find(this.getIdDocument(id)).first();
        if(doc!=null) {
            return getTargetObjBasedOnUniqueKey(doc);
        }
        return null;
    }


    public List<Document> getDailySummaryNDatesEndingToday(int n) {
        return this.getDailySummaryNDatesEnding(ControllerUtilities.getCurrentDateStrInYYYY_MM_DD(), n);
    }


    public List<Document> getEntityMonthlySummaryEndingToday(int n, String uniqueKey, String entity) {
        return this.getEntityMonthlySummary(ControllerUtilities.getCurrentDateStrInYYYY_MM_DD(), n, uniqueKey, entity);
    }

    public List<Document> getEntityYearlySummaryEndingToday(int n, String uniqueKey, String entity) {
        return this.getEntityYearlySummary(ControllerUtilities.getCurrentDateStrInYYYY_MM_DD(), n, uniqueKey, entity);
    }

    public List<Document> getDailySummaryForWeekEnding(String toDateStr) {
        return getDailySummaryNDatesEnding(toDateStr, -7);
    }

    public List<Document> getEntityDailySummaryForWeekEnding(String toDateStr,String uniqueKey, String entityType) {
        return getEntityDailySummaryNDatesEnding(toDateStr, -7, uniqueKey, entityType);
    }

    public List<Document> getDailySummaryFor30daysEnding(String toDateStr) {
        return getDailySummaryNDatesEnding(toDateStr, -30);
    }


    public List<Document> getMonthlySummaryEndingToday(int n) {
        return this.getMonthlySummary(ControllerUtilities.getCurrentDateStrInYYYY_MM_DD(), n);
    }

    public List<Document> getYearlySummaryEndingToday(int n) {
        return this.getYearlySummary(ControllerUtilities.getCurrentDateStrInYYYY_MM_DD(), n);
    }

    public List<Document> getEntityDailySummaryNDatesEndingToday(int n,String uniqueKey, String entityType) {
        return this.getEntityDailySummaryNDatesEnding(ControllerUtilities.getCurrentDateStrInYYYY_MM_DD(), n, uniqueKey, entityType);
    }



    public List<Document> getEntityMonthlySummary(String toDateStr, int n,String uniqueKey, String entityType) {


        DateFormat dateFormatInput = new SimpleDateFormat(Constants.DATE_FORMAT_YYYY_MM);
        Date toDate  = ControllerUtilities.getDateInFormat(toDateStr);
        if(toDate==null) {
            return new ArrayList<Document>();
        }

        Date fromDate = ControllerUtilities.getNMonthssDate(toDate, n);
        String fromDateStr = dateFormatInput.format(fromDate);

        logger.info("from:{}, to:{}",fromDateStr,toDateStr);
        return getEntityMonthlySummaryBetweenDates(toDateStr, fromDateStr, uniqueKey, entityType);

    }

    public List<Document> getEntityYearlySummary(String toDateStr, int n,String uniqueKey, String entityType) {


        DateFormat dateFormatInput = new SimpleDateFormat(Constants.DATE_FORMAT_YYYY);
        Date toDate  = ControllerUtilities.getDateInFormat(toDateStr);
        if(toDate==null) {
            return new ArrayList<Document>();
        }

        Date fromDate = ControllerUtilities.getNMonthssDate(toDate, n);
        String fromDateStr = dateFormatInput.format(fromDate);

        logger.info("from:{}, to:{}",fromDateStr,toDateStr);
        return getEntityMonthlySummaryBetweenDates(toDateStr, fromDateStr, uniqueKey, entityType);

    }

    public List<Document> getEntityMonthlySummaryBetweenDates(String toDateStr, String fromDateStr,String uniqueKey, String entityType) {


        logger.info("from:{}, to:{}",fromDateStr,toDateStr);
        DateFormat dateFormatInput = new SimpleDateFormat(Constants.DATE_FORMAT_YYYY_MM);

        Date toDate  = ControllerUtilities.getDateInFormatYYYY_MM(toDateStr);
        Date fromDate  = ControllerUtilities.getDateInFormatYYYY_MM(fromDateStr);
        if(toDate==null || fromDate==null) {
            return new ArrayList<Document>();
        }

        try {
            Bson filter = new Document("$gte", dateFormatInput.format(fromDate)).append("$lte", dateFormatInput.format(toDate)+"-31");

            Document matchDocFilter = new Document();
            matchDocFilter.append("eventDate", filter);
            if(uniqueKey!=null) {
                matchDocFilter.append(entityType,urlCodec.decode(uniqueKey));
            }

            logger.debug("Match doc is:{}",matchDocFilter);

            Document matchDoc = new Document();
            matchDoc.append("$match", matchDocFilter);
            Document groupBy = new Document();
            groupBy.append("$group", Document.parse("{_id:{ $substr:[\"$eventDate\",0,7]},amountBrokerage:{$sum:\"$amountBrokerage\"},amountKhareeddar:{$sum:\"$amountKhareeddar\"} ,amount:{$sum:\"$amount\"}}"));
            logger.debug(groupBy.toJson());
            List<Document> list = new ArrayList<Document>();
            list.add(matchDoc);
            list.add(groupBy);
            Document sortBy = new Document();
            sortBy.append("$sort", Document.parse("{_id:1}"));
            list.add(sortBy);

            List<Document> documents = mongoCollection.aggregate(list).into(new ArrayList<Document>());
            return documents;

        }catch(Exception e) {
            logger.error("Error in parsing date:",e);
            return new ArrayList<Document>();
        }
    }

    public List<Document> getEntityYearlySummaryBetweenDates(String toDateStr, String fromDateStr,String uniqueKey, String entityType) {


        logger.info("from:{}, to:{}",fromDateStr,toDateStr);
        DateFormat dateFormatInput = new SimpleDateFormat(Constants.DATE_FORMAT_YYYY);

        Date toDate  = ControllerUtilities.getDateInFormatYYYY(toDateStr);
        Date fromDate  = ControllerUtilities.getDateInFormatYYYY(fromDateStr);
        if(toDate==null || fromDate==null) {
            return new ArrayList<Document>();
        }

        try {
            Bson filter = new Document("$gte", dateFormatInput.format(fromDate)).append("$lte", dateFormatInput.format(toDate)+"-12-31");

            Document matchDocFilter = new Document();
            matchDocFilter.append("eventDate", filter);
            if(uniqueKey!=null) {
                matchDocFilter.append(entityType,urlCodec.decode(uniqueKey));
            }

            logger.debug("Match doc is:{}",matchDocFilter);

            Document matchDoc = new Document();
            matchDoc.append("$match", matchDocFilter);
            Document groupBy = new Document();
            groupBy.append("$group", Document.parse("{_id:{ $substr:[\"$eventDate\",0,4]},amountBrokerage:{$sum:\"$amountBrokerage\"},amountKhareeddar:{$sum:\"$amountKhareeddar\"} ,amount:{$sum:\"$amount\"}}"));
            logger.debug(groupBy.toJson());
            List<Document> list = new ArrayList<Document>();
            list.add(matchDoc);
            list.add(groupBy);

            Document sortBy = new Document();
            sortBy.append("$sort", Document.parse("{_id:1}"));
            list.add(sortBy);

            List<Document> documents = mongoCollection.aggregate(list).into(new ArrayList<Document>());
            return documents;

        }catch(Exception e) {
            logger.error("Error in parsing date:",e);
            return new ArrayList<Document>();
        }
    }

    public List<Document> getMonthlySummaryBetweenDates(String toDateStr, String fromDateStr) {


        logger.info("from:{}, to:{}",fromDateStr,toDateStr);
        DateFormat dateFormatInput = new SimpleDateFormat(Constants.DATE_FORMAT_YYYY_MM);
        Date toDate  = ControllerUtilities.getDateInFormatYYYY_MM(toDateStr);
        Date fromDate  = ControllerUtilities.getDateInFormatYYYY_MM(fromDateStr);
        if(toDate==null || fromDate==null) {
            return new ArrayList<Document>();
        }

        try {
            //Bson filter = new Document("$gte", dateFormatInput.format(fromDate)).append("$lte", dateFormatInput.format(toDate)+" 23:59:59");
            Bson filter = new Document("$gte", dateFormatInput.format(fromDate)).append("$lte", dateFormatInput.format(toDate)+"-31");
            //Bson filter = new Document("$gte", toDateStr).append("$lte", fromDateStr);
            logger.info("Filter:{}", filter);

            Document matchDoc = new Document();
            matchDoc.append("$match", new Document("eventDate", filter));
            Document groupBy = new Document();
            groupBy.append("$group", Document.parse("{_id:{ $substr:[\"$eventDate\",0,7]},amountBrokerage:{$sum:\"$amountBrokerage\"},amountKhareeddar:{$sum:\"$amountKhareeddar\"} ,amount:{$sum:\"$amount\"}}"));
            System.out.println(groupBy.toJson());
            List<Document> list = new ArrayList<Document>();
            list.add(matchDoc);
            list.add(groupBy);
            Document sortBy = new Document();
            sortBy.append("$sort", Document.parse("{_id:1}"));
            list.add(sortBy);

            //logger.info("Query:"+gson.toJson(list));
            List<Document> documents = mongoCollection.aggregate(list).into(new ArrayList<Document>());
            return documents;

        }catch(Exception e) {
            logger.error("Error in parsing date:",e);
            return new ArrayList<Document>();
        }
    }

    public List<Document> getYearlySummaryBetweenDates(String toDateStr, String fromDateStr) {


        logger.info("from:{}, to:{}",fromDateStr,toDateStr);
        DateFormat dateFormatInput = new SimpleDateFormat(Constants.DATE_FORMAT_YYYY);
        Date toDate  = ControllerUtilities.getDateInFormatYYYY(toDateStr);
        Date fromDate  = ControllerUtilities.getDateInFormatYYYY(fromDateStr);
        if(toDate==null || fromDate==null) {
            return new ArrayList<Document>();
        }

        try {
            //Bson filter = new Document("$gte", dateFormatInput.format(fromDate)).append("$lte", dateFormatInput.format(toDate)+" 23:59:59");
            Bson filter = new Document("$gte", dateFormatInput.format(fromDate)).append("$lte", dateFormatInput.format(toDate)+"-12-31");
            //Bson filter = new Document("$gte", toDateStr).append("$lte", fromDateStr);
            logger.info("Filter:{}", filter);

            Document matchDoc = new Document();
            matchDoc.append("$match", new Document("eventDate", filter));
            Document groupBy = new Document();
            groupBy.append("$group", Document.parse("{_id:{ $substr:[\"$eventDate\",0,4]},amountBrokerage:{$sum:\"$amountBrokerage\"},amountKhareeddar:{$sum:\"$amountKhareeddar\"} ,amount:{$sum:\"$amount\"}}"));
            System.out.println(groupBy.toJson());
            List<Document> list = new ArrayList<Document>();
            list.add(matchDoc);
            list.add(groupBy);
            Document sortBy = new Document();
            sortBy.append("$sort", Document.parse("{_id:1}"));
            list.add(sortBy);

            //logger.info("Query:"+gson.toJson(list));
            List<Document> documents = mongoCollection.aggregate(list).into(new ArrayList<Document>());
            return documents;

        }catch(Exception e) {
            logger.error("Error in parsing date:",e);
            return new ArrayList<Document>();
        }
    }


    public List<Document> getMonthlySummary(String toDateStr, int n) {


        DateFormat dateFormatInput = new SimpleDateFormat(Constants.DATE_FORMAT_YYYY_MM);
        Date toDate  = ControllerUtilities.getDateInFormat(toDateStr);
        if(toDate==null) {
            return new ArrayList<Document>();
        }

        Date fromDate = ControllerUtilities.getNMonthssDate(toDate, n);
        String fromDateStr = dateFormatInput.format(fromDate);

        return getMonthlySummaryBetweenDates(toDateStr,fromDateStr);
    }

    public List<Document> getYearlySummary(String toDateStr, int n) {


        DateFormat dateFormatInput = new SimpleDateFormat(Constants.DATE_FORMAT_YYYY);
        Date toDate  = ControllerUtilities.getDateInFormat(toDateStr);
        if(toDate==null) {
            return new ArrayList<Document>();
        }

        Date fromDate = ControllerUtilities.getNMonthssDate(toDate, n);
        String fromDateStr = dateFormatInput.format(fromDate);

        return getYearlySummaryBetweenDates(toDateStr, fromDateStr);
    }

    public List<Document> getDailySummaryBetweenDates(String toDateStr, String fromDateStr) {


        DateFormat dateFormatInput = new SimpleDateFormat("yyyy-MM-dd");
        Date toDate  = ControllerUtilities.getDateInFormat(toDateStr);
        Date fromDate  = ControllerUtilities.getDateInFormat(fromDateStr);
        if(toDate==null || fromDate==null) {
            return new ArrayList<Document>();
        }

        logger.info("from:{}, to:{}",fromDateStr,toDateStr);

        try {
            Bson filter = new Document("$gte", dateFormatInput.format(fromDate)).append("$lte", dateFormatInput.format(toDate));
            logger.info("Filter:{}", filter);

            Document matchDoc = new Document();
            matchDoc.append("$match", new Document("eventDate", filter));
            Document groupBy = new Document();
            groupBy.append("$group", Document.parse("{_id:\"$eventDate\",amountBrokerage:{$sum:\"$amountBrokerage\"},amountKhareeddar:{$sum:\"$amountKhareeddar\"} ,amount:{$sum:\"$amount\"}}"));

            Document sortBy = new Document();
            sortBy.append("$sort", Document.parse("{_id:1}"));
            List<Document> list = new ArrayList<Document>();
            list.add(matchDoc);
            list.add(groupBy);
            list.add(sortBy);

            logger.info(gson.toJson(list));
            List<Document> documents = mongoCollection.aggregate(list).into(new ArrayList<Document>());
            return documents;

        }catch(Exception e) {
            logger.error("Error in parsing date:",e);
            return new ArrayList<Document>();
        }
    }

    public List<Document> getDailySummaryNDatesEnding(String toDateStr, int n) {


        DateFormat dateFormatInput = new SimpleDateFormat("yyyy-MM-dd");
        Date toDate  = ControllerUtilities.getDateInFormat(toDateStr);
        if(toDate == null) {
            return new ArrayList<Document>();
        }

        Date fromDate = ControllerUtilities.getNDaysDate(toDate, n);

        String fromDateStr = dateFormatInput.format(fromDate);
        return  getDailySummaryBetweenDates(toDateStr, fromDateStr);
    }

    public List<Document> getEntityDailySummaryBetweenDates(String toDateStr, String fromDateStr,String uniqueKey, String entityType) {


        DateFormat dateFormatInput = new SimpleDateFormat("yyyy-MM-dd");
        Date toDate  = ControllerUtilities.getDateInFormat(toDateStr);
        Date fromDate  = ControllerUtilities.getDateInFormat(fromDateStr);
        if(toDate==null || fromDate==null) {
            return new ArrayList<Document>();
        }

        logger.info("from:{}, to:{}",fromDateStr,toDateStr);

        try {
            Bson filter = new Document("$gte", dateFormatInput.format(fromDate)).append("$lte", dateFormatInput.format(toDate));
            logger.info("Filter:{}", filter);

            Document matchDocFilter = new Document();
            matchDocFilter.append("eventDate", filter);
            if(uniqueKey != null) {
                matchDocFilter.append(entityType,urlCodec.decode(uniqueKey));
            }

            logger.info("Match doc is:{}", matchDocFilter);
            Document matchDoc = new Document();
            //matchDoc.append("$match",new Document("creationTime", filter));
            matchDoc.append("$match", matchDocFilter);
            Document groupBy = new Document();
            groupBy.append("$group", Document.parse("{_id:\"$eventDate\",amountBrokerage:{$sum:\"$amountBrokerage\"},amountKhareeddar:{$sum:\"$amountKhareeddar\"} ,amount:{$sum:\"$amount\"}}"));
            List<Document> list = new ArrayList<Document>();
            list.add(matchDoc);
            list.add(groupBy);
            Document sortBy = new Document();
            sortBy.append("$sort", Document.parse("{_id:1}"));
            list.add(sortBy);

            logger.info(gson.toJson(list));
            List<Document> documents = mongoCollection.aggregate(list).into(new ArrayList<Document>());

            //logger.info("DETAILS::"+gson.toJson(documents));
            return documents;

        }catch(Exception e) {
            logger.error("Error in parsing date:",e);
            return new ArrayList<Document>();
        }
    }

    public List<Document> getEntityDailySummaryNDatesEnding(String toDateStr, int n,String uniqueKey, String entityType) {


        DateFormat dateFormatInput = new SimpleDateFormat("yyyy-MM-dd");
        Date toDate  = ControllerUtilities.getDateInFormat(toDateStr);
        if(toDate==null) {
            return new ArrayList<Document>();
        }

        Date fromDate = ControllerUtilities.getNDaysDate(toDate, n);
        String fromDateStr = dateFormatInput.format(fromDate);
        return getEntityDailySummaryBetweenDates(toDateStr,fromDateStr,uniqueKey,entityType);

    }

    private String getMatchUniqueKeyDocBasedonEntityType(String entityType) {
        return null;
    }

    public void removeBasedOnId(String id) {
        mongoCollection.deleteOne(this.getIdDocument(id));
    }

    public abstract T getTargetObjBasedOnUniqueKey(Document doc);


    public List<T> list() {
        return getList(mongoCollection.find().into(new ArrayList<Document>()));
    }

    public List<T> list(int limit) {
        return getList(mongoCollection.find().sort(new Document("creationTime", -1)).limit(limit).into(new ArrayList<Document>()));
    }

    public List<T> list(Document filter) {
        return getList(mongoCollection.find(filter).into(new ArrayList<Document>()));
    }

    public List<T> list(Document filter, int limit) {
        return getList(mongoCollection.find(filter).sort(new Document("creationTime", -1)).limit(limit).into(new ArrayList<Document>()));
    }



    public List<T> search(String search) {
        String searchString = "{ $text: { $search:\"" + search + "\" } }";
        logger.info("SearchString:"+searchString);
        return getList(mongoCollection.find(Document.parse(searchString)).into(new ArrayList<Document>()));

    }

    public List<T> search(String search,int limit) {
        String searchString = "{ $text: { $search:\"" + search + "\" } }";
        logger.info("SearchString:"+searchString);
        return getList(mongoCollection.find(Document.parse(searchString)).limit(limit).into(new ArrayList<Document>()));

    }

    public List<T> searchValueFieldWithLimit(String search, int limit) {
        if(StringUtils.isBlank(search)) {
            return new ArrayList<T>();
        }
        //limit safeguard
        if(limit <0 || limit >1000) {
            limit =20;
        }

        Document regexDoc = new Document();
        regexDoc.append("$regex",search).append("$options","i");

        Document searchDoc = new Document();
        searchDoc.append("value", regexDoc);

        //String searchString = new Document()"{ value: { $regex: '"+search+"', $options: 'i' } }";
        logger.info("SearchString:" + gson.toJson(searchDoc));

        return getList(mongoCollection.find(searchDoc).limit(limit).into(new ArrayList<Document>()));
    }


    public List<String> searchKeysWithLimit(String search, int limit) {
        if(StringUtils.isBlank(search)) {
            return new ArrayList<String>();
        }
        //limit safeguard
        if(limit <0 || limit >1000) {
            limit =20;
        }

        Document regexDoc = new Document();
        regexDoc.append("$regex",search).append("$options","i");

        Document searchDoc = new Document();
        searchDoc.append("uniqueKey", regexDoc);

        //String searchString = new Document()"{ value: { $regex: '"+search+"', $options: 'i' } }";
        logger.info("SearchString:" + gson.toJson(searchDoc));

        BasicDBObject b = new BasicDBObject();
        b.append("uniqueKey",true);
        b.append("_id",false);

        List<Document> docs = mongoCollection.find(searchDoc).projection(Document.parse("{_id: false, uniqueKey: true}")).limit(limit).into(new ArrayList<Document>());
        List<String> objs = new ArrayList<String>();
        for(Document doc: docs) {
            objs.add(doc.getString("uniqueKey"));
        }
        return objs;
        //return getList(mongoCollection.find(searchDoc).projection().limit(limit).into(new ArrayList<Document>()));
    }


    protected abstract List<String> listKeys();

    protected abstract List<T> getList(List<Document> into);
    protected abstract Document uniqueDocument(T o);
    protected abstract Document getDocument(T o);
    protected abstract String getJsonString(T o);

    public static void main(String[] args) {
        MongoConnection.specialInit();
        KisaanDAO kisaanDAO = new KisaanDAOImpl();

        Kisaan kisaan = kisaanDAO.getBasedOnNickName("kisaan_30");
        logger.info(kisaan.toString());

        kisaan.setFirstName("Chandan1123");
        kisaan.setLastName("JKDD");

        kisaanDAO.update(kisaan);
    }
}