/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.tcs.btppllib.commons;

/**
 *
 * @author tomek
 */
public class CriticalException extends RuntimeException {
    
    public CriticalException() {
        super();
    }
    
    public CriticalException(String s) {
        super(s);
    }        
    
    public CriticalException(Exception e) {
        super(e);
    }
}
