/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.tcs.btppllib;

import pl.tcs.btppllib.commons.CriticalException;
import pl.tcs.btppllib.commons.OcitPort;
import pl.tcs.btppllib.telegrams.OcitRequestTelegram;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author tomek
 */
public class OcitClient {        
    
    private static final Logger log = Logger.getLogger(OcitClient.class);
    
    private int znr;
    private int fnr;
    
    private String host;
    
    OcitPort pnpPort = OcitPort.PnpPort;
    OcitPort phpPort = OcitPort.PhpPort;    
    
    private Channel pnpChannel;
    private Channel phpChannel;
    
    private List<EventLoopGroup> eventLoopGroups = new ArrayList<>();
        
    public OcitClient(final String host, int znr, int fnr) {
        this(host, znr, fnr, true);
    }
    
    // on one host always 2 channels will be opened:
    // -PNP (low-level)
    // -PHP (high-level)    
    public OcitClient(final String host, int znr, int fnr, boolean withBootstrap) {
        log.info("*** Initializing client for host: " + host);
        this.host = host;
        this.znr = znr;
        this.fnr = fnr;
        if (withBootstrap) {
            eventLoopGroups.clear();
            pnpChannel = bootstrapClient(host, pnpPort.getPortNr());
            phpChannel = bootstrapClient(host, phpPort.getPortNr());            
        }
    }
        
    public Channel bootstrapClient(String host, int port) {
        log.info(String.format("Bootstrapping client on host=%s and port=%d", host, port));
        
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        eventLoopGroups.add(workerGroup);
        
        try {
            Bootstrap b = new Bootstrap(); 
            b.group(workerGroup); 
            b.channel(NioSocketChannel.class); 
            b.option(ChannelOption.SO_KEEPALIVE, true); 
            b.option(ChannelOption.SO_TIMEOUT, 100 * 1000);
//            b.option(ChannelOption.SO_REUSEADDR, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel c) throws Exception {
                    ChannelPipeline pipeline = c.pipeline();
                    
                    // assemble + header analyzing
                    pipeline.addLast("frameAssembler", CustomHandlers.getPacketAssemblerDecoder());
                    pipeline.addLast("frameLengthFieldShortenDecoder", CustomHandlers.getLengthFieldShortenDecoder());                    
                                        
                    // custom frame handling, packet mutex!
                    pipeline.addLast("frameHandler", new OcitResponseTelegramHandler());                                        
                }            
            });
            
            ChannelFuture f = b.connect(host, port).sync();
            
            log.info("Done!!!!!!!!!!!!!");
            
            return f.channel();             
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            throw new CriticalException(e);
        }
    }    
    
    public void sendOnChannel(OcitPort port, OcitRequestTelegram inputTelegram) {
        ByteBuf outBuf = inputTelegram.toByteBuf();
        sendOnChannel(port, outBuf);        
    }
    
    public void sendOnChannel(OcitPort port, ByteBuf buf) {
        
        boolean channelsAvailable = testChannelAvailability();
        if (!channelsAvailable) {
            throw new UnsupportedOperationException("Cannot send on channels, should reinitialize");
        }        
        
        Channel outChannel = null;
        switch (port) {
            case PnpPort:
                outChannel = pnpChannel;
                break;
            case PhpPort:
                outChannel = phpChannel;
                break;            
        }
        
        outChannel.writeAndFlush(buf)
                .addListener(new ChannelFutureListener() {

                    @Override
                    public void operationComplete(ChannelFuture f) throws Exception {
                        if (f.isSuccess()) {
                            log.info("*** Sent request sent with success!!!");
                        } else {
                            log.error("*** ERROR when sending request");
                        }
                    }
                });
    }   
    
    public void closeAll() {
        try {
            pnpChannel.closeFuture().sync();
            phpChannel.closeFuture().sync();
            
            for (EventLoopGroup elg : eventLoopGroups) {
                elg.shutdownGracefully();
            }
            
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            throw new CriticalException(e);
        }        
    }
    
    public boolean testChannelAvailability() {
        // TODO: test connection!
        return (pnpChannel != null && phpChannel != null);        
    }
    
    public boolean testPnpAvailaibility() {
        return (pnpChannel != null);
    }
    
    public boolean testPhpAvailiability() {
        return (phpChannel != null);
    }

    public String getHost() {
        return host;
    }

    public int getZnr() {
        return znr;
    }

    public void setZnr(int znr) {
        this.znr = znr;
    }

    public int getFnr() {
        return fnr;
    }

    public void setFnr(int fnr) {
        this.fnr = fnr;
    }
}
