package xzcode.ggserver.core.handler.tcp;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import xzcode.ggserver.core.channel.DefaultChannelAttributeKeys;
import xzcode.ggserver.core.config.GGServerConfig;
import xzcode.ggserver.core.message.receive.RequestMessageTask;

/**
 * TCP自定协议解析
 * 
 *包体总长度   标识长度      标识内容       数据体
 * +--------+--------+-------+------------+
 * | 4 byte | 2 byte | tag   |  data body |
 * +--------+--------+-------+------------+
 * @author zai
 *
 */
public class TcpDecodeHandler extends ByteToMessageDecoder {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TcpDecodeHandler.class);

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
	
	

	
	public TcpDecodeHandler() {
		
	}
	
	public TcpDecodeHandler(GGServerConfig config) {
		super();
		this.config = config;
	}


	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		
		int readableBytes = in.readableBytes();
		//如果长度不足4位，放弃并等待下次读取
		if (readableBytes < HEADER_BYTES) {
			return;
		}
		
		in.markReaderIndex();
		
		int packLen = in.readInt();
		
		if (packLen > MAX_PACKAGE_LENGTH) {
			LOGGER.error("Package length {} is over limit {} ! Channel close !", packLen, MAX_PACKAGE_LENGTH);
			ctx.channel().close();
			return;
		}
		
		//readableBytes = in.readableBytes();
		
		int reqTagSize = in.readUnsignedShort();
		
		
		if (readableBytes - HEADER_BYTES - reqTagSize < packLen) {
			in.resetReaderIndex();
			return;
		}
		
		byte[] dataTag = new byte[reqTagSize];
		
		
		in.readBytes(dataTag);
		
		//String reqTag = new String(dataTag, config.getCharset());
		
		//读取数据体 =  总包长 - 标识长度占用字节 - 标识体占用字节数
		int bodyLen = packLen - REQUEST_TAG_LENGTH_BYTES - reqTagSize;
		byte[] data = null;
		if (bodyLen != 0) {
			
			data = new byte[bodyLen];			
			//读取数据体部分byte数组
			in.readBytes(data);
			
		}
		
		
		//获取与请求标识关联的方法参数对象
		//IOnMessageInvoker invoker = config.getRequestMessageManager().get(reqTag);
		
		//if (invoker != null) {
			config.getTaskExecutor().submit(new RequestMessageTask(dataTag, data, ctx.channel().attr(DefaultChannelAttributeKeys.SESSION).get(), config));
		//}
		
		if(LOGGER.isDebugEnabled()){
        	LOGGER.debug("\nReceived binary message  <----,\nchannel:{}\ntag:{}\nbytes-length:{}", ctx.channel(), dataTag, bodyLen);
        }
		
	}


}
