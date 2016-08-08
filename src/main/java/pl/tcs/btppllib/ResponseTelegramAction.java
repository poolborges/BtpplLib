/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.tcs.btppllib;

import org.apache.log4j.Logger;

/**
 *
 * @author tomek
 */
public class ResponseTelegramAction implements Runnable {
    
    private static final Logger log = Logger.getLogger(ResponseTelegramAction.class);

    @Override
    public void run() {
        log.debug("Default empty response telegram action");
    }    
}
