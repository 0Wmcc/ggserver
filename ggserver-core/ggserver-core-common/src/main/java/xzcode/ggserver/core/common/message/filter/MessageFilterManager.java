package xzcode.ggserver.core.common.message.filter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xzcode.ggserver.core.common.component.GGComponentManager;
import xzcode.ggserver.core.common.message.PackModel;
import xzcode.ggserver.core.common.message.receive.Request;
import xzcode.ggserver.core.common.message.send.Response;

/**
 * 消息过滤器集合
 *
 * @author zai
 * 2018-12-20 10:15:31
 */
public class MessageFilterManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageFilterManager.class);
	/**
	 * 反序列化前过滤器
	 */
	private final ArrayList<MessageFilterModel> beforeDeserializeFilters = new ArrayList<>(1);
	/**
	 * 请求过滤器
	 */
	private final ArrayList<MessageFilterModel> requestFilters = new ArrayList<>(1);
	
	/**
	 * 响应过滤器
	 */
	private final ArrayList<MessageFilterModel> responseFilters = new ArrayList<>(1);
	
	
	/**
	 * 更新组件引用的实例
	 * @param componentObjectMapper
	 * 
	 * @author zai
	 * 2019-02-09 14:24:04
	 */
	public void updateComponentObject(GGComponentManager componentObjectMapper) {
		
		updateComponentObject(componentObjectMapper, requestFilters);
		updateComponentObject(componentObjectMapper, responseFilters);
	}
	
	private void updateComponentObject(GGComponentManager componentObjectMapper, ArrayList<MessageFilterModel> filters) {
		
		for (MessageFilterModel filterModel : filters) {
			Object object = componentObjectMapper.getComponentObject(filterModel.getFilterClazz());
			filterModel.setFilter((IGGFilter<?>) object);				
		}
	}
	
	/**
	 * 添加过滤器
	 * @param filterModel
	 * 
	 * @author zai
	 * 2019-02-09 14:24:29
	 */
	public void add(MessageFilterModel filterModel) {
		Type[] types = filterModel.getFilterClazz().getGenericInterfaces();
		
		for (Type type : types) {
			if (type == GGBeforeDeserializeFilter.class) {
				beforeDeserializeFilters.add(filterModel);
				if (beforeDeserializeFilters.size() > 1) {
					sort(beforeDeserializeFilters);
				}
				requestFilters.trimToSize();
			}else if (type == GGRequestFilter.class) {
				requestFilters.add(filterModel);
				if (requestFilters.size() > 1) {
					sort(requestFilters);
				}
				requestFilters.trimToSize();
			}else if (type == GGResponseFilter.class ) {
				responseFilters.add(filterModel);
				if (responseFilters.size() > 1) {
					sort(responseFilters);
				}
				responseFilters.trimToSize();
			}
		}
		
	}
	
	public void sort(List<MessageFilterModel> list) {
		list.sort((o1,o2) -> o1.getOrder() - o2.getOrder());
	}
	
	/**
	 * 顺序执行序列化前过滤器
	 * 
	 * @param pack
	 * @return
	 * @author zzz
	 * 2019-10-08 18:56:37
	 */
	public boolean doBeforeDeserializeFilter(PackModel pack) {
		GGBeforeDeserializeFilter filter = null;
		for (MessageFilterModel filterModel : beforeDeserializeFilters) {
			filter = (GGBeforeDeserializeFilter) filterModel.getFilter();
			if (!filter.doFilter(pack)) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Message filtered by {}, action:{} .", filter.getClass().getName(), new String(pack.getAction()));					
				}
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 顺序执行请求过滤器
	 * @param action
	 * @param message
	 * @return
	 * 
	 * @author zai
	 * 2017-09-27
	 */
	public boolean doRequestFilters(Request request) {
		GGRequestFilter filter = null;
		for (MessageFilterModel filterModel : requestFilters) {
			filter = (GGRequestFilter) filterModel.getFilter();
			if (!filter.doFilter(request)) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Message filtered by {}, action:{} .", filter.getClass().getName(), request.getAction());					
				}
				return false;
			}
		}
		return true;
	}
	
	
	
	/**
	 * 顺序执行响应过滤器
	 * @param action
	 * @param message
	 * @return
	 * 
	 * @author zai
	 * 2017-09-27
	 */
	public boolean doResponseFilters(Response response) {
		GGResponseFilter filter = null;
		for (MessageFilterModel filterModel : responseFilters) {
			filter = (GGResponseFilter) filterModel.getFilter();
			if (!filter.doFilter(response)) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Message filtered by {}, action:{} .", filter.getClass().getName(),response.getAction());					
				}
				return false;
			}
		}
		return true;
	}

}