package com.bookkeeping.utilities;

import com.bookkeeping.DAO.*;
import com.bookkeeping.constants.Constants;
import com.bookkeeping.controller.Message;
import com.bookkeeping.controller.SingletonManagerDAO;
import com.bookkeeping.factory.factoryDAO;
import com.bookkeeping.model.*;
import com.bookkeeping.persistence.MongoConnection;
import com.google.gson.Gson;
import com.mongodb.client.MongoDatabase;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.servlet.SparkApplication;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.Part;
import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.ClassLoader.*;
import static spark.Spark.staticFileLocation;

/**
 * Created by chandan on 9/27/2015.
 */
public class ControllerUtilities {

    public static Set<String> IMAGE_FORMAT_SET = new HashSet<String>();
    static {
        IMAGE_FORMAT_SET.add("jpeg");
        IMAGE_FORMAT_SET.add("jpg");
        IMAGE_FORMAT_SET.add("png");
        IMAGE_FORMAT_SET.add("gif");
    }
    // helper function to get session cookie as string
    /*
    public static Configuration createFreemarkerConfiguration() {
        Configuration retVal = new Configuration();
        retVal.setClassForTemplateLoading(ControllerUtilities.class, "/freemarker");
        return retVal;
    }
    */

    static final Logger logger = LoggerFactory.getLogger(ControllerUtilities.class);
    public static Map<String,Tuple<Document>> joinByIdKey(List<Document> list1, List<Document> list2) {

        Map<String,Tuple<Document>> documentMap = new TreeMap<String, Tuple<Document>>();
        for(Document doc : list1) {
            String key = doc.get("_id").toString();
            Tuple<Document> tuple = new Tuple<Document>();
            tuple.setFirst(doc);
            documentMap.put(key,tuple);
        }

        for(Document doc : list2) {

            String key = doc.get("_id").toString();
            Tuple<Document> tuple;
            if(documentMap.get(key)!=null) {
                tuple = documentMap.get(key);
                tuple.setSecond(doc);

            } else {
                tuple = new Tuple<Document>();
                tuple.setSecond(doc);
            }
            documentMap.put(key,tuple);

        }
        //Gson gson = new Gson();

        //logger.info(gson.toJson(documentMap));
        return documentMap;

    }

    /**
     * Specialized join which does a list<ModelObj> and List<Document>
     * @param list1
     * @param list2
     * @return
     */
    public static Map<String,Tuple<Document>> joinByKey(List<? extends ModelObj> list1, List<Document> list2) {

        Gson gson = new Gson();
        Map<String,Tuple<Document>> documentMap = new TreeMap<String, Tuple<Document>>();
        for(ModelObj doc : list1) {
            String key = doc.getUniqueKey();
            if(key.equals("kisaan_4")) {
                System.out.println("**************");
                System.out.println("Kisaan4::" + gson.toJson(doc));
            }
            Tuple<Document> tuple = new Tuple<Document>();
            Document document = new Document();
            document.append("obj",doc);
            tuple.setFirst(document);
            documentMap.put(key, tuple);
        }

        for(Document doc : list2) {

            String key = doc.get("_id").toString();
            Tuple<Document> tuple;
            if(documentMap.get(key)!=null) {

                if(key.equals("kisaan_4")) {
                    System.out.println("**************");
                    System.out.println("Kisaan4::"+gson.toJson(doc));
                }
                tuple = documentMap.get(key);
                tuple.setSecond(doc);

            } else {
                tuple = new Tuple<Document>();
                tuple.setSecond(doc);
            }
            documentMap.put(key,tuple);

        }
        //Gson gson = new Gson();

        //logger.info(gson.toJson(documentMap));
        return documentMap;

    }
    public static List<Document> convertJoinedMapDocumentList(Map<String,Tuple<Document>> joinMap) {

        List<Document> documentList = new ArrayList<Document>();
        for(String key : joinMap.keySet()) {

            Tuple tuple = joinMap.get(key);
            Document document = new Document();
            document.append("_id",key);
            //Gson gson = new Gson();
            double transAmount = 0;
            if(tuple.getFirst()!=null) {
                //logger.info("first:"+gson.toJson(tuple.getFirst()));
                Document document1 = (Document)tuple.getFirst();
                transAmount = document1.getDouble("total");
            }
            //logger.info("Tran: {}"+transAmount);

            double paymentAmount = 0;
            if(tuple.getSecond()!=null) {
                //logger.info("Sec:"+gson.toJson(tuple.getSecond()));
                Document document1 = (Document)tuple.getSecond();
                paymentAmount = document1.getDouble("total");
            }
            //logger.info("pay: {}"+paymentAmount);

            document.append("transactionAmount",transAmount);
            document.append("paymentAmount",paymentAmount);

            //logger.info(gson.toJson(document));
            document.append("balance", transAmount - paymentAmount);
            documentList.add(document);

        }
        return documentList;

    }

