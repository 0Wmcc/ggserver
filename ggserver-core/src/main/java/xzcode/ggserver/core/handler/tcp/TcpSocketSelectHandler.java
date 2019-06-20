package xzcode.ggserver.core.handler.tcp;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import xzcode.ggserver.core.config.GGServerConfig;
import xzcode.ggserver.core.constant.GGServerTypeConstants;
import xzcode.ggserver.core.handler.web.WebSocketInboundFrameHandler;
import xzcode.ggserver.core.handler.web.WebSocketOutboundFrameHandler;

/**
 * socket类型选择处理器
 * 
 * @author zai
 * 2019-06-15 14:25:54
 */
public class TcpSocketSelectHandler extends ByteToMessageDecoder {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TcpSocketSelectHandler.class);
	/**
	 * 数据包长度标识 字节数
	 */
	public static final int PACKAGE_LENGTH_BYTES = 4;
	
	/**
	 * 每个数据包最大长度
	 */
	public static final int MAX_PACKAGE_LENGTH = 65536;
	
	/**
	 * 请求标识字符串长度单位 字节数
	 */
	public static final int REQUEST_TAG_LENGTH_BYTES = 2;
	
	/**
	 * 请求头 长度标识字节数
	 */
	public static final int HEADER_BYTES = PACKAGE_LENGTH_BYTES + REQUEST_TAG_LENGTH_BYTES;
	
	private GGServerConfig config;
	
	public TcpSocketSelectHandler() {
		
	}
	
	

	public TcpSocketSelectHandler(GGServerConfig config) {
		super();
		this.config = config;
	}



	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		
		int readableBytes = in.readableBytes();
		in.markReaderIndex();
		if (readableBytes < GGServerTypeConstants.TCP.length()) {
			return;
		}
		byte[] bytes = new byte[GGServerTypeConstants.TCP.length()];
		in.readBytes(bytes);
		String tag = new String(bytes);
		if (tag.equals(GGServerTypeConstants.TCP)) {
			ctx.pipeline().addAfter("TcpSocketSelectHandler","TcpDecodeHandler", new TcpDecodeHandler(config));
			
			ctx.pipeline().addAfter("InboundCommonHandler","TcpEncodeHandler", new TcpEncodeHandler(config));
			//发送tcp响应标识
			ctx.channel().writeAndFlush(GGServerTypeConstants.TCP.getBytes());
		}else {
			ctx.pipeline().addAfter("TcpSocketSelectHandler","HttpServerCodec", new HttpServerCodec());
			ctx.pipeline().addAfter("HttpServerCodec","HttpObjectAggregator", new HttpObjectAggregator(config.getHttpMaxContentLength()));
			ctx.pipeline().addAfter("HttpObjectAggregator","WebSocketInboundFrameHandler", new WebSocketInboundFrameHandler(config));
			
			ctx.pipeline().addAfter("InboundCommonHandler","WebSocketOutboundFrameHandler", new WebSocketOutboundFrameHandler(config));
			in.resetReaderIndex();
		}
		ctx.pipeline().remove("TcpSocketSelectHandler");
		
	}


}