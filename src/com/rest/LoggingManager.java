package com.rest;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class LoggingManager {
    private static final String LOG_PATH="debug.log";
    public static final Logger logger=Logger.getGlobal();
    static {
        try{
            FileHandler handler=new FileHandler(LOG_PATH);
            handler.setFormatter(new SimpleFormatter());
            logger.addHandler(handler);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
