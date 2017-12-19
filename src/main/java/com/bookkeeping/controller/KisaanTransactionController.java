package com.bookkeeping.controller;

import com.bookkeeping.Config.BrokerSingleton;
import com.bookkeeping.DAO.*;
import com.bookkeeping.constants.Constants;
import com.bookkeeping.model.*;
import com.bookkeeping.utilities.BookKeepingException;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * Created by chandan on 6/20/16.
 */
public class KisaanTransactionController {

    KisaanDAO kisaanDAO;
    KisaanTransactionDAO kisaanTransactionDAO;
    KisaanPaymentDAO kisaanPaymentDAO;
    KhareeddarDAO khareeddarDAO;
    TransactionItemDAO transactionItemDAO;
    SessionDAO sessionDAO;
    static final Logger logger = LoggerFactory.getLogger(KisaanTransactionController.class);
    public KisaanTransactionController() {
        kisaanDAO = SingletonManagerDAO.getInstance().getKisaanDAO();
        kisaanPaymentDAO = SingletonManagerDAO.getInstance().getKisaanPaymentDAO();
        kisaanTransactionDAO = SingletonManagerDAO.getInstance().getKisaanTransactionDAO();
        sessionDAO = SingletonManagerDAO.getInstance().getSessionDAO();
        khareeddarDAO = SingletonManagerDAO.getInstance().getKhareeddarDAO();
        transactionItemDAO = SingletonManagerDAO.getInstance().getTransactionItemDAO();
    }

    public void initializeRoutes() throws IOException {

        get(new Route("/listKisaanTransactions/kisaanName/:uniqueKey") {
            @Override
            public Object handle(Request request, Response response) {
                String uniqueKey = request.params(":uniqueKey");
                List<KisaanTransaction> kisaanTransactions = kisaanTransactionDAO.getBasedOnKisaanKey(uniqueKey);
                Gson gson = new Gson();
                return gson.toJson(kisaanTransactions);
            }
        });


        get(new Route("/listKisaanTransactions/khareeddarName/:uniqueKey") {
            @Override
            public Object handle(Request request, Response response) {
                String uniqueKey = request.params(":uniqueKey");
                List<KisaanTransaction> kisaanTransactions = kisaanTransactionDAO.getBasedOnKisaanKey(uniqueKey);
                Gson gson = new Gson();
                return gson.toJson(kisaanTransactions);
            }
        });

        get(new Route("/listKisaanTransactions/khareeddarName/*/date/*") {
            @Override
            public Object handle(Request request, Response response) {
                String[] args= request.splat();
                int size = args.length;
                if(size==2) {
                    String uniqueKey = args[0];
                    String targetDate = args[1];
                    List<KisaanTransaction> kisaanTransactions = kisaanTransactionDAO.getBasedOnKisaanKey(uniqueKey,targetDate);
                    Gson gson = new Gson();
                    return gson.toJson(kisaanTransactions);
                }
                logger.error("Bad request:{}",request.splat());
                return "[]";

            }
        });
        //listKisaanTransactions/kisaanName/:uniqueKey/:targetDate
        get(new Route("/listKisaanTransactions/kisaanName/*/date/*") {
            @Override
            public Object handle(Request request, Response response) {

                String[] args= request.splat();
                int size = args.length;
                if(size==2) {
                    String uniqueKey = args[0];
                    String targetDate = args[1];
                    List<KisaanTransaction> kisaanTransactions = kisaanTransactionDAO.getBasedOnKisaanKey(uniqueKey,targetDate);
                    Gson gson = new Gson();
                    return gson.toJson(kisaanTransactions);
                }
                logger.error("Bad request:{}",request.splat());
                return "[]";

            }
        });


        get(new FreemarkerBasedRoute("/add/kisaanTransaction", "addTransaction.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {

                Map<String,Object> root = new HashMap<String, Object>();
                /*
                String username = sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request));
                if (username == null) {
                    response.redirect("/login");
                    return;
                }
                root.put("username", username);
                */
                root.put("ENTITY_KISAAN", Constants.ENTITY_KISAAN);
                root.put("ENTITY_KHAREEDDAR", Constants.ENTITY_KHAREEDDAR);
                root.put("ENTITY_TRANSACTION_ITEM", Constants.ENTITY_TRANSACTION_ITEM);
                root.put("entity_kisaan", "kisaan");
                root.put("entity_khareeddar", "khareeddar");;
                root.put("entity_item", "itemTransaction");
                root.put("hamaaliRate",Constants.HAMALI_RATE);
                root.put("mapariRate",Constants.MAPARI_RATE);
                root.put("brokerage",Constants.BROKERAGE_RATE);
                root.put("cashRate",0);
                root.put("paidAmount", 0);
                root.put("CntItems", Constants.MAX_ITEM_ROWS);

                root.put("entityActual", "kisaanTransaction");
                root.put("dt", ControllerUtilities.getCurrentDateStrInYYYY_MM_DD());


                templateOverride.process(root, writer);

            }
        });

