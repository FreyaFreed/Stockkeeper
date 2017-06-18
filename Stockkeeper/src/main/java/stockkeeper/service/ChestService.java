package stockkeeper.service;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.player.inventory.ContainerLocalMenu;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import stockkeeper.data.Chest;
import stockkeeper.data.Stack;
import stockkeeper.data.World;
import stockkeeper.gui.GroupChangedEvent;

public class ChestService {
	
	
	public ChestService(GuiService _guiService, RestService _restService)
	{
		restService = _restService;
		guiService = _guiService;
	}

	private BlockPos topChestPos;
	private BlockPos bottomChestPos;
	private GuiService guiService;
	private RestService restService;

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
	public void handleGuiOpened(GuiOpenEvent event)
	{
		
		boolean isDoubleChest = false;	
		if(event.getGui() instanceof GuiChest)
		{
			if(topChestPos != null && bottomChestPos != null)
			{
				isDoubleChest = true;
				
			}
			else if(topChestPos != null)
			{
				
			}				
			else
			{
				
			}
				
			guiService.replaceChestGui(event, isDoubleChest ,topChestPos, bottomChestPos);
		}
		
		GuiScreen screen = FMLClientHandler.instance().getClient().currentScreen;		
		getChestContents(screen);


	}
	
	//stack.getItem().getRegistryName()
	//Item item = stack.getItem();					
	//String nbt = stack.serializeNBT().toString();
	//String is = item.toString();
	//NBTTagCompound nbt2 = JsonToNBT.getTagFromJson(nbt.toString());

	private void getChestContents(GuiScreen screen) {	
		if(screen instanceof GuiChest)
		{			
			GuiContainer test = (GuiContainer)screen;
			Container container = ((GuiContainer)screen).inventorySlots;
			List<Stack> topStacks = new ArrayList<Stack>(), bottomStacks = new ArrayList<Stack>();
			try {
				ContainerLocalMenu lowerChestInv = getChestInventory(container);
				
				DimensionType type = FMLClientHandler.instance().getClient().theWorld.provider.getDimensionType();					
				World world = World.getWorld(type);
				Chest topChest = new Chest(topChestPos.getX(), topChestPos.getY(), topChestPos.getZ(), world);					
			

				//Determine top chest contents
				for(int i =0; i < 27; i++)
				{
					ItemStack stack = lowerChestInv.getStackInSlot(i);
					if(stack != null)
					{						
					    Stack s = new Stack();
						s.Count = stack.stackSize;
						s.ItemId = Item.getIdFromItem(stack.getItem());
						s.Slot = i;
						s.ContainerId = topChest.Id;
						topChest.stacks.add(s);
					}
					else
					{
						Stack s = new Stack();
						s.Count = null;
						s.ItemId = null;
						s.Slot = i;
						s.ContainerId = topChest.Id;
						topChest.stacks.add(s);
					}
				}
				//Determine bottom chest contents if it exists
				if(bottomChestPos != null)
				{
					
					Chest bottomChest = new Chest(bottomChestPos.getX(), bottomChestPos.getY(), bottomChestPos.getZ(), world);					
					for(int i =27 ; i < 54; i++)
					{
						ItemStack stack = lowerChestInv.getStackInSlot(i);
						if(stack != null)
						{
							Stack s = new Stack();
							s.Count = stack.stackSize;
							s.ItemId = Item.getIdFromItem(stack.getItem());	
							s.Slot = i -27;
							s.ContainerId = bottomChest.Id;
							bottomChest.stacks.add(s);	
						}						
						else
						{
							Stack s = new Stack();
							s.Count = null;
							s.ItemId = null;
							s.Slot = i;
							s.ContainerId = bottomChest.Id;
							bottomChest.stacks.add(s);
						}
					}					
					//doublechest	
					restService.sendChestData(bottomChest);
				}				
				restService.sendChestData(topChest);
			}

			catch (Exception e) {
				e.printStackTrace();
			}
			topChestPos = null;
			bottomChestPos = null;
		}
	}

	private ContainerLocalMenu getChestInventory(Container container) throws IllegalAccessException {
		ContainerLocalMenu lowerChestInv = null;
		for(Field f : container.getClass().getDeclaredFields())
		{					
			if(f.getType() == IInventory.class)
			{
				f.setAccessible(true);
				lowerChestInv = (ContainerLocalMenu) f.get(container);						
			}
			
		}
		return lowerChestInv;
	}
	
	@SubscribeEvent
	public void chestGroupChanged(GroupChangedEvent event)
	{
		//client.sendMessage(MessageFactory.createGroupChangedMessage(event.newGroup, event.top, event.bottom));
	}	



}
