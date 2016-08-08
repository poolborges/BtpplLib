/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.tcs.btppllib.commons;

/**
 *
 * @author tomek
 */
public enum OnOffState {
    None(0),
    On(1),
    OffDefault(2),
    OffFlashSecondaryDirection(3),
    OffUnlit(4),
    OffFlashAll(5);
    
    private int val;
    
    OnOffState(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }
    
    public static OnOffState fromInt(int oosVal) {
        for (OnOffState oos : OnOffState.values()) {
            if (oos.getVal() == oosVal) {
                return oos;
            }
        }
        return null;
    }

    public static int toInt(OnOffState oos) {
        return oos.getVal();
    }
}
