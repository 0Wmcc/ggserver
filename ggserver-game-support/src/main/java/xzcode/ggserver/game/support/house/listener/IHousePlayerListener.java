package xzcode.ggserver.game.support.house.listener;

/**
 * 移除玩家监听器
 * 
 * @param <R>
 * @param <P>
 * @author zai
 * 2019-07-08 10:26:00
 */
@FunctionalInterface
public interface IHousePlayerListener<P> {
	
	void onRemove(P player);
	
}
