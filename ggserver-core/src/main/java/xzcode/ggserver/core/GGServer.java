package xzcode.ggserver.core;

import java.lang.reflect.ParameterizedType;
import java.util.concurrent.ScheduledFuture;

import xzcode.ggserver.core.config.GGServerConfig;
import xzcode.ggserver.core.event.EventRunnableInvoker;
import xzcode.ggserver.core.event.IEventInvoker;
import xzcode.ggserver.core.executor.task.TimeoutRunnable;
import xzcode.ggserver.core.executor.timeout.ISetTimeoutExecution;
import xzcode.ggserver.core.message.receive.IOnMessageAction;
import xzcode.ggserver.core.message.receive.invoker.OnMessagerInvoker;
import xzcode.ggserver.core.message.send.ISendMessage;
import xzcode.ggserver.core.session.GGSession;
import xzcode.ggserver.core.session.GGSessionThreadLocalUtil;

/**
 * socket服务工具
 * 
 * 
 * @author zai 2017-08-04
 */
public class GGServer implements ISendMessage, ISetTimeoutExecution{
	
	private GGServerConfig serverConfig;
	
	


	public GGServer(GGServerConfig serverConfig) {
		super();
		this.serverConfig = serverConfig;
	}


	

	/**
	 * 获取当前会话session对象
	 * 
	 * @return
	 * 
	 * @author zai 2017-08-04
	 */
	public GGSession getSession() {
		return GGSessionThreadLocalUtil.getSession();
	}
	
	/**
	 * 获取指定用户的session
	 * 
	 * @param userId
	 * @return
	 * @author zai
	 * 2019-01-19 15:50:11
	 */
	public GGSession getSession(Object userId) {
		return this.serverConfig.getUserSessonManager().get(userId);
	}

	/**
	 * 把用户绑定到当前通信会话
	 * 
	 * @param userId
	 * 
	 * @author zai 2017-08-04
	 */
	public void userRegister(Object userId) {
		GGSession session = GGSessionThreadLocalUtil.getSession();

		session.register(userId);

		// 已注册会话绑定
		this.serverConfig.getUserSessonManager().put(userId, session);
	}

	/**
	 * 判断是否已登录
	 * 
	 * @param userId
	 * @author zai 2018-12-29 10:22:11
	 */
	public boolean isRegistered() {
		GGSession session = GGSessionThreadLocalUtil.getSession();
		return session.getRegisteredUserId() != null;
	}

	/**
	 * 把用户从注册绑定中移除
	 * 
	 * @param userId
	 * @author zai 2017-08-19 01:09:56
	 */
	public GGSession userUnregister(Object userId) {
		GGSession session = GGSessionThreadLocalUtil.getSession();

		session.unregister();

		// 注销会话绑定
		this.serverConfig.getUserSessonManager().remove(userId);
		
		return session;
	}

	/**
	 * 断开指定用户的连接
	 * 
	 * @param userId
	 * @author zai 2017-08-19 01:12:07
	 */
	public void disconnect(Object userId) {
		GGSession session = this.serverConfig.getUserSessonManager().get(userId);
		if (session != null && session.getChannel() != null) {
			session.getChannel().close();
		}
	}

	/**
	 * 断开当前连接
	 * 
	 * @author zai 2017-09-21
	 */
	public void disconnect() {
		getSession().getChannel().close();
	}
	
	/**
	 * 动态监听消息
	 * 
	 * @param string
	 * @param socketOnMessage
	 * @author zai
	 * 2019-01-02 09:41:59
	 * @param <T>
	 */
	public <T> void on(String requestTag, IOnMessageAction<T> socketOnMessage) {
		
		
		OnMessagerInvoker<T> invoker = new OnMessagerInvoker<>();
		invoker.setOnMessage(socketOnMessage);
		invoker.setRequestTag(requestTag);
		//获取泛型类型参数
		Class<?> msgClass = (Class<?>) ((ParameterizedType)socketOnMessage.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
		invoker.setRequestMessageClass(msgClass );
		
		serverConfig.getMessageInvokerManager().put(requestTag, invoker);
	}
	
	/**
	 * 动态添加事件监听
	 * 
	 * @param eventTag
	 * @param runnable
	 * @author zai
	 * 2019-01-02 20:02:37
	 */
	public <T> void onEvent(String eventTag, Runnable runnable) {
		IEventInvoker eventInvoker = serverConfig.getEventInvokerManager().get(eventTag);
		if (eventInvoker != null) {
			((EventRunnableInvoker)eventInvoker).addRunnable(runnable);
			return;
		}
		EventRunnableInvoker invoker = new EventRunnableInvoker();
		invoker.setEventTag(eventTag);
		invoker.addRunnable(runnable);
		serverConfig.getEventInvokerManager().put(invoker);
	}



	@Override
	public void send(Object userId, String action, Object message) {
		this.serverConfig.getSendMessageManager().send(userId, action, message);;
		
	}



	@Override
	public void send(Object userId, String action) {
		this.serverConfig.getSendMessageManager().send(userId, action);
		
	}



	@Override
	public void send(String action) {
		this.serverConfig.getSendMessageManager().send(action);
		
	}


	@Override
	public void send(String action, Object message) {
		this.serverConfig.getSendMessageManager().send(action, message);
		
	}


	@Override
	public ScheduledFuture<?> setTimeout(Runnable runnable, long timeoutMilliSec) {
		return this.serverConfig.getTaskExecutor().setTimeout(runnable, timeoutMilliSec);
	}


	@Override
	public ScheduledFuture<?> setTimeout(TimeoutRunnable runnable, long timeoutMilliSec) {
		return this.serverConfig.getTaskExecutor().setTimeout(runnable, timeoutMilliSec);
	}

}
