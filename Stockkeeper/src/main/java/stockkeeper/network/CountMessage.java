package stockkeeper.network;

public class CountMessage extends StockKeeperMessage {

	public String itemName;
	public CountMessage(String itemName_) {
		super(MessageType.COUNT);
		itemName = itemName_;
	}

}
