package com.bookkeeping.controller;

import com.bookkeeping.Config.BrokerSingleton;
import com.bookkeeping.DAO.*;
import com.bookkeeping.constants.Constants;
import com.bookkeeping.factory.factoryDAO;
import com.bookkeeping.model.*;
import com.bookkeeping.persistence.MongoConnection;
import com.bookkeeping.utilities.*;
import com.google.gson.Gson;
import com.sun.org.apache.xpath.internal.operations.Mod;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.servlet.http.Cookie;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static spark.Spark.*;

/**
 * This class encapsulates the controllers for the Ask for Help web application.
 * Entry point into the web application.
 */
public class BookKeepingController {
    private final Configuration cfg;
    private final int port;
    private static String backUpDir;


    private final KisaanDAO kisaanDAO;
    private final KhareeddarDAO khareeddarDAO;
    private final LocationDAO locationDAO;
    private final KisaanTransactionDAO kisaanTransactionDAO;
    private final KisaanPaymentDAO kisaanPaymentDAO;
    private final KhareeddarPaymentDAO khareeddarPaymentDAO;
    private final TransactionItemDAO transactionItemDAO;
    private final UserDAO userDAO;
    private final SessionDAO sessionDAO;
    static final Logger logger = LoggerFactory.getLogger(BookKeepingController.class);
    static final Logger appLogger = LoggerFactory.getLogger("AppLogging");
    final static ThreadPoolExecutor MONGO_DB_THREAD_POOL = new ThreadPoolExecutor(2, // core size
            30, // max size
            10, // idle timeout
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(40));

    /*
    final static ThreadPoolExecutor ELASTIC_SEARCH_THREAD_POOL = new ThreadPoolExecutor(2, // core size
            30, // max size
            30, // idle timeout
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(40));
*/

    public static void main(String[] args) throws IOException {
        logger.info("Starting Application:");
        OSValue osValue = ControllerUtilities.getOSType();
        backUpDir = Constants.MONGO_DATABASE_BACKUP_DIR_ROOT;
        String publicLocation = Constants.MAC_PUBLIC_LOCATION;
        if (osValue.equals(OSValue.WINDOWS)) {
            backUpDir=Constants.WINDOWS_MONGO_DATABASE_BACKUP_DIR_ROOT;
            publicLocation=Constants.WINDOWS_PUBLIC_LOCATION;
            logger.info("Window OS detected");
        }


        try {

            logger.info("Input args length:{}",args.length);
            if (args.length == 0) {
                MongoConnection.specialInitWithBackup(backUpDir);
                FileSystemUtils.checkDirExistsOtherwiseCreate(backUpDir);
                FileSystemUtils.checkDirExistsOtherwiseCreate(publicLocation);
                BrokerSingleton.init();
                BrokerSingleton.getInstance("config.properties");
                new BookKeepingController(Constants.SITE_PORT);
                //new BookKeepingController(8081);
                //calling init to tell broker to be created from class path resource file
            }
            else {
                int NUMBER_ARGS = 6;
                if(args.length==NUMBER_ARGS) {

                    String portStr = args[0];
                    String mongoPortStr = args[1];
                    String mongoHost = args[2];
                    String backupDirLocal = args[3];
                    String mongoInstallationDir = args[4];
                    String propertiesFullFilePath = args[5];

                    try {
                        int port = Integer.parseInt(portStr);
                        int mongoPort = Integer.parseInt(mongoPortStr);
                        FileSystemUtils.checkDirExistsOtherwiseCreate(backupDirLocal);
                        FileSystemUtils.checkDirExistsOtherwiseCreate(publicLocation);
                        if(!FileSystemUtils.checkIfDirExists(mongoInstallationDir)) {
                            logger.error("MONGO installation dir:{} is not there, we cannot continue",mongoInstallationDir);
                            System.exit(-3);
                        }

                        logger.info("Port changed to:{}", port);
                        BrokerSingleton.getInstance(propertiesFullFilePath);
                        MongoConnection.init(mongoPort, mongoHost, mongoInstallationDir,backUpDir,Constants.MONGO_DATABASE);
                        backUpDir = backupDirLocal;

                        new BookKeepingController(port);
                    } catch(Exception e) {
                        logger.error("Arguments not matching the format expected, so exiting, exception:",e);
                        System.exit(-3);
                    }

                } else {
                    logger.error("Required arguments cnt:{} did not come, so aborting",NUMBER_ARGS);
                    System.out.println("Required arguments cnt:"+NUMBER_ARGS+" did not come, so aborting, input arg length:"+ args.length);
                    System.out.println("Usage:: script <port> <mongoPort> <backupDir>");
                    System.exit(-2);
                }
            }
        } catch(Exception e) {
            logger.info("Application shutting down, closing all connections:", e);
            try {
                MONGO_DB_THREAD_POOL.shutdown();
                //ELASTIC_SEARCH_THREAD_POOL.shutdown();
                //while (!MONGO_DB_THREAD_POOL.isTerminated() || !ELASTIC_SEARCH_THREAD_POOL.isTerminated()) {
                while (!MONGO_DB_THREAD_POOL.isTerminated()) {
                    Thread.sleep(1000);
                    logger.info("waiting for MONGO and ELASTIC threads to shutdown");
                }
            } catch(Exception ez) {
                logger.error("Error in closing Threads",ez);
            } finally {
                //ElasticCacheConnection.shutDown();
                MongoConnection.shutDown();
                logger.info("Shutdown Complete");
            }
            System.out.println("Application closing, some error");
            e.printStackTrace();
        }
    }

    public BookKeepingController(int port) throws IOException {
        logger.info("Starting Application on port:{}",port);

        this.port = port;

        kisaanDAO = new KisaanDAOImpl();
        khareeddarDAO = new KhareeddarDAOImpl();
        locationDAO = new LocationDAOImpl();
        kisaanTransactionDAO = new KisaanTransactionDAOImpl();
        kisaanPaymentDAO = new KisaanPaymentDAOImpl();
        khareeddarPaymentDAO = new KhareeddarPaymentDAOImpl();
        transactionItemDAO = new TransactionItemDAOImpl();

        userDAO = new UserDAO();
        sessionDAO = new SessionDAO();

        //Create admin user for the first time if not exist.
        if(!userDAO.exists(Constants.ADMIN_USER)) {
            userDAO.addUser(Constants.ADMIN_USER, Constants.ADMIN_PASS, Constants.ADMIN_USER);
        }

        //Create required indexes for all collections
        ControllerUtilities.initSetupCreateIndex();

        String passwordForCertificate = "badman123";
        String keyStoreName = "D:\\ssl\\jainTraveller\\keystore.jks";
        String truststoreFile = "D:\\ssl\\truststore.ts";

        this.cfg = SingletonConfiguration.getInstance().getConfiguration();//createFreemarkerConfiguration();
        logger.info("Port configured: {}",port);
        setPort(port);

        //setSecure(keyStoreName, passwordForCertificate, truststoreFile, passwordForCertificate);
        //externalStaticFileLocation("D:\\var\\www\\public\\");
        externalStaticFileLocation(Constants.MAC_PUBLIC_LOCATION);
        staticFileLocation("/public");
        //setSecure(keyStoreName,passwordForCertificate,truststoreFile,passwordForCertificate);
        logger.info("starting all routes");



        KisaanController kisaanController = new KisaanController();
        kisaanController.initializeRoutes();
        KhareeddarController khareeddarController = new KhareeddarController();
        khareeddarController.initializeRoutes();

        KhareeddarPaymentController khareeddarPaymentController = new KhareeddarPaymentController();
        khareeddarPaymentController.initializeRoutes();

        KisaanPaymentController kisaanPaymentController = new KisaanPaymentController();
        kisaanPaymentController.initializeRoutes();

        KisaanTransactionController kisaanTransactionController = new KisaanTransactionController();
        kisaanTransactionController.initializeRoutes();

        TransactionItemController transactionItemController = new TransactionItemController();
        transactionItemController.initializeRoutes();

        LocationController locationController = new LocationController();
        locationController.initializeRoutes();



        initializeRoutes();
        initializeStandardLoginSignUpRoutes();
        initializeListingRoutes();
        initializeStandardCalls();
        initializeWeeklySummaryCalls();
        initializeMongoAdminCalls();
        //initializeKisaanPayment();
        //initializeKisaanTransaction();
        //initializeKhareeddarPayment();
        //initializeKisaanRoutes();
        //initializeKhareeddarRoutes();
        //initializeTransactionItemRoutes();
        //initializeLocationRoutes();



        logger.info("started all routes");
    }

