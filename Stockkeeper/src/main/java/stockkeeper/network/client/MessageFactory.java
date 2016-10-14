package stockkeeper.network.client;

import java.util.List;

import net.minecraft.client.Minecraft;
import stockkeeper.data.Position;
import stockkeeper.data.Stack;
import stockkeeper.mod.StockKeeper;
import stockkeeper.mod.StockKeeperConfig;
import stockkeeper.network.ChestContentsMessage;
import stockkeeper.network.CountMessage;
import stockkeeper.network.FindItemMessage;
import stockkeeper.network.GroupChangedMessage;
import stockkeeper.network.InviteGroupMessage;
import stockkeeper.network.InviteMessage;
import stockkeeper.network.MakeGroupMessage;
import stockkeeper.network.RegisterMessage;
import stockkeeper.network.StockKeeperMessage;
import stockkeeper.network.StockKeeperMessage.MessageType;
import stockkeeper.network.checkChestGroupMessage;
import stockkeeper.worldinfo.WorldInfo;

public class MessageFactory {

	static WorldInfo info = new WorldInfo();

	//Adjacentchest must be null for a singlechest
	public static ChestContentsMessage createChestContentsMessage(Position chest_,Position adjacentChest_, List<Stack> stacks_)
	{

		ChestContentsMessage message;
		if(adjacentChest_ != null)
			message = new ChestContentsMessage(chest_, adjacentChest_, stacks_);
		else
			message = new ChestContentsMessage(chest_, stacks_);

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
		checkChestGroupMessage message = new checkChestGroupMessage(top,bottom);
		addMessageHeader(message);
		return message;
	}

	public static CountMessage createCountMessage(String itemName_)
	{
		CountMessage message = new CountMessage(itemName_);
		addMessageHeader(message);
		return message;
	}

	public static StockKeeperMessage createFindItemMessage(String itemName) {
		FindItemMessage message = new FindItemMessage(itemName) ;
		addMessageHeader(message);		
		return message;
	}

	public static StockKeeperMessage createGroupChangedMessage(String newGroup, Position top, Position bottom) {
		GroupChangedMessage message = new GroupChangedMessage(newGroup, top, bottom) ;
		addMessageHeader(message);	
		return message;
	}

	public static StockKeeperMessage createGroupInviteMessage(String groupname, int grouplevel, String username) {
		InviteGroupMessage message = new InviteGroupMessage(groupname, grouplevel, username);
		addMessageHeader(message);	
		return message;

	}

	public static StockKeeperMessage createGroupMessage(String groupname) {
		MakeGroupMessage message = new MakeGroupMessage(groupname);
		addMessageHeader(message);	
		return message;
	}

	public static StockKeeperMessage createInviteMessage(int level) {
		InviteMessage message = new InviteMessage(level);
		addMessageHeader(message);	
		return message;
	}

	public static StockKeeperMessage createRegistrationMessage(String inviteCode, String password) {
		RegisterMessage message = new RegisterMessage(inviteCode);
		addMessageHeader(message);	
		return message;
	}


}
