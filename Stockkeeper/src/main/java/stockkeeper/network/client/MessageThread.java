package stockkeeper.network.client;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.SortedMap;

import javax.crypto.SecretKey;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.sql.rowset.CachedRowSet;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import stockkeeper.data.Position;
import stockkeeper.gui.ChestGroupEvent;
import stockkeeper.mod.StockKeeper;
import stockkeeper.mod.StockKeeperConfig;
import stockkeeper.mod.StockkeeperMath;
import stockkeeper.network.ChestGroupReturnMessage;
import stockkeeper.network.CountReturnMessage;
import stockkeeper.network.EncryptedMessage;
import stockkeeper.network.FindItemReturnMessage;
import stockkeeper.network.InviteReturnMessage;
import stockkeeper.network.StockKeeperMessage;
import stockkeeper.network.StockkeeperReturnMessage;

public class MessageThread implements Runnable, ClipboardOwner {

	StockKeeperMessage message;
	SocketFactory socketFactory = SSLSocketFactory.getDefault();
	SecretKey key;
	String xform = "RSA/ECB/PKCS1Padding";
	public MessageThread(StockKeeperMessage message_) {
		message = message_;
		try {
			key =  StockKeeper.key;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void handleCheckGroup(StockkeeperReturnMessage returnMessage) {
		if(returnMessage.success)	
		{
			String groupName = (String)returnMessage.getField("groupName");
			MinecraftForge.EVENT_BUS.post(new ChestGroupEvent(groupName));
		}
		else
		{
			Minecraft.getMinecraft().thePlayer.addChatMessage(new TextComponentString("FAILED:" + returnMessage.message));
		}
	}
	private void handleCount(StockkeeperReturnMessage returnMessage) {
				
		if(returnMessage.success)	
		{
			String itemName = (String)returnMessage.getField("itemName");
			int amount = (Integer)returnMessage.getField("amount");
			Minecraft.getMinecraft().thePlayer.addChatMessage(new TextComponentString("Found " + amount + " " + itemName + " in stock."));
		}
		else
		{
			Minecraft.getMinecraft().thePlayer.addChatMessage(new TextComponentString("FAILED:" + returnMessage.message));
		}
	}
	private void handleFindMessage(StockkeeperReturnMessage returnMessage) {
		//FindItemReturnMessage findMessage = (FindItemReturnMessage)returnMessage;
		if (returnMessage.success)
		{
			CachedRowSet itemResults = (CachedRowSet)returnMessage.getField("itemResults");
			SortedMap<Double, Position> closest = StockkeeperMath.getSortedMap(itemResults, StockKeeper.toPosition(Minecraft.getMinecraft().thePlayer.getPosition()));
			Position pos = closest.get(closest.firstKey());
			TextComponentString text2 = new TextComponentString("Item found in chest at: " + pos.x + ":" + pos.y + ":" + pos.z);
			Minecraft.getMinecraft().thePlayer.addChatMessage(text2);
		}
		else
		{
			Minecraft.getMinecraft().thePlayer.addChatMessage(new TextComponentString("FAILED:" + returnMessage.message));
		}
	}
	private void handleInvite(StockkeeperReturnMessage returnMessage) {
		
		if (returnMessage.success)
		{
			String inviteCode = (String)returnMessage.getField("inviteCode");
			TextComponentString text = new TextComponentString("Invite code added to clipboard!");
			Minecraft.getMinecraft().thePlayer.addChatMessage(text);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			StringSelection inviteString = new StringSelection(inviteCode);
			clipboard.setContents(inviteString , this);
		}
		else
		{
			Minecraft.getMinecraft().thePlayer.addChatMessage(new TextComponentString("FAILED:" + returnMessage.message));
		}
	}
	private void handleReturnMessage(StockkeeperReturnMessage returnMessage, Socket clientSocket)
	{
		// TODO Make sure return messages are neatly handled to give the client info about the success or failure
		switch(returnMessage.messageType)
		{
		case COUNT:
			handleCount(returnMessage);
			break;
		case INVITE:
			handleInvite(returnMessage);
			break;
		case CHECKGROUP:
			handleCheckGroup(returnMessage);
			break;
		case FINDITEM:
			handleFindMessage(returnMessage);
			break;
		case INVALID_PASSWORD:
			handleInvalidPassword();
			break;
		case CONNECTION_FAILED:
			StockKeeper.isConnected = false;
			break;
		}

	}
	private void handleInvalidPassword() {
		TextComponentString text = new TextComponentString("§4Could not process request: Invalid Password");
		Minecraft.getMinecraft().thePlayer.addChatMessage(text);
		
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// TODO Auto-generated method stub

	}
	@Override
	public void run()
	{
		try
		{
			Socket sslsocket = new Socket(StockKeeperConfig.stockkeeperIp, 55555);
			ObjectOutputStream outputStream = new ObjectOutputStream(sslsocket.getOutputStream());
			outputStream.writeObject(new EncryptedMessage(message, message.playerUUID, key));

			ObjectInputStream returnStream = new ObjectInputStream(sslsocket.getInputStream());
			EncryptedMessage encryptedMessage = (EncryptedMessage)returnStream.readObject();
			StockkeeperReturnMessage returnMessage = (StockkeeperReturnMessage)encryptedMessage.decrypt(key);
			handleReturnMessage(returnMessage, sslsocket);
		}
		catch(ClassCastException ex)
		{
			handleConnectionFailed();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void handleConnectionFailed() {
		StockKeeper.isConnected = false;
		TextComponentString text = new TextComponentString("§4Connection failed. Please reconnect!");
		Minecraft.getMinecraft().thePlayer.addChatMessage(text);
		
	}

}
