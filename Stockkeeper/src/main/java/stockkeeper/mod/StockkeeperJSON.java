package stockkeeper.mod;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class StockkeeperJSON {

	public static JsonObject readFromFile(String path)
	{
		JsonParser parser = new JsonParser();
		File file = new File(path);
		FileReader reader = null;
		try {
			reader = new FileReader(path);
			JsonObject json =  parser.parse(reader).getAsJsonObject();
			return json;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static void writeToFile(Object object, String path)
	{
		  GsonBuilder gsonBuilder  = new GsonBuilder();
		// Allowing the serialization of static fields
		gsonBuilder.excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT);
		// Creates a Gson instance based on the current configuration
		Gson gson = gsonBuilder.create();		
		String json = gson.toJson(object, StockKeeperConfig.class);
		try {
			FileWriter file = new FileWriter(path);			
			file.write(json);
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		/*JsonObject jsonObject = new JsonObject();
		
		JSONObject json = new JSONObject(object);
		try {
			FileWriter file = new FileWriter(path);
			String test = json.toString();
			file.write(test);
			file.close();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

	}

}