    private void initializeMongoAdminCalls() throws IOException {

        get(new FreemarkerBasedRoute("/details/:entity/:key") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {

                Map<String,Object> root = new HashMap<String, Object>();

                String entity = StringEscapeUtils.escapeHtml4(request.params(":entity"));
                String key =  StringEscapeUtils.escapeHtml4(request.params(":key"));
               // StringEscapeUtils.unescapeHtml4(":entity)
                logger.info("Entity:{}, key:{} to print", entity, key);

                String dateStr = ControllerUtilities.getCurrentDateStrInYYYY_MM_DD();
                root.put("dateStr",dateStr);
                root.put("operation","Print");
                root.put("key",key);
                root.put("entity",entity);

                if(entity.equals("kisaan") || entity.equals("khareeddar")) {
                    response.redirect("/"+entity+"/"+key);
                    return;
                }

                this.template = cfg.getTemplate("print_default.ftl");
                this.templateOverride.setTemplate(this.template);


                if(StringUtils.isNotBlank(entity) && StringUtils.isNotBlank(key)) {
                    MongoCollectionDAO mongoCollectionDAO = factoryDAO.getDAO(entity);
                    if(mongoCollectionDAO==null) {
                        root.put("errors","Bad entity:"+entity);
                        templateOverride.process(root, writer);
                        return;
                    } else {
                        logger.info("Key:{}, entity:{}",key,entity);
                        Object mongoObject = mongoCollectionDAO.getBasedOnUniqueKey(key);
                        if(mongoObject!=null) {
                            try {
                                root.put("entityObject",mongoObject);
                                root.put("success","true");
                                String templateName = "print_"+entity +".ftl";
                                logger.info("Using template:"+templateName);
                                this.template = cfg.getTemplate(templateName);
                                this.templateOverride.setTemplate(this.template);
                                templateOverride.process(root, writer);

                                return;
                            } catch(Exception e) {
                                logger.error("Bad Template processing for entity:{} ",entity,e);
                                root.put("errors","Bad template processing for entity:"+entity);
                                templateOverride.process(root, writer);
                                return;
                            }
                        } else {
                            logger.error("Bad Key:{} for entity:{}, we could not find anything in db",key,entity);
                            root.put("errors","Bad Key:"+key+" for entity:"+entity+", we could not find anything in db");
                            templateOverride.process(root, writer);
                            return;

                        }

                    }
                } else {
                    logger.error("Bad request, One of the inputs is missing, entity:{} or query:{}", entity, key);
                    root.put("error","Bad request, One of the inputs is missing, entity:"+entity+" or key:"+key);
                    templateOverride.process(root, writer);
                }

            }
        });

        get(new FreemarkerBasedRoute("/invoice/:entity/:key/:invoiceEntity") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {

                Map<String,Object> root = new HashMap<String, Object>();

                String entity = StringEscapeUtils.escapeHtml4(request.params(":entity"));
                String invoiceEntity = StringEscapeUtils.escapeHtml4(request.params(":invoiceEntity"));

                String key =  StringEscapeUtils.escapeHtml4(request.params(":key"));
                logger.info("Entity:{}, key:{}, invoiceEntity:{} to invoice", entity, key, invoiceEntity);

                String dateStr = ControllerUtilities.getCurrentDateStrInYYYY_MM_DD();
                root.put("dateStr",dateStr);
                root.put("operation", "Invoice");
                root.put("key",key);
                root.put("entity",entity);
                root.put("broker",BrokerSingleton.broker);
                root.put("invoiceEntity", invoiceEntity);



                if(StringUtils.isNotBlank(entity)
                        && StringUtils.isNotBlank((invoiceEntity))
                        && StringUtils.isNotBlank(key)
                        && (invoiceEntity.equals("kisaan") || invoiceEntity.equals("khareeddar"))) {
                    MongoCollectionDAO mongoCollectionDAO = factoryDAO.getDAO(entity);
                    if(mongoCollectionDAO==null) {
                        root.put("errors","Bad entity:"+entity+" or invoiceEntity:"+invoiceEntity);
                    } else {
                        logger.info("Key:{}, entity:{}, invoiceEntity:{}",key,entity,invoiceEntity);
                        Object mongoObject = mongoCollectionDAO.getBasedOnUniqueKey(key);
                        if(mongoObject!=null) {
                            try {

                                root.put("entityObject",mongoObject);
                                root.put("success","true");

                                String templateName = "invoice_"+entity +".ftl";
                                logger.info("Using template:"+templateName);
                                this.template = cfg.getTemplate(templateName);
                                this.templateOverride.setTemplate(this.template);
                                templateOverride.process(root, writer);
                                return;
                            } catch(Exception e) {
                                logger.error("Bad Template processing for entity:{} ",entity,e);
                                root.put("errors","Bad template processing for entity:"+entity);
                            }
                        } else {
                            logger.error("Bad Key:{} for entity:{}, we could not find anything in db",key,entity);
                            root.put("errors","Bad Key:"+key+" for entity:"+entity+", we could not find anything in db");
                        }

                    }
                } else {
                    logger.error("Bad request, One of the inputs is missing, entity:{}" +
                            " or query:{} or invoiceEntity:{}, " +
                            "invoiceEntity has to be kisaan or khareeddar",entity,key,invoiceEntity);
                    root.put("error", "Bad request, One of the inputs is missing, entity:" + entity + " or key:" + key + ", or invoiceEntity" + invoiceEntity
                            + "<br> invoiceEntity has to be kisaan or khareeddar");
                }
                this.template = cfg.getTemplate("invoice.ftl");
                this.templateOverride.setTemplate(this.template);
                templateOverride.process(root, writer);

            }
        });


