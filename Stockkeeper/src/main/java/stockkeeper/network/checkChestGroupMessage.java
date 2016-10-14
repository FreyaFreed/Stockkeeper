package stockkeeper.network;

import stockkeeper.data.Position;

public class checkChestGroupMessage extends StockKeeperMessage {

	public Position top, bottom;
	public checkChestGroupMessage(Position top, Position bottom) {
		super(MessageType.CHECKGROUP);
		this.bottom = bottom;
		this.top = top;
	}
	public String getBottomID()
	{

		return new String(this.serverIP + ":" + bottom.worldName + ":" + bottom.x + ":" + bottom.y + ":" + bottom.z);
	}
	public String getTopID()
	{

		return new String(this.serverIP + ":" + top.worldName + ":" + top.x + ":" + top.y + ":" + top.z);
	}

}
