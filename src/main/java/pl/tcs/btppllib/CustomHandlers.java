/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.tcs.btppllib;

import static pl.tcs.btppllib.DeviceGetTimeTest.MAX_FRAME_LENGTH;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import java.util.List;

/**
 *
 * @author tomek
 */
public class CustomHandlers {
    
    public static OcitPacketAssembler getPacketAssemblerDecoder() {
        return new OcitPacketAssembler();
    }
    
    public static LengthFieldBasedFrameDecoder getLengthFieldShortenDecoder() {
        return new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH, 3, 1, 0, 4);        
    }        
    
    public static class OcitPacketAssembler extends ByteToMessageDecoder {

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            final int readBytes = in.readableBytes();
            if (readBytes < 4) {
                return;
            }
            
            final int msgLen = in.getByte(3);            
            if (readBytes < msgLen + 4) {
                return;
            }
            
            out.add(in.readBytes(msgLen + 4));
        }        
    }
}
