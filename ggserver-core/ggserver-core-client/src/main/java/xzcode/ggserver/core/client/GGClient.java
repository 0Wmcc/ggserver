package xzcode.ggserver.core.client;

import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import xzcode.ggserver.core.client.config.GGClientConfig;
import xzcode.ggserver.core.client.starter.IGGClientStarter;
import xzcode.ggserver.core.client.starter.impl.DefaultClientStarter;
import xzcode.ggserver.core.common.config.IGGConfigSupport;
import xzcode.ggserver.core.common.control.IGGContolSupport;
import xzcode.ggserver.core.common.event.IEventManager;
import xzcode.ggserver.core.common.filter.IFilterManager;
import xzcode.ggserver.core.common.future.GGFailedFuture;
import xzcode.ggserver.core.common.future.GGNettyFacadeFuture;
import xzcode.ggserver.core.common.future.IGGFuture;
import xzcode.ggserver.core.common.message.Pack;
import xzcode.ggserver.core.common.session.GGSession;

/**
 * 客户端
 * 
 * @author zzz
 * 2019-09-24 17:19:54
 */
public class GGClient 
implements 
	IGGConfigSupport<GGClientConfig>,
	IGGContolSupport
{
	
	private GGClientConfig config;
	
	private IGGClientStarter clientStarter;
	
	public GGSession connect(String host, int port) {
		return clientStarter.connect(host, port);
	}
	

	public GGClient(GGClientConfig config) {
		this.config = config;
		if (!this.config.isInited()) {
			this.config.init();
		}
		this.clientStarter = new DefaultClientStarter(config);
	}
	
	public GGClientConfig getConfig() {
		return config;
	}
	
	public void shutdown() {
		clientStarter.shutdown();
	}


	@Override
	public IGGFuture send(GGSession session, Pack pack, long delay, TimeUnit timeUnit) {
		if (!config.isChannelPoolEnabled()) {
			return IGGConfigSupport.super.send(session, pack, delay, timeUnit);			
		}

		// 序列化后发送过滤器
		if (!getFilterManager().doAfterSerializeFilters(pack)) {
			return GGFailedFuture.DEFAULT_FAILED_FUTURE;
		}
		
		//以下通过通道池进行发送
		Future<Channel> acquireFuture = config.getChannelPool().acquire();
		GGNettyFacadeFuture future = new GGNettyFacadeFuture();
		acquireFuture.addListener((Future<Channel> f) -> {
			Channel ch = f.getNow();
			if (ch.isActive()) {
				if (delay <= 0) {
					ChannelFuture channelFuture = ch.writeAndFlush(pack);
					future.setFuture((Future<?>) channelFuture);

				} else {
					getTaskExecutor().schedule(delay, timeUnit, () -> {
						ChannelFuture channelFuture = ch.writeAndFlush(pack);
						future.setFuture((Future<?>) channelFuture);
					});
				}
			}
		});
		return future;
	}
	
	


}
