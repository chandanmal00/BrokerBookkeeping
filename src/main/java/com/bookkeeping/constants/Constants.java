package com.bookkeeping.constants;

/**
 * Created by chandan on 9/14/2015.
 */
public class Constants {

    //has privilege to upload pics for listings
    public final static String ADMIN_USER="master@test.com";
    public final static String ADMIN_PASS="admin123";
    public final static String DEFAULT_PASS="test1234";

    public final static String APP_TITLE="Book Keeper";
    public final static String DOMAIN_NAME="maloo.xyz";
    public final static String APP_LINK="BookKeeper";
    public final static String APP_WEBSITE="www.bookkeeper.xyz";
    public final static String APP_WEBSITE_BIZ="Book Keeper";
    public final static String APP_ADDRESS="Sunnyvale, CA";
    public final static String APP_MESSAGE="Jiyo aur Jine Do";



    public final static String MONGO_HOST="localhost";
    public final static int MONGO_PORT=27017;
    public final static String MONGO_URI="mongodb://"+MONGO_HOST;
    public static final String MONGO_DATABASE = "bookkeeping";
    public static final String MONGO_DATABASE_BACKUP_DIR_ROOT = "/local/backup";
    public static final String WINDOWS_MONGO_DATABASE_BACKUP_DIR_ROOT = "D:\\mongo\\backup\\";
    public static final String MONGO_USERS_COLLECTION = "users";
    public static final String MONGO_INSTALLATION = "/Users/chandanmaloo/Downloads/mongodb-osx-x86_64-3.4.5";
    public static final String MONGO_SESSIONS_COLLECTION = "sessions";
    public static final String MONGO_ITEM_SOLD_COLLECTION = "item_sold";

    public final static String ELASTIC_CLUSTER_NAME="elasticsearch";
    public final static String ELASTIC_INDEX_NAME="bookkeeping";
    public final static String ELASTIC_TYPE_NAME="entities";

    public final static int ELASTIC_SEARCH_RESULTS_SIZE = 10;
    public final static int SEARCH_RESULTS_SIZE = 100;

    public final static int ELASTIC_SEARCH_SUGGEST_TIMEOUT = 3000;

    public final static int TRIAL_EDITION_LIMIT = 1000000;


    public final static String INFO_EMAIL = "no-reply@test.com";

    public final static String DEFAULT_EMAIL_LINK_SEND = "http://localhost:8082/newpost";

    public final static int SITE_PORT = 8000;

    public static final String MONGO_KISAAN_COLLECTION = "kisaan";
    public static final String MONGO_KHAREEDDAR_COLLECTION = "khareeddar";
    public static final String MONGO_LOCATION_COLLECTION = "location";
    public static final String MONGO_NATIONAL_IDENTITY_COLLECTION = "national_identity";
    public static final String MONGO_TRANSACTION_ITEM_COLLECTION = "transaction_items";
    public static final String MONGO_TRANSACTION_KISAAN_COLLECTION = "transaction_kisaan";
    public static final String MONGO_KISAAN_PAYMENT_COLLECTION = "payment_kisaan";
    public static final String MONGO_PAYMENT_KHAREEDDAR_COLLECTION = "payment_khareeddar";
    public static final float HAMALI_RATE = 3.75f;
    public static final float MAPARI_RATE = 2.5f;
    public static final float BROKERAGE_RATE = 1.5f; //in percentage


    //unique keys for each collection
    public static final String kisaanUniqueKey = "nickName";
    public static final String khareeddarUniqueKey = "firmName";
    public static final String locationUniqueKey = "place";
    public static final String transactionItemUniqueKey = "name";
    public static final String nationalIdentityUniqueKey = "aadhar";
    public static final String nationalIdentityPanUniqueKey = "pan";

    public static final String UNIQUE_KEY_SEPARATOR = "__";
    public static final String UNIQUE_KEY = "uniqueKey";
    public static final String ID_KEY = "_id";

    public static final String ERROR = "errors";
    public static final String SUCCESS = "success";
    public static final String NO_KEY = "NO_KEY_EXISTS";
    public static final String FAILURE = "failure";

    public static final String ENTITY_NAME = "ENTITY_NAME";
    public static final String ENTITY = "entity";
    public static final String PAYMENTS = "payments";
    public static final String TRANSACTIONS = "transactions";
    public static final String ENTITY_KEY_NAME = "entityKey";
    public static final String ENTITY_KEY_VALUE = "entityValue";
    public static final String ENTITY_KISAAN = "Kisaan";
    public static final String ENTITY_TRANSACTION_ITEM = "TransactionItem";
    public static final String ENTITY_KISAAN_PAYMENT = "KisaanPayment";
    public static final String ENTITY_KISAAN_TRANSACTION = "KisaanTransaction";
    public static final String ENTITY_KHAREEDDAR = "Khareeddar";
    public static final String ENTITY_KHAREEDDAR_PAYMENT = "KhareeddarPayment";
    public static final String ENTITY_LOCATION = "Location";
    public static final String TOTAL_PAYMENT_AMOUNT = "totalPaymentAmount";
    public static final String TOTAL_TRANSACTION_AMOUNT = "totalTransactionAmount";

    public static final String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String DATE_FORMAT_YYYY_MM = "yyyy-MM";
    public static final String DATE_FORMAT_YYYY = "yyyy";

    public static final String DATE_FORMAT_YYYY_MM_DD_FULL = "yyyy-MM-dd HH:mm:ss";


    public static final String ERROR_KEY = "errors";
    public static final int LIMIT_ROWS = 20;
    public static final int MAX_ITEM_ROWS = 14;
    public final static int MAX_ROWS = 1000;
    public static final String ROOT_USER = "root";

    public static final int DECIMAL_SCALE = 2;


    //Thumbnail Size
    public final static int THUMBNAIL_WIDTH = 160;
    public final static int THUMBNAIL_HEIGHT = 160;


    //Public location
    public final static String WINDOWS_PUBLIC_LOCATION="D:\\var\\www\\public\\";
    public final static String MAC_PUBLIC_LOCATION="/local/public/www/";
    public final static String DEFAULT_PHOTO="defaultPhoto";



    //drop: db.runCommand({ dropDatabase: 1 })
    //db.getCollectionNames()

    //Getting index: db.transaction_kisaan.getIndexes()
    //delete all indexes
    /*
    db.getCollectionNames().forEach(function(collName) {
        db.runCommand({dropIndexes: collName, index: "*"});
    });
    */
    /*
    1] Add the following plugin with main class to run to your pom.xml
<plugin>     <artifactId>maven-assembly-plugin</artifactId>     <configuration>         <archive>             <manifest>                 <mainClass>com.bookkeeping.controller.BookKeepingController</mainClass>             </manifest>         </archive>         <descriptorRefs>             <descriptorRef>jar-with-dependencies</descriptorRef>         </descriptorRefs>     </configuration> </plugin>

2] RUN: /Applications/IntelliJ\ IDEA\ 15\ CE.app/Contents/plugins/maven/lib/maven3/bin/mvn clean compile assembly:single

3] java -jar target/broker-bookkeeping-1.0-SNAPSHOT-jar-with-dependencies.jar 8081
     */


}
