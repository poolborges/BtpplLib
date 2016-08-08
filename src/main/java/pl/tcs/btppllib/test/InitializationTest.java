/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.tcs.btppllib.test;

import pl.tcs.btppllib.OcitClient;
import pl.tcs.btppllib.commons.OcitRoutingTable;
import pl.tcs.btppllib.commons.OcitRoutingTable.OcitDeviceType;
import static pl.tcs.btppllib.commons.OcitRoutingTable.OcitDeviceType.Central;
import static pl.tcs.btppllib.commons.OcitRoutingTable.OcitDeviceType.FieldDevice;
import pl.tcs.btppllib.commons.OcitRoutingTable.OcitRoutingTableEntry;
import static pl.tcs.btppllib.utils.Utils.sleep;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author tomek
 */
public class InitializationTest implements Runnable {
    
    private static final Logger log = Logger.getLogger(InitializationTest.class);
    
    private List<OcitClient> clients = new ArrayList<>();
    
    @Override
    public void run() {
        log.info("Starting initializartion test!");
        
        final String ocitRoute1File = "ocit_route1";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(ocitRoute1File);
        
        OcitRoutingTable routingTable = new OcitRoutingTable();
        routingTable.readFromStream(inputStream);
        
        List<OcitRoutingTableEntry> entries = routingTable.getEntries();
        for (OcitRoutingTableEntry entry : entries) {
            log.debug(String.format("[entry] znr=%d fnr=%d ipRemote=%s passwd=%s type=%s", entry.getZnr(), entry.getFnr(), entry.getIpRemote(), entry.getPasswd(),
                    entry.getDevType().toString()));
            
            final OcitDeviceType devType = entry.getDevType();
            if (devType == Central || devType == FieldDevice) {
                log.debug("Initializing client for remote access");
                
                OcitClient ocitClient = new OcitClient(entry.getIpRemote(), entry.getZnr(), entry.getFnr());
                clients.add(ocitClient);
                log.debug("OcitClient to remote device initialized!");                
            }
        }
        
        sleep(20);
        
        for (OcitClient client : clients) {
            client.closeAll();
        }
        
        sleep(5);
    }        
    
    public static void main(String[] args) {
        new Thread(new InitializationTest()).start();
    }            
}
