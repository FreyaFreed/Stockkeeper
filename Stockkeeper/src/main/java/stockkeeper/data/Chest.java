package stockkeeper.data;

import java.util.ArrayList;
import java.util.List;

public class Chest {
	public Chest(int x, int y, int z, World world){
		stacks = new ArrayList<Stack>();
		X = x;
		Y = y;
		Z = z;
		World = world;
		String separator = ",";
		Id = World.toString() + separator + X + separator + Y + separator + Z;
	}
	public Chest()
	{
		
	}
	public String Id;
	public int X, Y, Z;
	public World World;
	public List<Stack> stacks;

}
