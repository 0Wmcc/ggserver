package xzcode.ggserver.core.message.receive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xzcode.ggserver.core.config.GGServerConfig;
import xzcode.ggserver.core.session.GGSession;
import xzcode.ggserver.core.session.GGSessionThreadLocalUtil;

/**
 * 转发消息任务
 * 
 * 
 * @author zai
 * 2019-02-09 14:26:10
 */
public class RedirectMessageTask implements Runnable{
	
	private final static Logger LOGGER = LoggerFactory.getLogger(RedirectMessageTask.class);
	
	/**
	 * 配置
	 */
	private GGServerConfig config;
	
	/**
	 * 请求标识
	 */
	private String action;
	
	/**
	 * socket消息体对象
	 */
	private Object message;
	
	/**
	 * session
	 */
	private GGSession session;
	
	/**
	 * 同步对象
	 */
	private Object syncObj;
	
	
	public RedirectMessageTask() {
		
	}
	

	public RedirectMessageTask(String action, Object message, GGSession session, Object syncLock, GGServerConfig config) {
		this.message = message;
		this.session = session;
		this.action = action;
		this.config = config;
		this.syncObj = syncLock;
	}

	public RedirectMessageTask(String action, Object message, GGSession session, GGServerConfig config) {
		this.message = message;
		this.session = session;
		this.action = action;
		this.config = config;
	}



	@Override
	public void run() {
		
		GGSessionThreadLocalUtil.setSession(this.session);
		try {
			if (syncObj != null) {
				synchronized (syncObj) {
					config.getRequestMessageManager().invoke(action, message);
				}
			}else {
				config.getRequestMessageManager().invoke(action, message);				
			}
			
		} catch (Exception e) {
			LOGGER.error("Redirect Message Task ERROR!! -- actionId: {}, error: {}", action, e);
		}finally {
			GGSessionThreadLocalUtil.removeSession();
		}
		
	}
	

}
