/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.tcs.btppllib;

import pl.tcs.btppllib.utils.FletcherChecksum;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.apache.log4j.Logger;

/**
 *
 * @author tomek
 */
public class DeviceGetTimeTest {
    
    private static final Logger log = Logger.getLogger(DeviceGetTimeTest.class);
    
    public static int MAX_FRAME_LENGTH = 65*1024;
    
    public static void main(String[] args) throws Exception {
        final String connectionStr = args[0];
        String[] connParams = connectionStr.split(":");
        
        String host = connParams[0];
        int port = Integer.parseInt(connParams[1]);
        log.info(String.format("***** Running test on host=%s and port=%d", host, port));
        
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.option(ChannelOption.SO_TIMEOUT, 10000);
//            b.option(ChannelOption.SO_REUSEADDR, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel c) throws Exception {
                    ChannelPipeline pipeline = c.pipeline();
                    pipeline.addLast("frameJoiner", new OcitMessageDecoder());
                    pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH, 3, 1, 0, 4));                    
//                    pipeline.addLast("frameJoiner", new OcitMessageDecoder());
                    pipeline.addLast("timeHandler", new GetTimeTestHandler());                                        
                }            
            });
            
            ChannelFuture f = b.connect(host, port).sync(); // (5)
            
            Channel c = f.channel();            
            c.writeAndFlush(buildGetTimeRequest())
                    .addListener(new ChannelFutureListener() {

                        @Override
                        public void operationComplete(ChannelFuture f) throws Exception {
                            if (f.isSuccess()) {
                                log.info("*** GetTime request sent with success!!!");                                
                            } else {
                                log.error("*** ERROR when sending GetTime request");
                            }                           
                        }
                    });
            
//            c.closeFuture().sync();        
        
        } finally {
//            workerGroup.shutdownGracefully();
        }        
    }    
    
//    struct btppl_tg_header
//    {
//      OCIT_UI1 hdrlen;                /* Sizeof(OCITHeadEntries) + Length of Path */
//      OCIT_UI1 flags;                 /* Telegramtype, Version und Security */
//      OCIT_UI2 jobtime;               /* lower 16 Bits of UTC, when the tg has been created */
//      OCIT_UI2 jobtime_subsecond;     /* special counter for tg discrimination */
//      OCIT_UI2 member;                /* Member-Entry of the Object being called */
//      OCIT_UI2 otype;                 /* type of object being called */
//      OCIT_UI2 method;                /* method being called at object */
//      OCIT_UI2 znr;                   /* CentralDevice# where the object is called */
//      OCIT_UI2 fnr;                   /* FieldDevice# where the object is called */
//    };
//    UI1 = (byte)
//    UI2 = (short)          
    
