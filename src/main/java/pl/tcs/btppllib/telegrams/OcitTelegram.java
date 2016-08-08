/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.tcs.btppllib.telegrams;

import static pl.tcs.btppllib.utils.Utils.getUTCTime;
import static pl.tcs.btppllib.utils.Utils.toUnsigned;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.Serializable;

/**
 *
 * @author tomek
 */
public class OcitTelegram implements Serializable {
    
    protected byte hdrLen;
    protected byte opts;
    protected short jobTime;
    protected short jobTimeCnt;
    protected short member;
    protected short otype;
    protected short method;
    protected short znr;
    protected short fnr;    

    public byte getHdrLen() {
        return hdrLen;
    }

    public OcitTelegram setHdrLen(byte hdrLen) {
        this.hdrLen = hdrLen;
        return this;
    }

    public byte getOpts() {
        return opts;
    }

    public OcitTelegram setOpts(byte opts) {
        this.opts = opts;
        return this;
    }

    public short getJobTime() {
        return jobTime;
    }

    public OcitTelegram setJobTime(short jobTime) {
        this.jobTime = jobTime;
        return this;
    }

    public short getJobTimeCnt() {
        return jobTimeCnt;
    }

    public OcitTelegram setJobTimeCnt(short jobTimeCnt) {
        this.jobTimeCnt = jobTimeCnt;
        return this;
    }

    public short getMember() {
        return member;
    }

    public OcitTelegram setMember(short member) {
        this.member = member;
        return this;
    }

    public short getOtype() {
        return otype;
    }

    public OcitTelegram setOtype(short otype) {
        this.otype = otype;
        return this;
    }

    public short getMethod() {
        return method;
    }

    public OcitTelegram setMethod(short method) {
        this.method = method;
        return this;
    }

    public short getZnr() {
        return znr;
    }

    public OcitTelegram setZnr(short znr) {
        this.znr = znr;
        return this;
    }

    public short getFnr() {
        return fnr;
    }

    public OcitTelegram setFnr(short fnr) {
        this.fnr = fnr;
        return this;
    }            
}
