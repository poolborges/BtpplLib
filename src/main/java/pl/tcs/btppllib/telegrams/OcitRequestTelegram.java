/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.tcs.btppllib.telegrams;

import static pl.tcs.btppllib.utils.Utils.getUTCTime;
import static pl.tcs.btppllib.utils.Utils.toUnsigned;
import static pl.tcs.btppllib.telegrams.OcitRequestTelegram.HolderType.Param;
import static pl.tcs.btppllib.telegrams.OcitRequestTelegram.HolderType.Path;
import pl.tcs.btppllib.utils.FletcherChecksum;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tomek
 */
public class OcitRequestTelegram extends OcitTelegram {
    
    public OcitRequestTelegram() {
        setJobTimeCnt((short)0);
    }
    
    public static enum HolderType {
        Path,
        Param                
    }
    
    public static enum HolderValueType {
        UI1,
        UI2,
        UI4,
        I1,
        I2,
        I4;        
    }
        
    public static class TypeDescription {
        HolderType holderType;
        HolderValueType holderValueType;
        int value;

        public TypeDescription(HolderType holderType, HolderValueType holderValueType, int val) {
            this.holderType = holderType;
            this.holderValueType = holderValueType;
            this.value = val;
        }

        public HolderType getHolderType() {
            return holderType;
        }

        public HolderValueType getHolderValueType() {
            return holderValueType;
        }

        public int getValue() {
            return value;
        }
    }
    
    private List<TypeDescription> pathElems = new ArrayList<>();
    private List<TypeDescription> paramElems = new ArrayList<>();
    
    public void addPathElem(HolderValueType valueType, int val) {
        pathElems.add(new TypeDescription(Path, valueType, val));        
    }
    
    public void addParamElem(HolderValueType valueType, int val) {
        paramElems.add(new TypeDescription(Param, valueType, val));        
    }
    
    public OcitRequestTelegram finishTelegram() {
        int hdrLen = 16;
                                
        for (TypeDescription td : pathElems) {                        
            hdrLen += getPlus(td);            
        }
        
        for (TypeDescription td : paramElems) {                        
            hdrLen += getPlus(td);            
        }                
        
        setHdrLen((byte)hdrLen);
        
        return this;
    }
    
    protected int getPlus(TypeDescription td) {
        HolderValueType hvt = td.getHolderValueType();
        int plus = 0;

        switch (hvt) {
            case UI1:
            case I1:
                plus = 1;
                break;
            case UI2:
            case I2:
                plus = 2;
                break;
            case UI4:
            case I4:
                plus = 4;
                break;
            default:
                plus = 0;
        }

        return plus;
    }
    
    public ByteBuf toByteBuf() {
        ByteBuf retBuf = Unpooled.buffer();        
        
        retBuf.writeByte(toUnsigned(getHdrLen()));
        retBuf.writeByte(toUnsigned(getOpts()));
        retBuf.writeShort((int)getUTCTime());
        retBuf.writeShort(toUnsigned(getJobTimeCnt()));
        
        retBuf.writeShort(toUnsigned(getMember()));
        retBuf.writeShort(toUnsigned(getOtype()));
        retBuf.writeShort(toUnsigned(getMethod()));
        
        retBuf.writeShort(toUnsigned(getZnr()));
        retBuf.writeShort(toUnsigned(getFnr()));        
        
        for (TypeDescription elem : pathElems) {
            writeContentElem(retBuf, elem);
        }
        
        for (TypeDescription elem : paramElems) {
            writeContentElem(retBuf, elem);
        }        
                        
        byte[] bb = new byte[retBuf.readableBytes()];
        retBuf.getBytes(0, bb);
        FletcherChecksum fcs = new FletcherChecksum(bb);
        int checksum = fcs.getChecksumAsInt();
        
        retBuf.writeShort(checksum);        
        
        return retBuf;
    }
    
    protected void writeContentElem(ByteBuf buf, TypeDescription desc) {
        final HolderValueType holderValType = desc.getHolderValueType();
        
        switch (holderValType) {
            case UI1: 
                buf.writeByte(desc.getValue());
                break;
            case UI2:
                buf.writeShort(desc.getValue());
                break;
            case UI4:
                buf.writeInt(desc.getValue());
                break;
            case I1:
                break;
            case I2:
                break;
            case I4:
                break;                
            default:
                throw new UnsupportedOperationException("Invalid holder value type");            
        }
    }
}