//    enum btppl_checksum_type
//  {
//    BtpplFletcherOnly    = 0x00, /* just fletcher checksum in the telegram */
//    BtpplFletcherPlusSha = 0x01, /* fletcher checksum plus sha-authentication */
//    BtpplFletcherAll     = (BtpplFletcherOnly | BtpplFletcherPlusSha) /* all bits used for the checksum-type */
//  };
     
    private static byte TG_HEADER_SZ = (byte)16;
    private static enum BtpplChecksumType {
        BtpplFletcherOnly((byte)0x00), /* just fletcher checksum in the telegram */
        BtpplFletcherPlusSha((byte)0x01), /* fletcher checksum plus sha-authentication */
        BtpplFletcherAll((byte)(0x00 | 0x01));
        
        private byte val;
        
        BtpplChecksumType(byte val) {
            this.val = val;
        }        
        
        public byte getVal() {
            return val;
        }
    }; 
    
    private static int subsecondDiscriminator = 0;
    
    protected static String getBufStr(byte[] buf) {
        
        StringBuffer sb = new StringBuffer("[");
        for (byte b : buf) {
            sb.append(String.format("%2x,", 0xFF & b));
        }
        sb.append("]");
        
        return sb.toString();
    }
    
    protected static ByteBuf buildGetTimeRequest() {
        log.debug("*** Build GetTime request");
        
        ByteBuf retBuf = Unpooled.buffer(256);
        
        // preamble:
        retBuf.writeByte(0);
        retBuf.writeByte(0);
        retBuf.writeByte(0);
        retBuf.writeByte(0x12);
        
        // rest:
        
        retBuf.writeByte(toUnsigned(TG_HEADER_SZ)); 
        retBuf.writeByte(toUnsigned(BtpplChecksumType.BtpplFletcherOnly.getVal()));
        final long utcTime = getUTCTime();
        
//        retBuf.writeShort((int)utcTime);
        retBuf.writeShort(0x4397);
        retBuf.writeShort(subsecondDiscriminator);
//        subsecondDiscriminator++;
        
        retBuf.writeShort(0);    // member
        retBuf.writeShort(815);    // otype        
        retBuf.writeShort(103); // method (103 = GetTime)
        
        retBuf.writeShort(1);
        retBuf.writeShort(100);                     
        
//        int writerIdx = retBuf.writerIndex();
//        log.debug("@Writer index: " + writerIdx);
        int nb = retBuf.readableBytes() - 4;        
        byte[] fletcherInput = new byte[nb]; 
        retBuf.getBytes(4, fletcherInput);
//        for (int i = 0; i < fletcherInput.length; i++) {
//            fletcherInput[i] = (byte)(fletcherInput[i] & 0xFF);            
//        }        
        log.info("BUF: " + getBufStr(fletcherInput));
        FletcherChecksum fcs = new FletcherChecksum(fletcherInput);
//        byte[] checksum = fcs.getChecksum(); 
//        retBuf.writeByte(toUnsigned(checksum[0]));
//        retBuf.writeByte(toUnsigned(checksum[1]));
        int checksumInt = fcs.getChecksumAsInt();
        
        log.info(String.format("********** 0x%x VS 0x%x", checksumInt, 0xe530));
        retBuf.writeShort(checksumInt);
//        retBuf.writeShort(0xe530);
            
        log.debug("LogBuf: " + ByteBufUtil.prettyHexDump(retBuf));        
        
        return retBuf;
    } 
    
    private static int toUnsigned(byte b) {
        return b & 0xFF;
    }
    
    private static int toUnsigned(short s) {
        return s & 0xFFFF;
    }      
    
    private static long getUTCTime() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        return cal.getTimeInMillis();        
    }
    
//    void fletcher(VarArray buf, OCIT_UI4 bufsize, OCIT_UI1 * fletchlow,
//OCIT_UI1 * fletchhigh)
//{
//	OCIT_UI1 fl, fh;
//
//	fl = fh = 0;
//	while (bufsize > 0)
//	{
//		fl = (OCIT_UI1) ((fl + *buf) % 255);
//		fh = (OCIT_UI1) ((fh + fl) % 255);
//		buf++;
//		bufsize--;
//	}
//	*fletchhigh = fh;
//	*fletchlow = fl;
//}
    
//    public static class LengthBasedInitializer extends ChannelInitializer<Channel> {
//
//        @Override
//        protected void initChannel(Channel ch) throws Exception {
//            ChannelPipeline pipeline = ch.pipeline();
//            pipeline.addLast(new LengthFieldBasedFrameDecoder(65*1024, 3, 1));
//        }
//    }           
    
