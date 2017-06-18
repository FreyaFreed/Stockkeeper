package stockkeeper.data;

import net.minecraft.world.DimensionType;

public enum World {
	Overworld,
	Nether,
	End;


public static World getWorld(DimensionType type)
{
	switch(type)
	{
	case OVERWORLD: return World.Overworld;
	case NETHER: return World.Overworld; 		
	case THE_END: return World.Overworld;
	default: return World.Overworld;
	
	}
}
}
