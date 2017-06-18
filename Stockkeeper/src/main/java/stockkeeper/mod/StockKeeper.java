package stockkeeper.mod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import stockkeeper.command.StockkeeperCommand;
import stockkeeper.data.ItemViewmodel;
import stockkeeper.data.Position;
import stockkeeper.gui.OpenOverviewMenuEvent;
import stockkeeper.gui.StockOverviewMenu;
import stockkeeper.gui.StockkeeperContainer;
import stockkeeper.gui.StockkeeperMenu;
import stockkeeper.service.*;



@Mod(modid = StockKeeper.MODID, version = StockKeeper.VERSION)
public class StockKeeper
{
	
	public static final String MODID = "StockKeeper";
	public static final String VERSION = "Alpha 0.1.7 Forge:1.10.2";
	public static String password;
	public static java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(StockKeeper.class.getName());
	GuiScreen menu;
	boolean menuopen = false;
	KeyBinding openMenu, openCountMenu;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{

	}
	

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		RenderService renderService = new RenderService();
		RestService restService = new RestService(renderService);		
		GuiService guiService = new GuiService();
		ChestService chestService = new ChestService(guiService, restService);
		CommandService commandService = new CommandService();			
		
		MinecraftForge.EVENT_BUS.register(restService);
		MinecraftForge.EVENT_BUS.register(chestService);
		MinecraftForge.EVENT_BUS.register(guiService);
		MinecraftForge.EVENT_BUS.register(commandService);
		MinecraftForge.EVENT_BUS.register(renderService);
		MinecraftForge.EVENT_BUS.register(this);
		
		ClientCommandHandler.instance.registerCommand(new StockkeeperCommand(renderService, restService));
		
		openCountMenu = new KeyBinding("Open Stock Overview", org.lwjgl.input.Keyboard.KEY_O, "Stockkeeper");
		ClientRegistry.registerKeyBinding(openCountMenu);
		
		getItemList();
	}


	private void legacyInit() {
		MinecraftForge.EVENT_BUS.register(this);
		//ClientCommandHandler.instance.registerCommand(new StockkeeperCommand(renderService));		
		menu = new StockkeeperMenu();
		openMenu = new KeyBinding("Open Config Menu", org.lwjgl.input.Keyboard.KEY_P, "Stockkeeper");
		ClientRegistry.registerKeyBinding(openMenu);
		openCountMenu = new KeyBinding("Open Stock Overview", org.lwjgl.input.Keyboard.KEY_O, "Stockkeeper");
		ClientRegistry.registerKeyBinding(openCountMenu);
	}

	private void legacyPreInit() {
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
	}
	
	public void keyPress(KeyInputEvent event)
	{
		if(openMenu.isKeyDown())
		{
			Minecraft.getMinecraft().displayGuiScreen(new StockkeeperMenu());

		}
		if(openCountMenu.isKeyDown())
		{
			//client.sendMessage(MessageFactory.createCountAllMessage());			
			

		}
	}
	

	void openOverviewMenu(OpenOverviewMenuEvent event)
	{
		Minecraft.getMinecraft().displayGuiScreen(new StockOverviewMenu(new StockkeeperContainer(event.stacks)));
		
	}
		
	public void getItemList(){
		
		List<ItemViewmodel> returnModel = new ArrayList<ItemViewmodel>();
		
		IForgeRegistry<Item> items = GameRegistry.findRegistry(Item.class);
		items.toString();
		List<Item> itemlist = items.getValues();
		for (Item item : itemlist) {
			ItemViewmodel temp = new ItemViewmodel();
			temp.UnLocalizedName = item.getUnlocalizedName();		
			temp.StackLimit = item.getItemStackLimit();	
			temp.Id = item.getIdFromItem(item);
			returnModel.add(temp);		}	
		
		//RestService.updateItemList(returnModel);
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



	
}
