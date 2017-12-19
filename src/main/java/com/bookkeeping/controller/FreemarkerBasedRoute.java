package com.bookkeeping.controller;

import com.bookkeeping.constants.Constants;
import com.bookkeeping.utilities.ControllerUtilities;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by chandan on 6/20/16.
 */
abstract class FreemarkerBasedRoute extends Route {
    //final Template template;
    Template template;
    final TemplateOverride templateOverride = new TemplateOverride();
    static final Logger logger = LoggerFactory.getLogger(FreemarkerBasedRoute.class);
    Configuration cfg;
    SessionDAO sessionDAO = SingletonManagerDAO.getInstance().getSessionDAO();


    /**
     * Constructor
     *
     * @param path The route path which is used for matching. (e.g. /hello, users/:name)
     */
    public FreemarkerBasedRoute(final String path, final String templateName) throws IOException {
        super(path);
        cfg = SingletonConfiguration.getInstance().getConfiguration();
        template = cfg.getTemplate(templateName);
        templateOverride.setTemplate(template);

    }

    public FreemarkerBasedRoute(final String path) throws IOException {
        super(path);
        cfg = SingletonConfiguration.getInstance().getConfiguration();

    }

    @Override
    public Object handle(Request request, Response response) {
        String username = sessionDAO.findUserNameBySessionId(ControllerUtilities.getSessionCookie(request));
        logger.debug("Checking if User Logged in, otherwise asking for login");
        if (username == null) {

            response.redirect("/login");
            return null;
        }

        if(username!= null
                && !username.equals(Constants.ADMIN_USER) &&
                ( request.pathInfo().startsWith("/remove")
                        || request.pathInfo().equals("/save")
                        || request.pathInfo().equals("/restore"))
                ){
            logger.error("User:{} is accessing privileged areas");
            halt(401,"You do not have privileges, this incident will be reported..., return to home <a href=\"/\">Home</a>");

        }
        StringWriter writer = new StringWriter();
        templateOverride.setRequest(request);
        templateOverride.setResponse(response);
        try {
            doHandle(request, response, writer);
        } catch (Exception e) {
            logger.error("Internal Error while reading the request,",e);
            response.redirect("/internal_error");
        }
        return writer;
    }

    protected abstract void doHandle(final Request request, final Response response, final Writer writer)
            throws IOException, TemplateException;


    //protected abstract void doHandle(final Request request, final Response response, final Writer writer, final String templateName)
      //      throws IOException, TemplateException;

}
