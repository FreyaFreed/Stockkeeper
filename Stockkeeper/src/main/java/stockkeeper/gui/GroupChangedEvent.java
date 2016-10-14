package stockkeeper.gui;

import net.minecraftforge.fml.common.eventhandler.Event;
import stockkeeper.data.Position;

public class GroupChangedEvent extends Event {

	public String newGroup;
	public Position top, bottom;
	public GroupChangedEvent(String newGroup, Position top, Position bottom) {
		this.newGroup = newGroup;
		this.top = top;
		this.bottom = bottom;
	}


}
