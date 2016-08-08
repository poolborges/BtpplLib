/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.tcs.btppllib.commons;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author tomek
 */
public class OcitRoutingTable {
    
    private static final Logger log = Logger.getLogger(OcitRoutingTable.class);
    
    public OcitRoutingTable() {        
    }
    
    public OcitRoutingTable(List<OcitRoutingTableEntry> entries) {
        this.routingTableEntries = new ArrayList<>(entries);
    }    
    
    public static enum OcitDeviceType {
        Undefined(0),
        Central(1),
        SystemAccess(2),
        FieldDevice(3);
        
        private int type;
        
        OcitDeviceType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
        
        public static OcitDeviceType fromInt(int i) {
            return OcitDeviceType.values()[i];
        }

        public static int toInt(OcitDeviceType devType) {
            return devType.ordinal();
        }
    }        
    
    public static class OcitRoutingTableEntry {
        private int znr;
        private int fnr;
        private String ipRemote;
        private String passwd;
        private OcitDeviceType devType;
        
        public OcitRoutingTableEntry() {            
        }

        public OcitRoutingTableEntry(int znr, int fnr, String ipRemote, String passwd, OcitDeviceType devType) {
            this.znr = znr;
            this.fnr = fnr;
            this.ipRemote = ipRemote;
            this.passwd = passwd;
            this.devType = devType;
        }

        public int getZnr() {
            return znr;
        }

        public OcitRoutingTableEntry setZnr(int znr) {
            this.znr = znr;
            return this;
        }

        public int getFnr() {
            return fnr;
        }

        public OcitRoutingTableEntry setFnr(int fnr) {
            this.fnr = fnr;
            return this;
        }

        public String getIpRemote() {
            return ipRemote;
        }

        public OcitRoutingTableEntry setIpRemote(String ipRemote) {
            this.ipRemote = ipRemote;
            return this;
        }

        public String getPasswd() {
            return passwd;
        }

        public OcitRoutingTableEntry setPasswd(String passwd) {
            this.passwd = passwd;
            return this;
        }

        public OcitDeviceType getDevType() {
            return devType;
        }

        public OcitRoutingTableEntry setDevType(OcitDeviceType devType) {
            this.devType = devType;
            return this;
        }
        
        @Override
        public String toString() {
            return String.format("znr=%d fnr=%d ipRemote=%s passwd=%s devType=%s", 
                    znr, fnr, ipRemote, passwd, devType.toString());
        }
    }
    
    private List<OcitRoutingTableEntry> routingTableEntries = new ArrayList<>();
    
    public void addRoutingTableEntry(OcitRoutingTableEntry entry) {
        routingTableEntries.add(entry);
    }
    
    public void removeRoutingTableEntry(final int znr, final int fnr) {
        Iterator iter = routingTableEntries.iterator();
        while (iter.hasNext()) {
            OcitRoutingTableEntry entry = (OcitRoutingTableEntry)iter.next();
            if (entry.getZnr() == znr && entry.getFnr() == fnr) {
                iter.remove();
                break;
            }
        }
    }
    
    public List<OcitRoutingTableEntry> getEntries() {
        return routingTableEntries;
    }
    
    /**
     * Reads configuration as in ocit_route1 file in btppl lib
     * TODO: handle multiword passwords
     * 
     * @param fileLocation
     * @return number of succesfully read entries
     */
    public int readFromFile(String fileLocation) {
        log.info("Try to read configuration from file: " + fileLocation);
        
        try (BufferedReader br = new BufferedReader(new FileReader(
                    fileLocation))) {
            
            routingTableEntries.clear();
            
            String line = null;
            while ( (line = br.readLine()) != null ) {
                log.debug("** Read line: [" + line + "]");                                
                
                OcitRoutingTableEntry entry = readEntry(line);                
                if (entry != null) {                
                    routingTableEntries.add(entry);
                }    
            }
        
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new CriticalException(e);
        } finally {
            log.info("Finished reading from file: " + fileLocation);
        }       
        
        return routingTableEntries.size();
    }
    
    /**
     * Reads configuration e.g. from resources/ directory;
     * input is stream
     * 
     * @param inputStream
     * @return number of succesfully read entries
     */
    public int readFromStream(InputStream inputStream) {
        log.info("Reading from stream");
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            
            routingTableEntries.clear();
            
            String line = null;
            while ( (line = br.readLine()) != null ) {
                log.debug("** Read line: [" + line + "]");                                
                
                OcitRoutingTableEntry entry = readEntry(line);                
                if (entry != null) {                
                    routingTableEntries.add(entry);
                }    
            }
                        
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new CriticalException(e);
        } finally {
            log.info("Finished reading from stream");
        }       
        
        return routingTableEntries.size();                
    }
    
    protected OcitRoutingTableEntry readEntry(String line) {
        log.debug("** Read line: [" + line + "]");

        line = line.trim();
        if (line.startsWith("#") || line.startsWith(";")) {
            // skip, comment
            return null;
        }

        String[] fields = line.split("\\s+");

        if (fields.length != 5) {
            return null;
        }

        OcitRoutingTableEntry entry = new OcitRoutingTableEntry();

        entry.setZnr(Integer.parseInt(fields[0]));
        entry.setFnr(Integer.parseInt(fields[1]));
        entry.setIpRemote(fields[2]);
        entry.setPasswd(fields[3]);
        entry.setDevType(OcitDeviceType.fromInt(Integer.parseInt(fields[4])));
        
        return entry;
    }
    
    public void writeToFile(String fileLocation) {
        log.info("Try to write configuration from file: " + fileLocation);
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(
                    fileLocation, false))) {
            
            // header
            bw.write(String.format("# Generated by @tcs at: %s%n%n", DateFormat.getDateTimeInstance(
                DateFormat.LONG, DateFormat.LONG).format(new Date())));
            
            for (OcitRoutingTableEntry entry : routingTableEntries) {
                bw.write(String.format("%d    %d    %s    \"%s\"    %d%n", 
                        entry.getZnr(),
                        entry.getFnr(),
                        entry.getIpRemote(),
                        entry.getPasswd(),
                        OcitDeviceType.toInt(entry.devType)));
            }
        
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new CriticalException(e);
        } finally {
            log.info("Finished writing to file: " + fileLocation);
        }               
    }
}
