/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.tcs.btppllib;

import pl.tcs.btppllib.commons.CriticalException;
import pl.tcs.btppllib.telegrams.OcitResponseTelegram;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;

/**
 *
 * @author tomek
 */
public class OcitResponseTelegramHandler extends ChannelInboundHandlerAdapter {
    
    private static final Logger log = Logger.getLogger(OcitResponseTelegramHandler.class);
    
    private static ExecutorService executor = Executors.newCachedThreadPool();
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg; // (1)
        log.debug("****** Message received ******");
        log.debug(ByteBufUtil.prettyHexDump(buf));
        log.debug("******************************");
        
        final int bufLen = buf.readableBytes();
        
        if (bufLen < 18) {
            throw new CriticalException(String.format("Incomplete telegram received [size]=%d", bufLen));
        }        
        
        try {
            // member = 6, otype = 8, method = 10
            int memberPos = 6, 
                otypePos = 8, 
                methodPos = 10;
            
            final int member = buf.getUnsignedShort(memberPos);
            final int otype = buf.getUnsignedShort(otypePos);
            final int method = buf.getUnsignedShort(methodPos);
            
            Class handlerClazz = HandlersMapping.getResponseHandler(member, otype, method);
            if (handlerClazz != null) {
                log.debug(String.format("Found response handler for member=%d otype=%d method=%d with name=%s", member, otype, method, handlerClazz.getSimpleName()));
                
                OcitResponseTelegram resp = (OcitResponseTelegram) handlerClazz.newInstance();
                resp.fromByteBuf(buf);  // initialize fields
                
                List<ResponseTelegramAction> allActions = resp.getActions();
                for (ResponseTelegramAction action : allActions) {
                    executor.submit(action);                    
                }
            }                        
        } catch (InstantiationException | IllegalAccessException e) {
            throw new CriticalException(e);        
        } finally {
            buf.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }        
}
