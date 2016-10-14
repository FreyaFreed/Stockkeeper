package stockkeeper.gui;

import net.minecraftforge.fml.common.eventhandler.Event;

public class ChestGroupEvent extends Event {
	String group;

	public ChestGroupEvent(String group) {
		super();
		this.group = group;
	}

}
