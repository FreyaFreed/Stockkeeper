package stockkeeper.service;
import java.util.logging.Level;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.player.inventory.ContainerLocalMenu;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import stockkeeper.data.Position;
import stockkeeper.gui.StockKeeperGuiChest;


public class GuiService {
	

	
	public void replaceChestGui(GuiOpenEvent event, boolean isDoubleChest, BlockPos top, BlockPos bottom) {
		
		GuiChest oldGui = (GuiChest)event.getGui();
		try 
		{			
			int CHESTSIZE = 27;		
			if(isDoubleChest)
				CHESTSIZE = 54;			
				
			IInventory lowerInv = new ContainerLocalMenu("lowerChestInventory",new TextComponentString("Chest"), CHESTSIZE);//(IInventory)lower.get(oldGui);
			IInventory upperInv = new ContainerLocalMenu("upperChestInventory",new TextComponentString("Inventory"), 54);//(IInventory) upper.get(oldGui);			
			
			StockKeeperGuiChest newGui = new StockKeeperGuiChest(upperInv, lowerInv, "...", toPosition(top), toPosition(bottom));
			event.setGui(newGui);

		} 
		catch (Exception e) 
		{
		
		}
	}
	
	public static Position toPosition(BlockPos pos)
	{
		if(pos != null)
			return new Position("rip civcraft3.0", pos.getX(), pos.getY(), pos.getZ());
		else
			return null;
	}
	

}