//    public class TimeClientHandler extends ChannelInboundHandlerAdapter {
//
//        private ByteBuf buf;
//        private int msgLen;
//
//        @Override
//        public void handlerAdded(ChannelHandlerContext ctx) {
//            buf = ctx.alloc().buffer(4); // (1)
//        }
//
//        @Override
//        public void handlerRemoved(ChannelHandlerContext ctx) {
//            buf.release(); // (1)
//            buf = null;
//        }
//
//        @Override
//        public void channelRead(ChannelHandlerContext ctx, Object msg) {
//            ByteBuf m = (ByteBuf) msg;
//            buf.writeBytes(m); // (2)
//            m.release();
//            
//            final int readBytes = buf.readableBytes();
//
//            if (readBytes >= 4) { // (3)
//                this.msgLen = buf.getByte(3);
//                log.debug("@msgLen = " + msgLen);
//                
//                if (readBytes >= msgLen) {
//                    
//                    ctx.close();
//                }
//            }
//        }
//
//        @Override
//        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
//            cause.printStackTrace();
//            ctx.close();
//        }
//    }        
    
//    public static class OcitMessageDecoder extends MessageToMessageDecoder<ByteBuf> {
//        
//        private ByteBuf buf;
//        private int msgLen;
//
//        public OcitMessageDecoder() {
//            this.buf = Unpooled.buffer();
//        }
//        
//        @Override
//        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
//            try {
//                log.debug("DECODING!!!!");
//                final int bytesRead = in.readableBytes();            
//                if (bytesRead < 4) {
//                    buf.writeBytes(in);
//                    return;
//                }
//
//                msgLen = in.getByte(3);
//                log.debug("msgLen = " + msgLen + " bytesRead = " + bytesRead);
//
//                if (bytesRead < msgLen) {
//                    buf.writeBytes(in);
//                    return;
//                }
//                
//                out.add(in.readBytes(msgLen));
//            } finally {
//                ctx.close();
//            }
//        }
//    }
    
    public static class OcitMessageDecoder extends ByteToMessageDecoder {

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
    
    public static class GetTimeTestHandler extends SimpleChannelInboundHandler<ByteBuf>  /*ChannelInboundHandlerAdapter*/ {
        
//        @Override
//        public void channelRead(ChannelHandlerContext ctx, Object msg) {
//            ByteBuf m = (ByteBuf) msg; // (1)
//            log.info("****** Message received");                                    
//            
//            try {
//                m.readShort(); m.readShort();  // preamble
//                byte hdrLen = m.readByte();
//                byte opts = m.readByte();
//                short jobTime = m.readShort();
//                short jobTimeCnt = m.readShort();
//                short member = m.readShort();
//                short otype = m.readShort();
//                short method = m.readShort();
//                short znr = m.readShort();
//                short fnr = m.readShort();
//                short retcode = m.readShort();
//                
//                long devTime = m.readUnsignedInt() * 1000;
//                log.info("devTime = " + devTime);
//                Date date = new Date(devTime);
//                
//                log.info(String.format("znr=%d fnr=%d retcode=%d date=%s", znr, fnr, retcode, date.toString()));                
//                
//                
////                long currentTimeMillis = (m.readUnsignedInt() - 2208988800L) * 1000L;
////                System.out.println(new Date(currentTimeMillis));
////                ctx.close();
//            } finally {
//                m.release();
//            }
//        }
//
//        @Override
//        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
//            cause.printStackTrace();
//            ctx.close();
//        }        
        
        // @btppl_connection_send [btppl_osdep.c] -> send telegram to device

        @Override
        protected void channelRead0(ChannelHandlerContext chc, ByteBuf m) throws Exception {
            log.info("@readable bytes = " + m.readableBytes());
            
            byte hdrLen = m.readByte();
            byte opts = m.readByte();
            short jobTime = m.readShort();
            short jobTimeCnt = m.readShort();
            short member = m.readShort();
            short otype = m.readShort();
            short method = m.readShort();
            short znr = m.readShort();
            short fnr = m.readShort();
            short retcode = m.readShort();

            long devTime = m.readUnsignedInt() * 1000;
            log.info("devTime = " + devTime);
            Date date = new Date(devTime);

            log.info(String.format("channelRead0: hdrLen=%d znr=%d fnr=%d retcode=%d date=%s", hdrLen, znr, fnr, retcode, date.toString()));
        }
    } 
}
