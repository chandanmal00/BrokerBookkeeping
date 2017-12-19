package com.bookkeeping.controller;

import com.bookkeeping.DAO.KhareeddarDAO;
import com.bookkeeping.DAO.KhareeddarPaymentDAO;
import com.bookkeeping.DAO.KisaanTransactionDAO;
import com.bookkeeping.constants.Constants;
import com.bookkeeping.model.*;
import com.bookkeeping.utilities.ControllerUtilities;
import com.google.gson.Gson;
import freemarker.template.TemplateException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * Created by chandan on 6/20/16.
 */
public class KhareeddarController {

    KhareeddarDAO khareeddarDAO;
    KisaanTransactionDAO kisaanTransactionDAO;
    KhareeddarPaymentDAO khareeddarPaymentDAO;
    SessionDAO sessionDAO;
    static final Logger logger = LoggerFactory.getLogger(KhareeddarController.class);
    public KhareeddarController() {
        khareeddarPaymentDAO = SingletonManagerDAO.getInstance().getKhareeddarPaymentDAO();
        kisaanTransactionDAO = SingletonManagerDAO.getInstance().getKisaanTransactionDAO();
        sessionDAO = SingletonManagerDAO.getInstance().getSessionDAO();
        khareeddarDAO = SingletonManagerDAO.getInstance().getKhareeddarDAO();
    }

    public void initializeRoutes() throws IOException {
        // used to display Khareeddar Detail Page

        get(new FreemarkerBasedRoute("/addKhareeddar", "addKhareeddar.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                Map<String,Object> root = new HashMap<String, Object>();
                root.put("entityValue", "khareeddar");
                root.put("ENTITY_KEY_NAME", Constants.ENTITY_KEY_NAME);
                root.put("ENTITY_NAME", Constants.ENTITY_KHAREEDDAR);
                templateOverride.process(root, writer);
            }

        });

        post(new FreemarkerBasedRoute("/addKhareeddar", "addKhareeddar.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer)
                    throws IOException, TemplateException {
                Map<String, String> root = new HashMap<String, String>();
                String username = sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request));
                if (username == null) {
                    response.redirect("/login");
                    return;
                }
                root.put("entityValue", "khareeddar");
                root.put("ENTITY_NAME", Constants.ENTITY_KHAREEDDAR);

                String firstName = StringEscapeUtils.escapeHtml4(request.queryParams("firstName"));
                String lastName = StringEscapeUtils.escapeHtml4(request.queryParams("lastName"));
                String firmName = StringEscapeUtils.escapeHtml4(request.queryParams("firmName"));
                String place = StringEscapeUtils.escapeHtml4(request.queryParams("place"));
                String address = StringEscapeUtils.escapeHtml4(request.queryParams("address"));
                String taluka = StringEscapeUtils.escapeHtml4(request.queryParams("taluka"));
                String district = StringEscapeUtils.escapeHtml4(request.queryParams("district"));
                String state = StringEscapeUtils.escapeHtml4(request.queryParams("state"));

                root.put("firstName", firstName);
                root.put("lastName", lastName);
                root.put("firmName", firmName);
                root.put("place", place);
                root.put("taluka", taluka);
                root.put("district", address);
                root.put("address", district);
                root.put("state", state);

                if (StringUtils.isBlank(firmName) || StringUtils.isBlank(firstName) || StringUtils.isBlank(lastName) || StringUtils.isBlank(place)) {
                    root.put("errors", "FirmName, FirstName, LastName, Place are mandatory fields!!! Please enter values...");
                    templateOverride.process(root, writer);
                } else {
                    // extract tags


                    //TODO: handle inputs
                    logger.info("yay, welcoming Khareeddar:" + firmName + " to the system");

                    Khareeddar khareeddar = new Khareeddar(firmName);

                    khareeddar.setFirstName(firstName);
                    khareeddar.setLastName(lastName);

                    Location location = new Location(place);
                    location.setCreatedBy(sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request)));
                    ControllerUtilities.handleLocation(location, address, district, taluka, state);