        post(new FreemarkerBasedRoute("/add/kisaanTransaction", "addTransaction.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                float delta = 0.2f;
                double deltaDouble = 0.2d;
                Map<String, Object> root = new HashMap<String, Object>();
                root.put("ENTITY_KHAREEDDAR", Constants.ENTITY_KHAREEDDAR);
                root.put("ENTITY_KISAAN", Constants.ENTITY_KISAAN);
                root.put("ENTITY_ITEM", Constants.ENTITY_TRANSACTION_ITEM);
                root.put("entity_kisaan", "kisaan");
                root.put("entity_khareeddar", "khareeddar");
                root.put("entity_item", "itemTransaction");
                root.put("entityActual", "kisaanTransaction");
                root.put("shop", BrokerSingleton.broker);
                root.put("CntItems", Constants.MAX_ITEM_ROWS);

                /*
                String username = sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request));
                if (username == null) {
                    response.redirect("/login");
                    return;
                }
                */

                List<ItemSell> itemSoldList = new ArrayList<ItemSell>();

                BigDecimal amount = BigDecimal.ZERO;
                float cashRate = 0;
                float brokerage = 0;
                float mapariAmount = 0;
                float hamaaliAmount = 0;
                float mapariRate = 0;
                float hamaaliRate = 0;
                double paidAmount = 0;
                double totalAmount = 0;
                double actualTotal = 0;

                float quantity = 0;
                float priceFloat = 0f;
                String kisaanName = StringEscapeUtils.escapeHtml4(request.queryParams("kisaan"));
                String dt = StringEscapeUtils.escapeHtml4(request.queryParams("dt"));
                String actualTotalStr = StringEscapeUtils.escapeHtml4(request.queryParams("actualTotal"));
                String countItems = StringEscapeUtils.escapeHtml4(request.queryParams("countItems"));
                String khareeddarName = StringEscapeUtils.escapeHtml4(request.queryParams("khareeddar"));
                String cashRateStr = StringEscapeUtils.escapeHtml4(request.queryParams("cashRate"));
                String cashRateAmountStr = StringEscapeUtils.escapeHtml4(request.queryParams("cashRateAmount"));

                String brokerageStr = StringEscapeUtils.escapeHtml4(request.queryParams("brokerage"));
                String brokerageAmountStr = StringEscapeUtils.escapeHtml4(request.queryParams("brokerageAmount"));
                String mapariRateStr = StringEscapeUtils.escapeHtml4(request.queryParams("mapariRate"));
                String hamaaliRateStr = StringEscapeUtils.escapeHtml4(request.queryParams("hamaaliRate"));
                String mapariAmountStr = StringEscapeUtils.escapeHtml4(request.queryParams("mapariAmount"));
                String hamaaliAmountStr = StringEscapeUtils.escapeHtml4(request.queryParams("hamaaliAmount"));
                String paidAmountStr = StringEscapeUtils.escapeHtml4(request.queryParams("paidAmount"));
                String totalAmountStr = StringEscapeUtils.escapeHtml4(request.queryParams("totalAmount"));
                String paymentType = StringEscapeUtils.escapeHtml4(request.queryParams("paymentType"));
                String deductionsStr = StringEscapeUtils.escapeHtml4(request.queryParams("deductions"));
                float brokerageAmount = 0f;
                float cashRateAmount = 0f;


                //If paidAmount is not set, make it 0 for now
                if (StringUtils.isBlank(paidAmountStr)) {
                    paidAmountStr = "0";
                }

                root.put("kisaan", kisaanName);
                root.put("khareeddar", khareeddarName);
                root.put("dt", dt);
                root.put("deductions", deductionsStr);
                root.put("brokerageAmount", brokerageAmountStr);
                root.put("cashRateAmount", cashRateAmountStr);

                /*
                root.put("item", itemBarcode);
                root.put("quantity", quantityStr);
                root.put("price", priceStr);
                root.put("amount", amountStr);
                */
                root.put("cashRate", cashRateStr);
                root.put("mapariRate", mapariRateStr);
                root.put("hamaaliRate", hamaaliRateStr);
                root.put("mapariAmount", mapariAmountStr);
                root.put("hamaaliAmount", hamaaliAmountStr);
                root.put("brokerage", brokerageStr);
                root.put("paidAmount", paidAmountStr);
                root.put("totalAmount", totalAmountStr);
                root.put("paymentType", paymentType);
                root.put("actualTotal", actualTotalStr);
                if (StringUtils.isBlank(dt) || !ControllerUtilities.verifyDateInFormat(dt)) {
                    root.put(Constants.ERROR, "Transaction date field is either empty or is not in yyyy-mm-dd format, e.g 2016-12-01 for 1st Dec, 2016, input:" + dt);
                    logger.error("Transaction date field is either empty or is not in yyyy-mm-dd format, e.g 2016-12-01 for 1st Dec, 2016, input:" + dt);
                    templateOverride.process(root, writer);
                    return;
                }

                String itemBarcode = null;
                String quantityStr = null;
                String priceStr = null;
                String amountStr = null;
                String bhartiStr = null;
                int actualCounter = 0;
                double runningTotal = 0;
                float bhartiFloat = 0f;
                logger.info("No. of items most probably is {}", countItems);
                int errorCount = 0;
                List<String> errorMessages = new ArrayList<String>();

                try {

                    int counter = 0;
                    //Start reading all items one by one, the only ones with value are considered.
                    while (counter <= Constants.MAX_ITEM_ROWS) {
                        itemBarcode = StringEscapeUtils.escapeHtml4(request.queryParams("itemName_" + counter));
                        quantityStr = StringEscapeUtils.escapeHtml4(request.queryParams("quantity_" + counter));
                        priceStr = StringEscapeUtils.escapeHtml4(request.queryParams("price_" + counter));
                        amountStr = StringEscapeUtils.escapeHtml4(request.queryParams("amount_" + counter));
                        bhartiStr = StringEscapeUtils.escapeHtml4(request.queryParams("bharti_" + counter));

                        counter++;
                        if (StringUtils.isBlank(itemBarcode)
                                || StringUtils.isBlank(quantityStr)
                                || StringUtils.isBlank(amountStr)
                                || StringUtils.isBlank(priceStr)
                                || StringUtils.isBlank(bhartiStr)) {
                            continue;
                            //We are ignoring this input as all required fields are missing, there was some issue here
                        } else {
                            logger.info("Additional:{} --> {},{},{},{}", counter, itemBarcode, quantityStr, priceStr, amountStr);
                            quantity = Float.parseFloat(quantityStr);
                            priceFloat = ControllerUtilities.formatDecimalValue(Float.parseFloat(priceStr));
                            bhartiFloat = ControllerUtilities.formatDecimalValue(Float.parseFloat(bhartiStr));
                            ItemSell itemSell = new ItemSell(itemBarcode, bhartiFloat, quantity, priceFloat, dt);
                            root.put("itemName_" + counter, itemBarcode);
                            root.put("price_" + counter, priceStr);
                            root.put("quantity_" + counter, quantityStr);
                            root.put("amount_" + counter, amountStr);
                            root.put("bharti_" + counter, bhartiStr);
                            logger.info(itemSell.toString());
                            amount = ControllerUtilities.formatDecimalValue(BigDecimal.valueOf(Double.parseDouble(amountStr)));
                            if (quantity <= 0 || priceFloat <= 0 || amount.floatValue()<=0
                                    || (itemSell.getAmount().doubleValue()!=amount.doubleValue()
                                    && Math.abs(itemSell.getAmount().doubleValue() - amount.doubleValue()) > deltaDouble )
                                    || bhartiFloat > 1 || bhartiFloat < 0) {
                                errorMessages.add("Quantity/Price/Amount field cannot be NEGATIVE or ZERO and calcAmount has to be same as amount input, itemName:" + itemBarcode + ", quantity:" + quantityStr + ", price:" + priceStr + ", amount:" + amountStr + ", calcAmount:" + itemSell.getAmount());
                                logger.error("Negative or ZERO values as well amountTotal changes not allowed for quantity:{}, price:{}, amount:{}, amountCalc:{}, itemName:{}", quantity, priceStr, amountStr, itemSell.getAmount(), itemBarcode);
                                errorCount++;
                            }
                            itemSell.setCreatedBy(sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request)));
                            itemSoldList.add(itemSell);

                            TransactionItem transactionItem = SingletonManagerDAO.getInstance().getTransactionItemDAO().getBasedOnUniqueKey(itemBarcode);
                            if (transactionItem == null) {
                                //New Item, lets add it
                                TransactionItem transactionItem1 = new TransactionItem(itemBarcode);
                                SingletonManagerDAO.getInstance().getTransactionItemDAO().add(transactionItem1);

                            }

                            runningTotal += itemSell.getAmount().doubleValue();
                            actualCounter++;
                        }
                    }
                    logger.info("Read total itemsCount:{}, input counter:{}",actualCounter,countItems);

                } catch (Exception e) {
                    root.put(Constants.ERROR, "There was some change in the actualCounter or one of the fields have bad inputs, please report this to sysadmin, countItem:" + actualCounter);
                    //logger.error("Bad countItems:{} field or one of the fields have bad inputs, Most likely its quantity:{}, price:{}, amount:{} , this needs to be reported"
                    //        , countItems, quantityStr, priceStr, amountStr, e);
                    logger.error("Bad actualCounter:{} field or one of the fields have bad inputs, Most likely its quantity:{}, price:{}, amount:{} , this needs to be reported"
                            , actualCounter, quantityStr, priceStr, amountStr, e);
                            templateOverride.process(root, writer);
                    return;

                }

