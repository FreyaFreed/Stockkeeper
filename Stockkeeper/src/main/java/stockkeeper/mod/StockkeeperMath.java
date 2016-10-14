package stockkeeper.mod;
import java.sql.SQLException;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.sql.rowset.CachedRowSet;

import stockkeeper.data.Position;

public class StockkeeperMath {

	public static SortedMap<Double, Position> getSortedMap(CachedRowSet result, Position currentPos)
	{
		SortedMap<Double, Position> sortedCoords = new TreeMap<Double, Position>();
		try
		{
			while(result.next())
			{
				Position chest = new Position(result.getInt("x"), result.getInt("y"), result.getInt("z"));
				double distance = currentPos.distanceTo(chest);
				sortedCoords.put(distance, chest);
			}
		}
		catch(SQLException e)
		{

		}
		return sortedCoords;



	}



}
