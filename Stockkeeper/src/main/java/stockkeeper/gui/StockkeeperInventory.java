package stockkeeper.gui;

import net.minecraft.inventory.InventoryBasic;

public class StockkeeperInventory extends InventoryBasic {

	public StockkeeperInventory(String title, boolean customName, int slotCount) {
		super(title, customName, slotCount);
		
	}
	@Override
	public int getInventoryStackLimit()
    {
        return 99999;
    }

}