    public static Map<String,Tuple<Document>> joinByDate(List<Document> list1, List<Document> list2) {

        Map<String,Tuple<Document>> documentMap = new TreeMap<String, Tuple<Document>>();
        for(Document doc : list1) {
            String key = doc.get("_id").toString();
            Tuple<Document> tuple = new Tuple<Document>();
            tuple.setFirst(doc);
            documentMap.put(key,tuple);
        }

        for(Document doc : list2) {

            String key = doc.get("_id").toString();
            Tuple<Document> tuple;
            if(documentMap.get(key)!=null) {
                tuple = documentMap.get(key);
                tuple.setSecond(doc);

            } else {
                tuple = new Tuple<Document>();
                tuple.setSecond(doc);
            }
            documentMap.put(key,tuple);

        }
        return documentMap;

    }
    public static void messageSoftwareTrial() {
        System.out.println("*************************************************************************************");
        System.out.println("*************************************************************************************");
        System.out.println("****You have reached the limit of adding: " + Constants.TRIAL_EDITION_LIMIT + " documents for trial edition");
        System.out.println("****To continue usage you need to purchase this software!!!");
        System.out.println("****Please contact the creator of the system!!!");
        System.out.println("*************************************************************************************");
        System.out.println("*************************************************************************************");
    }

    public static String formatUniqueKey(String uniqueKey) {
        String[] arr= uniqueKey.split(" ");
        return StringUtils.join(arr,Constants.UNIQUE_KEY_SEPARATOR);
    }

