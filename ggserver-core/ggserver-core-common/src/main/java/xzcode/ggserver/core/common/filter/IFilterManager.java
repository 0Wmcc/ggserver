package xzcode.ggserver.core.common.filter;

import xzcode.ggserver.core.common.message.Pack;
import xzcode.ggserver.core.common.message.request.Request;
import xzcode.ggserver.core.common.message.response.Response;

/**
 * 过滤器管理器
 * 
 * 
 * @author zai 2019-12-01 17:01:14
 */
public interface IFilterManager {

	boolean doBeforeDeserializeFilters(Pack pack);

	boolean doRequestFilters(Request<?> request);

	boolean doResponseFilters(Response response);

	boolean doAfterSerializeFilters(Pack pack);

	void addBeforeDeserializeFilter(IBeforeDeserializeFilter filter);

	void addRequestFilter(IRequestFilter filter);

	void addResponseFilter(IResponseFilter filter);

	void removeBeforeDeserializeFilter(IBeforeDeserializeFilter filter);

	void removeResponseFilter(IResponseFilter filter);

	void removeRequestFilter(IRequestFilter filter);

	void addAfterSerializeFilter(IAfterSerializeFilter filter);

	void removeAfterSerializeFilter(IAfterSerializeFilter filter);

}