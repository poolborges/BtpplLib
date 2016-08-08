/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.tcs.btppllib.utils;

import java.util.Calendar;
import java.util.TimeZone;

/**
 *
 * @author tomek
 */
public class Utils {
    
    public static int toUnsigned(byte b) {
        return b & 0xFF;
    }
    
    public static int toUnsigned(short s) {
        return s & 0xFFFF;
    }      
    
    public static long getUTCTime() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        return cal.getTimeInMillis();        
    }
    
    public static void sleep(int seconds) {
        try {
            Thread.sleep(seconds*1000L);
        } catch (InterruptedException e) {}        
    }
    
    public static void sleepMs(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {}                
    }
}
