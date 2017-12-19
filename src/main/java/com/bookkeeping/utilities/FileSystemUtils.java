package com.bookkeeping.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by chandanmaloo on 7/16/17.
 */
public class FileSystemUtils {

    static final Logger logger = LoggerFactory.getLogger(FileSystemUtils.class);
    public static void checkDirExistsOtherwiseCreate(String dir) {
        if(checkIfDirExists(dir)) {
            logger.info("Directory already exists, dir:{}",dir);
        } else {
            File f = new File(dir);
            f.mkdirs();
            logger.info("Created dir:{}",dir);
        }
    }

    public static boolean checkIfDirExists(String dir) {
        File f = new File(dir);
        if(!f.exists()) {
            return false;
        }
        if(f.exists() && f.isDirectory()) {
            logger.info("We are good the backupDir exists");
            return true;
        }
        throw new IllegalArgumentException("Some issue with the dir:"+dir);
    }

}
