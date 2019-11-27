package xzcode.ggserver.game.card.games.cardgames.player;

import java.util.ArrayList;
import java.util.List;

import xzcode.ggserver.game.card.games.cardgames.poker.PokerCard;

/**
     牌类玩家基类
 * 
 * @author zai
 * 2019-01-21 20:21:20
 */
public abstract class CardPlayer<C extends PokerCard, R, H> extends RoomPlayer<R> implements ICardPlayer<C>{
	
	/**
	 * 手牌
	 */
	protected List<C> handcards = new ArrayList<>(maxHandcarNum());
	
	public void reset() {

		this.handcards.clear();
	}
	
	
	
	
	/**
	 * 获取手牌
	 * 
	 * @return
	 * @author zai
	 * 2019-01-22 19:46:17
	 */
	public List<C> getHandcards() {
		return handcards;
	}
	

}
