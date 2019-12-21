package xzcode.ggserver.game.card.games.house;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xzcode.ggserver.core.server.IGGServer;
import xzcode.ggserver.core.server.impl.GGServer;
import xzcode.ggserver.game.card.games.player.RoomPlayer;
import xzcode.ggserver.game.card.games.room.Room;

/**
 * 大厅管理器
 * 
 * @param <P>
 * @param <R>
 * @param <H>
 * @author zai
 * 2019-12-21 11:34:05
 */
public abstract class House< P extends RoomPlayer<R>, R extends Room<P, R>, H> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(House.class);
	

	protected Class<P> pClass;
	protected Class<R> rClass;
	protected Class<H> hClass;
	
	/**
	 * ggserver对象
	 */
	protected IGGServer gg;

	/**
	 * 大厅id
	 */
	protected String houseId;

	/**
	 * 游戏id
	 */
	protected Long gameId;
	
	/**
	 * 玩家集合
	 */
	protected ConcurrentHashMap<String, P> players = new ConcurrentHashMap<>(2000);


	/**
	 * 房间集合
	 */
	protected ConcurrentHashMap<String, R> rooms = new ConcurrentHashMap<>(1000);
	
	
	

	public House() {
		init();
	}
	
	public String getHouseId() {
		return houseId;
	}
	
	/**
	 * 设置大厅id
	 * 
	 * @param houseId
	 * @author zai
	 * 2019-07-08 10:57:31
	 */
	public void setHouseId(String houseId) {
		this.houseId = houseId;
		
		
	}
	/**
	 * 初始化
	 * 
	 * @author zai
	 * 2019-07-08 10:58:17
	 */
	private void init() {
		
		this.pClass = this.getPClass();
		this.rClass = this.getRClass();
		this.hClass = this.getHClass();
		
	}
	
	private Class<?> getSuperClassGenericsClass(int index){
		return (Class<?>) ((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[index];
	}
	
	@SuppressWarnings("unchecked")
	private Class<P> getPClass(){
		return (Class<P>) getSuperClassGenericsClass(0);
	}
	@SuppressWarnings("unchecked")
	private Class<R> getRClass(){
		return (Class<R>) getSuperClassGenericsClass(1);
	}
	@SuppressWarnings("unchecked")
	private Class<H> getHClass(){
		return (Class<H>) getSuperClassGenericsClass(2);
	}
	
	

	/**
	 * 获取房间
	 * 
	 * @param roomNo
	 * @return
	 * @author zai 2018-12-27 13:57:56
	 */
	public R getRoom(String roomNo) {
		return rooms.get(roomNo);
	}

	/**
	 * 移除房间
	 * 
	 * @param roomNo
	 * @return
	 * @author zai 2019-01-04 17:49:01
	 */
	public R removeRoom(String roomNo) {
		return rooms.remove(roomNo);
	}

	/**
	 * 添加房间
	 * 
	 * @param roomNo
	 * @param room
	 * @author zai 2018-12-27 13:57:46
	 */
	public R addRoom(R room) {
		return rooms.putIfAbsent(room.getRoomNo(), room);
	}

	/**
	 * 是否存在房间
	 * 
	 * @param roomNo
	 * @author zai 2018-12-27 13:57:46
	 */
	public boolean containsRoom(String roomNo) {
		return rooms.containsKey(roomNo);
	}
	
	/**
	 * 获取所有房间数量
	 * 
	 * @return
	 * @author zai
	 * 2019-04-16 12:24:39
	 */
	public int getTotalRoomsNum() {
		return rooms.size();
	}
	
	/**
	 * 添加玩家
	 * 
	 * @param player
	 * @author zai
	 * 2019-04-26 15:25:41
	 */
	public void addPlayer(P player) {
		this.players.put(player.getPlayerId(), player);
	}
	
	/**
	 * 移除玩家
	 * 
	 * @param player
	 * @author zai
	 * 2019-04-26 15:25:47
	 */
	public void removePlayer(P player) {
		this.players.remove(player.getPlayerId());
	}
	
	/**
	 * 获取玩家数
	 * 
	 * @author zai
	 * 2019-04-26 15:30:31
	 */
	public int getPlayerNum() {
		return this.players.size();
	}


	public Long getGameId() {
		return gameId;
	}

	public void setGameId(Long gameId) {
		this.gameId = gameId;
	}


	public IGGServer getGG() {
		return gg;
	}


	public void setGG(GGServer gg) {
		this.gg = gg;
	}
	
	
	
}