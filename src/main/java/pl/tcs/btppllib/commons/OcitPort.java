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
public enum OcitPort {
    PnpPort(3110),  // low-prio port
    PhpPort(2504);  // high-prio port
    
    private int portNr;
    
    OcitPort(int portNr) {
        this.portNr = portNr;
    }

    public int getPortNr() {
        return portNr;
    }            
}
