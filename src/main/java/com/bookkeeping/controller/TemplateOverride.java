package com.bookkeeping.controller;

import com.bookkeeping.Config.BrokerSingleton;
import com.bookkeeping.constants.Constants;
import com.bookkeeping.logging.LogResult;
import com.bookkeeping.model.Broker;
import com.bookkeeping.utilities.ControllerUtilities;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * Created by chandan on 9/27/2015.
 */
public class TemplateOverride {

    Template template;
    Request request;
    Response response;
    SessionDAO sessionDAO = SingletonManagerDAO.getInstance().getSessionDAO();
    static final Logger logger = LoggerFactory.getLogger(TemplateOverride.class);
    public TemplateOverride() {
    }

    public void setTemplate(Template template) {
        this.template = template;
    }


    public void setRequest(Request request) {
        this.request = request;
    }


    public void setResponse(Response response) {
        this.response = response;
    }

    public TemplateOverride(Request request, Response response, Template template) {
        this.request = request;
        this.response = response;
        this.template = template;
    }

    public void process(Object rootMap, Writer out) throws TemplateException, IOException {


        String username = sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request));
        /*
        System.out.println("User name found:"+username);
        if (username == null) {
            response.redirect("/login");
            return;
        }*/

        Map<String,Object> root = null;
        try {
            root = (Map<String, Object>) rootMap;
            //IMP: for login control
            root.put("username",username);
            if(username!=null && username.equals(Constants.ADMIN_USER)) {
                root.put("admin", "true");
            }

            Broker broker = BrokerSingleton.getInstance(BrokerSingleton.propFile);
            if(broker==null) {
                root.put("broker",broker);
            }
            root.put("APP_TITLE", Constants.APP_TITLE);
            root.put("APP_LINK", Constants.APP_LINK);
            root.put("APP_MESSAGE", Constants.APP_MESSAGE);
            root.put("APP_WEBSITE", Constants.APP_WEBSITE);
            root.put("APP_WEBSITE_BIZ", Constants.APP_WEBSITE_BIZ);
            root.put("INFO_EMAIL", Constants.INFO_EMAIL);
            root.put("ADDRESS", Constants.APP_ADDRESS);
            root.put("defaultMapariRate",Constants.MAPARI_RATE);
            root.put("defaultHamaaliRate",Constants.HAMALI_RATE);
            root.put("defaultBrokerageComission",Constants.BROKERAGE_RATE);
            if (SingletonManagerDAO.getInstance().getKisaanPaymentDAO().count() > Constants.TRIAL_EDITION_LIMIT
                    || SingletonManagerDAO.getInstance().getKhareeddarPaymentDAO().count() > Constants.TRIAL_EDITION_LIMIT
                    || SingletonManagerDAO.getInstance().getKisaanTransactionDAO().count() > Constants.TRIAL_EDITION_LIMIT
                    || SingletonManagerDAO.getInstance().getKisaanDAO().count() > Constants.TRIAL_EDITION_LIMIT
                    || SingletonManagerDAO.getInstance().getKhareeddarDAO().count() > Constants.TRIAL_EDITION_LIMIT)  {
                //logger.info("You have reached the limit for trial edition, to continue usage you need to purchase this software. Please contact the system admin");
                //ControllerUtilities.messageSoftwareTrial();
                //root.put("trial","yes");
            }
           // root.put("HEIGHT", Constants.THUMBNAIL_HEIGHT);
            //root.put("WIDTH", Constants.THUMBNAIL_WIDTH);
            root.put("session", ControllerUtilities.getSessionCookie(request));
            this.template.process(root, out);
            LogResult.logIntoApplicationLogs(this.request, this.response, root);
        } catch(Exception e) {
            logger.warn("{} not matching map class",request.pathInfo());
            this.template.process(rootMap, out);
            LogResult.logIntoApplicationLogs(this.request, this.response,rootMap.toString());
        }
        /*
        } else {
            logger.warn("{} not matching map class",request.pathInfo());
            LogResult.logIntoApplicationLogs(this.request, this.response, "NULL");
        }
        */

    }
}
