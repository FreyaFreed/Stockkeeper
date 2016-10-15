package stockkeeper.mod;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

import javax.crypto.SecretKey;

import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.player.inventory.ContainerLocalMenu;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import stockkeeper.command.StockkeeperCommand;
import stockkeeper.data.Position;
import stockkeeper.data.Stack;
import stockkeeper.data.UserCredentials;
import stockkeeper.encryption.EncryptionUtils;
import stockkeeper.gui.GroupChangedEvent;
import stockkeeper.gui.StockKeeperGuiChest;
import stockkeeper.gui.StockkeeperMenu;
import stockkeeper.network.ChestContentsMessage;
import stockkeeper.network.KeyExchangeMessage;
import stockkeeper.network.KeyExchangeMessage.MessageType;
import stockkeeper.network.StockKeeperMessage;
import stockkeeper.network.client.MessageFactory;
import stockkeeper.network.client.StockKeeperClient;
import stockkeeper.worldinfo.WorldInfo;


@Mod(modid = StockKeeper.MODID, version = StockKeeper.VERSION)
public class StockKeeper
{
	public static final String MODID = "StockKeeper";
	public static final String VERSION = "Alpha 0.1 Forge:1.10.2";
	public static String password;
	public static SecretKey key;
	public static boolean isConnected = false;
	public static java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(StockKeeper.class.getName());
	public static boolean establishSession()
	{

		Socket socket;
		try {
			socket = new Socket(StockKeeperConfig.stockkeeperIp, 55556);
			ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
			toServer.writeObject(new KeyExchangeMessage(MessageType.PUBLICKEY_REQUEST));

			ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());
			KeyExchangeMessage keyMessage = (KeyExchangeMessage)fromServer.readObject();
			PublicKey serverKey = (PublicKey)EncryptionUtils.fromBytes(keyMessage.message);

			toServer = new ObjectOutputStream(socket.getOutputStream());
			UUID playerUUID = Minecraft.getMinecraft().getSession().getProfile().getId();
			UserCredentials credentials = new UserCredentials(key, "test", playerUUID);
			byte[] bytesCredentials = EncryptionUtils.toBytes(credentials);
			byte[] encryptedCredentials = EncryptionUtils.encrypt(bytesCredentials, serverKey, EncryptionUtils.xform);
			toServer.writeObject(new KeyExchangeMessage(MessageType.USER_CREDENTIALS, encryptedCredentials));



		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ConnectException e) {
			isConnected = false;
			return false;
			//e.printStackTrace();

		}

		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		isConnected = true;
		return isConnected;

	}
	public static Position toPosition(BlockPos pos)
	{
		if(pos != null)
			return new Position(new WorldInfo().getWorldName(), pos.getX(), pos.getY(), pos.getZ());
		else
			return null;
	}
	public BlockPos lastOpenedChest;
	public BlockPos lastOpenedChestAdjacent;
	public BlockPos topChestPos;
	public BlockPos bottomChestPos;
	WorldInfo worldInfo;
	Logger log;
	StockKeeperClient client;
	GuiScreen menu;

	boolean menuopen = false;

	KeyBinding openMenu;


	@SubscribeEvent
	public void chestGroupChanged(GroupChangedEvent event)
	{
		client.sendMessage(MessageFactory.createGroupChangedMessage(event.newGroup, event.top, event.bottom));
	}

	private void commandNotFound() {
		Minecraft.getMinecraft().thePlayer.addChatMessage(new TextComponentString("§4Invalid command: §f/stock help §6for more information"));
		//handleHelp();

	}

	private void handleAdd(String[] args) {
		// TODO Auto-generated method stub

	}

	@SubscribeEvent
	public void handleCommand(CommandEvent event)
	{
		//MinecraftForge.MC_VERSION
		if(event.getCommand() instanceof StockkeeperCommand)
		{
			String[] args = event.getParameters();
			if(args.length == 0)
				handleHelp();
			else
			{
				if(args[0].equals("help"))
					handleHelp();
				else if(args[0].equals("invite"))
					handleInvite(args);
				else if(args[0].equals("add"))
					handleAdd(args);
				else if(args[0].equals("register"))
					handleRegister(args);
				else if(args[0].equals("count"))
					handleCount(args);
				else if(args[0].equals("find"))
					handleFindItem(args);
				else if(args[0].equals("groupinvite") || args[0].equals("gi"))
					handleGroupInvite(args);
				else if(args[0].equals("creategroup") || args[0].equals("cg"))
					handleCreateGroup(args);
				else if (args[0].equals("config"))
					handleConfigMenu();
				else
					commandNotFound();

			}
		}

	}

	private void handleConfigMenu() {

		Timer timer = new Timer();
		//FMLClientHandler.instance().displayGuiScreen(Minecraft.getMinecraft().thePlayer, new StockkeeperMenu());
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				Minecraft.getMinecraft().displayGuiScreen(new StockkeeperMenu());
			}
		}, TimeUnit.SECONDS.toMillis(1));


	}

	private void handleCount(String[] args) {
		if(args.length > 1)
			client.sendMessage(MessageFactory.createCountMessage(args[1].replaceAll("_", " ").toLowerCase()));
		else
			Minecraft.getMinecraft().thePlayer.addChatMessage(new TextComponentString("§4Invalid amount of arguments \n §6Usage: §f/stock count [itemName]"));

	}
	private void handleCreateGroup(String[] args) {
		String groupname = args[1];
		client.sendMessage(MessageFactory.createGroupMessage(groupname));
	}

	private void handleFind(String[] args) {
		// TODO Auto-generated method stub

	}

	private void handleFindItem(String[] args)
	{
		if(args.length > 1)
		{
			String itemName = args[1];
			client.sendMessage(MessageFactory.createFindItemMessage(itemName.replaceAll("_", " ").toLowerCase()));
		}
		else
		{
			Minecraft.getMinecraft().thePlayer.addChatMessage(new TextComponentString("§4Invalid amount of arguments \n §6Usage: §f/stock find [itemName]"));
		}
	}
	private void handleGroupInvite(String[] args) {
		String groupname = args[1];
		int grouplevel = Integer.parseInt(args[2]);
		String username = args[3];
		client.sendMessage(MessageFactory.createGroupInviteMessage(groupname, grouplevel, username));

	}
	@SubscribeEvent
	public void handleGuiOpened(GuiOpenEvent event)
	{
		LOG.info("GuiEvent is called");
		if(event.getGui() instanceof GuiChest)
		{
			if(topChestPos != null && bottomChestPos != null)
				client.sendMessage(MessageFactory.createChestGroupMessage(toPosition(topChestPos), toPosition(bottomChestPos)));
			else if(topChestPos != null)
				client.sendMessage(MessageFactory.createChestGroupMessage(toPosition(topChestPos), null));
			else
				LOG.info("Chestposition not found");
			replaceChestGui(event);
		}

		StockKeeperMessage topChest, bottomChest;
		GuiScreen screen = FMLClientHandler.instance().getClient().currentScreen;		
		if(screen instanceof GuiChest)
		{
			GuiContainer test = (GuiContainer)screen;
			Container container = ((GuiContainer)screen).inventorySlots;
			List<Stack> topStacks = new ArrayList<Stack>(), bottomStacks = new ArrayList<Stack>();
			try {
				ContainerLocalMenu lowerChestInv = null;
				for(Field f : container.getClass().getDeclaredFields())
				{					
					if(f.getType() == IInventory.class)
					{
						f.setAccessible(true);
						lowerChestInv = (ContainerLocalMenu) f.get(container);						
					}
					
				}
				//Field field = container.getClass().getDeclaredField("lowerChestInventory");
				//field.setAccessible(true);
				//ContainerLocalMenu lowerChestInv = (ContainerLocalMenu)field.get(container);


				//Determine top chest contents
				for(int i =0; i < 27; i++)
				{
					ItemStack stack = lowerChestInv.getStackInSlot(i);
					if(stack != null)
						topStacks.add(new Stack(stack.getDisplayName().toLowerCase(),stack.stackSize));
					else
						topStacks.add(null);
				}
				//Determine bottom chest contents if it exists
				if(bottomChestPos != null)
				{
					for(int i =27 ; i < 54; i++)
					{
						ItemStack stack = lowerChestInv.getStackInSlot(i);
						if(stack != null)
						{
							bottomStacks.add(new Stack(stack.getDisplayName(),stack.stackSize));
						}
						else
						{
							bottomStacks.add(null);
						}
					}
					topChest = MessageFactory.createChestContentsMessage(toPosition(topChestPos), toPosition(bottomChestPos), topStacks);
					bottomChest = MessageFactory.createChestContentsMessage(toPosition(bottomChestPos), toPosition(topChestPos), bottomStacks);
					client.sendMessage(topChest);
					client.sendMessage(bottomChest);
				}
				else
				{
					topChest = MessageFactory.createChestContentsMessage(toPosition(topChestPos), null, topStacks);
					client.sendMessage(topChest);
				}
			}

			catch (Exception e) {
				e.printStackTrace();
			}
			topChestPos = null;
			bottomChestPos = null;
		}


	}

	private void handleHelp() {
		String newLine = "\n";//System.getProperty("line.separator");
		String commandUsage = ""
				+ "§4Count: §6counts the amount of the specific item in all chests that the use has access to." + newLine
				+ " §6Usage: §f/stock count [itemName] §6Spaces in items are represented with '_', not case senstive" + newLine
				+ "§4Find: §6finds the closest chest containing the item." + newLine
				+ " §6Usage: §f/stock find [itemName] §6Spaces in items are represented with '_', not case senstive " + newLine
				+ "§4Invite: §6generates an invite code that allows another user to register an account on the server, code is copied to your clipboard" + newLine
				+ " §6Usage: §f/stock invite [level] §6 level 0 = can use the system, 1 = can invite other players 2= can create groups. You can invite one level below your current level." + newLine
				+ "§4Register: §6register an account using an invite code granted by a user" + newLine
				+ " §6Usage: §f/stock register [inviteCode] [password] §6 Password will be used to verify you in the future, make sure to add it to your config!" + newLine
				+ "§4Create Group: §6Creates a group for chests. Only members of this group can get information about chests added to this group" + newLine
				+ " §6Usage: §f/stock cg [groupName] §6Not case sensitive." + newLine
				+ "§4Group Invite: §6 invites another user to a group. You need at least group level 2" + newLine
				+ " §6Usage: §f/stock gi [groupName] [groupLevel [userName] §6Not case sensitive. 0 = Get data from chests 1=Add chests, 2=invite 3=admin" + newLine
				+ "§4Config: §6 Opens the config menu. Can also be opened with hotkey!" + newLine
				+ " §6Usage §f/stock config";
		Minecraft.getMinecraft().thePlayer.addChatMessage(new TextComponentString(commandUsage));
		

	}

	private void handleInvite(String[] args) {
		int level = Integer.parseInt(args[1]);
		client.sendMessage(MessageFactory.createInviteMessage(level));
	}

	public void HandleLore(ItemStack stack)
	{
		NBTTagCompound tag = stack.serializeNBT();
		if(tag != null)
		{

			if(tag.hasKey("tag"))
			{
				NBTTagList test = tag.getCompoundTag("tag").getCompoundTag("display").getTagList("Lore", Constants.NBT.TAG_STRING);
				System.out.println(test.getStringTagAt(0));
			}
		}
	}

	private void handleRegister(String[] args) {
		String inviteCode = args[1];
		String password = args[2];
		client.sendMessage(MessageFactory.createRegistrationMessage(inviteCode, password));

	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(this);
		ClientCommandHandler.instance.registerCommand(new StockkeeperCommand());
		key = EncryptionUtils.generateAESKey(0);
		if(StockKeeperConfig.stockkeeperIp != null)
			establishSession();
		menu = new StockkeeperMenu();
		openMenu = new KeyBinding("Open Config Menu", org.lwjgl.input.Keyboard.KEY_P, "Stockkeeper");
		ClientRegistry.registerKeyBinding(openMenu);
		
	}

	@SubscribeEvent
	public void keyPress(KeyInputEvent event)
	{
		if(openMenu.isKeyDown())
		{
			Minecraft.getMinecraft().displayGuiScreen(new StockkeeperMenu());

		}
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		FileHandler fh;
		try {
			fh = new FileHandler("Stockkeeper.log");
			 LOG.addHandler(fh);
			 SimpleFormatter formatter = new SimpleFormatter();  
		     fh.setFormatter(formatter); 
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
		LOG.setLevel(Level.FINE);
		try
		{
			JsonObject config =  StockkeeperJSON.readFromFile("config.json");
			StockKeeperConfig.stockkeeperIp = config.get("stockkeeperIp").getAsString();
			StockKeeperConfig.defaultGroup = config.get("defaultGroup").getAsString();
			StockKeeperConfig.password = config.get("password").getAsString();
			StockKeeperConfig.savePassword = config.get("savePassword").getAsBoolean();
		}
		catch(NullPointerException e)
		{
			LOG.log(Level.WARNING, "", e);
		}


		worldInfo = new WorldInfo();
		worldInfo.preInit(event);
		log = event.getModLog();
		client = new StockKeeperClient();

	}

	private void replaceChestGui(GuiOpenEvent event) {
		LOG.info("Replacing chest gui");
		GuiChest oldGui = (GuiChest)event.getGui();
		try {
			//Field upper = oldGui.getClass().getDeclaredField("upperChestInventory");
			//Field lower = oldGui.getClass().getDeclaredField("lowerChestInventory");
			//lower.setAccessible(true); upper.setAccessible(true);
			//IInventory lowerInv = (IInventory)lower.get(oldGui);
			///IInventory upperInv =  (IInventory)upper.get(oldGui);
			
			//doublechest
			int CHESTSIZE = 54;
			//singlechest
			if(bottomChestPos == null)
				CHESTSIZE = 27;			
				
			IInventory lowerInv = new ContainerLocalMenu("lowerChestInventory",new TextComponentString("Chest"), CHESTSIZE);//(IInventory)lower.get(oldGui);
			IInventory upperInv = new ContainerLocalMenu("upperChestInventory",new TextComponentString("Inventory"), 54);//(IInventory) upper.get(oldGui);			
			
			StockKeeperGuiChest newGui = new StockKeeperGuiChest(upperInv, lowerInv, "...", toPosition(topChestPos), toPosition(bottomChestPos));
			event.setGui(newGui);

		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			LOG.log(Level.WARNING, "", e);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			LOG.log(Level.WARNING, "", e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.log(Level.WARNING, "", e);
		}
	}
	@SubscribeEvent
	public void RightClick(RightClickBlock event)
	{
		TileEntity entity = event.getWorld().getTileEntity(event.getPos());
		if(entity instanceof TileEntityChest)
		{
			TileEntityChest chest = (TileEntityChest)entity;

			//Clicked chest is bottom chest
			if(chest.adjacentChestXNeg != null)
			{
				topChestPos = chest.adjacentChestXNeg.getPos();
				bottomChestPos = chest.getPos();
			}
			else if(chest.adjacentChestZNeg != null)
			{
				topChestPos =chest.adjacentChestZNeg.getPos();
				bottomChestPos = chest.getPos();
			}

			//Clicked chest is top chest
			else if(chest.adjacentChestXPos != null)
			{
				topChestPos = chest.getPos();
				bottomChestPos =chest.adjacentChestXPos.getPos();
			}
			else if(chest.adjacentChestZPos != null)
			{
				topChestPos = chest.getPos();
				bottomChestPos = chest.adjacentChestZPos.getPos();
			}
			//Clicked chest is a singlechest
			else
				topChestPos = chest.getPos();
		}

	}

	@SubscribeEvent
	public void TickEvent(net.minecraftforge.fml.common.gameevent.TickEvent event)
	{
		//if(menuopen)
		//Minecraft.getMinecraft().displayGuiScreen(new StockkeeperMenu());
	}
}
