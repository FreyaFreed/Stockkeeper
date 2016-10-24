package stockkeeper.gui;

import java.util.List;

import net.minecraftforge.fml.common.eventhandler.Event;
import stockkeeper.data.Stack;

public class OpenOverviewMenuEvent extends Event {
	
	public List<Stack> stacks;

	public OpenOverviewMenuEvent(List<Stack> stacks) {
		super();
		this.stacks = stacks;
	}

}
