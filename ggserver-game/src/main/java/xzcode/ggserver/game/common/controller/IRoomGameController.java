package xzcode.ggserver.game.common.controller;

import java.util.Map;

import xzcode.ggserver.game.common.interfaces.condition.ICheckCondition;
import xzcode.ggserver.game.common.player.Player;

/**
 * 房间游戏控制器接口
 * 
 * @param <R>
 * @param <P>
 * @author zai
 * 2019-02-16 17:57:18
 */
public interface IRoomGameController<R, P extends Player> {

	/**
	 * 获取所有玩家
	 * 
	 * @return
	 * 
	 * @author zai 2019-02-10 14:18:49
	 */
	Map<Object, P> getPlayers(R room);

	/**
	 * 根据id获取玩家
	 * 
	 * @return
	 * 
	 * @author zai 2019-02-10 14:34:15
	 */
	P getPlayer(R room, Object playerId);
	
	/**
	 * 根据条件获取玩家
	 * 
	 * @param room
	 * @param condition
	 * @return
	 * @author zai
	 * 2019-02-16 17:48:42
	 */
	P getPlayer(R room, ICheckCondition<P> condition);

	/**
	 * 遍历所有玩家
	 * 
	 * @param room
	 * @param eachPlayer
	 * 
	 * @author zai 2019-02-10 14:19:14
	 */
	void eachPlayer(R room, ForEachPlayer<P> eachPlayer);
	
	/**
	 * 遍历所有玩家并返回布尔值
	 * 
	 * @param room
	 * @param eachPlayer
	 * @return
	 * @author zai
	 * 2019-02-11 10:56:05
	 */
	boolean boolEachPlayer(R room, BoolForEachPlayer<P> eachPlayer);

	/**
	 * 广播给所有玩家
	 * 
	 * @param room
	 * @param actionId
	 * @param message
	 * @author zai 2019-01-25 11:06:29
	 */
	void bcToAllPlayer(R room, String actionId, Object message);

	
	/**
	 * 随机获取玩家
	 * 
	 * @return
	 * @author zai
	 * 2019-02-16 17:59:11
	 */
	P getRandomPlayer(R room);

	/**
	 * 随机获取满足条件的玩家
	 * 
	 * @param condition
	 * @return
	 * @author zai
	 * 2019-02-16 18:03:09
	 */
	P getRandomPlayer(R room, ICheckCondition<P> condition);

	
	/**
	 * 获取指定条件下，首个满足条件的玩家(多个玩家的时候)
	 * @param room
	 * @param curplayer
	 * @param condition
	 * @return
	 */
	P getNextPlayer(R room ,P curplayer ,ICheckCondition<P> condition );
	
}
