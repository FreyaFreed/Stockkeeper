package stockkeeper.gui;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import stockkeeper.mod.StockKeeper;
import stockkeeper.mod.StockKeeperConfig;
import stockkeeper.mod.StockkeeperJSON;

public class StockkeeperMenu extends GuiScreen {

	class GuiMenuItem
	{
		public GuiLabel label;
		public GuiTextField field;
		public GuiMenuItem() {
			//this.label = new GuiLabel(t, p_i45540_2_, p_i45540_3_, p_i45540_4_, p_i45540_5_, p_i45540_6_, p_i45540_7_)
			//this.field = new GuiTextField(componentId, fontrendererObj, x, y, par5Width, par6Height)
		}

	}



	private GuiTextField passwordText, defaultGroupText;
	private GuiCheckBox savePassword;
	private GuiLabel password, defaultGroup;
	private int currentFields;
	private GuiTextField ipText;
	private GuiLabel ip;


	public StockkeeperMenu() {
		super();
	}
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		switch(button.id)
		{
		case 0:
			handleCloseMenu();
			break;
		case 1:
			StockKeeperConfig.savePassword = ((GuiCheckBox)button).isChecked();
			break;
		case 2:
			connect();
			break;
		}
		super.actionPerformed(button);
	}


	private GuiMenuItem AddMenuItem(String label)
	{
		return null;
	}
	private void connect() {
		if(!StockKeeper.isConnected)
		{
			if(StockKeeper.establishSession())
				Minecraft.getMinecraft().thePlayer.addChatMessage(new TextComponentString("Succesfully connected"));
			else
				Minecraft.getMinecraft().thePlayer.addChatMessage(new TextComponentString("Failed to connect"));
		}
		else
		{
			Minecraft.getMinecraft().thePlayer.addChatMessage(new TextComponentString("Already connected!"));
		}


	}


	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.passwordText.drawTextBox();
		this.defaultGroupText.drawTextBox();
		this.ipText.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialTicks);

	}
	private void handleCloseMenu() {
		StockKeeperConfig.defaultGroup = defaultGroupText.getText();
		StockKeeperConfig.stockkeeperIp = ipText.getText();
		StockKeeperConfig.password = passwordText.getText();
		StockKeeperConfig config = StockKeeperConfig.Instance();
		if(StockKeeperConfig.savePassword)
			StockkeeperJSON.writeToFile(config, "config.json");
		else
		{
			String temp = StockKeeperConfig.password;
			StockKeeperConfig.password = "";
			StockkeeperJSON.writeToFile(config, "D:/config.json");
			StockKeeperConfig.password = temp;

		}

	}

	@Override
	public void initGui() {
		super.initGui();

		labelList.clear();
		this.defaultGroupText = new GuiTextField(0, this.fontRendererObj, this.width/2 , this.height/2 -20 , 100, 20);
		this.defaultGroupText.setMaxStringLength(23);
		this.defaultGroupText.setText(StockKeeperConfig.defaultGroup);

		this.defaultGroup = new GuiLabel(this.fontRendererObj, 0, this.width/2 - 100, this.height/2 - 20, 100, 20, 0xFFFFFF);
		defaultGroup.addLine("Default group");
		this.labelList.add(defaultGroup);

		this.passwordText = new GuiTextField(1, this.fontRendererObj, this.width/2 , this.height/2 , 100, 20);
		this.passwordText.setText(StockKeeperConfig.password);
		this.passwordText.setMaxStringLength(23);

		this.password = new GuiLabel(this.fontRendererObj, 1, this.width/2 - 100, this.height/2, 100, 20, 0xFFFFFF);
		password.addLine("Password");
		this.labelList.add(password);

		this.ipText = new GuiTextField(2, this.fontRendererObj, this.width/2 , this.height/2 + 20, 100, 20);
		this.ipText.setText(StockKeeperConfig.stockkeeperIp);
		this.ipText.setMaxStringLength(23);

		this.ip = new GuiLabel(this.fontRendererObj, 2, this.width/2 - 100, this.height/2 + 20, 100, 20, 0xFFFFFF);
		ip.addLine("Server ip");
		this.labelList.add(ip);

		GuiButton connect = new GuiButton( 2, this.width/2 + 105 , this.height/2 + 20, 100, 20, "Connect");
		this.buttonList.add(connect);


		this.savePassword = new GuiCheckBox(1, this.width/2+105 , this.height/2 +5, "Save password?", StockKeeperConfig.savePassword);
		this.buttonList.add(savePassword);

		GuiButton button = new GuiButton( 0, this.width - 150, this.height -40, 100, 20, "Save");
		this.buttonList.add(button);

	}

	@Override
	protected void keyTyped(char par1, int par2) throws IOException
	{
		this.passwordText.textboxKeyTyped(par1, par2);
		this.defaultGroupText.textboxKeyTyped(par1, par2);
		this.ipText.textboxKeyTyped(par1, par2);
		super.keyTyped(par1, par2);
	}

	@Override
	protected void mouseClicked(int x, int y, int btn) throws IOException {
		super.mouseClicked(x, y, btn);
		this.passwordText.mouseClicked(x, y, btn);
		this.defaultGroupText.mouseClicked(x, y, btn);
		this.ipText.mouseClicked(x, y, btn);

	}

	@Override
	public void onGuiClosed() {
		// TODO Auto-generated method stub
		super.onGuiClosed();
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();
		this.passwordText.updateCursorCounter();
		this.defaultGroupText.updateCursorCounter();
		this.ipText.updateCursorCounter();

	}


}
