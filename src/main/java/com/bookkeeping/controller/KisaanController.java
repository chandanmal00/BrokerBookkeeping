package com.bookkeeping.controller;

import com.bookkeeping.DAO.KisaanDAO;
import com.bookkeeping.DAO.KisaanPaymentDAO;
import com.bookkeeping.DAO.KisaanTransactionDAO;
import com.bookkeeping.DAO.MongoCollectionDAO;
import com.bookkeeping.constants.Constants;
import com.bookkeeping.factory.factoryDAO;
import com.bookkeeping.model.*;
import com.bookkeeping.utilities.ControllerUtilities;
import com.google.gson.Gson;
import freemarker.template.TemplateException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * Created by chandan on 6/20/16.
 */
public class KisaanController extends  DefaultController{

    KisaanDAO kisaanDAO;
    KisaanTransactionDAO kisaanTransactionDAO;
    KisaanPaymentDAO kisaanPaymentDAO;

    static final Logger logger = LoggerFactory.getLogger(KisaanController.class);
    public KisaanController() {
        kisaanDAO = SingletonManagerDAO.getInstance().getKisaanDAO();
        kisaanPaymentDAO = SingletonManagerDAO.getInstance().getKisaanPaymentDAO();
        kisaanTransactionDAO = SingletonManagerDAO.getInstance().getKisaanTransactionDAO();
    }

    public void initializeRoutes() throws IOException {
        // used to display Kisaan Detail Page


        get(new Route("/listKisaans") {
            @Override
            public Object handle(Request request, Response response) {
                List<Kisaan> kisaanList = kisaanDAO.list();
                Gson gson = new Gson();
                return gson.toJson(kisaanList);
            }
        });



        get(new FreemarkerBasedRoute("/addKisaan", "addKisaan.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                Map<String,Object> root = new HashMap<String, Object>();
                root.put("ENTITY_NAME", Constants.ENTITY_KISAAN);
                root.put("entityValue", "kisaan");
                templateOverride.process(root, writer);

            }

        });

