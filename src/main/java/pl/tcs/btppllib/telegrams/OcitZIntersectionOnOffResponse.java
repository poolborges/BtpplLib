/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.tcs.btppllib.telegrams;

import static pl.tcs.btppllib.Telegrams.Method.ZIntersectionOnOff_Get;
import pl.tcs.btppllib.commons.OnOffState;
import io.netty.buffer.ByteBuf;
import org.apache.log4j.Logger;

/**
 *
 * @author tomek
 */
public class OcitZIntersectionOnOffResponse extends OcitResponseTelegram {
    
    private static final Logger log = Logger.getLogger(OcitZIntersectionOnOffResponse.class);
    
    private long actProcess; // current
    private long actStartTime;
    private long actEndTime;
    private OnOffState actOnOffState;
    
    private long nextProcess; // incoming
    private long nextStartTime;
    private long nextEndTime;
    private OnOffState nextOnOffState;
    
    @Override
    public OcitResponseTelegram fromByteBuf(ByteBuf buf) {
        OcitResponseTelegram resp = super.fromByteBuf(buf);
        
        if (method == ZIntersectionOnOff_Get.getVal()) {
            actProcess = buf.readUnsignedInt();
            actStartTime = buf.readUnsignedInt();
            actEndTime = buf.readUnsignedInt();
            actOnOffState = OnOffState.fromInt(buf.readUnsignedByte());

            nextProcess = buf.readUnsignedInt();
            nextStartTime = buf.readUnsignedInt();
            nextEndTime = buf.readUnsignedInt();
            nextOnOffState = OnOffState.fromInt(buf.readUnsignedByte());
        }    
        
        log.info(String.format("actOnOffState=%s nextOnOffState=%s", actOnOffState.toString(), nextOnOffState.toString()));
        
        return this;        
    }

    public long getActProcess() {
        return actProcess;
    }

    public void setActProcess(long actProcess) {
        this.actProcess = actProcess;
    }

    public long getActStartTime() {
        return actStartTime;
    }

    public void setActStartTime(long actStartTime) {
        this.actStartTime = actStartTime;
    }

    public long getActEndTime() {
        return actEndTime;
    }

    public void setActEndTime(long actEndTime) {
        this.actEndTime = actEndTime;
    }

    public OnOffState getActOnOffState() {
        return actOnOffState;
    }

    public void setActOnOffState(OnOffState actOnOffState) {
        this.actOnOffState = actOnOffState;
    }

    public long getNextProcess() {
        return nextProcess;
    }

    public void setNextProcess(long nextProcess) {
        this.nextProcess = nextProcess;
    }

    public long getNextStartTime() {
        return nextStartTime;
    }

    public void setNextStartTime(long nextStartTime) {
        this.nextStartTime = nextStartTime;
    }

    public long getNextEndTime() {
        return nextEndTime;
    }

    public void setNextEndTime(long nextEndTime) {
        this.nextEndTime = nextEndTime;
    }

    public OnOffState getNextOnOffState() {
        return nextOnOffState;
    }

    public void setNextOnOffState(OnOffState nextOnOffState) {
        this.nextOnOffState = nextOnOffState;
    }
}
