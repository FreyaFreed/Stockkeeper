package stockkeeper.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.GuiScrollingList;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import stockkeeper.data.Position;
import stockkeeper.mod.StockKeeperConfig;

public class StockKeeperGuiChest extends GuiChest {

	private GuiTextField text;
	GuiScrollingList list;
	String chestGroup;
	Position top, bottom;
	public StockKeeperGuiChest(IInventory upperInv, IInventory lowerInv, String chestGroup, Position top, Position bottom) {
		super(upperInv, lowerInv);
		this.chestGroup = chestGroup;
		MinecraftForge.EVENT_BUS.register(this);
		this.top = top;
		this.bottom = bottom;

	}

	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		this.drawDefaultBackground();
		this.text.drawTextBox();
		super.drawScreen(par1, par2, par3);
	}
	@SubscribeEvent
	void handleChestGroupEvent(ChestGroupEvent event)
	{
		
		this.chestGroup = event.group;
		if(text != null && this.chestGroup != null)
			text.setText(this.chestGroup);
		else if(text != null)
			text.setText(StockKeeperConfig.defaultGroup);		
		


	}

	@Override
	public void initGui() {

		super.initGui();
		GuiButton button = new GuiButton( 1, 125, 200, 100, 20, "Hello");

		this.text = new GuiTextField(2, this.fontRendererObj, this.guiLeft, this.guiTop-20, 100, 20);
		this.text.setFocused(true);
		this.text.setMaxStringLength(23);
		this.text.setText("");



	}
	@Override
	protected void keyTyped(char par1, int par2) throws IOException
	{
		this.text.textboxKeyTyped(par1, par2);

		if(par1 == 'e')
		{

		}
		else
		{
			super.keyTyped(par1, par2);
		}
	}
	@Override
	protected void mouseClicked(int x, int y, int btn) throws IOException {
		super.mouseClicked(x, y, btn);
		this.text.mouseClicked(x, y, btn);

	}
	@Override
	public void onGuiClosed() {
		if(this.chestGroup != text.getText() && text.getText() != "")
			MinecraftForge.EVENT_BUS.post(new GroupChangedEvent(text.getText(), top, bottom));
		this.chestGroup = "";
		this.text.setText("");
		MinecraftForge.EVENT_BUS.unregister(this);
		super.onGuiClosed();
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();
		this.text.updateCursorCounter();
		this.text.setFocused(true);
	}



}
