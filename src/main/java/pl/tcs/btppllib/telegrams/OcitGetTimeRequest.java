/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.tcs.btppllib.telegrams;

import static pl.tcs.btppllib.Telegrams.Member.Member_SystemobjektFeldgeraet;
import static pl.tcs.btppllib.Telegrams.Method.SystemobjektFeldgeraet_GetTime;
import static pl.tcs.btppllib.Telegrams.OType.OType_SystemobjektFeldgeraet;

/**
 *
 * @author tomek
 */
public class OcitGetTimeRequest extends OcitRequestTelegram {        
    
    public OcitGetTimeRequest() {
        setMember(Member_SystemobjektFeldgeraet.getVal());
        setOtype(OType_SystemobjektFeldgeraet.getVal());
        setMethod(SystemobjektFeldgeraet_GetTime.getVal());                
    }    
}