        post(new FreemarkerBasedRoute("/addKisaan", "addKisaan.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException {

                String firstName = StringEscapeUtils.escapeHtml4(request.queryParams("firstName"));
                String lastName = StringEscapeUtils.escapeHtml4(request.queryParams("lastName"));
                String nickName = StringEscapeUtils.escapeHtml4(request.queryParams("nickName"));
                String ageString = StringEscapeUtils.escapeHtml4(request.queryParams("age"));
                String aadhar = StringEscapeUtils.escapeHtml4(request.queryParams("aadhar"));
                String pan = StringEscapeUtils.escapeHtml4(request.queryParams("pan"));
                String place = StringEscapeUtils.escapeHtml4(request.queryParams("place"));
                String taluka = StringEscapeUtils.escapeHtml4(request.queryParams("taluka"));
                String district = StringEscapeUtils.escapeHtml4(request.queryParams("district"));
                String state = StringEscapeUtils.escapeHtml4(request.queryParams("state"));
                String address = StringEscapeUtils.escapeHtml4(request.queryParams("address"));

                HashMap<String, String> root = new HashMap<String, String>();
                root.put("ENTITY_NAME", Constants.ENTITY_KISAAN);
                root.put("entityValue", "kisaan");

                root.put("firstName", firstName);
                root.put("lastName", lastName);
                root.put("nickName", nickName);
                root.put("age", ageString);
                root.put("aadhar", aadhar);
                root.put("pan", pan);
                root.put("place", place);
                root.put("taluka", taluka);
                root.put("district", district);
                root.put("state", state);
                root.put("address", address);
                /*
                Set<String> set = request.queryParams();
                for(String s: set) {
                    logger.info(s + "::"+request.queryParams(s));
                }
                */

                if (StringUtils.isBlank(nickName)
                        || StringUtils.isBlank(firstName)
                        || StringUtils.isBlank(lastName)
                        || StringUtils.isBlank(place)) {
                    root.put("errors", "NickName, firstName, last Name, place are mandatory fields,please enter values.");
                    templateOverride.process(root, writer);
                    return;
                } else {
                    // extract tags
                    int age = -1;
                    if (StringUtils.isNotBlank(ageString)) {
                        age = Integer.parseInt(ageString);
                    }

                    //TODO: handle inputs
                    logger.info("yay, welcoming Kisaan:" + nickName + " to the system");

                    Kisaan kisaan = new Kisaan(nickName);

                    kisaan.setAge(age);
                    kisaan.setFirstName(firstName);
                    kisaan.setLastName(lastName);
                    Location location = new Location(place);
                    location.setCreatedBy(sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request)));
                    ControllerUtilities.handleLocation(location, address, district, taluka, state);
                    kisaan.setLocation(location);
                    NationalIdentity nationalIdentity;
                    if (StringUtils.isNotBlank(pan)) {
                        nationalIdentity = new NationalIdentity(pan);
                        nationalIdentity.setCreatedBy(sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request)));
                        if (StringUtils.isNotBlank(aadhar)) {
                            nationalIdentity.setAadhar(aadhar);
                            kisaan.setNationalIdentity(nationalIdentity);
                        }
                    } else {
                        if (StringUtils.isNotBlank(aadhar)) {
                            nationalIdentity = new NationalIdentity(null, aadhar);
                            nationalIdentity.setCreatedBy(sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request)));
                            kisaan.setNationalIdentity(nationalIdentity);
                        }
                    }

                    kisaan.setCreatedBy(sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request)));
                    logger.info(kisaan.toString());
                    kisaanDAO.add(kisaan);
                    root.put("success", "true");
                    templateOverride.process(root, writer);
                    return;
                }
            }
        });


        get(new FreemarkerBasedRoute("/kisaan/:uniqueKey", "entity.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {

                String uniqueKey = StringEscapeUtils.escapeHtml4(request.params(":uniqueKey"));
                if(StringUtils.isBlank(uniqueKey)) {
                    response.redirect("/post_not_found/kisaan/"+uniqueKey);
                }
                logger.info("Calling route /kisaan: get " + uniqueKey);
                Kisaan kisaan = kisaanDAO.getBasedOnUniqueKey(uniqueKey);

                if (kisaan == null) {
                    //TODO: create kisaan not found
                    response.redirect("/post_not_found/kisaan/"+uniqueKey);
                    return;
                } else {

                    List<KisaanTransaction> kisaanTransactionList = kisaanTransactionDAO.getBasedOnKisaanKeyWithLimit(uniqueKey, Constants.LIMIT_ROWS);
                    List<KisaanPayment> kisaanPaymentList = kisaanPaymentDAO.getBasedOnKisaanWithLimit(uniqueKey, Constants.LIMIT_ROWS);
                    // empty comment to hold new comment in form at bottom of blog entry detail page

                    //Display Transactions

                    Map<String, Object> root = new HashMap<String, Object>();
                    root.put(Constants.TRANSACTIONS, kisaanTransactionList);
                    root.put(Constants.PAYMENTS, kisaanPaymentList);
                    root.put(Constants.ENTITY, kisaan);
                    root.put("entityValue", "kisaan");
                    root.put("rows", Constants.LIMIT_ROWS);
                    root.put(Constants.ENTITY_NAME, Constants.ENTITY_KISAAN);
                    root.put(Constants.TOTAL_PAYMENT_AMOUNT, kisaanPaymentDAO.paymentSumBasedOnKisaan(uniqueKey));
                    //root.put(Constants.TOTAL_TRANSACTION_AMOUNT,kisaanTransactionDAO.transactionSumForKisaan(uniqueKey));
                    root.put("summ", kisaanTransactionDAO.transactionSumForKisaanNew(uniqueKey));


                    //List<Document> list = mongoDAO.getDailySummaryForWeekEnding(currentDateStr);
                    // root.put("weeklyPayment", kisaanPaymentDAO.getDailySummaryNDatesEndingToday(-7));
                    //root.put("weeklyTransactions", kisaanTransactionDAO.getDailySummaryNDatesEndingToday(-7));
                    List<Document> weeklyPayments = kisaanPaymentDAO.getEntityDailySummaryNDatesEndingToday(-7, uniqueKey, "kisaan");
                    List<Document> weeklyTransactions = kisaanTransactionDAO.getEntityDailySummaryNDatesEndingToday(-7, uniqueKey, "kisaan");

                    List<Document> monthlyPayments = kisaanPaymentDAO.getEntityMonthlySummaryEndingToday(-3, uniqueKey, "kisaan");
                    List<Document> monthlyTransactions = kisaanTransactionDAO.getEntityMonthlySummaryEndingToday(-3, uniqueKey, "kisaan");

                    root.put("weeklyPayments", weeklyPayments);
                    root.put("weeklyTransactions", weeklyTransactions);
                    Map<String, Tuple<Document>> joinMapWeekly = ControllerUtilities.joinByDate(weeklyTransactions, weeklyPayments);
                    root.put("joinMapWeekly", joinMapWeekly);


                    root.put("monthlyPayments", monthlyPayments);
                    root.put("monthlyTransactions", monthlyTransactions);
                    Map<String, Tuple<Document>> joinMapMonthly = ControllerUtilities.joinByDate(monthlyTransactions, monthlyPayments);
                    root.put("joinMapMonthly", joinMapMonthly);

                    templateOverride.process(root, writer);
                    return;
                }
            }
        });

        get(new FreemarkerBasedRoute("/edit/kisaan/:uniqueKey", "edit_kisaan.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                Map<String,Object> root = new HashMap<String, Object>();
                root.put("ENTITY_NAME", Constants.ENTITY_KISAAN);
                root.put("entityValue", "kisaan");

                String uniqueKey = StringEscapeUtils.escapeHtml4(request.params(":uniqueKey"));

                if(StringUtils.isBlank(uniqueKey)) {
                    response.redirect("/not_found/kisaan/"+uniqueKey);
                }
                logger.info("Calling route /edit/kisaan: get " + uniqueKey);
                root.put("nickName", uniqueKey);
                Kisaan kisaan = kisaanDAO.getBasedOnUniqueKey(uniqueKey);
                root.put("entity", kisaan);

                if (kisaan == null) {
                    //TODO: create kisaan not found
                    response.redirect("/post_not_found/kisaan/"+uniqueKey);
                    return;
                } else {

                    root.put("firstName", kisaan.getFirstName());
                    root.put("lastName", kisaan.getLastName());
                    root.put("nickName", kisaan.getNickName());
                    if(kisaan.getAge()!=-1) {
                        root.put("age", kisaan.getAge());
                    }
                    if(kisaan.getNationalIdentity()!=null) {
                        root.put("aadhar", kisaan.getNationalIdentity().getAadhar());
                        root.put("pan", kisaan.getNationalIdentity().getPan());
                    }
                    if(kisaan.getLocation()!=null) {
                        root.put("place", kisaan.getLocation().getPlace());
                        root.put("taluka", kisaan.getLocation().getTaluka());
                        root.put("district", kisaan.getLocation().getDistrict());
                        root.put("state", kisaan.getLocation().getState());
                        root.put("address", kisaan.getLocation().getAddress());
                    }


                }
                if (kisaan.getPhoto()!=null) {
                    root.put("photo",kisaan.getPhoto());
                }
                templateOverride.process(root, writer);

            }

        });

        post(new FreemarkerBasedRoute("/edit/kisaan", "edit_kisaan.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {

                HashMap<String, Object> root = new HashMap<String, Object>();
                root.put("ENTITY_NAME", Constants.ENTITY_KISAAN);
                root.put("entityValue", "kisaan");
                String photoFileName = null;
                InputStream filecontent=null;
                String formFilename = null;

                try {
                    List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request.raw());
                    for (FileItem item : items) {
                        if (item.isFormField()) {
                            // Process regular form field (input type="text|radio|checkbox|etc", select, etc).
                            String fieldname = item.getFieldName();
                            String fieldvalue = item.getString();
                            root.put(fieldname, fieldvalue);
                            // ... (do your job here)
                        } else {
                            // Process form file field (input type="file").
                            String fieldname = item.getFieldName();
                            formFilename = FilenameUtils.getName(item.getName());
                            logger.info(fieldname +" file one");
                            filecontent = item.getInputStream();
                            // ... (do your job here)
                        }
                    }
                } catch (FileUploadException e) {
                    logger.error("not able to undersand ",e);
                    e.printStackTrace();
                }

                String firstName = StringEscapeUtils.escapeHtml4((String)root.get("firstName"));
                String lastName = StringEscapeUtils.escapeHtml4((String)root.get("lastName"));
                String nickName = StringEscapeUtils.escapeHtml4((String)root.get("nickName"));
                String ageString = StringEscapeUtils.escapeHtml4((String)root.get("age"));
                String aadhar = StringEscapeUtils.escapeHtml4((String)root.get("aadhar"));
                String pan = StringEscapeUtils.escapeHtml4((String)root.get("pan"));
                String place = StringEscapeUtils.escapeHtml4((String)root.get("place"));
                String taluka = StringEscapeUtils.escapeHtml4((String)root.get("taluka"));
                String district = StringEscapeUtils.escapeHtml4((String)root.get("district"));
                String state = StringEscapeUtils.escapeHtml4((String)root.get("state"));
                String address = StringEscapeUtils.escapeHtml4((String)root.get("address"));

                String oldFileName = StringEscapeUtils.escapeHtml4((String)root.get("oldFileName"));

                logger.info("HERE,"+nickName+","+oldFileName);

                if (StringUtils.isBlank(nickName)
                        || StringUtils.isBlank(firstName)
                        || StringUtils.isBlank(lastName)
                        || StringUtils.isBlank(place)) {
                    root.put("errors", "NickName, FirstName, LastName, Place are mandatory fields!! Please enter values...");
                    logger.error("NickName, FirstName, LastName, Place are mandatory fields!! Please enter values...");
                    root.put("nickName",nickName);
                    templateOverride.process(root, writer);
                    return;
                } else {
                    // extract tags
                    int age = -1;
                    if (StringUtils.isNotBlank(ageString)) {
                        age = Integer.parseInt(ageString);
                    }

                    //TODO: handle inputs
                    logger.info("yay, Updating Kisaan:" + nickName + " to the system");

                    Kisaan kisaan = kisaanDAO.getBasedOnNickName(nickName);
                    logger.info("formPhoto:"+formFilename);
                    if (StringUtils.isNotBlank(formFilename)) {
                        //((kisaan.getPhoto()==null && formFilename!=Constants.DEFAULT_PHOTO) || !kisaan.getPhoto().equals(formFilename))
                        try {
                            photoFileName = ControllerUtilities.processPhotoUpload(filecontent, formFilename, (String)root.get("entityValue"));
                        } catch (ServletException e) {
                            logger.error("Error with reading photo, so not uploading it", e);
                        }
                    } else {
                        logger.info("No change in profile Photo");
                    }

                    kisaan.setAge(age);
                    kisaan.setFirstName(firstName);
                    kisaan.setLastName(lastName);
                    Location location = new Location(place);
                    location.setCreatedBy(sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request)));
                    ControllerUtilities.handleLocation(location, address, district, taluka, state);
                    kisaan.setLocation(location);
                    NationalIdentity nationalIdentity;
                    if (StringUtils.isNotBlank(pan)) {
                        nationalIdentity = new NationalIdentity(pan);
                        nationalIdentity.setCreatedBy(sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request)));
                        if (StringUtils.isNotBlank(aadhar)) {
                            nationalIdentity.setAadhar(aadhar);
                            kisaan.setNationalIdentity(nationalIdentity);
                        }
                    } else {
                        if (StringUtils.isNotBlank(aadhar)) {
                            nationalIdentity = new NationalIdentity(null, aadhar);
                            nationalIdentity.setCreatedBy(sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request)));
                            kisaan.setNationalIdentity(nationalIdentity);
                        }
                    }

                    kisaan.setUpdatedBy(sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request)));
                    kisaan.setUpdateDate(ControllerUtilities.getCurrentDateStrInYYYY_MM_DD());
                    if (photoFileName!=null) {
                        logger.info("Persisting Photo for Kisaan:"+photoFileName);
                        kisaan.setPhoto(photoFileName);
                    }
                    logger.info(kisaan.toString());
                    kisaanDAO.update(kisaan);
                    root.put("success", "true");
                    root.put("entity",kisaan);
                    templateOverride.process(root, writer);
                    return;
                }
            }

        });


        /*
        post(new FreemarkerBasedRoute("/edit/kisaan", "edit_kisaan.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {

                String firstName = StringEscapeUtils.escapeHtml4(request.raw().getParameter("firstName"));
                String lastName = StringEscapeUtils.escapeHtml4(request.queryParams("lastName"));
                String nickName = StringEscapeUtils.escapeHtml4(request.raw().getParameter("nickName"));
                String ageString = StringEscapeUtils.escapeHtml4(request.queryParams("age"));
                String aadhar = StringEscapeUtils.escapeHtml4(request.queryParams("aadhar"));
                String pan = StringEscapeUtils.escapeHtml4(request.queryParams("pan"));
                String place = StringEscapeUtils.escapeHtml4(request.queryParams("place"));
                String taluka = StringEscapeUtils.escapeHtml4(request.queryParams("taluka"));
                String district = StringEscapeUtils.escapeHtml4(request.queryParams("district"));
                String state = StringEscapeUtils.escapeHtml4(request.queryParams("state"));
                String address = StringEscapeUtils.escapeHtml4(request.queryParams("address"));

                HashMap<String, String> root = new HashMap<String, String>();
                root.put("ENTITY_NAME", Constants.ENTITY_KISAAN);
                root.put("entity", "kisaan");

                root.put("firstName", firstName);
                root.put("lastName", lastName);
                root.put("nickName", nickName);
                root.put("age", ageString);
                root.put("aadhar", aadhar);
                root.put("pan", pan);
                root.put("place", place);
                root.put("taluka", taluka);
                root.put("district", district);
                root.put("state", state);
                root.put("address", address);


                logger.info("HERE,"+nickName);
                Gson gson = new Gson();
                logger.info(gson.toJson(request.queryParams()));
                logger.info(gson.toJson(request.contentType()));
                logger.info(gson.toJson(request.pathInfo()));
                logger.info(gson.toJson(request.headers()));
                if (StringUtils.isBlank(nickName)
                        || StringUtils.isBlank(firstName)
                        || StringUtils.isBlank(lastName)
                        || StringUtils.isBlank(place)) {
                    root.put("errors", "NickName, FirstName, LastName, Place are mandatory fields!! Please enter values...");
                    logger.error("NickName, FirstName, LastName, Place are mandatory fields!! Please enter values...");
                    root.put("nickName","error");
                    templateOverride.process(root, writer);
                    return;
                } else {
                    // extract tags
                    int age = -1;
                    if (StringUtils.isNotBlank(ageString)) {
                        age = Integer.parseInt(ageString);
                    }

                    //TODO: handle inputs
                    logger.info("yay, Updating Kisaan:" + nickName + " to the system");

                    Kisaan kisaan = kisaanDAO.getBasedOnNickName(nickName);

                    kisaan.setAge(age);
                    kisaan.setFirstName(firstName);
                    kisaan.setLastName(lastName);
                    Location location = new Location(place);
                    location.setCreatedBy(sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request)));
                    ControllerUtilities.handleLocation(location, address, district, taluka, state);
                    kisaan.setLocation(location);
                    NationalIdentity nationalIdentity;
                    if (StringUtils.isNotBlank(pan)) {
                        nationalIdentity = new NationalIdentity(pan);
                        nationalIdentity.setCreatedBy(sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request)));
                        if (StringUtils.isNotBlank(aadhar)) {
                            nationalIdentity.setAadhar(aadhar);
                            kisaan.setNationalIdentity(nationalIdentity);
                        }
                    } else {
                        if (StringUtils.isNotBlank(aadhar)) {
                            nationalIdentity = new NationalIdentity(null, aadhar);
                            nationalIdentity.setCreatedBy(sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request)));
                            kisaan.setNationalIdentity(nationalIdentity);
                        }
                    }

                    kisaan.setUpdatedBy(sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request)));
                    kisaan.setUpdateDate(ControllerUtilities.getCurrentDateStrInYYYY_MM_DD());
                    logger.info(kisaan.toString());
                    kisaanDAO.update(kisaan);
                    root.put("success", "true");
                    templateOverride.process(root, writer);
                    return;
                }
            }

        });
        */


    }






    public static void main(String[] args) {
        //
        URLCodec urlCodec = new URLCodec("UTF-8");
        try {
            System.out.println(urlCodec.decode("chandan maloo"));
            System.out.println(urlCodec.decode("chandan%20maloo"));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
