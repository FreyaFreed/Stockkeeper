package stockkeeper.worldinfo;

import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldInfo{

	public static class WorldIDPacket implements IMessage {

		public static final String CHANNEL_NAME = "world_id";
		private String worldID;


		public WorldIDPacket() {}


		public WorldIDPacket(String worldID) {
			this.worldID = worldID;
		}


		@Override
		public void fromBytes(ByteBuf buf) {
			worldID = ByteBufUtils.readUTF8String(buf);
		}


		public String getWorldID() {
			return worldID;
		}


		@Override
		public void toBytes(ByteBuf buf) {
			if(worldID != null) {
				ByteBufUtils.writeUTF8String(buf, worldID);
			}
		}
	}
	public static class WorldListener implements IMessageHandler<WorldIDPacket, IMessage> {
		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(WorldIDPacket message, MessageContext ctx) {
			lastResponse = System.currentTimeMillis();
			worldID = message.getWorldID();			
			MinecraftForge.EVENT_BUS.post(new WorldInfoEvent(worldID, serverAddress, niceServerAddress));
			return null;
		}
	}
	private static final int MIN_DELAY_MS = 1000;
	private static long lastRequest;
	private static long lastResponse; 
	private static String worldID;
	private static String serverAddress;
	private static String niceServerAddress;
	private static Minecraft mc;



	
	private static final String CHANNEL_NAME = "world_id";


	public static final Map<String, String> worldIdtoName = new HashMap<String, String>()
	{{

		put("a72e4777-ad62-4e3b-a4e0-8cf2d15147ea", "Rokko Steppe");
		put("b25abb31-fd1e-499d-a5b5-510f9d2ec501", "Volans");
		put("a7cbf239-6c11-4146-a715-ef0a9827b4c4", "Drakontas");
		put("44f4b133-a646-461a-a14a-5fd8c8dbc59c", "Tjikko");
		put("a358b10c-7041-40c5-ac5e-db5483a9dfc2", "Eilon");
		put("182702a7-ea3f-41de-a2d3-c046842d5e74", "Abydos");
		put("7120b7a6-dd21-468c-8cd7-83d96f735589", "Padzahr");
		put( "197e2c4f-2fd6-464a-8754-53b24d9f7898", "Isolde");
		put("de730958-fa83-4e73-ab7f-bfdab8e27960", "Naunet");
		put( "63a68417-f07f-4cb5-a9d8-e5e702565967", "Tigrillo");
		put( "7f03aa4d-833c-4b0c-9d3b-a65a5c6eada0", "Ulca Felya");
		put( "fc891b9e-4b20-4c8d-8f97-7436383e8105", "Sheol");

	}};


	private static final Map<String, String> worldNameToId = new HashMap<String, String>()
	{{
		put("a72e4777-ad62-4e3b-a4e0-8cf2d15147ea", "Rokko Steppe");
		put("b25abb31-fd1e-499d-a5b5-510f9d2ec501", "Volans");
		put("a7cbf239-6c11-4146-a715-ef0a9827b4c4", "Drakontas");
		put("44f4b133-a646-461a-a14a-5fd8c8dbc59c", "Tjikko");
		put("a358b10c-7041-40c5-ac5e-db5483a9dfc2", "Eilon");
		put("182702a7-ea3f-41de-a2d3-c046842d5e74", "Abydos");
		put("7120b7a6-dd21-468c-8cd7-83d96f735589", "Padzahr");
		put( "197e2c4f-2fd6-464a-8754-53b24d9f7898", "Isolde");
		put("de730958-fa83-4e73-ab7f-bfdab8e27960", "Naunet");
		put( "63a68417-f07f-4cb5-a9d8-e5e702565967", "Tigrillo");
		put( "7f03aa4d-833c-4b0c-9d3b-a65a5c6eada0", "Ulca Felya");
		put( "fc891b9e-4b20-4c8d-8f97-7436383e8105", "Sheol");

	}};


	/**
	 * Returns a version of the server IP/hostname without special characters or spaces
	 * It should be suitable for file/folder naming
	 */
	private static String cleanServerAddress(String dirtyServerAddress){
		if (dirtyServerAddress.contains(":")){
			dirtyServerAddress = dirtyServerAddress.substring(0, dirtyServerAddress.indexOf(':'));
		}
		if (dirtyServerAddress.contains("/")){
			dirtyServerAddress = dirtyServerAddress.substring(0, dirtyServerAddress.indexOf('/'));
		}
		dirtyServerAddress = dirtyServerAddress.trim();
		return(dirtyServerAddress);
	}


	private SimpleNetworkWrapper channel;


	public WorldInfo getInstance(){
		return this;
	}


	/**
	 * Returns a version of the server IP/hostname without special characters or spaces
	 * It should be suitable for file/folder naming
	 */
	public String getNiceServerIP(){
		return(niceServerAddress);
	}


	public String getServerAddress(){
		if (serverAddress == null){
			return("NO_SERVER_IP");
		}
		else{
			return(serverAddress);
		}
	}


	public String getWorldName(){
		if (worldID == null){
			WorldProvider provider = mc.theWorld.provider;
			if(provider instanceof WorldProviderEnd) {
				return "world_the_end";
			} else if(provider instanceof WorldProviderHell) {
				return "world_nether";
			} else {
				return "world";
			}
			//return("NO_WORLD_NAME");
		}
		else{
			return worldIdtoName.get(worldID);
			//return(worldID);
		}
	}


	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if(!mc.isSingleplayer() && mc.thePlayer != null && !mc.thePlayer.isDead) {
			if(mc.thePlayer.getDisplayName().equals(event.getEntity().getDisplayName())) {
				serverAddress = mc.getCurrentServerData().serverIP;
				niceServerAddress = cleanServerAddress(this.getServerAddress());
				worldID = null;
				if (this.channel != null){
					requestWorldID();
				}
			}
		}
	}



	public void preInit(FMLPreInitializationEvent event) {
		mc = Minecraft.getMinecraft();
		try{
			channel = NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL_NAME);
			if (channel != null){
				channel.registerMessage(WorldListener.class, WorldIDPacket.class, 0, Side.CLIENT);
				//logger.info("Successfully registered channel '" + CHANNEL_NAME + "'");
			}
		}
		catch (RuntimeException e){
			//logger.warn("Failed to register '" + CHANNEL_NAME + "' channel!");
		}
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}
	private void requestWorldID() {
		if (channel == null){ return; }
		long now = System.currentTimeMillis();
		if((lastRequest + MIN_DELAY_MS < now) && (lastResponse + MIN_DELAY_MS < now)) {
			//System.out.println("Sending request..");
			channel.sendToServer(new WorldIDPacket());
			lastRequest = System.currentTimeMillis();
		}
	}
}