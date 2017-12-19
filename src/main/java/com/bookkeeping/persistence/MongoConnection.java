package com.bookkeeping.persistence;

import com.bookkeeping.constants.Constants;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by chandan on 9/21/2015.
 */
public class MongoConnection {
    static final Logger logger = LoggerFactory.getLogger(MongoConnection.class);

    private static MongoDatabase database = null;
    private static String mongoDatabaseName = null;
    private static MongoClient mongoClient =null;
    private static int mongoPort;
    private static String mongoHost;
    private static String mongoInstallationDir;
    private static boolean isInitialized = false;
    private static String mongoBackupDir;


    private MongoConnection() {
        //logger.info("Starting Application to mongoURI:{}",Constants.MONGO_URI);
        //mongoClient = new MongoClient(new MongoClientURI(Constants.MONGO_URI,mongoPort));
        mongoClient = new MongoClient(mongoHost,mongoPort);
        logger.info("Mongo host:{}, port: {} ",mongoClient.getAddress().getHost(), mongoClient.getAddress().getPort());
        database = mongoClient.getDatabase(mongoDatabaseName);

    }

    public static MongoDatabase getInstance() {
        if(isInitialized) {
            if (database == null) {
                synchronized (MongoConnection.class) {
                    if (database == null) {

                        logger.info("Getting a new Connection for Mongo");
                        new MongoConnection();
                    }
                }
            }
        } else {
            logger.error("MongoConnection not initialized, first call init with port and dbDir, exiting...");
            System.exit(-3);
        }
        return database;
    }

    public static void shutDown() {
        if(mongoClient!=null) {
            mongoClient.close();
        }
    }

    public static void init(int port,String host, String installationDir, String backupDir,String databaseName) {
        mongoPort = port;
        mongoHost = host;
        mongoInstallationDir = installationDir;
        mongoBackupDir = backupDir;
        mongoDatabaseName =  databaseName;
        isInitialized = true;
    }

    public static void specialInit() {
        MongoConnection.init(Constants.MONGO_PORT, Constants.MONGO_HOST, Constants.MONGO_INSTALLATION, Constants.MONGO_DATABASE_BACKUP_DIR_ROOT, Constants.MONGO_DATABASE);
    }

    public static void specialInitWithBackup(String backupDir) {
        MongoConnection.init(Constants.MONGO_PORT, Constants.MONGO_HOST, Constants.MONGO_INSTALLATION, backupDir, Constants.MONGO_DATABASE);
    }

    public static void main(String[] args) {
        MongoConnection.specialInit();
        MongoDatabase db = MongoConnection.getInstance();

    }

    public static String getMongoInstallationDir() {
        return mongoInstallationDir;
    }

    public static int getMongoPort() {
        return mongoPort;
    }

    public static MongoDatabase getDatabase() {
        return database;
    }

    public static MongoClient getMongoClient() {
        return mongoClient;
    }

    public static String getMongoHost() {
        return mongoHost;
    }

    public static String getMongoBackupDir() {
        return mongoBackupDir;
    }

    public static String getMongoDatabaseName() {
        return mongoDatabaseName;
    }
}
