package stockkeeper.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.player.inventory.ContainerLocalMenu;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import stockkeeper.data.Stack;

public class StockkeeperContainer extends Container {
	StockkeeperInventory inventory;
	List<ItemStack> stacks;
	public StockkeeperContainer(List<Stack> stockkeeperStacks) {
		inventory = new StockkeeperInventory("Stock", true, 54);
		stacks = converStacks(stockkeeperStacks);
		this.drawOverview();
		
		
	}
	private List<ItemStack> converStacks(List<Stack> stockkeeperStacks) {

		List<ItemStack> stacks = new ArrayList<ItemStack>();
		for(Stack stockkeeperStack : stockkeeperStacks)
		{
			ItemStack stack = null;
			NBTTagCompound nbt;
			try {
				nbt = JsonToNBT.getTagFromJson(stockkeeperStack.serializedStack);
				stack = ItemStack.loadItemStackFromNBT(nbt);
				stack.stackSize = stockkeeperStack.size;
			} catch (NBTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			stacks.add(stack);
					
		}
		return stacks;
	}
	int scrollOffset = 0;
	int stackRows = 0;
	//public void drawOverview(List<ItemStack> stacks)
	public void drawOverview()
	{
		stackRows = (int)Math.ceil(stacks.size()/9.f);
		for(int y = 0; y < 6; y++)
		{
			for(int x = 0; x < 9; x++)
			{		
					int slotIndex = x+9*y;
					int stackIndex = x+9*(y+scrollOffset);
					//System.out.println(slotIndex);
					Slot slot = new Slot(inventory,slotIndex, x*24, y*18);				
					this.addSlotToContainer(slot);
					
					if(stackIndex < stacks.size())
						slot.putStack(stacks.get(stackIndex));
					else
						slot.putStack(null);
			}
		}
		/*int i =0;
		for(ItemStack stack : stacks)
		{
			this.inventorySlots.get(i).putStack(stack);
			
			i++;
		}*/
		//this.inventoryItemStacks = stacks;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	 public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player)
	 {
		//System.out.println("Slot clicked:" + slotId);
		return null;
	 }

	public void scrollDown() {
		if(scrollOffset < stackRows - 6)
			scrollOffset += 1;	
		
	}

	public void scrollUp() {
		if(scrollOffset > 0)
		{					
		scrollOffset -= 1;
		}
		
	}

}
