/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.tcs.btppllib;

/**
 *
 * @author tomek
 */
public class Telegrams {
    
    public static enum Member {
        Member_SystemobjektFeldgeraet(0),
        Member_RemoteDevice(0),
        Member_ZIntersectionOnOff(1);
        
        private short val;
        
        Member(int val) {
            this.val = (short)val;
        }
        
        public short getVal() {
            return this.val;
        }
    }
    
    public static enum OType {
        OType_SystemobjektFeldgeraet(815),
        OType_RemoteDevice(817),
        OType_AEAggregiert(432),
        OType_DigitalInput(500),
        OType_ZIntersectionOnOff(224);
        
        private short val;
        
        OType(int val) {
            this.val = (short)val;
        }
        
        public short getVal() {
            return this.val;
        }
    }
    
    public static enum Method {
        SystemobjektFeldgeraet_GetTime(103),
        SystemobjektFeldgeraet_InstanceInfo(104),
        AEAggregiert_GetTriggerValue(150),
        ZIntersectionOnOff_Get(0),
        ZIntersectionOnOff_Switch(16),
        DigitalInput_Get(0),
        DigitalInput_GetValue(16);
        
        private short val;
        
        Method(int val) {
            this.val = (short)val;
        }
        
        public short getVal() {
            return this.val;
        }
    }        
}
