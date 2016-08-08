/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.tcs.btppllib;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author tomek
 */
public class HandlersMapping {
    
    public static class Id {
        private int member;
        private int otype;
        private int method;

        public Id(int member, int otype, int method) {
            this.member = member;
            this.otype = otype;
            this.method = method;
        }

        public int getMember() {
            return member;
        }

        public int getOtype() {
            return otype;
        }

        public int getMethod() {
            return method;
        }        

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Id other = (Id) obj;
            if (this.member != other.member) {
                return false;
            }
            if (this.otype != other.otype) {
                return false;
            }
            if (this.method != other.method) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 79 * hash + this.member;
            hash = 79 * hash + this.otype;
            hash = 79 * hash + this.method;
            return hash;
        }
    }
        
    private static Map<Id, Class> responseHandlersMapping = new HashMap<>();
    private static Map<Id, Class> requestHandlersMapping = new HashMap<>();
        
    public static <T> void addResponseHandler(int member, int otype, int method, Class<T> handlerClazz) {
        addResponseHandler(new Id(member, otype, method), handlerClazz);
    }    
    
    public static <T> void addResponseHandler(Id id, Class<T> handlerClazz) {
        responseHandlersMapping.put(id, handlerClazz);
    }
    
    public static <T> Class<T> getResponseHandler(Id id) {
        return responseHandlersMapping.get(id);
    }
    
    public static <T> Class<T> getResponseHandler(int member, int otype, int method) {
        return getResponseHandler(new Id(member, otype, method));
    }
    
    public static <T> void addRequestHandler(int member, int otype, int method, Class<T> handlerClazz) {
        addRequestHandler(new Id(member, otype, method), handlerClazz);
    }    
    
    public static <T> void addRequestHandler(Id id, Class<T> handlerClazz) {        
        requestHandlersMapping.put(id, handlerClazz);
    }
    
    public static <T> Class<T> getRequestHandler(Id id) {
        return requestHandlersMapping.get(id);
    }
    
    public static <T> Class<T> getRequestHandler(int member, int otype, int method) {
        return getRequestHandler(new Id(member, otype, method));
    }
}
