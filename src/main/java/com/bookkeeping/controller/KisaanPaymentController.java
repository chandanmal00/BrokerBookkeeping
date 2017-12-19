package com.bookkeeping.controller;

import com.bookkeeping.DAO.*;
import com.bookkeeping.constants.Constants;
import com.bookkeeping.model.Kisaan;
import com.bookkeeping.model.KisaanPayment;
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
public class KisaanPaymentController {

    KisaanDAO kisaanDAO;
    KisaanTransactionDAO kisaanTransactionDAO;
    KisaanPaymentDAO kisaanPaymentDAO;
    SessionDAO sessionDAO;
    static final Logger logger = LoggerFactory.getLogger(KisaanPaymentController.class);
    public KisaanPaymentController() {
        kisaanDAO = SingletonManagerDAO.getInstance().getKisaanDAO();
        kisaanPaymentDAO = SingletonManagerDAO.getInstance().getKisaanPaymentDAO();
        kisaanTransactionDAO = SingletonManagerDAO.getInstance().getKisaanTransactionDAO();
        sessionDAO = SingletonManagerDAO.getInstance().getSessionDAO();

    }

    public void initializeRoutes() throws IOException {

        get(new Route("/listKisaanPayment/kisaanName/:uniqueKey") {
            @Override
            public Object handle(Request request, Response response) {
                String uniqueKey = request.params(":uniqueKey");
                List<KisaanPayment> kisaanPaymentList = kisaanPaymentDAO.getBasedOnKisaan(uniqueKey);
                Gson gson = new Gson();
                return gson.toJson(kisaanPaymentList);
            }
        });

        get(new Route("/listKisaanPayment/kisaanName/*/date/*") {
            @Override
            public Object handle(Request request, Response response) {
                String[] args= request.splat();
                int size = args.length;
                if(size==2) {
                    String uniqueKey = args[0];
                    String targetDate = args[1];
                    List<KisaanPayment> kisaanPaymentList = kisaanPaymentDAO.getBasedOnKisaan(uniqueKey,targetDate);
                    Gson gson = new Gson();
                    return gson.toJson(kisaanPaymentList);
                }
                logger.error("Bad request:{}",request.splat());
                return "[]";

            }
        });


        get(new FreemarkerBasedRoute("/addKisaanPayment", "addKisaanPayment.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                Map<String,String> root = new HashMap<String, String>();
                root.put("ENTITY_NAME", Constants.ENTITY_KISAAN);
                root.put("entityValue", "kisaan");
                root.put("entityActual", "kisaanPayment");
                root.put("dt", ControllerUtilities.getCurrentDateStrInYYYY_MM_DD());

                templateOverride.process(root, writer);

            }
        });

        post(new FreemarkerBasedRoute("/addKisaanPayment", "addKisaanPayment.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                Map<String,Object> root = new HashMap<String, Object>();

                root.put("ENTITY_NAME", Constants.ENTITY_KISAAN);
                root.put("entityValue", "kisaan");

                String kisaanName= StringEscapeUtils.escapeHtml4(request.queryParams("kisaan"));
                String amount= StringEscapeUtils.escapeHtml4(request.queryParams("amount"));
                String tag= StringEscapeUtils.escapeHtml4(request.queryParams("tag"));
                String dt= StringEscapeUtils.escapeHtml4(request.queryParams("dt"));
                String paymentType= StringEscapeUtils.escapeHtml4(request.queryParams("paymentType"));



                root.put("kisaan", kisaanName);
                root.put("amount", amount);
                root.put("tag", tag);
                root.put("dt", dt);
                root.put("paymentType", paymentType);

                if(StringUtils.isBlank(kisaanName) || StringUtils.isBlank(amount)) {
                    root.put("errors", "Mandatory fields missing, fields marked with * are compulsory, please enter values.");
                    templateOverride.process(root, writer);
                    return;
                }

                if(StringUtils.isBlank(dt) || !ControllerUtilities.verifyDateInFormat(dt)) {
                    root.put("errors", "Date field is empty or Input Date not in yyyy-mm-dd format...,please correct");
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

                Kisaan kisaan = kisaanDAO.getBasedOnUniqueKey(kisaanName);
                if(kisaan==null) {
                    root.put("errors", "Kisaan:"+kisaanName+" does not exists, Please create him first");
                    logger.error("Kisaan: {} does not exist in the system, we need to create him",kisaanName);
                    templateOverride.process(root, writer);
                    return;
                }
                //verifyInputs()
                KisaanPayment kisaanPayment = new KisaanPayment(kisaan.getUniqueKey(),amountDouble,dt);
                if(StringUtils.isNotBlank(tag)) {
                    kisaanPayment.setTag(tag);
                }
                if(StringUtils.isNotBlank(paymentType)) {
                    kisaanPayment.setPaymentType(paymentType);
                }

                kisaanPayment.setCreatedBy(sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request)));
                kisaanPaymentDAO.add(kisaanPayment);
                KisaanPayment kisaanPayment1 = kisaanPaymentDAO.getBasedOnUniqueKey(kisaanPayment.getUniqueKey());
                root.put("entityActual", "kisaanPayment");
                if(kisaanPayment1!=null) {
                    root.put("entityObject", kisaanPayment1);
                    root.put("success", "true");
                    templateOverride.process(root, writer);
                } else {

                    root.put("errors", "Some error in saving the kisaanPayment, contact ADMIN, key:"+kisaanPayment.getUniqueKey());
                    logger.error("Not able to find the inserted KisaanPayment for kisaan:{}, key:{}, Investigate mongo issue"
                            ,kisaanName,kisaanPayment.getUniqueKey());
                    templateOverride.process(root, writer);
                }
            }
        });




    }

    public static void main(String[] args) {
        KisaanPaymentDAO kisaanPaymentDAO = new KisaanPaymentDAOImpl();
        Gson gson = new Gson();
        System.out.println( gson.toJson(kisaanPaymentDAO.getDailySummaryForWeekEnding("2016-06-24")));

        KisaanTransactionDAO kisaanTransactionDAO = new KisaanTransactionDAOImpl();
        gson = new Gson();
        System.out.println( gson.toJson(kisaanTransactionDAO.getDailySummaryForWeekEnding("2016-06-24")));

        System.out.println("Quartely monthly Summary:");
        System.out.println( gson.toJson(kisaanTransactionDAO.getMonthlySummaryEndingToday(-3)));
    }
}
