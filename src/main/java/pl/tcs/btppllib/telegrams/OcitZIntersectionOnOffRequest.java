/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.tcs.btppllib.telegrams;

import static pl.tcs.btppllib.Telegrams.Member.Member_ZIntersectionOnOff;
import pl.tcs.btppllib.Telegrams.Method;
import static pl.tcs.btppllib.Telegrams.OType.OType_ZIntersectionOnOff;

/**
 *
 * @author tomek
 */
public class OcitZIntersectionOnOffRequest extends OcitRequestTelegram {
    
    public OcitZIntersectionOnOffRequest(Method method) {
        setMember(Member_ZIntersectionOnOff.getVal());
        setOtype(OType_ZIntersectionOnOff.getVal());
        setMethod(method.getVal());
    }            
}
