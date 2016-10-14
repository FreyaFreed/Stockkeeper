package stockkeeper.network;

public class InviteMessage extends StockKeeperMessage {

	public int level;
	public InviteMessage(int level_) {
		super(MessageType.INVITE);
		level = level_;
	}

}