    public static String getCurrentDateStrInYYYY_MM_DD() {
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_YYYY_MM_DD);
        String currentDateStr =  dateFormat.format(currentDate);
        return currentDateStr;
    }


    public static boolean verifyDateInFormat(String dateStr) {
        DateFormat dateFormatInput = new SimpleDateFormat(Constants.DATE_FORMAT_YYYY_MM_DD);
        logger.debug("Input DateStr:{}", dateStr);

        try {
            Date targetDate = dateFormatInput.parse(dateStr);
            return true;
        } catch(Exception e) {
            logger.error("Input date:{} is not in format: ",dateStr,Constants.DATE_FORMAT_YYYY_MM_DD,e);
        }
        return false;
    }

    public static Date getDateInFormat(String dateStr) {
        DateFormat dateFormatInput = new SimpleDateFormat(Constants.DATE_FORMAT_YYYY_MM_DD);
        logger.debug("Input DateStr:{}", dateStr);

        try {
            Date targetDate = dateFormatInput.parse(dateStr);
            return targetDate;
        }catch(Exception e) {
            logger.error("Input date:{} is not in format: ",dateStr,Constants.DATE_FORMAT_YYYY_MM_DD,e);
        }
        return null;
    }

    public static Date getDateInFormatYYYY_MM(String dateStr) {
        DateFormat dateFormatInput = new SimpleDateFormat(Constants.DATE_FORMAT_YYYY_MM);
        logger.debug("Input DateStr:{}", dateStr);

        try {
            Date targetDate = dateFormatInput.parse(dateStr);
            return targetDate;
        }catch(Exception e) {
            logger.error("Input date:{} is not in format: ",dateStr,Constants.DATE_FORMAT_YYYY_MM,e);
        }
        return null;
    }

    public static Date getDateInFormatYYYY(String dateStr) {
        DateFormat dateFormatInput = new SimpleDateFormat(Constants.DATE_FORMAT_YYYY);
        logger.debug("Input DateStr:{}", dateStr);

        try {
            Date targetDate = dateFormatInput.parse(dateStr);
            return targetDate;
        }catch(Exception e) {
            logger.error("Input date:{} is not in format: ",dateStr,Constants.DATE_FORMAT_YYYY,e);
        }
        return null;
    }


    public static Map<String,String>  listIndexes() {
        Map<String,String> mapCollectionIndexes = new HashMap<String, String>();

        mapCollectionIndexes.put(Constants.MONGO_TRANSACTION_ITEM_COLLECTION,
                "{ uniqueKey: \"text\"}");


        mapCollectionIndexes.put(Constants.MONGO_KISAAN_PAYMENT_COLLECTION,
                "{ uniqueKey: \"text\",\"kisaan\": \"text\", tag:\"text\" }");

        mapCollectionIndexes.put(Constants.MONGO_KISAAN_PAYMENT_COLLECTION + "__2",
                "{ kisaan:1, tag:1 }");

        mapCollectionIndexes.put(Constants.MONGO_KISAAN_PAYMENT_COLLECTION + "__3",
                "{ eventDate:1,kisaan: 1, tag:1}");

        mapCollectionIndexes.put(Constants.MONGO_PAYMENT_KHAREEDDAR_COLLECTION,
                "{ uniqueKey: \"text\",\"khareeddar\": \"text\", tag:\"text\" }");

        mapCollectionIndexes.put(Constants.MONGO_PAYMENT_KHAREEDDAR_COLLECTION + "__2",
                "{ khareeddar:1, tag:1 }");

        mapCollectionIndexes.put(Constants.MONGO_PAYMENT_KHAREEDDAR_COLLECTION + "__3",
                "{ eventDate:1,khareeddar: 1, tag:1}");



        mapCollectionIndexes.put(Constants.MONGO_TRANSACTION_KISAAN_COLLECTION,
                "{ uniqueKey: \"text\", \"kisaan\": \"text\", khareeddar:\"text\" }");

        mapCollectionIndexes.put(Constants.MONGO_TRANSACTION_KISAAN_COLLECTION + "__2",
                "{ kisaan: 1, khareeddar:1 }");

        mapCollectionIndexes.put(Constants.MONGO_TRANSACTION_KISAAN_COLLECTION + "__3",
                "{ eventDate:1, kisaan:1, khareeddar:1 }");

        mapCollectionIndexes.put(Constants.MONGO_KHAREEDDAR_COLLECTION,
                "{ uniqueKey: \"text\",\"firstName\": \"text\", lastName:\"text\" }");
        mapCollectionIndexes.put(Constants.MONGO_KHAREEDDAR_COLLECTION+"__2",
                "{ uniqueKey: 1}");

        mapCollectionIndexes.put(Constants.MONGO_KISAAN_COLLECTION,
                "{ uniqueKey: \"text\",\"firstName\": \"text\", lastName:\"text\" }");
        mapCollectionIndexes.put(Constants.MONGO_KISAAN_COLLECTION+"__2",
                "{ uniqueKey: 1}");


        mapCollectionIndexes.put(Constants.MONGO_ITEM_SOLD_COLLECTION,
                "{ item.uniqueKey: \"text\"}");

        mapCollectionIndexes.put(Constants.MONGO_ITEM_SOLD_COLLECTION,
                "{ uniqueKey: \"text\",\"itemName\": \"text\"}");

        mapCollectionIndexes.put(Constants.MONGO_ITEM_SOLD_COLLECTION + "__2",
                "{ itemName: 1}");

        mapCollectionIndexes.put(Constants.MONGO_ITEM_SOLD_COLLECTION + "__3", "{ eventDate:1,itemName:1}");

        return mapCollectionIndexes;

    }

    public static void initSetupCreateIndex() {
        MongoDatabase instance = MongoConnection.getInstance();
        Map<String, String> mapCollectionIndexes = listIndexes();

        int i=0;
        for (String collection : mapCollectionIndexes.keySet()) {
            try {

                String key = collection;
                if(collection.split("__").length>=2) {
                    key = collection.split("__")[0];
                }
                logger.info("Creating index {} with key:{} and key:{} -> {}",i,key,collection,mapCollectionIndexes.get(collection));
                instance.getCollection(key).createIndex(Document.parse(mapCollectionIndexes.get(collection)));

            } catch (Exception e) {
                logger.error("Index already exists for collection:{}, error:{}",collection, e.getMessage());
            } finally {
                i++;
            }
        }

    }


        /*
    public static Map<String,String>  listIndexes() {
        Map<String,String> mapCollectionIndexes = new HashMap<String, String>();
        mapCollectionIndexes.put(Constants.MONGO_TRANSACTION_KISAAN_COLLECTION,
                "{ uniqueKey: \"text\", \"kisaan.uniqueKey\": \"text\", \"khareeddar.uniqueKey\": \"text\" }");
        mapCollectionIndexes.put(Constants.MONGO_KISAAN_COLLECTION,
                "{ uniqueKey: \"text\", firstName:\"text\", lastName:\"text\" }");
        mapCollectionIndexes.put(Constants.MONGO_KISAAN_PAYMENT_COLLECTION,
                "{ uniqueKey: \"text\",\"kisaan.uniqueKey\": \"text\", tag:\"text\" }");
        mapCollectionIndexes.put(Constants.MONGO_KHAREEDDAR_COLLECTION,
                "{ uniqueKey: \"text\", firstName:\"text\", lastName:\"text\" }");
        mapCollectionIndexes.put(Constants.MONGO_PAYMENT_KHAREEDDAR_COLLECTION,
                "{ uniqueKey: \"text\", \"khareeddar.uniqueKey\": \"text\", tag:\"text\"}");
        ;
        return mapCollectionIndexes;

    }

*/

