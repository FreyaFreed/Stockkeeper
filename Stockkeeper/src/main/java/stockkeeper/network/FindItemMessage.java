package stockkeeper.network;

public class FindItemMessage extends StockKeeperMessage {

	public String itemName;
	public FindItemMessage(String itemName) {
		super(MessageType.FINDITEM);
		this.itemName = itemName;
	}
}
