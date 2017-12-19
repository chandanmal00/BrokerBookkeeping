package com.bookkeeping.Config;

import com.bookkeeping.model.Broker;
import com.bookkeeping.model.Location;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by chandanm on 6/26/16.
 */
public class BrokerSingleton {


    public static boolean NO_INPUT=false;
    public static boolean INITIALIZED=false;
    public static volatile Broker broker;
    static final Logger logger = LoggerFactory.getLogger(BrokerSingleton.class);
    public static volatile String propFile;

    private BrokerSingleton() {
    }

    public static Broker getInstance(String configFile) {
        if(broker==null) synchronized (BrokerSingleton.class) {
            if (broker == null) {
                InputStream inputStream = null;
                //tackle the first time when propFile is not SET
                if (propFile == null) {
                    propFile = configFile;
                }

                try {
                    new BrokerSingleton();
                    Properties prop = new Properties();
                    if (NO_INPUT) {
                        //inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
                        logger.info("Reading local config.properties file:" + propFile);
                        inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(propFile);
                        //inputStream = BrokerSingleton.class.getResourceAsStream(propFile);
                    } else {
                        logger.info("Reading properties passed as an argument:" + propFile);
                        inputStream = new FileInputStream(propFile);
                    }

                    if (inputStream == null) {
                        throw new Exception("InputStream in null");
                    }
                    prop.load(inputStream);
                    String name = prop.getProperty("BROKER_NAME");
                    Broker brokerLocal = new Broker(name);
                    brokerLocal.setProprietor(prop.getProperty("BROKER_PROPRIETOR"));
                    brokerLocal.setFirmName(prop.getProperty("BROKER_FIRM_NAME"));

                    String place = prop.getProperty("BROKER_PLACE");
                    Location location = new Location(place);
                    location.setAddress(prop.getProperty("BROKER_ADDRESS"));

                    location.setDistrict(prop.getProperty("BROKER_DISTRICT"));

                    location.setState(prop.getProperty("BROKER_STATE"));

                    location.setTaluka(prop.getProperty("BROKER_TALUKA"));

                    brokerLocal.setLocation(location);

                    broker = brokerLocal;
                    Gson gson = new Gson();
                    logger.info(gson.toJson(brokerLocal));
                } catch (Exception e) {
                    logger.info("Error in reading file:{}", configFile, e);
                    System.out.println("Error in reading file:" + configFile);
                    e.printStackTrace();
                    System.exit(-3);

                } finally {
                    IOUtils.closeQuietly(inputStream);
                }
            }
        }
        return broker;
    }

    /**
     * Tells the code to use config.properties part of the code resource vs external
     * if true we will use properties file passed as a argument
     */
    public static void init() {
        NO_INPUT=true;
    }

    public static void main(String[] args) {
       // BrokerSingleton.init();
        BrokerSingleton.init();
        BrokerSingleton.getInstance("config.properties");
    }

}
