package stockkeeper.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ScrollPaneLayout;

import org.lwjgl.input.Mouse;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class StockOverviewMenu extends SKGuiContainer {

	StockkeeperContainer overviewContainer;
	public StockOverviewMenu(StockkeeperContainer inventorySlotsIn) {
		super(inventorySlotsIn);
		overviewContainer = inventorySlotsIn;
		
	}
	List<ItemStack> stacks = new ArrayList<ItemStack>();
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		// TODO Auto-generated method stub

	}
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		this.drawDefaultBackground();		
		super.drawScreen(par1, par2, par3);
	}
	@Override
	public void initGui()
    {
		overviewContainer.drawOverview();
		super.initGui();
		
     /*   labelList.clear();        
        for (Slot slot : overviewContainer.inventorySlots)
		{
        	//this.fontRendererObj. 
        	//this.itemRender.renderItemOverlayIntoGUI(fontRendererObj, slot.getStack(),  this.guiLeft + slot.xDisplayPosition, this.guiTop + slot.yDisplayPosition, "Test");
        	
        	
        	
        	GuiLabel label =  new GuiLabel(this.fontRendererObj, 0, this.guiLeft + slot.xDisplayPosition, this.guiTop + slot.yDisplayPosition, 100, 20, 0xFFFFFF);			
			label.addLine("test");			
        	//this.labelList.add(label);        	
			
		}*/
       
    }
	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		int mouseScroll = Mouse.getEventDWheel();
		if(mouseScroll > 0)
		{
			if(overviewContainer.scrollOffset > 0)
			{
				overviewContainer.scrollUp();			
			//overviewContainer.scrollOffset -= 1;			
			overviewContainer.drawOverview();
			}
			
		}
		if(mouseScroll < 0)
		{
			overviewContainer.scrollDown();
			//overviewContainer.scrollOffset += 1;
			overviewContainer.drawOverview();
			
		}
			
		
		
	}



}
/*	int x = 0;
for(Block block : Block.REGISTRY)
{
	ItemStack itemStack = new ItemStack(block, x);
	if(itemStack == null || itemStack.getItem() == null)
	{
		//System.out.println(itemStack.getDisplayName());
	}
	else
	{
		stacks.add(itemStack);
		x++;
	}
}*/

//for(int x = 0; x < 93; x++)
//{
	//for(int y = 0; y < 6; y++)
	//{
//		stacks.add(new ItemStack(Block.getBlockById(1), x));				
	//}
	
//}
