/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.tcs.btppllib.test;

import pl.tcs.btppllib.OcitClient;
import static pl.tcs.btppllib.Telegrams.Method.ZIntersectionOnOff_Get;
import pl.tcs.btppllib.commons.OcitPort;
import pl.tcs.btppllib.commons.OcitRoutingTable;
import static pl.tcs.btppllib.commons.OcitRoutingTable.OcitDeviceType.Central;
import static pl.tcs.btppllib.commons.OcitRoutingTable.OcitDeviceType.FieldDevice;
import pl.tcs.btppllib.telegrams.OcitGetTimeRequest;
import pl.tcs.btppllib.telegrams.OcitRequestTelegram;
import static pl.tcs.btppllib.telegrams.OcitRequestTelegram.HolderValueType.UI1;
import pl.tcs.btppllib.telegrams.OcitZIntersectionOnOffRequest;
import static pl.tcs.btppllib.utils.Utils.sleep;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author tomek
 */
public class SendTelegramsTest implements Runnable {
    private static final Logger log = Logger.getLogger(SendTelegramsTest.class);
    
    private List<OcitClient> clients = new ArrayList<>();
    
    @Override
    public void run() {
        log.info("Starting send telegrams test!");
        
        final String ocitRoute1File = "ocit_route1";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(ocitRoute1File);
        
        OcitRoutingTable routingTable = new OcitRoutingTable();
        routingTable.readFromStream(inputStream);
        
        List<OcitRoutingTable.OcitRoutingTableEntry> entries = routingTable.getEntries();
        for (OcitRoutingTable.OcitRoutingTableEntry entry : entries) {
            log.debug(String.format("[entry] znr=%d fnr=%d ipRemote=%s passwd=%s type=%s", entry.getZnr(), entry.getFnr(), entry.getIpRemote(), entry.getPasswd(),
                    entry.getDevType().toString()));
            
            final OcitRoutingTable.OcitDeviceType devType = entry.getDevType();
            if (devType == Central || devType == FieldDevice) {
                log.debug("Initializing client for remote access");
                
                OcitClient ocitClient = new OcitClient(entry.getIpRemote(), entry.getZnr(), entry.getFnr());
                clients.add(ocitClient);
                log.debug("OcitClient to remote device initialized!");                
            }
        }
        
        sleep(5);  // wait for socket initialization
        
        short remoteZnr = 1, remoteFnr = 100;
        
        OcitClient destClient = null;
        for (OcitClient client : clients) {
            if (client.getZnr() == remoteZnr && client.getFnr() == remoteFnr) {
                log.debug(String.format("Found client for znr=%d and fnr=%d", remoteZnr, remoteFnr));
                destClient = client;
                break;
            }
        }        
        
        for (int i = 0; i < 1000; i++) {
            OcitRequestTelegram request = null;
            
            if (i%2 == 0) {
                request = new OcitGetTimeRequest();
            } else {
                request = new OcitZIntersectionOnOffRequest(ZIntersectionOnOff_Get);
                request.addPathElem(UI1, 0);
            }
            
            request.setZnr(remoteZnr);
            request.setFnr(remoteFnr);                   
            request.finishTelegram();
            
            destClient.sendOnChannel((i%2 == 0) ? OcitPort.PnpPort : OcitPort.PhpPort, request);
        }
        
        sleep(50);   // before stop wait for responses!
        
        for (OcitClient client : clients) {
            client.closeAll();
        }
        
        sleep(5);
    }        
    
    public static void main(String[] args) {
        new Thread(new SendTelegramsTest()).start();
    }                    
}
