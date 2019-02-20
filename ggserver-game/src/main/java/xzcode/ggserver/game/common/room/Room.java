package xzcode.ggserver.game.common.room;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import xzcode.ggserver.game.common.player.Player;

/**
 * 游戏房间
 * 
 * 
 * @author zai 2018-05-24
 */
public abstract class Room<P extends Player>{	
	/**
	 * 房间id
	 */
	protected Object roomId;

	/**
	 * 房间编号
	 */
	protected String roomNo;

	/**
	 * 房主id
	 */
	protected Object ownerUserId;

	/**
	 * 房主编号
	 */
	protected String ownerUserNo;
	
	/**
	 * 获取最大玩家数
	 * 
	 * @return
	 * @author zai
	 * 2019-02-20 19:07:54
	 */
	public abstract int getMaxPalyerNum();	
	
	/**
	 * 移除玩家
	 * 
	 * @param playerId
	 * @return
	 * @author zai
	 * 2019-02-20 19:10:19
	 */
	public P removePlayer(Object playerId) {
		return players.remove(playerId);
	}

	/**
	 * 玩家集合
	 */
	protected Map<Object, P> players = new ConcurrentHashMap<>();
	
	
	/**
	 * 是否满员
	 * 
	 * @return
	 * @author zai
	 * 2019-02-20 20:24:18
	 */
	public boolean isFullPlayers() {
		return players.size() >= this.getMaxPalyerNum();
	}
	
	/**
	 * 获取当前玩家数
	 * 
	 * @return
	 * @author zai
	 * 2019-02-20 20:24:50
	 */
	public int getPlayerNum() {
		return players.size();
	}
	
	
	public Map<Object, P> getPlayers() {
		return players;
	}

	public P getPlayer(Object userId) {
		return players.get(userId);
	}
	/**
	 * 获取房间当前按座位号排序玩家列表
	 * 
	 * @return
	 * @author Wmc
	 * 2019-02-20 15:30:50
	 */
	public List<P> getOrderPlayerList() {
		List<P> OrderPlayerList = new ArrayList<>(players.size());
		for (Object key : players.keySet()) {
			P p = players.get(key);
			OrderPlayerList.add(p);
		}
		OrderPlayerList.sort((x, y) -> x.getSeatNum() - y.getSeatNum());
		return OrderPlayerList;
	}

	/**
	 * 添加玩家
	 * 
	 * @param player
	 * @author zai 2019-01-24 19:42:15
	 */
	public void addPlayer(P player) {
		this.players.put(player.getPlayerId(), player);
	}

	public Object getRoomId() {
		return roomId;
	}

	public void setRoomId(Object roomId) {
		this.roomId = roomId;
	}

	public String getRoomNo() {
		return roomNo;
	}

	public void setRoomNo(String roomNo) {
		this.roomNo = roomNo;
	}

	public Object getOwnerUserId() {
		return ownerUserId;
	}

	public void setOwnerUserId(Object ownerUserId) {
		this.ownerUserId = ownerUserId;
	}

	public String getOwnerUserNo() {
		return ownerUserNo;
	}

	public void setOwnerUserNo(String ownerUserNo) {
		this.ownerUserNo = ownerUserNo;
	}

}