/*
        get(new FreemarkerBasedRoute("/print/:entity/:key", "print.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                String entity = request.params(":entity");
                String key = request.params(":key");
                Map<String, Object> root = new HashMap<String, Object>();

                String dateStr = ControllerUtilities.getCurrentDateStrInYYYY_MM_DD();
                root.put("dateStr",dateStr);
                root.put("operation","Print");
                if (ControllerUtilities.verifyEntityInputs(entity, root)) {
                    MongoCollectionDAO mongoDAO = factoryDAO.getDAO(entity);
                    Gson gson = new Gson();;

                    Object mongoObject = mongoDAO.getBasedOnId(key);
                    root.put("entityObject", mongoObject);
                    templateOverride.process(root, writer);
                    return;
                } else {
                    logger.error("Some issue in processing this request, most like entity input:{} is bad", entity);
                    root.put("errors","Some issue in processing this request, most like entity input:"+entity+" is bad");
                    templateOverride.process(root, writer);

                }

            }
        });
        */

        get(new FreemarkerBasedRoute("/save", "db_admin.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                /*
                String username = sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request));
                if(username==null || !username.equals(Constants.ADMIN_USER)) {
                    halt(401,"This request requires privilege");
                }
                */
                Map<String, Object> root = new HashMap<String, Object>();
                String dateStr = ControllerUtilities.getCurrentDateStrInYYYY_MM_DD();
                root.put("dateStr", dateStr);
                root.put("dir", backUpDir + "/" + dateStr);
                root.put("operation", "Save");
                try {
                    MongoAdminCommand.exportMongoDatabaseToDir(dateStr, backUpDir);
                    root.put("success", true);

                } catch (BookKeepingException e) {
                    root.put("errors", e.getMessage());
                    logger.error("There was an error in exporting MongoDatabase, error:{}", e.getMessage());
                } catch (Exception e) {
                    logger.error("Mysterical error", e);
                    root.put("errors", "Please contact admin, there is some issue with MongoExport");

                }
                templateOverride.process(root, writer);

            }
        });

        post(new FreemarkerBasedRoute("/restore", "db_admin.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                /*
                String username = sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request));
                if(username==null || !username.equals(Constants.ADMIN_USER)) {
                    halt(401,"This request requires privilege");
                }
                */
                String dateStr = StringEscapeUtils.escapeHtml4(request.queryParams("dateStr"));
                Map<String, Object> root = new HashMap<String, Object>();
                root.put("dateStr", dateStr);
                root.put("operation", "Restore");
                root.put("dir", backUpDir + "/" + dateStr);
                try {

                    MongoAdminCommand.importMongoDatabaseFromDir(backUpDir, dateStr);
                    root.put("success", true);

                } catch (BookKeepingException e) {
                    root.put("errors", e.getMessage());
                    logger.error("There was an error in exporting MongoDatabase, error:{}", e.getMessage());
                } catch (Exception e) {
                    logger.error("Mysterical error", e);
                    root.put("errors", "Please contact admin, there is some issue with MongoExport");

                }
                templateOverride.process(root, writer);

            }
        });

    }

    private void initializeWeeklySummaryCalls() throws IOException {

        get(new FreemarkerBasedRoute("/last7days", "daily_summary.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                String entity = request.params(":entity");
                Map<String, Object> root = new HashMap<String, Object>();

                root.put("DAY",7);

                Gson gson = new Gson();
                String currentDateStr = ControllerUtilities.getCurrentDateStrInYYYY_MM_DD();
                root.put("endingDate",currentDateStr);
                root.put("summaryType","daily");
                root.put("type","days");

                List<Document> paymentList = kisaanPaymentDAO.getDailySummaryForWeekEnding(currentDateStr);
                root.put("kisaanPaymentList", paymentList);
                List<Document> transactionList = kisaanTransactionDAO.getDailySummaryForWeekEnding(currentDateStr);
                root.put("kisaanTransactionList", transactionList);
                List<Document> list = khareeddarPaymentDAO.getDailySummaryForWeekEnding(currentDateStr);
                root.put("khareeddarPaymentList", list);
                Map<String,Tuple<Document>> joinMap = ControllerUtilities.joinByDate(transactionList, paymentList);
                root.put("joinMap", joinMap);

                Map<String,Tuple<Document>> joinMapKhareeddar = ControllerUtilities.joinByDate(transactionList,list);
                root.put("joinMapKhareeddar", joinMapKhareeddar);

                templateOverride.process(root, writer);
            }
        });

        post(new FreemarkerBasedRoute("/dateRangeSearch", "daily_summary.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                String fromDateStr = StringEscapeUtils.escapeHtml4(request.queryParams("fromDate"));
                String toDateStr = StringEscapeUtils.escapeHtml4(request.queryParams("toDate"));
                String summaryType = StringEscapeUtils.escapeHtml4(request.queryParams("type"));

                Map<String, Object> root = new HashMap<String, Object>();
                root.put("endingDate", toDateStr);
                root.put("type", "range");
                root.put("startDate", fromDateStr);
                root.put("summaryType", summaryType);
                if (StringUtils.isBlank(fromDateStr) || StringUtils.isBlank(toDateStr) || StringUtils.isBlank(summaryType)) {

                    logger.error("Empty fromDate:{} or toDate field:{} or summaryType:{}", fromDateStr, toDateStr, summaryType);
                    root.put(Constants.ERROR, String.format("fromDate:%s or toDate:%s or summaryType:%s, try again: <a href=\"/dateRangeSearch\">link</a>", fromDateStr, toDateStr, summaryType));
                    root.put("entityList", new ArrayList());
                    templateOverride.process(root, writer);
                    return;
                }

                if (summaryType.equals("daily") || summaryType.equals("monthly") || summaryType.equals("yearly")) {

                    Gson gson = new Gson();
                    if (summaryType.equals("daily")) {
                        List<Document> paymentList = kisaanPaymentDAO.getDailySummaryBetweenDates(toDateStr, fromDateStr);
                        root.put("kisaanPaymentList", paymentList);
                        List<Document> transactionList = kisaanTransactionDAO.getDailySummaryBetweenDates(toDateStr, fromDateStr);
                        root.put("kisaanTransactionList", transactionList);
                        List<Document> list = khareeddarPaymentDAO.getDailySummaryBetweenDates(toDateStr, fromDateStr);
                        root.put("khareeddarPaymentList", list);
                        Map<String, Tuple<Document>> joinMap = ControllerUtilities.joinByDate(transactionList, paymentList);
                        root.put("joinMap", joinMap);

                        Map<String, Tuple<Document>> joinMapKhareeddar = ControllerUtilities.joinByDate(transactionList, list);
                        root.put("joinMapKhareeddar", joinMapKhareeddar);
                    } else if (summaryType.equals("monthly")) {
                        List<Document> paymentList = kisaanPaymentDAO.getMonthlySummaryBetweenDates(toDateStr, fromDateStr);
                        root.put("kisaanPaymentList", paymentList);
                        List<Document> transactionList = kisaanTransactionDAO.getMonthlySummaryBetweenDates(toDateStr, fromDateStr);
                        root.put("kisaanTransactionList", transactionList);
                        List<Document> list = khareeddarPaymentDAO.getMonthlySummaryBetweenDates(toDateStr, fromDateStr);
                        root.put("khareeddarPaymentList", list);
                        Map<String, Tuple<Document>> joinMap = ControllerUtilities.joinByDate(transactionList, paymentList);
                        root.put("joinMap", joinMap);

                        Map<String, Tuple<Document>> joinMapKhareeddar = ControllerUtilities.joinByDate(transactionList, list);
                        root.put("joinMapKhareeddar", joinMapKhareeddar);
                    } else {
                        List<Document> paymentList = kisaanPaymentDAO.getYearlySummaryBetweenDates(toDateStr, fromDateStr);
                        root.put("kisaanPaymentList", paymentList);
                        List<Document> transactionList = kisaanTransactionDAO.getYearlySummaryBetweenDates(toDateStr, fromDateStr);
                        root.put("kisaanTransactionList", transactionList);
                        List<Document> list = khareeddarPaymentDAO.getYearlySummaryBetweenDates(toDateStr, fromDateStr);
                        root.put("khareeddarPaymentList", list);
                        Map<String, Tuple<Document>> joinMap = ControllerUtilities.joinByDate(transactionList, paymentList);
                        root.put("joinMap", joinMap);

                        Map<String, Tuple<Document>> joinMapKhareeddar = ControllerUtilities.joinByDate(transactionList, list);
                        root.put("joinMapKhareeddar", joinMapKhareeddar);
                    }

                    templateOverride.process(root, writer);
                } else {
                    logger.error("summaryType:{} not supported", summaryType);
                    root.put(Constants.ERROR, String.format("summaryType:%s not supported, try again: <a href=\"/dateRangeSearch\">link</a>", summaryType));
                    templateOverride.process(root, writer);
                    return;
                }
            }
        });

        get(new FreemarkerBasedRoute("/last7days/:entity", "list_daily_summary.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                String entity = request.params(":entity");
                Map<String, Object> root = new HashMap<String, Object>();

                root.put("ENTITY_NAME",entity);
                root.put("DAY", 7);
                if (ControllerUtilities.verifyEntityInputs(entity, root)) {
                    MongoCollectionDAO mongoDAO = factoryDAO.getDAO(entity);
                    Gson gson = new Gson();
                    String currentDateStr =  ControllerUtilities.getCurrentDateStrInYYYY_MM_DD();
                    root.put("endingDate",currentDateStr);
                    root.put("summaryType","daily");
                    root.put("type","days");

                    List<Document> list = mongoDAO.getDailySummaryForWeekEnding(currentDateStr);
                    root.put("entityList", list);
                    templateOverride.process(root, writer);
                    return;
                } else {
                    logger.error("Some issue in processing this request, most like entity input:{} is bad", entity);
                    root.put("error","Some issue in processing this request, most like entity input:"+entity+" is bad");
                    root.put("entityList", new ArrayList());
                    templateOverride.process(root, writer);

                }
            }
        });

        get(new FreemarkerBasedRoute("/last7days/:entity/:toDate", "list_daily_summary.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                String entity = request.params(":entity");
                String toDateStr = request.params(":toDate");
                Map<String, Object> root = new HashMap<String, Object>();

                root.put("ENTITY_NAME",entity);
                root.put("DAY",7);
                if (ControllerUtilities.verifyDateInFormat(toDateStr) && ControllerUtilities.verifyEntityInputs(entity,root)) {
                    MongoCollectionDAO mongoDAO = factoryDAO.getDAO(entity);
                    Gson gson = new Gson();
                    root.put("endingDate",toDateStr);
                    root.put("summaryType","daily");
                    root.put("type","days");
                    List<Document> list = mongoDAO.getDailySummaryForWeekEnding(toDateStr);
                    root.put("entityList", list);
                    templateOverride.process(root, writer);
                    return;
                } else {
                    logger.error("Some issue in processing this request, most like entity input:{} is bad", entity);
                    String error = null;
                    StringBuffer sb = new StringBuffer();
                    if(root.get("error")!=null) {
                        sb.append(root.get("error"));
                        sb.append("<br>");
                    }
                    sb.append("Some issue in processing this request, most like entity input:"+entity+" is bad or date input:"+toDateStr+" is bad");
                    root.put("error",sb.toString());
                    root.put("entityList", new ArrayList());
                    templateOverride.process(root, writer);

                }
            }
        });


        get(new FreemarkerBasedRoute("/last30days", "daily_summary.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                String entity = request.params(":entity");
                Map<String, Object> root = new HashMap<String, Object>();

                root.put("DAY",30);

                Gson gson = new Gson();
                String currentDateStr =  ControllerUtilities.getCurrentDateStrInYYYY_MM_DD();
                root.put("endingDate",currentDateStr);
                root.put("summaryType","daily");
                root.put("type","days");

                List<Document> paymentList = kisaanPaymentDAO.getDailySummaryFor30daysEnding(currentDateStr);
                root.put("kisaanPaymentList", paymentList);
                List<Document> transactionList = kisaanTransactionDAO.getDailySummaryFor30daysEnding(currentDateStr);
                root.put("kisaanTransactionList", transactionList);
                List<Document> list = khareeddarPaymentDAO.getDailySummaryFor30daysEnding(currentDateStr);
                root.put("khareeddarPaymentList", list);
                Map<String,Tuple<Document>> joinMap = ControllerUtilities.joinByDate(transactionList, paymentList);
                root.put("joinMap", joinMap);
                Map<String,Tuple<Document>> joinMapKhareeddar = ControllerUtilities.joinByDate(transactionList,list);
                root.put("joinMapKhareeddar", joinMapKhareeddar);
                templateOverride.process(root, writer);
                return;
            }
        });

        get(new FreemarkerBasedRoute("/last30days/:entity", "list_daily_summary.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                String entity = request.params(":entity");
                Map<String, Object> root = new HashMap<String, Object>();

                root.put("ENTITY_NAME", entity);
                root.put("DAY", 30);
                if (ControllerUtilities.verifyEntityInputs(entity, root)) {
                    MongoCollectionDAO mongoDAO = factoryDAO.getDAO(entity);
                    Gson gson = new Gson();
                    String currentDateStr = ControllerUtilities.getCurrentDateStrInYYYY_MM_DD();
                    root.put("endingDate", currentDateStr);
                    root.put("summaryType", "daily");
                    root.put("type", "days");
                    List<Document> list = mongoDAO.getDailySummaryFor30daysEnding(currentDateStr);
                    root.put("entityList", list);
                    templateOverride.process(root, writer);
                    return;
                } else {
                    logger.error("Some issue in processing this request, most like entity input:{} is bad", entity);
                    root.put("error", "Some issue in processing this request, most like entity input:" + entity + " is bad");
                    root.put("entityList", new ArrayList());
                    templateOverride.process(root, writer);

                }
            }
        });

        get(new FreemarkerBasedRoute("/last30days/:entity/:toDate", "list_daily_summary.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                String entity = request.params(":entity");
                String toDateStr = request.params(":toDate");
                Map<String, Object> root = new HashMap<String, Object>();

                root.put("ENTITY_NAME",entity);
                root.put("DAY",30);
                if (ControllerUtilities.verifyDateInFormat(toDateStr) && ControllerUtilities.verifyEntityInputs(entity,root)) {
                    MongoCollectionDAO mongoDAO = factoryDAO.getDAO(entity);
                    Gson gson = new Gson();
                    root.put("endingDate",toDateStr);
                    root.put("summaryType","daily");
                    root.put("type", "days");
                    List<Document> list = mongoDAO.getDailySummaryFor30daysEnding(toDateStr);
                    root.put("entityList", list);
                    templateOverride.process(root, writer);
                    return;
                } else {
                    logger.error("Some issue in processing this request, most like entity input:{} is bad", entity);
                    String error = null;
                    StringBuffer sb = new StringBuffer();
                    if(root.get("error")!=null) {
                        sb.append(root.get("error"));
                        sb.append("<br>");
                    }
                    sb.append("Some issue in processing this request, most like entity input:"+entity+" is bad or date input:"+toDateStr+" is bad");
                    root.put("error", sb.toString());
                    root.put("entityList", new ArrayList());
                    templateOverride.process(root, writer);

                }
            }
        });

        get(new FreemarkerBasedRoute("/quarterly", "daily_summary.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                String entity = request.params(":entity");
                Map<String, Object> root = new HashMap<String, Object>();

                root.put("DAY", 3);
                Gson gson = new Gson();
                String currentDateStr = ControllerUtilities.getCurrentDateStrInYYYY_MM_DD();
                root.put("endingDate", currentDateStr);
                root.put("summaryType", "quarterly");
                root.put("type", "months");
                List<Document> paymentList = kisaanPaymentDAO.getMonthlySummary(currentDateStr, -3);
                root.put("kisaanPaymentList", paymentList);
                List<Document> transactionList = kisaanTransactionDAO.getMonthlySummary(currentDateStr, -3);
                root.put("kisaanTransactionList", transactionList);
                List<Document> list = khareeddarPaymentDAO.getMonthlySummary(currentDateStr, -3);
                root.put("khareeddarPaymentList", list);
                Map<String, Tuple<Document>> joinMap = ControllerUtilities.joinByDate(transactionList, paymentList);
                root.put("joinMap", joinMap);
                Map<String, Tuple<Document>> joinMapKhareeddar = ControllerUtilities.joinByDate(transactionList, list);
                logger.info("Khareeddar::" + gson.toJson(joinMapKhareeddar));
                root.put("joinMapKhareeddar", joinMapKhareeddar);
                templateOverride.process(root, writer);
                return;
            }
        });


        get(new FreemarkerBasedRoute("/quarterly/:entity", "list_daily_summary.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                String entity = request.params(":entity");
                Map<String, Object> root = new HashMap<String, Object>();

                root.put("ENTITY_NAME", entity);
                root.put("DAY", 3);
                if (ControllerUtilities.verifyEntityInputs(entity, root)) {
                    MongoCollectionDAO mongoDAO = factoryDAO.getDAO(entity);
                    Gson gson = new Gson();
                    String currentDateStr = ControllerUtilities.getCurrentDateStrInYYYY_MM_DD();
                    root.put("endingDate", currentDateStr);
                    root.put("summaryType", "quarterly");
                    root.put("type", "months");
                    List<Document> list = mongoDAO.getMonthlySummary(currentDateStr, -3);
                    root.put("entityList", list);
                    templateOverride.process(root, writer);
                    return;
                } else {
                    logger.error("Some issue in processing this request, most like entity input:{} is bad", entity);
                    root.put("error", "Some issue in processing this request, most like entity input:" + entity + " is bad");
                    root.put("entityList", new ArrayList());
                    templateOverride.process(root, writer);

                }
            }
        });

        get(new FreemarkerBasedRoute("/quarterly/:entity/:toDate", "list_daily_summary.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                String entity = request.params(":entity");
                String toDateStr = request.params(":toDate");
                Map<String, Object> root = new HashMap<String, Object>();

                root.put("ENTITY_NAME", entity);
                root.put("DAY", 3);
                if (ControllerUtilities.verifyDateInFormat(toDateStr) && ControllerUtilities.verifyEntityInputs(entity, root)) {
                    MongoCollectionDAO mongoDAO = factoryDAO.getDAO(entity);
                    Gson gson = new Gson();
                    root.put("endingDate", toDateStr);
                    root.put("summaryType", "quarterly");
                    root.put("type", "months");
                    List<Document> list = mongoDAO.getMonthlySummary(toDateStr, -3);
                    root.put("entityList", list);
                    templateOverride.process(root, writer);
                    return;
                } else {
                    logger.error("Some issue in processing this request, most like entity input:{} is bad", entity);
                    String error = null;
                    StringBuffer sb = new StringBuffer();
                    if (root.get("error") != null) {
                        sb.append(root.get("error"));
                        sb.append("<br>");
                    }
                    sb.append("Some issue in processing this request, most like entity input:" + entity + " is bad or date input:" + toDateStr + " is bad");
                    root.put("error", sb.toString());
                    root.put("entityList", new ArrayList());
                    templateOverride.process(root, writer);

                }
            }
        });

        get(new FreemarkerBasedRoute("/yearly", "daily_summary.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                String entity = request.params(":entity");
                Map<String, Object> root = new HashMap<String, Object>();

                root.put("DAY",3);
                Gson gson = new Gson();
                String currentDateStr =  ControllerUtilities.getCurrentDateStrInYYYY_MM_DD();
                root.put("endingDate",currentDateStr);
                root.put("summaryType","yearly");
                root.put("type","years");
                List<Document> paymentList = kisaanPaymentDAO.getYearlySummary(currentDateStr, -3);
                root.put("kisaanPaymentList", paymentList);
                List<Document> transactionList = kisaanTransactionDAO.getYearlySummary(currentDateStr, -3);
                root.put("kisaanTransactionList", transactionList);
                List<Document> list = khareeddarPaymentDAO.getYearlySummary(currentDateStr, -3);
                root.put("khareeddarPaymentList", list);
                Map<String,Tuple<Document>> joinMap = ControllerUtilities.joinByDate(transactionList, paymentList);
                root.put("joinMap", joinMap);
                Map<String,Tuple<Document>> joinMapKhareeddar = ControllerUtilities.joinByDate(transactionList,list);
                logger.info("Khareeddar::"+gson.toJson(joinMapKhareeddar));
                root.put("joinMapKhareeddar", joinMapKhareeddar);
                templateOverride.process(root, writer);
                return;
            }
        });


        get(new FreemarkerBasedRoute("/yearly/:entity", "list_daily_summary.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                String entity = request.params(":entity");
                Map<String, Object> root = new HashMap<String, Object>();

                root.put("ENTITY_NAME", entity);
                root.put("DAY", 3);
                if (ControllerUtilities.verifyEntityInputs(entity, root)) {
                    MongoCollectionDAO mongoDAO = factoryDAO.getDAO(entity);
                    Gson gson = new Gson();
                    String currentDateStr = ControllerUtilities.getCurrentDateStrInYYYY_MM_DD();
                    root.put("endingDate", currentDateStr);
                    root.put("summaryType", "yearly");
                    root.put("type", "years");
                    List<Document> list = mongoDAO.getYearlySummary(currentDateStr, -3);
                    root.put("entityList", list);
                    templateOverride.process(root, writer);
                    return;
                } else {
                    logger.error("Some issue in processing this request, most like entity input:{} is bad", entity);
                    root.put("error", "Some issue in processing this request, most like entity input:" + entity + " is bad");
                    root.put("entityList", new ArrayList());
                    templateOverride.process(root, writer);

                }
            }
        });

        get(new FreemarkerBasedRoute("/yearly/:entity/:toDate", "list_daily_summary.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                String entity = request.params(":entity");
                String toDateStr = request.params(":toDate");
                Map<String, Object> root = new HashMap<String, Object>();

                root.put("ENTITY_NAME", entity);
                root.put("DAY", 3);
                if (ControllerUtilities.verifyDateInFormat(toDateStr) && ControllerUtilities.verifyEntityInputs(entity, root)) {
                    MongoCollectionDAO mongoDAO = factoryDAO.getDAO(entity);
                    Gson gson = new Gson();
                    root.put("endingDate", toDateStr);
                    root.put("summaryType", "yearly");
                    root.put("type", "years");
                    List<Document> list = mongoDAO.getYearlySummary(toDateStr, -3);
                    root.put("entityList", list);
                    templateOverride.process(root, writer);
                    return;
                } else {
                    logger.error("Some issue in processing this request, most like entity input:{} is bad", entity);
                    String error = null;
                    StringBuffer sb = new StringBuffer();
                    if (root.get("error") != null) {
                        sb.append(root.get("error"));
                        sb.append("<br>");
                    }
                    sb.append("Some issue in processing this request, most like entity input:" + entity + " is bad or date input:" + toDateStr + " is bad");
                    root.put("error", sb.toString());
                    root.put("entityList", new ArrayList());
                    templateOverride.process(root, writer);

                }
            }
        });

        /*

        get(new Route("/last30days/:entity") {
            @Override
            public Object handle(Request request, Response response) {
                String entity = request.params(":entity");
                Map<String, Object> root = new HashMap<String, Object>();
                root.put("ENTITY_NAME",entity);
                root.put("DAY",30);

                if (ControllerUtilities.verifyEntityInputs(entity, root)) {
                    MongoCollectionDAO mongoDAO = factoryDAO.getDAO(entity);
                    Gson gson = new Gson();
                    Date currentDate = new Date();
                    DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_YYYY_MM_DD);
                    return gson.toJson(mongoDAO.getDailySummaryFor30daysEnding(dateFormat.format(currentDate)));
                } else {
                    logger.error("Some issue in processing this request, most like entity input:{} is bad", entity);
                    return "[]";

                }
            }
        });

        get(new Route("/last30days/:entity/:toDate") {
            @Override
            public Object handle(Request request, Response response) {
                String entity = request.params(":entity");
                String toDateStr = request.params(":toDate");
                Map<String,Object> root = new HashMap<String, Object>();
                root.put("ENTITY_NAME",entity);
                root.put("DAY",30);
                root.put("toDate",toDateStr);

                if(ControllerUtilities.verifyDateInFormat(toDateStr) && ControllerUtilities.verifyEntityInputs(entity,root)) {
                    MongoCollectionDAO mongoDAO = factoryDAO.getDAO(entity);
                    Gson gson = new Gson();
                    return gson.toJson(mongoDAO.getDailySummaryFor30daysEnding(toDateStr));
                } else {
                    logger.error("Some issue in processing this request, most like entity input:{} is bad or input Date:{} is not YYYY-MM-DD format",entity,toDateStr);
                    return "[]";

                }
            }


        });
        */
    }


    private void initializeStandardCalls() throws IOException {

        //This only works for ItemModel because of the check
        //We can expand this to collections for which we have a field called as value;
        get(new Route("/search/:entity/:searchTerm") {
            @Override
            public Object handle(Request request, Response response) {
                String entity = request.params(":entity");
                String term = request.params(":searchTerm");
                //if (StringUtils.isBlank(entity) || StringUtils.isBlank(term) || !entity.equals(Constants.ENTITY_ITEM)) {
                if (StringUtils.isBlank(entity) || StringUtils.isBlank(term)) {
                    return  "[]";
                }

                Map<String,Object> root = new HashMap<String, Object>();

                if(ControllerUtilities.verifyEntityInputs(entity,root)) {
                    MongoCollectionDAO mongoDAO = factoryDAO.getDAO(entity);
                    Gson gson = new Gson();
                    return gson.toJson(mongoDAO.searchValueFieldWithLimit(term, 30));
                } else {
                    logger.error("Some issue in processing this request, most like entity input is bad, input:{}",entity);
                    return "[]";

                }
            }
        });

        get(new Route("/searchKeys/:entity/:searchTerm") {
            @Override
            public Object handle(Request request, Response response) {
                String entity = request.params(":entity");
                String term = request.params(":searchTerm");
                if (StringUtils.isBlank(entity) || StringUtils.isBlank(term)) {
                    return  "[]";
                }

                Map<String,Object> root = new HashMap<String, Object>();

                if(ControllerUtilities.verifyEntityInputs(entity,root)) {
                    MongoCollectionDAO mongoDAO = factoryDAO.getDAO(entity);
                    Gson gson = new Gson();
                    return gson.toJson(mongoDAO.searchKeysWithLimit(term, 30));
                } else {
                    logger.error("Some issue in processing this request, most like entity input is bad, input:{}",entity);
                    return "[]";

                }
            }
        });


        get(new Route("/listKeys/:entity") {
            @Override
            public Object handle(Request request, Response response) {
                String entity = request.params(":entity");

                Map<String,Object> root = new HashMap<String, Object>();

                if(ControllerUtilities.verifyEntityInputs(entity,root)) {
                    MongoCollectionDAO mongoDAO = factoryDAO.getDAO(entity);
                    Gson gson = new Gson();
                    return gson.toJson(mongoDAO.listKeys());
                } else {
                    logger.error("Some issue in processing this request, most like entity input is bad, input:{}", entity);
                    return "[]";

                }
            }
        });

        get(new FreemarkerBasedRoute("/dateRangeSearch/:entity", "dateRange.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {

                /*
                String username = sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request));
                if(username==null || !username.equals(Constants.ADMIN_USER)) {
                    halt(401,"This request requires privilege");
                }
                */
                String entity = request.params(":entity");
                Map<String,Object> root = new HashMap<String, Object>();

                if(ControllerUtilities.verifyEntityInputs(entity,root)) {
                    MongoCollectionDAO mongoDAO = factoryDAO.getDAO(entity);
                    String entityName = factoryDAO.getEntityString(entity);
                    root.put("ENTITY_NAME", entityName);
                    root.put("entity", entity);
                    templateOverride.process(root, writer);
                    return;
                } else {
                    logger.error("Some issue in processing this request");
                    templateOverride.process(root, writer);
                    return;
                }
            }
        });

        /*
        Not implemented yet
         */
        get(new FreemarkerBasedRoute("/dateRangeSearch", "dateRange.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {

                Map<String,Object> root = new HashMap<String, Object>();
                templateOverride.process(root, writer);

            }
        });


        get(new FreemarkerBasedRoute("/remove/:entity", "remove.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {

                /*
                String username = sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request));
                if(username==null || !username.equals(Constants.ADMIN_USER)) {
                    halt(401,"This request requires privilege");
                }
                */
                String entity = request.params(":entity");
                Map<String,Object> root = new HashMap<String, Object>();

                if(ControllerUtilities.verifyEntityInputs(entity,root)) {
                    MongoCollectionDAO mongoDAO = factoryDAO.getDAO(entity);
                    String entityName = factoryDAO.getEntityString(entity);
                    root.put("ENTITY_NAME", entityName);
                    root.put("entity", entity);
                    templateOverride.process(root, writer);
                    return;
                } else {
                    logger.error("Some issue in processing this request");
                    templateOverride.process(root, writer);
                    return;
                }
            }
        });



        get(new FreemarkerBasedRoute("/remove/:entity/:entityValue", "remove.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {

                /*
                String username = sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request));
                if(username==null || !username.equals(Constants.ADMIN_USER)) {
                    halt(401,"This request requires privilege");
                }
                */
                String entity = request.params(":entity");
                String entityValue = request.params(":entityValue");
                Map<String,Object> root = new HashMap<String, Object>();

                root.put("entity", entity);
                root.put("entityValue", entityValue);
                if (StringUtils.isNotBlank(entityValue) && ControllerUtilities.verifyEntityInputs(entity, root)) {
                    MongoCollectionDAO mongoDAO = factoryDAO.getDAO(entity);
                    String entityName = factoryDAO.getEntityString(entity);
                    root.put("ENTITY_NAME", entityName);
                    mongoDAO.remove(entityValue);
                    root.put("success", "true");

                    templateOverride.process(root, writer);
                    return;
                } else {
                    logger.error("Some issue in processing this request");
                    root.put(Constants.ERROR, "Error in processing removal for key: " + entityValue + ", for entity:" + entity);
                    templateOverride.process(root, writer);
                    return;
                }
            }
        });

        post(new FreemarkerBasedRoute("/remove", "remove.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                Map<String, Object> root = new HashMap<String, Object>();
                String entity = StringEscapeUtils.escapeHtml4(request.queryParams("entity"));
                String entityValue = StringEscapeUtils.escapeHtml4(request.queryParams("entityValue"));

                root.put("entity", entity);
                root.put("entityValue", entityValue);
                if (StringUtils.isNotBlank(entityValue) && ControllerUtilities.verifyEntityInputs(entity, root)) {
                    MongoCollectionDAO mongoDAO = factoryDAO.getDAO(entity);
                    String entityName = factoryDAO.getEntityString(entity);
                    root.put("ENTITY_NAME", entityName);
                    //ModelObj entity = mongoDAO.getBasedOnUniqueKey(entityValue);
                    //root.put("entity", entity);
                    mongoDAO.remove(entity);
                    root.put("success", "true");
                    //root.put("delete", "true");
                    templateOverride.process(root, writer);
                    return;
                } else {
                    logger.error("Some issue in processing this request");
                    templateOverride.process(root, writer);
                    return;
                }
            }
        });


        get(new FreemarkerBasedRoute("/searchEntity/:entity", "search_entity.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {

                /*
                String username = sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request));
                if(username==null || !username.equals(Constants.ADMIN_USER)) {
                    halt(401,"This request requires privilege");
                }
                */
                String entity = request.params(":entity");
                Map<String, Object> root = new HashMap<String, Object>();

                if (ControllerUtilities.verifyEntityInputs(entity, root)) {
                    MongoCollectionDAO mongoDAO = factoryDAO.getDAO(entity);
                    String entityName = factoryDAO.getEntityString(entity);
                    root.put("ENTITY_NAME", entityName);
                    root.put("entity", entity);
                    templateOverride.process(root, writer);
                    return;
                } else {
                    logger.error("Input Entity {} is not in the system",entity);
                    root.put(Constants.ERROR,"Input Entity:"+entity+", is not supported");
                    templateOverride.process(root, writer);
                    return;
                }
            }
        });

        post(new FreemarkerBasedRoute("/searchEntity", "list.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                Map<String, Object> root = new HashMap<String, Object>();
                String entity = StringEscapeUtils.escapeHtml4(request.queryParams("entity"));
                String entityValue = StringEscapeUtils.escapeHtml4(request.queryParams("entityValue"));
                int limit = Constants.SEARCH_RESULTS_SIZE;
                root.put("limit",limit);
                root.put("entity", entity);
                root.put("entityValue", entityValue);
                if (StringUtils.isNotBlank(entityValue) && ControllerUtilities.verifyEntityInputs(entity, root)) {
                    MongoCollectionDAO mongoDAO = factoryDAO.getDAO(entity);
                    String entityName = factoryDAO.getEntityString(entity);
                    root.put("ENTITY_NAME", entityName);
                    List<ModelObj> list= mongoDAO.search(entityValue,limit);
                    root.put("limit",limit);
                    root.put("success", "true");
                    root.put("entityList",list);
                    root.put("ENTITY_NAME", entityName);
                    root.put("entity", entity);
                    root.put("search", true);
                    root.put("searchString", entityValue);
                    if(entity.equals("kisaan")) {

                        KisaanTransactionDAO kisaanTransactionDAO = SingletonManagerDAO.getInstance().getKisaanTransactionDAO();
                        List<Document> transactionList = kisaanTransactionDAO.getSummaryByEntity(entity);
                        Gson gson = new Gson();

                        KisaanPaymentDAO kisaanPaymentDAO = SingletonManagerDAO.getInstance().getKisaanPaymentDAO();
                        List<Document> paymentList = kisaanPaymentDAO.getSummaryByEntity(entity);

                        Map<String, Tuple<Document>> joinMap = ControllerUtilities.joinByIdKey(transactionList, paymentList);


                        List<Document> entityRecs = ControllerUtilities.convertJoinedMapDocumentList(joinMap);

                        Map<String, Tuple<Document>> joinMapEntityRecs = ControllerUtilities.joinByKey(list, entityRecs);
                        root.put("joinMap", joinMapEntityRecs);
                    }

                    if(entity.equals("khareeddar")) {
                        //Gson gson = new Gson();

                        KisaanTransactionDAO kisaanTransactionDAO = SingletonManagerDAO.getInstance().getKisaanTransactionDAO();
                        List<Document> transactionList = kisaanTransactionDAO.getSummaryByEntity(entity);

                        KhareeddarPaymentDAO khareeddarPaymentDAO = SingletonManagerDAO.getInstance().getKhareeddarPaymentDAO();
                        List<Document> paymentList = khareeddarPaymentDAO.getSummaryByEntity(entity);

                        Map<String, Tuple<Document>> joinMap = ControllerUtilities.joinByIdKey(transactionList, paymentList);

                        List<Document> entityRecs = ControllerUtilities.convertJoinedMapDocumentList(joinMap);

                        Map<String, Tuple<Document>> joinMapEntityRecs = ControllerUtilities.joinByKey(list, entityRecs);
                        root.put("joinMap", joinMapEntityRecs);
                    }

                    templateOverride.process(root, writer);
                    return;
                } else {
                    logger.error("Some issue in processing this request");
                    templateOverride.process(root, writer);
                    return;
                }
            }
        });



        post(new FreemarkerBasedRoute("/list", "list_daily_summary.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                String entity = StringEscapeUtils.escapeHtml4(request.queryParams("entity"));
                String fromDateStr = StringEscapeUtils.escapeHtml4(request.queryParams("fromDate"));
                String toDateStr = StringEscapeUtils.escapeHtml4(request.queryParams("toDate"));
                String summaryType = StringEscapeUtils.escapeHtml4(request.queryParams("type"));

                Map<String, Object> root = new HashMap<String, Object>();
                root.put("endingDate",toDateStr);
                root.put("type","range");
                root.put("startDate",fromDateStr);
                root.put("summaryType",summaryType);
                if (StringUtils.isBlank(entity) ||StringUtils.isBlank(summaryType)  || StringUtils.isBlank(fromDateStr) || StringUtils.isBlank(toDateStr)) {
                    logger.error("Empty entity:{} or fromDate:{} or toDate field:{} or summaryType:{}", entity, fromDateStr,toDateStr,summaryType);
                    //logger.error("Empty fromDate:{} or toDate field:{}", fromDateStr, toDateStr);
                    root.put(Constants.ERROR, String.format("Empty entity:%s or fromDate:%s or toDate:%s or summaryType:%s, try again: <a href=\"/dateRangeSearch/%s\">link</a>", entity, fromDateStr,toDateStr,entity,summaryType));

                    root.put("entityList", new ArrayList());
                    root.put("ENTITY_NAME", entity);
                    root.put("entity", entity);
                    templateOverride.process(root, writer);
                    return;
                }

                int limitInt = 30;

                if (ControllerUtilities.verifyEntityInputs(entity, root)) {
                    MongoCollectionDAO mongoDAO = factoryDAO.getDAO(entity);
                    String entityName = factoryDAO.getEntityString(entity);
                    if(summaryType.equals("daily") || summaryType.equals("monthly") || summaryType.equals("yearly")) {
                        List<ModelObj> list = null;
                        if(summaryType.equals("daily")) {
                            list = mongoDAO.getDailySummaryBetweenDates(toDateStr, fromDateStr);
                        } else if(summaryType.equals("monthly")) {
                            list = mongoDAO.getMonthlySummaryBetweenDates(toDateStr, fromDateStr);
                        } else {
                            list = mongoDAO.getYearlySummaryBetweenDates(toDateStr, fromDateStr);

                        }
                        root.put("entityList", list);
                        root.put("ENTITY_NAME", entityName);
                        root.put("entity", entity);

                        templateOverride.process(root, writer);
                    } else {
                        logger.error("summaryType:{} not supported", summaryType);
                        root.put(Constants.ERROR, String.format("summaryType:%s not supported, try again: <a href=\"/dateRangeSearch\">link</a>", summaryType));
                        templateOverride.process(root, writer);
                        return;
                    }
                    return;
                } else {
                    logger.error("Some issue in processing this request");
                    root.put("entityList", new ArrayList());
                    root.put(Constants.ERROR, "Some issue in processing this request");
                    templateOverride.process(root, writer);
                    return;
                }
            }
        });

        get(new FreemarkerBasedRoute("/list/:entity/:limit", "list.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                String entity = request.params(":entity");
                String limit = request.params(":limit");
                Map<String, Object> root = new HashMap<String, Object>();
                if(StringUtils.isBlank(entity) || StringUtils.isBlank(limit)) {
                    logger.error("Empty entity:{} or limit field:{}", entity, limit);
                    root.put(Constants.ERROR, String.format("Empty entity:%s or limit field:%s", entity, limit));
                    root.put("entityList", new ArrayList());
                    root.put("ENTITY_NAME",entity);

                    root.put("entity",entity);
                    templateOverride.process(root, writer);
                    return;
                }
                int limitInt = 30;
                try {
                    limitInt = Integer.parseInt(limit);
                } catch (Exception e) {
                    logger.error("Limit is expected to be an integer, input:{}", limit);
                    root.put(Constants.ERROR,String.format("Limit is expected to be an integer, input:%s",limit));
                    root.put("entityList", new ArrayList());
                    root.put("ENTITY_NAME",entity);
                    root.put("entity", entity);

                    templateOverride.process(root, writer);
                    return;

                }
                if(limitInt<0) {
                    limitInt = 30;
                }
                if(limitInt>Constants.MAX_ROWS) {
                    limitInt = Constants.MAX_ROWS;
                }


                if (ControllerUtilities.verifyEntityInputs(entity, root)) {
                    MongoCollectionDAO mongoDAO = factoryDAO.getDAO(entity);
                    String entityName = factoryDAO.getEntityString(entity);
                    List<ModelObj> list = mongoDAO.list(limitInt);
                    root.put("entityList", list);
                    root.put("ENTITY_NAME", entityName);
                    root.put("entity", entity);
                    if(entity.equals("kisaan")) {

                        KisaanTransactionDAO kisaanTransactionDAO = SingletonManagerDAO.getInstance().getKisaanTransactionDAO();
                        List<Document> transactionList = kisaanTransactionDAO.getSummaryByEntity(entity);
                        Gson gson = new Gson();

                        KisaanPaymentDAO kisaanPaymentDAO = SingletonManagerDAO.getInstance().getKisaanPaymentDAO();
                        List<Document> paymentList = kisaanPaymentDAO.getSummaryByEntity(entity);

                        Map<String, Tuple<Document>> joinMap = ControllerUtilities.joinByIdKey(transactionList, paymentList);


                        List<Document> entityRecs = ControllerUtilities.convertJoinedMapDocumentList(joinMap);

                        Map<String, Tuple<Document>> joinMapEntityRecs = ControllerUtilities.joinByKey(list, entityRecs);
                        root.put("joinMap", joinMapEntityRecs);
                    }

                    if(entity.equals("khareeddar")) {
                        //Gson gson = new Gson();

                        KisaanTransactionDAO kisaanTransactionDAO = SingletonManagerDAO.getInstance().getKisaanTransactionDAO();
                        List<Document> transactionList = kisaanTransactionDAO.getSummaryByEntity(entity);

                        KhareeddarPaymentDAO khareeddarPaymentDAO = SingletonManagerDAO.getInstance().getKhareeddarPaymentDAO();
                        List<Document> paymentList = khareeddarPaymentDAO.getSummaryByEntity(entity);

                        Map<String, Tuple<Document>> joinMap = ControllerUtilities.joinByIdKey(transactionList, paymentList);

                        List<Document> entityRecs = ControllerUtilities.convertJoinedMapDocumentList(joinMap);

                        Map<String, Tuple<Document>> joinMapEntityRecs = ControllerUtilities.joinByKey(list, entityRecs);
                        root.put("joinMap", joinMapEntityRecs);
                    }


                    templateOverride.process(root, writer);
                    return;
                } else {
                    logger.error("Some issue in processing this request");
                    root.put("entityList", new ArrayList());
                    templateOverride.process(root, writer);
                    return;
                }
            }
        });

        get(new FreemarkerBasedRoute("/fullList/:entity", "list.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                String entity = request.params(":entity");
                Map<String,Object> root = new HashMap<String, Object>();

                if(ControllerUtilities.verifyEntityInputs(entity,root)) {
                    MongoCollectionDAO mongoDAO = factoryDAO.getDAO(entity);
                    String entityName = factoryDAO.getEntityString(entity);
                    List<ModelObj> list = mongoDAO.list();
                    root.put("entityList", list);
                    root.put("ENTITY_NAME", entityName);
                    root.put("entity", entity);
                    if(entity.equals("kisaan")) {

                        System.out.println("HERE");
                        KisaanTransactionDAO kisaanTransactionDAO = SingletonManagerDAO.getInstance().getKisaanTransactionDAO();
                        List<Document> transactionList = kisaanTransactionDAO.getSummaryByEntity(entity);
                        Gson gson = new Gson();
                        KisaanPaymentDAO kisaanPaymentDAO = SingletonManagerDAO.getInstance().getKisaanPaymentDAO();
                        List<Document> paymentList = kisaanPaymentDAO.getSummaryByEntity(entity);

                        Map<String, Tuple<Document>> joinMap = ControllerUtilities.joinByIdKey(transactionList, paymentList);
                        List<Document> entityRecs = ControllerUtilities.convertJoinedMapDocumentList(joinMap);

                        Map<String, Tuple<Document>> joinMapEntityRecs = ControllerUtilities.joinByKey(list, entityRecs);
                        root.put("joinMap", joinMapEntityRecs);

                    }

                    if(entity.equals("khareeddar")) {
                        //Gson gson = new Gson();

                        KisaanTransactionDAO kisaanTransactionDAO = SingletonManagerDAO.getInstance().getKisaanTransactionDAO();
                        List<Document> transactionList = kisaanTransactionDAO.getSummaryByEntity(entity);

                        KhareeddarPaymentDAO khareeddarPaymentDAO = SingletonManagerDAO.getInstance().getKhareeddarPaymentDAO();
                        List<Document> paymentList = khareeddarPaymentDAO.getSummaryByEntity(entity);

                        Map<String, Tuple<Document>> joinMap = ControllerUtilities.joinByIdKey(transactionList, paymentList);

                        List<Document> entityRecs = ControllerUtilities.convertJoinedMapDocumentList(joinMap);

                        Map<String, Tuple<Document>> joinMapEntityRecs = ControllerUtilities.joinByKey(list, entityRecs);
                        root.put("joinMap", joinMapEntityRecs);
                    }


                    templateOverride.process(root, writer);
                    return;
                } else {
                    logger.error("Some issue in processing this request");
                    root.put("entityList",new ArrayList());
                    templateOverride.process(root, writer);
                    return;
                }
            }
        });

        post(new FreemarkerBasedRoute("/search") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {

                Map<String,Object> root = new HashMap<String, Object>();

                this.template = cfg.getTemplate("list_default.ftl");
                this.templateOverride.setTemplate(this.template);

                String entity= StringEscapeUtils.escapeHtml4(request.queryParams("entity"));
                String query = StringEscapeUtils.escapeHtml4(request.queryParams("query"));
                int limit = Constants.SEARCH_RESULTS_SIZE;
                root.put("entity",entity);
                root.put("query",query);
                root.put("limit",limit);
                root.put("ENTITY_NAME",entity);
                if(StringUtils.isNotBlank(entity) && StringUtils.isNotBlank(query)) {
                    MongoCollectionDAO mongoCollectionDAO = factoryDAO.getDAO(entity);
                    if(mongoCollectionDAO==null) {
                        root.put("entityList",new ArrayList());
                        root.put("errors","Bad Entity input, entity:"+entity);
                        templateOverride.process(root, writer);
                        return;
                    } else {
                        String templateName = "list_"+entity +".ftl";
                        this.template = cfg.getTemplate(templateName);
                        this.templateOverride.setTemplate(this.template);
                        List<Object> list = mongoCollectionDAO.search(query,limit);
                        try {
                            root.put("entityList",list);
                            templateOverride.process(root, writer);
                            return;
                        } catch(Exception e) {
                            logger.error("Bad Template processing for entity:{} ",entity,e);
                            root.put("entityList",new ArrayList());
                            root.put("errors","Bad Template processing for entity:"+entity);

                            templateOverride.process(root, writer);
                            return;
                        }

                    }
                } else {
                    logger.error("Bad request, One of the inputs is missing, entity:{} or query:{}",entity,query);
                    root.put("entityList",new ArrayList());
                    root.put("errors","Bad request, One of the inputs is missing, entity:"+entity+"or query:"+query);
                    templateOverride.process(root, writer);
                }



            }
        });

        post(new FreemarkerBasedRoute("/multiSearch","multi_search.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {

                Map<String,Object> root = new HashMap<String, Object>();
                String query = StringEscapeUtils.escapeHtml4(request.queryParams("query"));
                int limit = Constants.SEARCH_RESULTS_SIZE;
                root.put("query",query);
                root.put("limit",limit);
                logger.info("Multi Search called with:"+query);
                try {
                    if (StringUtils.isNotBlank(query)) {
                        root = ControllerUtilities.multiSearch(query,limit);
                        root.put("query",query);
                        root.put("limit",limit);
                        templateOverride.process(root, writer);
                        return;
                    } else {
                        logger.error("Bad request, Search query is missing,  query:{}", query);
                        root.put("entityList", new ArrayList());
                        root.put("kisaan", new ArrayList());
                        root.put("khareeddar", new ArrayList());
                        root.put("kisaanPayment", new ArrayList());
                        root.put("kisaanTransaction", new ArrayList());
                        root.put("khareeddarPayment", new ArrayList());
                        templateOverride.process(root, writer);
                    }
                } catch(Exception e) {
                    logger.error("Most likely an text index is missing for a collection",e);
                    root.put("query",query);
                    root.put("entityList", new ArrayList());
                    root.put("kisaan", new ArrayList());
                    root.put("khareeddar", new ArrayList());
                    root.put("kisaanPayment", new ArrayList());
                    root.put("kisaanTransaction", new ArrayList());
                    root.put("khareeddarPayment", new ArrayList());
                    templateOverride.process(root, writer);
                }

            }
        });




    }


    private void initializeTransactionItemRoutes() throws IOException {
        // used to display Khareeddar Detail Page



        get(new Route("/listItems") {
            @Override
            public Object handle(Request request, Response response) {
                List<TransactionItem> transactionItemList = transactionItemDAO.list();
                Gson gson = new Gson();
                return gson.toJson(transactionItemList);
            }
        });



    }
    private void initializeLocationRoutes() throws IOException {
        // used to display Khareeddar Detail Page



        get(new Route("/listLocations") {
            @Override
            public Object handle(Request request, Response response) {
                List<Location> locations = locationDAO.list();
                Gson gson = new Gson();
                return gson.toJson(locations);
            }
        });

    }

    private void initializeStandardLoginSignUpRoutes() throws IOException {

        //New

        get(new RelaxedFreemarkerBasedRoute("/login", "login.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                //SimpleHash root = new SimpleHash();
                String username = sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request));
                if (username != null) {
                    response.redirect("/");
                } else {
                    HashMap<String, Object> root = new HashMap<String, Object>();
                    String permalink = request.queryParams("permalink");
                    root.put("username", "");
                    root.put("permalink", permalink);
                    root.put("login", "true");

                    templateOverride.process(root, writer);
                }
            }
        });

        // process output coming from login form. On success redirect folks to the welcome page
        // on failure, just return an error and let them try again.
        post(new RelaxedFreemarkerBasedRoute("/login", "login.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {

                String username = request.queryParams("email");
                String password = request.queryParams("password");
                logger.info("Login: User submitted: " + username);

                Document user = userDAO.validateLogin(username, password);

                if (user != null) {
                    // valid user, let's log them in
                    String sessionID = sessionDAO.startSession(user.get("_id").toString());

                    if (sessionID == null) {
                        response.redirect("/internal_error");
                    } else {
                        // set the cookie for the user's browser
                        String permalink = request.queryParams("permalink");
                        ControllerUtilities.deleteAllCookies(request, response);


                        response.raw().addCookie(new Cookie(ControllerUtilities.getCookieKey(request,"session"), sessionID));

                        if (permalink != null && StringUtils.isNotBlank(permalink)) {
                            response.redirect("/post/" + permalink);
                        } else {
                            response.redirect("/");
                        }
                    }
                } else {
                    HashMap<String, Object> root = new HashMap<String, Object>();
                    root.put("username", StringEscapeUtils.escapeHtml4(username));
                    root.put("password", "");
                    root.put("login_error", "Invalid Login");
                    root.put("error", "Invalid Login");
                    root.put("login", "true");
                    templateOverride.process(root, writer);

                    int p = (int) Math.sqrt((double)10);
                }
            }
        });



        // present signup form for blog
        get(new RelaxedFreemarkerBasedRoute("/signup", "login.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException {

                String permalink = request.queryParams("permalink");
                logger.info("Signup request from user,{}", permalink);
                HashMap<String, Object> root = new HashMap<String, Object>();

                // initialize values for the form.
                root.put("username", "");
                root.put("password", "");
                root.put("email", "");
                root.put("permalink", permalink);
                root.put("signup", "true");

                templateOverride.process(root, writer);
            }
        });

        post(new RelaxedFreemarkerBasedRoute("/signup", "login.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                String email = request.queryParams("email");
                String username = email;
                String password = request.queryParams("password");
                String verify = request.queryParams("password_confirm");
                logger.info("handler for Signup request from user,{}" + username);

                HashMap<String, String> root = new HashMap<String, String>();
                root.put("username", StringEscapeUtils.escapeHtml4(username));
                root.put("email", StringEscapeUtils.escapeHtml4(email));

                if (LoginUtils.validateSignupV2(username, password, verify, email, root)) {
                    // good user
                    logger.info("Signup: Creating user with: " + username + ", password:******** ");
                    if (!userDAO.addUser(username, password, email)) {
                        // duplicate user
                        logger.error("Username already in use, Please choose another, input:{}", username);
                        root.put("error", "Username already in use!!, Please choose another");
                        root.put("signup", "true");
                        templateOverride.process(root, writer);
                    } else {
                        // good user, let's start a session
                        String sessionID = sessionDAO.startSession(username);
                        logger.info("Session ID is" + sessionID);
                        String permalink = request.queryParams("permalink");
                        response.raw().addCookie(new Cookie(ControllerUtilities.getCookieKey(request,"session"), sessionID));
                        if (permalink != null && StringUtils.isNotBlank(permalink)) {
                            /*
                            String cookie = ControllerUtilities.getSessionCookie(request);
                            String username = sessionDAO.findUserNameBySessionId(cookie);
                            */
                            logger.info("permalink:{} present so redirecting", permalink);
                            response.redirect("/post/" + permalink);
                        } else {
                            response.redirect("/");
                        }
                    }
                } else {
                    // bad signup
                    //root.put("username_error", "Validation issues, Please try again respecting each field type or the passwords are not identical");
                    logger.error("User Registration did not validate, user:{}", username);
                    root.put("signup_error", "Validation Issue");
                    root.put("error", "Validation Issue");
                    root.put("signup", "true");
                    templateOverride.process(root, writer);
                }
            }
        });

        // present signup form for blog
        get(new RelaxedFreemarkerBasedRoute("/reset", "reset.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException {

                if (ControllerUtilities.getSessionCookie(request) == null) {
                    //String username = sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request));
                    response.raw().addCookie(new Cookie(ControllerUtilities.getCookieKey(request,"session"), sessionDAO.startVanillaSession()));
                }
                HashMap<String, Object> root = new HashMap<String, Object>();
                // initialize values for the form.
                templateOverride.process(root, writer);
            }
        });

        // present signup form for blog
        post(new RelaxedFreemarkerBasedRoute("/reset", "reset_success.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException {

                if (ControllerUtilities.getSessionCookie(request) == null) {
                    //String username = sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request));
                    response.raw().addCookie(new Cookie(ControllerUtilities.getCookieKey(request, "session"), sessionDAO.startVanillaSession()));
                }
                HashMap<String, Object> root = new HashMap<String, Object>();
                String email = request.queryParams("email");
                root.put("email", email);
                logger.info("Reset request received for email:"+email);
                if(StringUtils.isNotBlank(email) && userDAO.exists(email) && userDAO.updatePassword(email)) {
                    root.put("success", "yes");
                    root.put("defaultPassword",Constants.DEFAULT_PASS);
                    //TODO email admin
                } else {
                    root.put("error","User does not exist, input:"+email);
                    logger.error("Seems like some error in resetting for user:"+email);
                }
                // initialize values for the form.
                templateOverride.process(root, writer);
            }
        });




        // allows the user to logout of the blog
        get(new RelaxedFreemarkerBasedRoute("/logout", "login.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {

                String sessionID = ControllerUtilities.getSessionCookie(request);
                HashMap<String, String> root = new HashMap<String, String>();
                root.put("logout", "true");
                root.put("login", "true");
                if (sessionID != null) {
                    // deletes from session table
                    sessionDAO.endSession(sessionID);
                    ControllerUtilities.deleteAllCookies(request, response);
                    //response.redirect("/login");
                }
                templateOverride.process(root, writer);
            }
        });


    }

    private void initializeSearchRoutes() throws IOException {

        // used to display actual blog post detail page
        post(new FreemarkerBasedRoute("/search", "search.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                String phrase = StringEscapeUtils.escapeHtml4(request.queryParams("search"));
                //String username = sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request));

                System.out.println("/search: get " + phrase);

                if (phrase.equals("")) {
                    // redisplay page with errors
                    HashMap<String, String> root = new HashMap<String, String>();
                    root.put("errors", "Search is Empty, Go ahead and try the new improved Search");
                    root.put("search", phrase);
                    templateOverride.process(root, writer);
                } else {
                    //Here you need to call Elastic Search
                    response.redirect("/search/" + phrase);
                }
            }
        });

        get(new FreemarkerBasedRoute("/search", "search.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {

                HashMap<String, Object> root = new HashMap<String, Object>();
                String username = sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request));
                root.put("username", username);
                templateOverride.process(root, writer);
            }
        });

        get(new FreemarkerBasedRoute("/search/", "search.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                HashMap<String, Object> root = new HashMap<String, Object>();
                String username = sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request));
                root.put("username", username);
                templateOverride.process(root, writer);
            }
        });


    }

    private void initializeListingRoutes() throws IOException {

        get(new RelaxedFreemarkerBasedRoute("/", "/home.ftl") {
            @Override
            public void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                String username = sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request));
                Map<String,Object> root = new HashMap<String, Object>();
                if (username != null) {
                    root.put("username", username);
                }

                templateOverride.process(root, writer);
            }
        });

        get(new RelaxedFreemarkerBasedRoute("/about", "/static/about.ftl") {
            @Override
            public void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                String username = sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request));
                Map<String,Object> root = new HashMap<String, Object>();
                if (username != null) {
                    root.put("username", username);
                }

                templateOverride.process(root, writer);
            }
        });

        get(new RelaxedFreemarkerBasedRoute("/help", "/static/help.ftl") {
            @Override
            public void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                String username = sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request));
                Map<String,Object> root = new HashMap<String, Object>();
                if (username != null) {
                    root.put("username", username);
                }

                templateOverride.process(root, writer);
            }
        });

        get(new RelaxedFreemarkerBasedRoute("/privacy", "/static/privacy.ftl") {
            @Override
            public void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                String username = sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request));
                Map<String,Object> root = new HashMap<String, Object>();
                if (username != null) {
                    root.put("username", username);
                }

                templateOverride.process(root, writer);
            }
        });

        get(new RelaxedFreemarkerBasedRoute("/tos", "/static/tos.ftl") {
            @Override
            public void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                String username = sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request));
                Map<String,Object> root = new HashMap<String, Object>();
                if (username != null) {
                    root.put("username", username);
                }

                templateOverride.process(root, writer);
            }
        });

        get(new RelaxedFreemarkerBasedRoute("/disclaimer", "/static/disclaimer.ftl") {
            @Override
            public void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                String username = sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request));
                Map<String,Object> root = new HashMap<String, Object>();
                if (username != null) {
                    root.put("username", username);
                }

                templateOverride.process(root, writer);
            }
        });



    }


    private void initializeRoutes() throws IOException {

        get(new Route("/getState") {
            @Override
            public Object handle(Request request, Response response) {
                List<Document> stateDocs =null;//= stateDAO.getAllStates();
                Gson gson = new Gson();
                return gson.toJson(stateDocs);
            }
        });

        post(new Route("/jainVote") {
            @Override
            public Object handle(Request request, Response response) {
                String permalink = request.queryParams("permalink");
                String cookie = ControllerUtilities.getSessionCookie(request);
                String username = sessionDAO.findUserNameBySessionId(cookie);
                Gson gson = new Gson();
                Message message = new Message();
                message.setContents("failure");

                if (permalink == null || username == null || StringUtils.isBlank(permalink)) {
                    logger.info("userName is {} or permalink is {}, so redirecting to signup...", username, permalink);
                    message.setRedirect("/login?permalink=" + permalink);
                    return gson.toJson(message);
                }

                logger.info("user name:" + username + ",/post: get " + permalink);
                try {
                    logger.info("Using Thread based update");
                  //  MONGO_DB_THREAD_POOL.execute(new MongoDbJainUpdateWorker(permalink, username));
                    //ELASTIC_SEARCH_THREAD_POOL.execute(new ElasticSearchWorkerDAO(permalink, username));
                    //askForHelpDAO.updateListingWithJainFoodOption(permalink, username)
                    message.setContents("success");
                } catch (Exception e) {
                    logger.error("Exception, unable to update permalink:{} for user{}", permalink, username, e);
                    message.setRedirect("/login?permalink=" + permalink);
                }
                return gson.toJson(message);
            }
        });





        // tells the user that the URL is dead
        get(new RelaxedFreemarkerBasedRoute("/post_not_found", "/static/post_not_found.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                String username = sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request));
                HashMap<String, Object> root = new HashMap<String, Object>();
                root.put("username", StringEscapeUtils.escapeHtml4(username));
                templateOverride.process(root, writer);
            }
        });

        get(new RelaxedFreemarkerBasedRoute("/post_not_found/:entity/:entityValue", "/static/post_not_found.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                String username = sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request));
                HashMap<String, Object> root = new HashMap<String, Object>();
                root.put("username", StringEscapeUtils.escapeHtml4(username));
                String entityValue = StringEscapeUtils.escapeHtml4(request.params(":entityValue"));
                String entity = StringEscapeUtils.escapeHtml4(request.params(":entity"));
                root.put("entity",entity);
                root.put("entityValue",entityValue);
                templateOverride.process(root, writer);
            }
        });


        // used to process internal errors
        get(new RelaxedFreemarkerBasedRoute("/internal_error", "/static/internal_error.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                HashMap<String, Object> root = new HashMap<String, Object>();
                String username = sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request));
                root.put("username", username);
                root.put("error", "System has encountered an error.");
                templateOverride.process(root, writer);
            }
        });
    }

    /*
    private Configuration createFreemarkerConfiguration() {
        Configuration retVal = new Configuration();
        retVal.setClassForTemplateLoading(BookKeepingController.class, "/freemarker");
        return retVal;
    }
*/

}