                    khareeddar.setLocation(location);
                    khareeddar.setCreatedBy(sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request)));
                    khareeddarDAO.add(khareeddar);
                    //writing it
                    root.put("success", "true");
                    templateOverride.process(root, writer);
                }
            }
        });


        get(new FreemarkerBasedRoute("/khareeddar/:uniqueKey", "entity.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                Map<String,Object> root = new HashMap<String, Object>();
                String username = sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request));
                if (username == null) {
                    response.redirect("/login");
                    return;
                }
                String uniqueKey = request.params(":uniqueKey");
                if(StringUtils.isBlank(uniqueKey)) {
                    response.redirect("/post_not_found/khareeddar/"+uniqueKey);
                }
               // String username = sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request));
                logger.info("Calling route /khareeddar: get " + uniqueKey);
                Khareeddar khareeddar = khareeddarDAO.getBasedOnFirmName(uniqueKey);

                if (khareeddar == null) {
                    //TODO: create khareeddar not found
                    response.redirect("/post_not_found/khareeddar/"+uniqueKey);
                }
                else {

                    List<KisaanTransaction> kisaanTransactionList = kisaanTransactionDAO.getBasedOnKhareeddarKeyWithLimit(uniqueKey,Constants.LIMIT_ROWS);
                    List<KhareeddarPayment> khareeddarPaymentList = khareeddarPaymentDAO.getBasedOnKhareeddarKeyWithLimit(uniqueKey,Constants.LIMIT_ROWS);
                    root.put(Constants.TRANSACTIONS,kisaanTransactionList);
                    root.put(Constants.PAYMENTS,khareeddarPaymentList);
                    root.put(Constants.ENTITY,khareeddar);
                    root.put("entityValue","khareeddar");
                    root.put("rows",Constants.LIMIT_ROWS);
                    root.put(Constants.ENTITY_NAME,Constants.ENTITY_KHAREEDDAR);
                    root.put(Constants.TOTAL_PAYMENT_AMOUNT,khareeddarPaymentDAO.paymentSumBasedOnKhareeddar(uniqueKey));
                    //root.put(Constants.TOTAL_TRANSACTION_AMOUNT,kisaanTransactionDAO.transactionSumForKhareeddar(uniqueKey));
                    root.put("summ",kisaanTransactionDAO.transactionSumForKhareeddarNew(uniqueKey));

                    /*
                    root.put("weeklyPayment", khareeddarPaymentDAO.getDailySummaryNDatesEndingToday(-7));
                    root.put("weeklyTransactions", kisaanTransactionDAO.getDailySummaryNDatesEndingToday(-7));
                    root.put("monthlyPayment", khareeddarPaymentDAO.getMonthlySummaryEndingToday(-3));
                    root.put("monthlyTransactions", kisaanTransactionDAO.getMonthlySummaryEndingToday(-3));
*/

                    /*
                    root.put("weeklyPayment", khareeddarPaymentDAO.getEntityDailySummaryNDatesEndingToday(-7, uniqueKey, "khareeddar"));
                    root.put("weeklyTransactions", kisaanTransactionDAO.getEntityDailySummaryNDatesEndingToday(-7, uniqueKey, "khareeddar"));

                    root.put("monthlyPayment", khareeddarPaymentDAO.getEntityMonthlySummaryEndingToday(-3, uniqueKey, "khareeddar"));
                    root.put("monthlyTransactions", kisaanTransactionDAO.getEntityMonthlySummaryEndingToday(-3,uniqueKey, "khareeddar"));
                    */

                    List<Document> weeklyPayments=khareeddarPaymentDAO.getEntityDailySummaryNDatesEndingToday(-7, uniqueKey, "khareeddar");
                    List<Document> weeklyTransactions=kisaanTransactionDAO.getEntityDailySummaryNDatesEndingToday(-7, uniqueKey, "khareeddar");
                    Gson gson = new Gson();

                    List<Document> monthlyPayments=khareeddarPaymentDAO.getEntityMonthlySummaryEndingToday(-3, uniqueKey, "khareeddar");
                    List<Document> monthlyTransactions=kisaanTransactionDAO.getEntityMonthlySummaryEndingToday(-3, uniqueKey, "khareeddar");

                    root.put("weeklyPayments", weeklyPayments);
                    root.put("weeklyTransactions", weeklyTransactions);
                    Map<String, Tuple<Document>> joinMapWeekly = ControllerUtilities.joinByDate(weeklyTransactions, weeklyPayments);
                    root.put("joinMapWeekly", joinMapWeekly);


                    root.put("monthlyPayments", monthlyPayments);
                    root.put("monthlyTransactions", monthlyTransactions);
                    Map<String, Tuple<Document>> joinMapMonthly = ControllerUtilities.joinByDate(monthlyTransactions, monthlyPayments);
                    root.put("joinMapMonthly", joinMapMonthly);


                    templateOverride.process(root, writer);
                }
            }
        });

        get(new FreemarkerBasedRoute("/edit/khareeddar/:uniqueKey", "edit_khareeddar.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                Map<String, Object> root = new HashMap<String, Object>();
                root.put("ENTITY_NAME", Constants.ENTITY_KHAREEDDAR);
                root.put("entityValue", "khareeddar");


                String uniqueKey = StringEscapeUtils.escapeHtml4(request.params(":uniqueKey"));

                if(StringUtils.isBlank(uniqueKey)) {
                    response.redirect("/post_not_found/khareeddar/"+uniqueKey);
                }
                root.put("firmName", uniqueKey);
                logger.info("Calling route /edit/khareeddar: get " + uniqueKey);
                Khareeddar khareeddar =khareeddarDAO.getBasedOnUniqueKey(uniqueKey);
                root.put("entity", khareeddar);

                if (khareeddar == null) {
                    //TODO: create kisaan not found
                    response.redirect("/post_not_found/khareeddar/"+uniqueKey);
                    return;
                } else {

                    root.put("firstName", khareeddar.getFirstName());
                    root.put("lastName", khareeddar.getLastName());
                    root.put("nickName", khareeddar.getFirstName());

                    if (khareeddar.getLocation() != null) {
                        root.put("place", khareeddar.getLocation().getPlace());
                        root.put("taluka", khareeddar.getLocation().getTaluka());
                        root.put("district", khareeddar.getLocation().getDistrict());
                        root.put("state", khareeddar.getLocation().getState());
                        root.put("address", khareeddar.getLocation().getAddress());
                    }
                }

                if (khareeddar.getPhoto()!=null) {
                    root.put("photo",khareeddar.getPhoto());
                }
                templateOverride.process(root, writer);

            }

        });

        post(new FreemarkerBasedRoute("/edit/khareeddar", "edit_khareeddar.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {

                HashMap<String, Object> root = new HashMap<String, Object>();
                root.put("ENTITY_NAME", Constants.ENTITY_KHAREEDDAR);
                root.put("entityValue", "khareeddar");
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
                        } else {
                            // Process form file field (input type="file").
                            String fieldname = item.getFieldName();
                            formFilename = FilenameUtils.getName(item.getName());
                            logger.info(fieldname +" file one");
                            filecontent = item.getInputStream();
                        }
                    }
                } catch (FileUploadException e) {
                    logger.error("not able to undersand ",e);
                    e.printStackTrace();
                }

                String firstName = StringEscapeUtils.escapeHtml4((String)root.get("firstName"));
                String lastName = StringEscapeUtils.escapeHtml4((String)root.get("lastName"));
                String firmName = StringEscapeUtils.escapeHtml4((String)root.get("firmName"));
                String place = StringEscapeUtils.escapeHtml4((String)root.get("place"));
                String taluka = StringEscapeUtils.escapeHtml4((String)root.get("taluka"));
                String district = StringEscapeUtils.escapeHtml4((String)root.get("district"));
                String state = StringEscapeUtils.escapeHtml4((String)root.get("state"));
                //String address = StringEscapeUtils.escapeHtml4(request.queryParams("address"));
                String address = StringEscapeUtils.escapeHtml4((String)root.get("address"));
                String oldFileName = StringEscapeUtils.escapeHtml4((String)root.get("oldFileName"));

                root.put("firstName", firstName);
                root.put("lastName", lastName);
                root.put("firmName", firmName);
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
                logger.info("HERE,"+firmName+","+oldFileName);

                if (StringUtils.isBlank(firmName)
                        || StringUtils.isBlank(firstName)
                        || StringUtils.isBlank(lastName)
                        || StringUtils.isBlank(place)) {
                    root.put("errors", "FirmName, firstName, last Name, place are mandatory fields, Please enter values.");
                    root.put("firmName",firmName);
                    logger.error("FirmName, firstName, last Name, place are mandatory fields,please enter values.");
                    templateOverride.process(root, writer);
                    return;
                } else {
                    // extract tags


                    //TODO: handle inputs
                    logger.info("yay, Updating Khareeddar:" + firmName + " to the system");

                    Khareeddar khareeddar = khareeddarDAO.getBasedOnFirmName(firmName);
                    root.put("entity", khareeddar);

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

                    khareeddar.setFirstName(firstName);
                    khareeddar.setLastName(lastName);
                    Location location = new Location(place);
                    location.setCreatedBy(sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request)));
                    ControllerUtilities.handleLocation(location, address, district, taluka, state);
                    khareeddar.setLocation(location);

                    khareeddar.setUpdatedBy(sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request)));
                    khareeddar.setUpdateDate(ControllerUtilities.getCurrentDateStrInYYYY_MM_DD());
                    if (photoFileName!=null) {
                        logger.info("Persisting Photo for Khareeddar:"+photoFileName);
                        khareeddar.setPhoto(photoFileName);
                    }
                    logger.info(khareeddar.toString());
                    khareeddarDAO.update(khareeddar);
                    root.put("success", "true");
                    templateOverride.process(root, writer);
                    return;
                }
            }

        });

        get(new Route("/listKhareeddars") {
            @Override
            public Object handle(Request request, Response response) {
                List<Khareeddar> khareeddarList = khareeddarDAO.list();
                Gson gson = new Gson();
                return gson.toJson(khareeddarList);
            }
        });



    }
}
