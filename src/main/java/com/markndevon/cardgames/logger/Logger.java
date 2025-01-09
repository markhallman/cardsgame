package com.markndevon.cardgames.logger;

import org.springframework.stereotype.Component;

@Component
public class Logger {

    private static final boolean DEBUG = true;

    private static final Logger logger = new Logger();

    private Logger() {}

    public static Logger getInstance() {
        return logger;
    }

    public void log(final String log) {
        if(DEBUG) {
            System.out.println(log);
        }
    }

    public void error(final String log, final Exception exception){
        if(DEBUG) {
            System.out.println(log);
            System.out.println(exception.getMessage());
        }
    }
}
