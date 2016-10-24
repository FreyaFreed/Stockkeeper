package stockkeeper.network.client;

import java.util.List;

import net.minecraft.client.Minecraft;
import stockkeeper.data.Position;
import stockkeeper.data.Stack;
import stockkeeper.mod.StockKeeper;
import stockkeeper.mod.StockKeeperConfig;
import stockkeeper.network.StockKeeperMessage;
import stockkeeper.network.MessageType;
import stockkeeper.worldinfo.WorldInfo;

public class MessageFactory {

	static WorldInfo info = new WorldInfo();

	//Adjacentchest must be null for a singlechest
	public static StockKeeperMessage createChestContentsMessage(Position chest,Position adjacentChest, List<Stack> stacks)
	{

		
		//if(adjacentChest_ != null)
		//	message = new ChestContentsMessage(chest_, adjacentChest_, stacks_);
		//else
		//	message = new ChestContentsMessage(chest_, stacks_);
		StockKeeperMessage message = new StockKeeperMessage(MessageType.CHESTCONTENTS);
		message.setField("chest", chest);
		message.setField("stacks",stacks);
		message.setField("adjacentChest", adjacentChest);
		addMessageHeader(message);
		return message;
	}

	private static void addMessageHeader(StockKeeperMessage message) {
		
		message.playerUUID = Minecraft.getMinecraft().thePlayer.getUniqueID();
		message.userName = Minecraft.getMinecraft().thePlayer.getName();
		message.serverIP = info.getNiceServerIP();
		message.worldName = info.getWorldName();
		message.password = StockKeeperConfig.password;
	}

	public static StockKeeperMessage createChestGroupMessage(Position top, Position bottom) {
		StockKeeperMessage message = new StockKeeperMessage(MessageType.CHECKGROUP);
		message.setField("top", top);
		message.setField("bottom",bottom);
		addMessageHeader(message);		
		return message;
	}

	public static StockKeeperMessage createCountMessage(String itemName)
	{
		StockKeeperMessage message = new StockKeeperMessage(MessageType.COUNT);
		message.setField("itemName",itemName);		
		addMessageHeader(message);
		return message;
	}

	public static StockKeeperMessage createFindItemMessage(String itemName) {
		StockKeeperMessage message = new StockKeeperMessage(MessageType.FINDITEM);
		message.setField("itemName",itemName);	
		addMessageHeader(message);		
		return message;
	}

	public static StockKeeperMessage createGroupChangedMessage(String newGroup, Position top, Position bottom) {
		StockKeeperMessage message = new StockKeeperMessage(MessageType.GROUPCHANGED);
		message.setField("newGroup",newGroup);	
		message.setField("top",top);	
		message.setField("bottom",bottom);	
		addMessageHeader(message);	
		return message;
	}

	public static StockKeeperMessage createGroupInviteMessage(String groupname, int grouplevel, String username) {
		StockKeeperMessage message = new StockKeeperMessage(MessageType.INVITEGROUP);
		message.setField("groupname",groupname);	
		message.setField("grouplevel",grouplevel);	
		message.setField("username",username);	
		addMessageHeader(message);	
		return message;

	}

	public static StockKeeperMessage createGroupMessage(String groupname) {
		StockKeeperMessage message = new StockKeeperMessage(MessageType.MAKEGROUP);
		message.setField("groupname",groupname);	
		addMessageHeader(message);	
		return message;
	}

	public static StockKeeperMessage createInviteMessage(int level) {
		StockKeeperMessage message = new StockKeeperMessage(MessageType.INVITE);
		message.setField("level",level);
		addMessageHeader(message);	
		return message;
	}

	public static StockKeeperMessage createRegistrationMessage(String inviteCode, String password) {
		StockKeeperMessage message = new StockKeeperMessage(MessageType.REGISTER);
		message.setField("inviteCode",inviteCode);
		message.setField("password",password);
		addMessageHeader(message);	
		return message;
	}

	public static StockKeeperMessage createCountAllMessage() {
		StockKeeperMessage message = new StockKeeperMessage(MessageType.COUNTALL);		
		addMessageHeader(message);	
		return message;
	}


}
