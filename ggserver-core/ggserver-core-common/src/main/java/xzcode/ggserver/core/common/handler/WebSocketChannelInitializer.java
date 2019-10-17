package xzcode.ggserver.core.common.handler;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;
import xzcode.ggserver.core.common.config.GGConfig;
import xzcode.ggserver.core.common.handler.common.InboundCommonHandler;
import xzcode.ggserver.core.common.handler.common.OutboundCommonHandler;
import xzcode.ggserver.core.common.handler.idle.IdleHandler;
import xzcode.ggserver.core.common.handler.web.WebSocketInboundFrameHandler;
import xzcode.ggserver.core.common.handler.web.WebSocketOutboundFrameHandler;

/**
 * WebSocket channel 初始化处理器
 * 
 * 
 * @author zai
 * 2017-08-02
 */
public class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {
	
	private static final Logger logger = LoggerFactory.getLogger(WebSocketChannelInitializer.class);
	
	private GGConfig config;
	
	public WebSocketChannelInitializer() {
	}
	
	public WebSocketChannelInitializer(GGConfig config) {
		this.config = config;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		
	   	if (config.getIdleCheckEnabled()) {
	   		
		   	 //空闲事件触发器
		   	 ch.pipeline().addLast(new IdleStateHandler(config.getReaderIdleTime(), config.getWriterIdleTime(), config.getAllIdleTime(), TimeUnit.MILLISECONDS));
		   	 
		   	 //心跳包处理
		   	 ch.pipeline().addLast(new IdleHandler(this.config));
		   	 
	   	}
	   	
	   	ch.pipeline().addLast( new HttpServerCodec());
	   	ch.pipeline().addLast(new HttpObjectAggregator(config.getMaxDataLength()));
	   	ch.pipeline().addLast(new WebSocketInboundFrameHandler(this.config));
	   	
	   	
	   	//inbound异常处理
	   	ch.pipeline().addLast(new InboundCommonHandler(this.config));
	   	
        //Outbound 是反顺序执行
	   	ch.pipeline().addLast(new WebSocketOutboundFrameHandler(this.config ));
        
        //outbound异常处理
        ch.pipeline().addLast(new OutboundCommonHandler());
        
        
		
	}


	public GGConfig getConfig() {
		return config;
	}
	
	public void setConfig(GGConfig config) {
		this.config = config;
	}

}