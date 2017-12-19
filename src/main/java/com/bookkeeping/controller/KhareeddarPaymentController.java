package com.bookkeeping.controller;

import com.bookkeeping.DAO.KhareeddarDAO;
import com.bookkeeping.DAO.KhareeddarPaymentDAO;
import com.bookkeeping.DAO.KhareeddarPaymentDAOImpl;
import com.bookkeeping.DAO.KisaanTransactionDAO;
import com.bookkeeping.constants.Constants;
import com.bookkeeping.model.Khareeddar;
import com.bookkeeping.model.KhareeddarPayment;
import com.bookkeeping.utilities.ControllerUtilities;
import com.google.gson.Gson;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * Created by chandan on 6/20/16.
 */
public class KhareeddarPaymentController {

    KhareeddarDAO khareeddarDAO;
    KisaanTransactionDAO kisaanTransactionDAO;
    KhareeddarPaymentDAO khareeddarPaymentDAO;

    static final Logger logger = LoggerFactory.getLogger(KhareeddarPaymentController.class);
    public KhareeddarPaymentController() {
        khareeddarPaymentDAO = SingletonManagerDAO.getInstance().getKhareeddarPaymentDAO();
        kisaanTransactionDAO = SingletonManagerDAO.getInstance().getKisaanTransactionDAO();
        khareeddarDAO = SingletonManagerDAO.getInstance().getKhareeddarDAO();
    }

    public void initializeRoutes() throws IOException {

        get(new Route("/listKhareeddarPayment/khareeddarName/:uniqueKey") {
            @Override
            public Object handle(Request request, Response response) {
                String uniqueKey = request.params(":uniqueKey");
                List<KhareeddarPayment> khareeddarPaymentList = khareeddarPaymentDAO.getBasedOnKhareeddarKey(uniqueKey);

                Gson gson = new Gson();
                return gson.toJson(khareeddarPaymentList);
            }
        });

        get(new Route("/listKhareeddarPayment/khareeddarName/*/date/*") {
            @Override
            public Object handle(Request request, Response response) {
                String[] args= request.splat();
                int size = args.length;
                if(size==2) {
                    String uniqueKey = args[0];
                    String targetDate = args[1];
                    List<KhareeddarPayment> khareeddarPaymentList = khareeddarPaymentDAO.getBasedOnKhareeddarKey(uniqueKey,targetDate);
                    Gson gson = new Gson();
                    return gson.toJson(khareeddarPaymentList);
                }
                logger.error("Bad request:{}",request.splat());
                return "[]";

            }
        });

        get(new FreemarkerBasedRoute("/addKhareeddarPayment", "addKhareeddarPayment.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {

                Map<String,Object> root = new HashMap<String, Object>();
                root.put("ENTITY_NAME", Constants.ENTITY_KHAREEDDAR);
                root.put("entityValue", "khareeddar");
                root.put("entityActual", "kisaanPayment");
                root.put("dt", ControllerUtilities.getCurrentDateStrInYYYY_MM_DD());
                templateOverride.process(root, writer);

            }
        });

        post(new FreemarkerBasedRoute("/addKhareeddarPayment", "addKhareeddarPayment.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                String khareeddarName= StringEscapeUtils.escapeHtml4(request.queryParams("khareeddar"));
                String amount= StringEscapeUtils.escapeHtml4(request.queryParams("amount"));
                String tag= StringEscapeUtils.escapeHtml4(request.queryParams("tag"));
                String dt= StringEscapeUtils.escapeHtml4(request.queryParams("dt"));
                String paymentType= StringEscapeUtils.escapeHtml4(request.queryParams("paymentType"));

                Map<String,Object> root = new HashMap<String, Object>();

                root.put("ENTITY_NAME", Constants.ENTITY_KHAREEDDAR);
                root.put("entityValue", "khareeddar");

                root.put("khareeddar", khareeddarName);
                root.put("amount", amount);
                root.put("tag", tag);
                root.put("dt", dt);
                root.put("paymentType", paymentType);

                if(StringUtils.isBlank(khareeddarName) || StringUtils.isBlank(amount)) {
                    root.put("errors", "Mandatory fields missing, fields marked with * are compulsory, please enter values.");
                    templateOverride.process(root, writer);
                    return;
                }
                if(StringUtils.isBlank(dt) || !ControllerUtilities.verifyDateInFormat(dt)) {
                    root.put("errors", "Input Date is empty OR not in yyyy-mm-dd format...,please correct");
                    templateOverride.process(root, writer);
                    return;

                }

                double amountDouble = 0d;
                try {

                    amountDouble = Double.parseDouble(amount);
                } catch (Exception e) {
                    root.put("errors", "Amount field has to be integer/decimal, no strings allowed");
                    logger.error("Amount field is not double {}",amount,e);
                    templateOverride.process(root, writer);
                    return;

                }

                Khareeddar khareeddar = khareeddarDAO.getBasedOnUniqueKey(khareeddarName);
                if(khareeddar==null) {
                    root.put("errors", "khareeddar:"+khareeddarName+" does not exists, Please create him first");
                    logger.error("khareeddar: {} does not exist in the system, we need to create him",khareeddarName);
                    templateOverride.process(root, writer);
                    return;
                }
                //verifyInputs()
                KhareeddarPayment khareeddarPayment = new KhareeddarPayment(khareeddar.getUniqueKey(),amountDouble,dt);
                khareeddarPayment.setCreatedBy(sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request)));
                if(StringUtils.isNotBlank(tag)) {
                    khareeddarPayment.setTag(tag);
                }
                if(StringUtils.isNotBlank(paymentType)) {
                    khareeddarPayment.setPaymentType(paymentType);
                }
                khareeddarPayment.setEventDate(dt);

                logger.info(khareeddarPayment.toString());

                khareeddarPaymentDAO.add(khareeddarPayment);

                KhareeddarPayment khareeddarPayment1 = khareeddarPaymentDAO.getBasedOnUniqueKey(khareeddarPayment.getUniqueKey());
                root.put("entityActual", "khareeddarPayment");

                if(khareeddarPayment1!=null) {

                    root.put("entityObject", khareeddarPayment1);
                    root.put("success", "true");
                    templateOverride.process(root, writer);
                } else {
                    root.put("errors", "Some error in saving the khareeddarPayment, contact ADMIN, key:"+khareeddarPayment.getUniqueKey());
                    logger.error("Not able to find the inserted KhareeddarPayment for Khareeddar:{}, key:{}, Investigate mongo issue"
                            ,khareeddarName,khareeddarPayment.getUniqueKey());
                    templateOverride.process(root, writer);
                }

            }
        });


    }

    public static void main(String[] args) {
        KhareeddarPaymentDAO khareeddarPaymentDAO = new KhareeddarPaymentDAOImpl();
        Gson gson = new Gson();
        logger.info(gson.toJson(khareeddarPaymentDAO.getBasedOnUniqueKey("Bora__2000.0__2016-06-25")));
    }


}