                if (errorCount>0) {
                    logger.error("Negative or ZERO values for one of the items, no. of items failed:"+errorCount);
                    root.put(Constants.ERROR, StringUtils.join(errorMessages,"<br>"));
                    templateOverride.process(root, writer);
                    return;
                }

                Gson gson = new Gson();
                System.out.println(gson.toJson(root));
                float deductions = 0f;


                logger.info("{},{},{},{},no. of items Added:{}", kisaanName, paymentType, khareeddarName, paidAmountStr, actualCounter);

                logger.info( totalAmountStr + ":" + deductionsStr + ":" + brokerageStr + ":" + brokerageAmountStr + ":" + mapariRateStr + ":" + mapariAmountStr
                            + hamaaliRateStr + ":" + hamaaliAmountStr + ":" + kisaanName);
                if (StringUtils.isBlank(kisaanName)
                        || StringUtils.isBlank(paymentType)
                        || StringUtils.isBlank(khareeddarName)
                        || StringUtils.isBlank(paidAmountStr)
                        || StringUtils.isBlank(totalAmountStr)
                        || StringUtils.isBlank(deductionsStr)
                        || StringUtils.isBlank(brokerageStr)
                        || StringUtils.isBlank(brokerageAmountStr)
                        || StringUtils.isBlank(mapariRateStr)
                        || StringUtils.isBlank(mapariAmountStr)
                        || StringUtils.isBlank(hamaaliRateStr)
                        || StringUtils.isBlank(hamaaliAmountStr)
                        || StringUtils.isBlank(actualTotalStr)
                        ) {
                    logger.info(paymentType + ":" + paidAmountStr + ":" + totalAmountStr + ":" + deductionsStr + ":" + brokerageStr + ":" + brokerageAmountStr + ":" + mapariRateStr + ":" + mapariAmountStr
                            + hamaaliRateStr + ":" + hamaaliAmountStr + ":" + kisaanName);
                    root.put(Constants.ERROR, "Mandatory fields missing, fields marked with * are compulsory, please enter values.");
                    templateOverride.process(root, writer);
                    return;
                } else {

                    logger.info("HERE, we are good until now");

                    try {
                        if (StringUtils.isNotBlank(cashRateStr)) {
                            cashRate = ControllerUtilities.formatDecimalValue(Float.parseFloat(cashRateStr));
                            if (cashRate < 0 && cashRate > 100) {
                                throw new BookKeepingException("Discount Percent cannot be less than 0 or greater than 100, value:" + cashRate);
                            }
                        }
                        if (StringUtils.isNotBlank(brokerageStr)) {
                            brokerage = ControllerUtilities.formatDecimalValue(Float.parseFloat(brokerageStr));
                            if (brokerage < 0 && brokerage > 100) {
                                throw new BookKeepingException("Discount Percent cannot be less than 0 or greater than 100, value:" + brokerage);
                            }
                        }

                        if (StringUtils.isNotBlank(mapariRateStr)) {
                            mapariRate = ControllerUtilities.formatDecimalValue(Float.parseFloat(mapariRateStr));
                            if (mapariRate < 0) {
                                throw new BookKeepingException("mapariRate value cannot be less than 0, value:" + mapariRate);
                            }
                        }
                        if (StringUtils.isNotBlank(hamaaliRateStr)) {
                            hamaaliRate = ControllerUtilities.formatDecimalValue(Float.parseFloat(hamaaliRateStr));
                            if (hamaaliRate < 0) {
                                throw new BookKeepingException("hamaaliRate value cannot be less than 0, value:" + hamaaliRate);
                            }
                        }

                        if (StringUtils.isNotBlank(mapariAmountStr)) {
                            mapariAmount = ControllerUtilities.formatDecimalValue(Float.parseFloat(mapariAmountStr));
                            if (mapariAmount < 0) {
                                throw new BookKeepingException("mapariAmount value cannot be less than 0, value:" + mapariAmount);
                            }
                        }
                        if (StringUtils.isNotBlank(hamaaliAmountStr)) {
                            hamaaliAmount = ControllerUtilities.formatDecimalValue(Float.parseFloat(hamaaliAmountStr));
                            if (hamaaliAmount < 0) {
                                throw new BookKeepingException("hamaaliAmount value cannot be less than 0, value:" + hamaaliAmount);
                            }
                        }

                        if (StringUtils.isNotBlank(brokerageAmountStr)) {
                            brokerageAmount = ControllerUtilities.formatDecimalValue(Float.parseFloat(brokerageAmountStr));
                            if (brokerageAmount < 0) {
                                throw new BookKeepingException("brokerageAmount value cannot be less than 0, value:" + brokerageAmount);
                            }
                        }

                        if (StringUtils.isNotBlank(cashRateAmountStr)) {
                            cashRateAmount = ControllerUtilities.formatDecimalValue(Float.parseFloat(cashRateAmountStr));
                            if (cashRateAmount < 0) {
                                throw new BookKeepingException("cashRateAmount value cannot be less than 0, value:" + cashRateAmount);
                            }
                        }

                        if (StringUtils.isNotBlank(deductionsStr)) {
                            deductions = ControllerUtilities.formatDecimalValue(Float.parseFloat(deductionsStr));
                            if (deductions < 0) {
                                throw new BookKeepingException("deductions value cannot be less than 0, value:" + deductions);
                            }
                        }


                        float deductionCalc = ControllerUtilities.formatDecimalValue(mapariAmount + hamaaliAmount + brokerageAmount + cashRateAmount);
                        if (deductionCalc != deductions && Math.abs(deductionCalc - deductions) >= deltaDouble) {
                            throw new BookKeepingException("Deductions value do not match,input:" + deductions + ", calculated value:" + deductionCalc);
                        }

                        paidAmount = ControllerUtilities.formatDecimalValue(Double.parseDouble(paidAmountStr));
                        totalAmount = ControllerUtilities.formatDecimalValue(Double.parseDouble(totalAmountStr));
                        actualTotal = ControllerUtilities.formatDecimalValue(Double.parseDouble(actualTotalStr));

                        float brokerageRateCalc = (float) ControllerUtilities.formatDecimalValue(actualTotal * brokerage / 100);


                        if (ControllerUtilities.formatDecimalValue(brokerageAmount) != brokerageRateCalc) {
                            throw new BookKeepingException("Brokerage rate calculation does not match,input:" + brokerageAmount + ", calculated value:" + brokerageRateCalc);
                        }

                        double cashRateAmountCal = (float) ControllerUtilities.formatDecimalValue(actualTotal * cashRate / 100);
                        if (ControllerUtilities.formatDecimalValue(cashRateAmount) != cashRateAmountCal) {
                            throw new BookKeepingException("cashRateAmountCal rate calculation does not match,input:" + cashRateAmount + ", calculated value:" + cashRateAmountCal);
                        }


                    } catch (BookKeepingException e) {
                        root.put(Constants.ERROR, e.getMessage());
                        logger.error(e.getMessage(), e);
                        templateOverride.process(root, writer);
                        return;

                    } catch (Exception e) {
                        root.put(Constants.ERROR, "Integer or float expected but found String/Other for one of the fields: paidAmount, totalAmount, additionalCharges, discoutPercent, discount");
                        logger.error("Some values were non integers/float", e);
                        templateOverride.process(root, writer);
                        return;
                    }


                    if (runningTotal != actualTotal && Math.abs(runningTotal - actualTotal) >= deltaDouble) {
                        root.put(Constants.ERROR, String.format("RunningTotal:" + runningTotal + " does not match ActualTotal:" + actualTotal + ", so not adding transaction, please check with sysadmin"));
                        logger.error("RunningTotal:{} does not match ActualTotal:{}, so not adding transaction, please check with sysadmin", runningTotal, actualTotal);
                        templateOverride.process(root, writer);
                        return;

                    }


                    double calcTotal = actualTotal - (double)deductions;

                    if (calcTotal!= totalAmount && Math.abs(calcTotal - totalAmount) > deltaDouble) {
                        logger.error("CalculatedTotal(A=B-C) and TotalAmount from the form do not match, totalAmount(A):{},actualTotal(B):{},deductions(C):{}, calc:{}", actualTotal, totalAmount, deductions,calcTotal);
                        root.put(Constants.ERROR, String.format("CalculatedTotal(A=B-C) and TotalAmount from the form do not match, totalAmount(A):" + totalAmount + ",actualTotal(B):" + actualTotal + ",deductions(C):" + deductions));
                        templateOverride.process(root, writer);
                        return;
                    }

                    if (actualTotal <= 0 || totalAmount <= 0) {
                        logger.error("Negative or ZERO value not allowed in actualTotal:{} and totalAmount:{}", actualTotal, totalAmount);
                        root.put(Constants.ERROR, String.format("Negative or ZERO value not allowed in actualTotal:" + actualTotal + " and totalAmount:" + totalAmount));
                        templateOverride.process(root, writer);
                        return;
                    }

                    Khareeddar khareeddar = khareeddarDAO.getBasedOnUniqueKey(khareeddarName);
                    if (khareeddar == null) {
                        logger.info("Creating khareeddar:{} as it does not exists in the system");
                        khareeddar = new Khareeddar(khareeddarName);
                        khareeddar.setCreatedBy(sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request)));
                        khareeddarDAO.add(khareeddar);
                    }


                    Kisaan kisaan = kisaanDAO.getBasedOnUniqueKey(kisaanName);
                    if (kisaan == null) {
                        logger.info("Kisaan:{} does not exists, so creating it", kisaan);
                        kisaan = new Kisaan(kisaanName);
                        kisaan.setCreatedBy(sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request)));
                        kisaanDAO.add(kisaan);
                    }


                    KisaanTransaction kisaanTransaction = new KisaanTransaction(
                            kisaanName,
                            khareeddarName,
                            dt,
                            itemSoldList
                    );

                    kisaanTransaction.setMapariRate(mapariRate);
                    kisaanTransaction.setHamaaliRate(hamaaliRate);
                    kisaanTransaction.setBrokerCommission(brokerage);
                    kisaanTransaction.setCashSpecialRate(cashRate);
                    kisaanTransaction.calculate();

                    if ( kisaanTransaction.getAmountHamaali() != ControllerUtilities.formatDecimalValue(hamaaliAmount)
                            && Math.abs(kisaanTransaction.getAmountHamaali() - ControllerUtilities.formatDecimalValue(hamaaliAmount)) > deltaDouble) {

                        root.put(Constants.ERROR, String.format("Amount Hamaali calculated:" + kisaanTransaction.getAmountHamaali() + " does not match passed Hamaali Amount: " + hamaaliAmount + ", please check sysadmin"));
                        logger.error("Amount Hamaali calculated:" + kisaanTransaction.getAmountHamaali() + " does not match passed Hamaali Amount: " + hamaaliAmount + ", please check sysadmin");
                        templateOverride.process(root, writer);
                        return;
                    }

                    if (kisaanTransaction.getAmountMapari() != ControllerUtilities.formatDecimalValue(mapariAmount)
                            && Math.abs(kisaanTransaction.getAmountMapari() - ControllerUtilities.formatDecimalValue(mapariAmount)) > deltaDouble) {

                        root.put(Constants.ERROR, String.format("Amount Mapari calculated:" + kisaanTransaction.getAmountMapari() + " does not match passed Hamaali Amount: " + mapariAmount + ", please check sysadmin"));
                        logger.error("Amount Mapari calculated:" + kisaanTransaction.getAmountMapari() + " does not match passed Hamaali Amount: " + mapariAmount + ", please check sysadmin");
                        templateOverride.process(root, writer);
                        return;
                    }


                    kisaanTransaction.setCreatedBy(sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request)));
                    for (ItemSell itemSell : itemSoldList) {
                        itemSell.setTransactionId(kisaanTransaction.getUniqueKey());

                    }

                    //We should confirm to add here the transaction

                    //**STARTS: NEED TO DO THIS PART IN ONE TRANSACTION**//
                    KisaanPayment kisaanPayment = null;
                    if (paidAmount > 0) {
                        //add
                        kisaanPayment = new KisaanPayment(kisaanName, paidAmount, dt);
                        kisaanPayment.setTag("InitialPayment");
                        kisaanPayment.setPaymentType(paymentType);
                        kisaanPayment.setTransactionId(kisaanTransaction.getUniqueKey());
                        kisaanPayment.setCreatedBy(sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request)));
                        kisaanPaymentDAO.add(kisaanPayment);
                    }

                    //Add ItemSell list of now.
                    SingletonManagerDAO.getInstance().getItemSellDAO().add(itemSoldList);
                    kisaanTransaction.setAmountPaid(paidAmount);
                    kisaanTransactionDAO.add(kisaanTransaction);

                    KisaanTransaction kisaanTransaction1 = kisaanTransactionDAO.getBasedOnUniqueKey(kisaanTransaction.getUniqueKey());

                   /* if(kisaanTransaction1==null) {
                        //Reverting the transaction as something went wrong

                        root.put(Constants.ERROR, "Something strange happened while inserting transaction for khareeddar: \"<b>" + khareeddarName + "</b>\", Please contact system admin");
                        logger.error("There was an issue with the transaction, should not happend, need to be checked with admin, transaction", kisaanTransaction);
                        templateOverride.process(root, writer);
                        return;
                    }
                    */
                    root.put("entity", "transaction");
                    root.put("entityValue", "transaction");
                    root.put("kisaanPayment", kisaanPayment);

                    if (kisaanTransaction1 != null) {
                        root.put("entityObject", kisaanTransaction1);
                        root.put("success", "true");
                        templateOverride.process(root, writer);
                    } else {
                        //delete inserted items
                        SingletonManagerDAO.getInstance().getItemSellDAO().remove(itemSoldList);
                        if (paidAmount > 0 && kisaanPayment != null) {
                            kisaanPaymentDAO.remove(kisaanPayment);
                        }
                        root.put(Constants.ERROR, "Some error in saving the kisaanTransaction, contact ADMIN, key:" + kisaanTransaction.getUniqueKey());
                        logger.error("Not able to find the inserted kisaanTransaction for kisaan:{}, key:{}, Investigate mongo issue"
                                , kisaanName, kisaanTransaction.getUniqueKey());
                        templateOverride.process(root, writer);
                    }
                    //**ENDS: NEED TO DO THIS PART IN ONE TRANSACTION**//
                }

            }
        });



    }


}
