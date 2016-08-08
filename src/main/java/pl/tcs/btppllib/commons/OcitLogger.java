/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.tcs.btppllib.commons;

import org.apache.log4j.Logger;

/**
 *
 * @author tomek
 */
public class OcitLogger {        
    
    public enum OcitLoggerManagerType {
        LOG4J,
        TINYLOG,             
        JLOGAPI
    }    
    
    public abstract static class OcitLoggerManager {     
        public abstract void setClass(Class clazz);       
        public abstract void setName(String name);
        
        public abstract void info(Object message);
        public abstract void info(Object message, Throwable t);

        public abstract void debug(Object message);
        public abstract void debug(Object message, Throwable t);

        public abstract void error(Object message);
        public abstract void error(Object message, Throwable t);

        public abstract void warn(Object message);
        public abstract void warn(Object message, Throwable t);        
    }
    
    public static class OcitLoggerLog4jManager extends OcitLoggerManager {
        private Logger log4jLogger;
        
        public OcitLoggerLog4jManager() {            
        }
        
        @Override
        public void setClass(Class clazz) {
            log4jLogger = Logger.getLogger(clazz);
        }
        
        @Override
        public void setName(String name) {
            log4jLogger = Logger.getLogger(name);
        }
        
        public OcitLoggerLog4jManager(Class clazz) {
            log4jLogger = Logger.getLogger(clazz);
        }
        
        public OcitLoggerLog4jManager(String name) {
            log4jLogger = Logger.getLogger(name);
        }
        
        @Override
        public void info(Object message) {
            log4jLogger.info(message);        
        }

        @Override
        public void info(Object message, Throwable t) {
            log4jLogger.info(message, t);
        }

        @Override
        public void debug(Object message) {
            log4jLogger.debug(message);        
        }

        @Override
        public void debug(Object message, Throwable t) {
            log4jLogger.debug(message, t);        
        }

        @Override
        public void error(Object message) {
            log4jLogger.error(message);        
        }

        @Override
        public void error(Object message, Throwable t) {
            log4jLogger.error(message, t);
        }

        @Override
        public void warn(Object message) {
            log4jLogger.warn(message);
        }

        @Override
        public void warn(Object message, Throwable t) {
            log4jLogger.warn(message, t);
        }
    }
    
//    public static class OcitLoggerTinylogManager extends OcitLoggerManager {        
//    }    
//    
//    public static class OcitLoggerJLogApiManager extends OcitLoggerManager {        
//    }
    
    private OcitLoggerManager loggerManager;    

    public void setLoggerManager(OcitLoggerManager loggerManager) {
        this.loggerManager = loggerManager;
    }        
    
    public static OcitLoggerManager getManager(OcitLoggerManagerType type) {
        switch (type) {
            case LOG4J:
            default:    
                return new OcitLoggerLog4jManager();            
        }
    }
    
    public static OcitLogger getLogger(Class clazz) {
        return getLogger(clazz, OcitLoggerManagerType.LOG4J);
    }
    
    public static OcitLogger getLogger(String name) {
        return getLogger(name, OcitLoggerManagerType.LOG4J);
    }
    
    public static OcitLogger getLogger(Class clazz, OcitLoggerManagerType loggerType) {
        OcitLogger logger = new OcitLogger();
        OcitLoggerManager loggerManager = getManager(loggerType);
        loggerManager.setClass(clazz);
        logger.setLoggerManager(loggerManager);
        return logger;
    }
    
    public static OcitLogger getLogger(String name, OcitLoggerManagerType loggerType) {
        OcitLogger logger = new OcitLogger();
        OcitLoggerManager loggerManager = getManager(loggerType);
        loggerManager.setName(name);
        logger.setLoggerManager(loggerManager);
        return logger;
    }
    
    public void info(Object message) {
        loggerManager.info(message);
    }
    
    public void info(Object message, Throwable t) {
        loggerManager.info(message, t);
    }
    
    public void debug(Object message) {
        loggerManager.debug(message);
    }
    
    public void debug(Object message, Throwable t) {
        loggerManager.debug(message, t);
    }
    
    public void error(Object message) {
        loggerManager.error(message);
    }
    
    public void error(Object message, Throwable t) {
        loggerManager.error(message, t);
    }
    
    public void warn(Object message) {
        loggerManager.warn(message);
    }
    
    public void warn(Object message, Throwable t) {
        loggerManager.warn(message, t);
    }
}
