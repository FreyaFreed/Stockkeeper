package stockkeeper.network;

public class MakeGroupMessage extends StockKeeperMessage {

	public String groupname;
	public MakeGroupMessage(String groupname) {
		super(MessageType.MAKEGROUP);
		this.groupname = groupname;

	}

}
