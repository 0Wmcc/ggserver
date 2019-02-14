package xzcode.ggserver.game.common.holder.timeout;

import xzcode.ggserver.game.common.interfaces.finish.CheckFinishedAction;
import xzcode.ggserver.game.common.interfaces.finish.ICheckFinishHolder;
import xzcode.ggserver.game.common.interfaces.finish.OnFinishAction;

/**
 * 可完成的超时容器
 * 
 * @author zai
 * 2019-01-21 11:59:37
 */
public abstract class FinishAbleTimeoutHolder extends TimeoutHolder implements ICheckFinishHolder{

	
	/**
	 * 是否已完成
	 */
	protected boolean finished;
	
	protected OnFinishAction onFinishAction;
	
	protected CheckFinishedAction checkFinishedAction;
	
	@Override
	public void onFinish(OnFinishAction action) {
		this.onFinishAction = action;
	}

	@Override
	public void onCheckFinish(CheckFinishedAction checkFinishedAction) {
		this.checkFinishedAction = checkFinishedAction;
	}

	@Override
	public boolean checkFinish() {
		if (!finished) {
			if (checkFinishedAction != null) {
				this.finished = checkFinishedAction.check(this);
			}
		}
		return finished;
	}

	
	@Override
	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	

	@Override
	public void runOnFinishAction() {
		if (onFinishAction != null) {
			onFinishAction.action(this);
		}
	}
	
}
