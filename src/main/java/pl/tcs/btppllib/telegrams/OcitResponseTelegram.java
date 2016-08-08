/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.tcs.btppllib.telegrams;

import pl.tcs.btppllib.ResponseTelegramAction;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author tomek
 */
public class OcitResponseTelegram extends OcitTelegram {
    private static final Logger log = Logger.getLogger(OcitResponseTelegram.class);
    
    private short retcode;
    
    private List<ResponseTelegramAction> telegramActions = new ArrayList<>();
    
    public OcitResponseTelegram() {
        log.debug("Add test debug telegram action!");
        telegramActions.add(new ResponseTelegramAction());
    }

    public short getRetcode() {
        return retcode;
    }

    public OcitResponseTelegram setRetcode(short retcode) {
        this.retcode = retcode;
        return this;
    }
    
    public OcitResponseTelegram fromByteBuf(ByteBuf buf) {
        buf.readerIndex(0);
        
        setHdrLen(buf.readByte());
        setOpts(buf.readByte());
        setJobTime(buf.readShort());
        setJobTimeCnt(buf.readShort());
        setMember(buf.readShort());
        setOtype(buf.readShort());
        setMethod(buf.readShort());
        setZnr(buf.readShort());
        setFnr(buf.readShort());
        setRetcode(buf.readShort());
        
        log.debug(String.format("znr=%d fnr=%d  member=%d otype=%d method=%d", 
                getZnr(), getFnr(), getMember(), getOtype(), getMethod() ));
        
        return this;
    }    
    
    public OcitResponseTelegram registerAction(ResponseTelegramAction action) {
        log.info(String.format("Registering response telegram action=%s", action.getClass().getSimpleName()));       
        telegramActions.add(action);                
        
        return  this;
    }    
    
    public List<ResponseTelegramAction> getActions() {
        return telegramActions;
    }
}
