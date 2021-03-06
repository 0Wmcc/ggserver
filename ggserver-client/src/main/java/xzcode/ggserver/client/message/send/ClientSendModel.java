package xzcode.ggserver.client.message.send;

/**
 * 消息发送模型
 * 
 * @author zai
 * 2019-03-12 19:20:01
 */
public class ClientSendModel {
	

	/* 发送消息标识 */
	private byte[] action;

	/* 消息体 */
	private byte[] message;

	/* io操作完成回调 */
	private Runnable callback;

	public ClientSendModel(byte[] action, byte[] message) {
		this.action = action;
		this.message = message;
	}
	public ClientSendModel(byte[] action, byte[] message, int sendType) {
		this.action = action;
		this.message = message;
	}

	public ClientSendModel(byte[] action, byte[] message, Runnable callback) {
		super();
		this.action = action;
		this.message = message;
		this.callback = callback;
	}

	public static ClientSendModel create(byte[] action, byte[] message, int sendType) {
		return new ClientSendModel(action, message);
	}
	public static ClientSendModel create(byte[] action, byte[] message) {
		return new ClientSendModel(action, message);
	}

	public static ClientSendModel create(byte[] action, byte[] message, Runnable callback) {
		return new ClientSendModel(action, message, callback);
	}

	public byte[] getSendTag() {
		return action;
	}

	public void setSendTag(byte[] sendTag) {
		this.action = sendTag;
	}

	public Object getMessage() {
		return message;
	}

	public void setMessage(byte[] message) {
		this.message = message;
	}
	
	public Runnable getCallback() {
		return callback;
	}
	
	public void setCallback(Runnable callback) {
		this.callback = callback;
	}

}