//    public static void initSetupCreateIndex() {
//        MongoDatabase instance = MongoConnection.getInstance();
//        Map<String, String> mapCollectionIndexes = new HashMap<String, String>();
//        mapCollectionIndexes.put(Constants.MONGO_TRANSACTION_KISAAN_COLLECTION,
//                "{ uniqueKey: \"text\", \"kisaan.uniqueKey\": \"text\", \"khareeddar.uniqueKey\": \"text\" }");
//        mapCollectionIndexes.put(Constants.MONGO_KISAAN_COLLECTION,
//                "{ uniqueKey: \"text\", firstName:\"text\", lastName:\"text\", place:\"text\" }");
//        mapCollectionIndexes.put(Constants.MONGO_KISAAN_PAYMENT_COLLECTION,
//                "{ uniqueKey: \"text\",\"kisaan.uniqueKey\": \"text\", tag:\"text\" }");
//        mapCollectionIndexes.put(Constants.MONGO_KHAREEDDAR_COLLECTION,
//                "{ uniqueKey: \"text\", firstName:\"text\", lastName:\"text\" }");
//        mapCollectionIndexes.put(Constants.MONGO_PAYMENT_KHAREEDDAR_COLLECTION,
//                "{ uniqueKey: \"text\", \"khareeddar.uniqueKey\": \"text\", tag:\"text\"}");
//
//        for (String collection : mapCollectionIndexes.keySet()) {
//            try {
//                instance.getCollection(collection).createIndex(Document.parse(mapCollectionIndexes.get(collection)));
//
//            } catch (Exception e) {
//                logger.error("Index already exists for collection:{}, error:{}",collection, e.getMessage());
//            }
//        }
//
//    }
    /**
     * Positve date means ahead, negative means back
     * @param toDate
     * @param n
     * @return
     */
    public static Date getNDaysDate(Date toDate, int n) {
        logger.debug("Input Date:{} and diff: {}", toDate, n);
        Calendar cal = Calendar.getInstance();
        cal.setTime(toDate);
        cal.add(Calendar.DATE, n);
        return cal.getTime();
    }

    public static Date getNMonthssDate(Date toDate, int n) {
        logger.debug("Input Date:{} and diff: {}",toDate,n);
        Calendar cal = Calendar.getInstance();
        cal.setTime(toDate);
        cal.add(Calendar.MONTH, n);
        return cal.getTime();
    }

    public static Date getNDaysDateFromCurrentDate(int n) {
        String currentDateStr = getCurrentDateStrInYYYY_MM_DD();
        return getNDaysDate(getDateInFormat(currentDateStr), n);
    }

    public static String formatDateInYYYY_MM_DD(Date inputDate) {
        DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_YYYY_MM_DD);
        String currentDateStr =  dateFormat.format(inputDate);
        return currentDateStr;
    }


    public static Map<String,Object> multiSearch(String search,int limit) {
        //String searchString = "{ $text: { $search:\""+search+"\" } }";

        KisaanDAO kisaanDAO = SingletonManagerDAO.getInstance().getKisaanDAO();
        List<Kisaan> kisaanList = kisaanDAO.search(search);

        KisaanPaymentDAO kisaanPaymentDAO = SingletonManagerDAO.getInstance().getKisaanPaymentDAO();
        List<KisaanPayment> kisaanPaymentList = kisaanPaymentDAO.search(search,limit);

        KisaanTransactionDAO kisaanTransactionDAO = SingletonManagerDAO.getInstance().getKisaanTransactionDAO();
        List<KisaanTransaction> kisaanTransactionList = kisaanTransactionDAO.search(search,limit);

        KhareeddarDAO khareeddarDAO = SingletonManagerDAO.getInstance().getKhareeddarDAO();
        List<Khareeddar> khareeddarList = khareeddarDAO.search(search,limit);

        KhareeddarPaymentDAO khareeddarPaymentDAO = SingletonManagerDAO.getInstance().getKhareeddarPaymentDAO();
        List<KhareeddarPayment> khareeddarPaymentList = khareeddarPaymentDAO.search(search,limit);

        Map<String,Object> root = new HashMap<String, Object>();
        root.put("kisaan",kisaanList);
        root.put("kisaanPayment",kisaanPaymentList);
        root.put("kisaanTransaction",kisaanTransactionList);
        root.put("khareeddar",khareeddarList);
        root.put("khareeddarPayment",khareeddarPaymentList);
        return root;

    }

    public static String getSessionCookie(final Request request) {
        if (request.raw().getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.raw().getCookies()) {
            if (cookie.getName().equals(getCookieKey(request,"session"))) {
                return cookie.getValue();
            }
        }
        return null;
    }

    // helper function to get session cookie as string
    public static Cookie getSessionCookieActual(final Request request) {
        if (request.raw().getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.raw().getCookies()) {
            if (cookie.getName().equals(getCookieKey(request,"session"))) {
                return cookie;
            }
        }
        return null;
    }

    public static String getCookieKey(final Request request, String key) {
        return request.host().split(":")[0]+"_"+request.port()+"_"+key;
    }
    public static void deleteProfileCookie(final Request request, Response response) {
        deleteCookie(request,response,"profile");
    }

    public static void deleteSessionCookie(final Request request, Response response) {
        deleteCookie(request,response,"session");
    }

    public static void deleteCookie(final Request request, Response response,String cookieStr) {
        if (request.raw().getCookies() == null) {
            return;
        }
        for (Cookie cookie : request.raw().getCookies()) {
            if (cookie.getName().equals(getCookieKey(request,cookieStr))) {
                cookie.setMaxAge(0);
                response.raw().addCookie(cookie);
            }
        }
    }

    public static void deleteAllCookies(final Request request, Response response) {
        deleteProfileCookie(request,response);
        deleteSessionCookie(request,response);

    }
    // helper function to get session cookie as string
    public static Cookie getProfileCookieActual(final Request request) {
        if (request.raw().getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.raw().getCookies()) {
            if (cookie.getName().equals(getCookieKey(request,"profile"))) {
                return cookie;
            }
        }
        return null;
    }

    // tags the tags string and put it into an array
    public static ArrayList<String> extractTags(String tags) {

        // probably more efficent ways to do this.
        //
        // whitespace = re.compile('\s')

        tags = tags.replaceAll("\\s", "");
        String tagArray[] = tags.split(",");

        // let's clean it up, removing the empty string and removing dups
        ArrayList<String> cleaned = new ArrayList<String>();
        for (String tag : tagArray) {
            if (!tag.equals("") && !cleaned.contains(tag)) {
                cleaned.add(tag);
            }
        }

        return cleaned;
    }


    public static boolean shouldReturnHtml(Request request) {
        String accept = request.headers("Accept");
        return accept != null && accept.contains("text/html");
    }

    public static double calculateTotal(float priceFloat, float quantityFloat, float hamaaliRateFloat,
                                        float mapariateFloat, float cashSpecialRate,
                                        float brokerageRate) {

        double totalAmount = (double)quantityFloat*priceFloat;
        totalAmount=(100-brokerageRate)*totalAmount;

        if(cashSpecialRate>0) {
            totalAmount = (100-cashSpecialRate)*totalAmount;
        }
        totalAmount-= mapariateFloat*quantityFloat;
        totalAmount-=hamaaliRateFloat*quantityFloat;
        return totalAmount;

    }

    public static void handleLocation(Location location, String address, String district, String taluka, String state) {

        if(StringUtils.isNotBlank(address)) {
            location.setAddress(address);
        }

        if(StringUtils.isNotBlank(district)) {
            location.setDistrict(district);
        }

        if(StringUtils.isNotBlank(taluka)) {
            location.setTaluka(taluka);
        }
        if(StringUtils.isNotBlank(state)) {
            location.setState(state);
        }
    }

    public static double calculateTotalKhareeddar(float priceFloat, float quantityFloat)
    {

        double totalAmount = quantityFloat*priceFloat;
        return totalAmount;

    }

    public static boolean verifyEntityInputs(String entity, Map<String,Object> root) {
        if(StringUtils.isNotBlank(entity)) {
            MongoCollectionDAO mongoDAO = factoryDAO.getDAO(entity);
            String entityName = factoryDAO.getEntityString(entity);
            if(mongoDAO!=null && entityName!=null) {
                return true;
            } else {
                logger.error("Bad request, Bad entity, input:{}",entity);
                root.put("error","bad request");
                return false;
            }

        } else {
            logger.error("Bad request, Empty entity");
            root.put("error","bad request");
            return false;

        }

    }

    public static float formatDecimalValue(float num) {
        BigDecimal bigDecimal = new BigDecimal(num);
        bigDecimal = bigDecimal.setScale(2,BigDecimal.ROUND_HALF_EVEN);
        return bigDecimal.floatValue();
    }

    public static double formatDecimalValue(double num) {
        BigDecimal bigDecimal = new BigDecimal(num);
        bigDecimal = bigDecimal.setScale(2,BigDecimal.ROUND_HALF_EVEN);
        return bigDecimal.doubleValue();
    }

    public static BigDecimal formatDecimalValue(BigDecimal bigDecimal) {
        bigDecimal = bigDecimal.setScale(2,BigDecimal.ROUND_HALF_EVEN);
        return bigDecimal;
    }
    public static String formatDecimal(float num) {
        DecimalFormat df=new DecimalFormat("0.00");
        return df.format(num);
    }

    public static String formatDecimal(double num) {
        DecimalFormat df=new DecimalFormat("0.00");
        return df.format(num);
    }

    public static OSValue getOSType() {
        String OSName = System.getProperty("os.name");
        if (OSName.toLowerCase().contains("window")) {
            return OSValue.WINDOWS;
        }
        return OSValue.MAC;
    }

    public static void main(String[] args) {
        String dateStr="2016-06-10";
        System.out.println(verifyDateInFormat(dateStr));
        System.out.println(getDateInFormat(dateStr));
        System.out.println(getNDaysDate(getDateInFormat(dateStr),-7));

        System.out.println(getInputImageFormat("chandna.maloo"));
        System.out.println(getInputImageFormat("chandna.maloo.JPG"));
        System.out.println(getInputImageFormat("chandna.maloo.PNG"));
        System.out.println(getInputImageFormat("chandna.maloo.jpeg"));
        System.out.println(getInputImageFormat("chandna"));
    }
    /*
    final Part uploadedFile = request.raw().getPart("uploadedFile");
            final Path path = Paths.get("/tmp/meh");
            try (final InputStream in = uploadedFile.getInputStream()) {
                Files.copy(in, path);
            }

     */

    /**
     *
     * @param fileContent
     * @param fileName
     * @param entity
     * @return
     * @throws ServletException
     * @throws IOException
     * TODO: detect photo format and write thumbnail in the same format
     */

    public static String processPhotoUpload(InputStream fileContent, String fileName, String entity)
            throws ServletException, IOException {

        // Create path components to save the file
        final String path = Constants.MAC_PUBLIC_LOCATION+"/images/"+entity+"/";
        OutputStream out = null;
        logger.info("Created needed dir and started process...");
        String outputFileName = UUID.randomUUID()+"_"+fileName;
        if (fileContent==null) {
            logger.error("No incoming filestream for output:"+outputFileName);
            return null;
        }

        try {
            FileSystemUtils.checkDirExistsOtherwiseCreate(path);
            out = new FileOutputStream(new File(path+File.separator+outputFileName));
            int read = 0;
            final byte[] bytes = new byte[1024];

            while ((read = fileContent.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }

            logger.info("File{0}being uploaded to {1}",
                    new Object[]{fileName, path});

            //Creating Thumbnail
            try {
                logger.info("Creating Thumbnails for :{}",outputFileName);
                String defaultFormat="jpg";
                defaultFormat = getInputImageFormat(fileName);
                Thumbnails.of(path+File.separator+outputFileName)
                        .size(Constants.THUMBNAIL_WIDTH, Constants.THUMBNAIL_HEIGHT)
                        .outputFormat(defaultFormat)
                        .toFiles(Rename.PREFIX_DOT_THUMBNAIL);


            } catch(Exception e) {
                logger.error("Failed to create Thumbnail for:{}",outputFileName,e);
            }
            return outputFileName;
        } catch (FileNotFoundException fne) {
            logger.error("Problems during file upload. Error: {0}",
                    new Object[]{fne.getMessage()});
        } finally {
            if (out != null) {
                out.close();
            }
            if (fileContent != null) {
                fileContent.close();
            }
        }
        return null;
    }

    /**
     * Gets the imageType format
     * @param fileName
     * @return
     */
    public static String getInputImageFormat(String fileName) {
        String[] arr = fileName.split("\\.");
        String format = arr[arr.length-1].toLowerCase();

        if (IMAGE_FORMAT_SET.contains(format)) {
            return format;
        } else {
            logger.warn("Found a new format for image:{}, we need to add it, returning jpg for now",format);
            return "jpg";
        }
    }
    
    public static String getFileName(final Part part) {
        final String partHeader = part.getHeader("content-disposition");
        logger.info("Part Header = {0}", partHeader);
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(
                        content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }


}
