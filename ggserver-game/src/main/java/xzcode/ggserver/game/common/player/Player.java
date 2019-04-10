package xzcode.ggserver.game.common.player;

/**
     玩家基类
 * 
 * @author zai
 * 2019-01-21 20:21:20
 */
public class Player {
	
	/**
	 * 用户id
	 */
	protected Long playerId; 
	
	/**
	 * 用户编号
	 */
	protected String playerNo; 
	
	/**
	 * 用户昵称
	 */
	protected String nickname; 
	
	/**
	 * 用户头像
	 */
	protected String avatar; 
	
	/**
	 * IP地址
	 */
	protected String ip; 
	
	/**
	 * 用户座号
	 */
	protected int seatNum; 
	
	
	/**
	 * 是否在线
	 */
	protected boolean online;
	/**
	 * 是否参与游戏
	 */
	protected boolean inGame;
	
	/**
	 * 是否机器人
	 */
	protected boolean robot;
	
	
	
	public Long getPlayerId() {
		return playerId;
	}


	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}


	public String getPlayerNo() {
		return playerNo;
	}


	public void setPlayerNo(String playerNo) {
		this.playerNo = playerNo;
	}


	public String getNickname() {
		return nickname;
	}


	public void setNickname(String nickname) {
		this.nickname = nickname;
	}


	public String getAvatar() {
		return avatar;
	}


	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}


	public String getIp() {
		return ip;
	}


	public void setIp(String ip) {
		this.ip = ip;
	}


	public int getSeatNum() {
		return seatNum;
	}


	public void setSeatNum(int seatNum) {
		this.seatNum = seatNum;
	}


	public boolean isOnline() {
		return online;
	}


	public void setOnline(boolean online) {
		this.online = online;
	}


	public boolean isRobot() {
		return robot;
	}


	public void setRobot(boolean robot) {
		this.robot = robot;
	}
	
	

	public boolean isInGame() {
		return inGame;
	}
	
	public void setInGame(boolean inGame) {
		this.inGame = inGame;
	}

	

}
