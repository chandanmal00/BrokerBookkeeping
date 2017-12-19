package com.bookkeeping.controller;

import freemarker.template.Configuration;

/**
 * Created by chandan on 6/20/16.
 */
public class SingletonConfiguration {

    private static volatile SingletonConfiguration instance;
    private Configuration configuration;

    private SingletonConfiguration() {
        Configuration retVal = new Configuration();
        retVal.setClassForTemplateLoading(SingletonConfiguration.class, "/freemarker");
        this.configuration = retVal;
        System.out.println(configuration);
    }

    public static SingletonConfiguration getInstance() {
        if(instance==null) {
            synchronized (SingletonConfiguration.class) {
                if(instance==null) {
                    instance = new SingletonConfiguration();
                }
            }

        }
        return instance;

    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

}
