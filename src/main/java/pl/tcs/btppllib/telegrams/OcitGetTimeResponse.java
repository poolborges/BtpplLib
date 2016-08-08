/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.tcs.btppllib.telegrams;

import io.netty.buffer.ByteBuf;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author tomek
 */
public class OcitGetTimeResponse extends OcitResponseTelegram {
    
    private static final Logger log = Logger.getLogger(OcitGetTimeResponse.class);
    
    private Date devTime;
    private int timeZone;
    private int source;    
    
    @Override
    public OcitResponseTelegram fromByteBuf(ByteBuf buf) {
        OcitResponseTelegram resp = super.fromByteBuf(buf);
                                
        long devTime = buf.readUnsignedInt() * 1000;        
        setDevTime(new Date(devTime));            
        setTimeZone(buf.readInt());        
        setSource(buf.readUnsignedByte());
        
        log.debug("Device time: " + getDevTime().toString());        
        
        return this;
    }    

    public Date getDevTime() {
        return devTime;
    }

    public void setDevTime(Date devTime) {
        this.devTime = devTime;
    }

    public int getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(int timeZone) {
        this.timeZone = timeZone;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }
}
