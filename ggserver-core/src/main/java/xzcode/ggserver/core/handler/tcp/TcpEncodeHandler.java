package xzcode.ggserver.core.handler.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import xzcode.ggserver.core.channel.DefaultChannelAttributeKeys;
import xzcode.ggserver.core.config.GGServerConfig;
import xzcode.ggserver.core.executor.task.RequestMessageTask;
import xzcode.ggserver.core.message.send.SendModel;

/**
 * 
 * @author zai
 * 包体总长度       标识长度      标识内容       数据体
 * +--------+--------+-------+------------+
 * | 4 byte | 2 byte | tag   |  data body |
 * +--------+--------+-------+------------+
 * 2018-12-07 13:38:22
 */
public class TcpEncodeHandler extends ChannelOutboundHandlerAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(TcpEncodeHandler.class);
	    

	private GGServerConfig config;
	
	private static final Gson GSON = new GsonBuilder()
    		.serializeNulls()
    		.create();
	
	
	public TcpEncodeHandler() {
	}
	
	
	public TcpEncodeHandler(GGServerConfig config) {
		super();
		this.config = config;
	}

	
	
	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		
		Channel channel = ctx.channel();
		if (!channel.isActive()) {
    		if(LOGGER.isDebugEnabled()){
        		LOGGER.debug("\nWrite channel:{} is inActive...", ctx.channel());        		
        	}
			return;
		}
		
		ByteBuf out = null;
		
		if (msg instanceof SendModel) {
		
			SendModel sendModel = (SendModel) msg;
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("\nSending message ---> \ntag:{}\nmessage:{}", sendModel.getSendTag(), GSON.toJson(sendModel));
			}
			
			
			byte[] tagBytes = sendModel.getSendTag();
		
			//如果有消息体
			if (sendModel.getMessage() != null) {
				
				byte[] bodyBytes = (byte[]) sendModel.getMessage();
				
				int packLen = 2 + tagBytes.length + bodyBytes.length;
				
				out = ctx.alloc().buffer(packLen);
				
				out.writeInt(packLen);
				out.writeShort(tagBytes.length);
				out.writeBytes(tagBytes);
				out.writeBytes(bodyBytes);
				if (LOGGER.isInfoEnabled()) {
                	LOGGER.info("message sended. \nchannel:{}\ntag:{}\nmessage-length:{}", channel, new String(tagBytes, config.getCharset()), bodyBytes.length);
                }
			} else {
			
				//如果没消息体
				
				int packLen = 2 + tagBytes.length;
				
				out = ctx.alloc().buffer(packLen);
				
				out.writeInt(packLen);
				out.writeShort(tagBytes.length);
				out.writeBytes(tagBytes);
				
				if (LOGGER.isInfoEnabled()) {
                	LOGGER.info("message sended. \nchannel:{}\ntag:{}\nmessage-length:{}", channel, new String(tagBytes, config.getCharset()), 0);
                }
				
			}
			ChannelFuture channelFuture = null;
			if(channel.isWritable()){
				channelFuture =  ctx.writeAndFlush(out);
			}else {
				try {
					if (LOGGER.isInfoEnabled()) {
	                	LOGGER.info("Channel is not writable, change to sync mode! \nchannel:{}", channel);
	                }
					channelFuture = channel.writeAndFlush(out).sync();
	                if (LOGGER.isInfoEnabled()) {
	                	LOGGER.info("Sync message sended. \nchannel:{}\nmessage:{}", channel, GSON.toJson(msg));
	                }
	            } catch (InterruptedException e) {
	            	if (LOGGER.isInfoEnabled()) {
	            		LOGGER.info("write and flush msg exception. msg:[{}]", GSON.toJson(msg), e);
	            	}
	            }
			}
			//添加回调
			if (channelFuture != null) {
				channelFuture.addListener(future -> {
					if (future.isSuccess()) {
						if (sendModel.getCallback() != null) {
							config.getTaskExecutor().submit(new RequestMessageTask(sendModel.getCallback(), channel.attr(DefaultChannelAttributeKeys.SESSION).get()));
	    				}
					}
				});
			}
		}else if (msg instanceof byte[]) {
			byte[] bytes = (byte[]) msg;
			out = ctx.alloc().buffer(bytes.length);
			out.writeBytes(bytes);
			ctx.writeAndFlush(out);
		}
		
	}


	

}