package com.bookkeeping.controller;

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
abstract class RelaxedFreemarkerBasedRoute extends Route {
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
    public RelaxedFreemarkerBasedRoute(final String path, final String templateName) throws IOException {
        super(path);
        cfg = SingletonConfiguration.getInstance().getConfiguration();
        template = cfg.getTemplate(templateName);
        templateOverride.setTemplate(template);

    }

    public RelaxedFreemarkerBasedRoute(final String path) throws IOException {
        super(path);
        cfg = SingletonConfiguration.getInstance().getConfiguration();

    }

    @Override
    public Object handle(Request request, Response response) {
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
