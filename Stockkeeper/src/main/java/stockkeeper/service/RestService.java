package stockkeeper.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParser;

import net.minecraft.client.Minecraft;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.math.BlockPos;
import scala.languageFeature.postfixOps;
import scala.util.parsing.json.JSONObject;
import stockkeeper.data.Chest;
import stockkeeper.data.FindClosest;
import stockkeeper.data.ItemCount;
import stockkeeper.data.ItemViewmodel;

public class RestService {
	
	RenderService renderService;
	
	public RestService(RenderService render) {
		rest = new RestTemplate();
		//rest.setErrorHandler(new ServerErrorHandler(rest));
		renderService = render;
	}
	private RestTemplate rest;
	
	public void sendChestData(Chest chest) {		
		
		ResponseEntity<Void> test= rest.postForEntity("http://localhost:24105/chest/process", chest , Void.class);		
	}

	public void updateItemList(List<ItemViewmodel> returnModel) {
		try
		{
		rest.postForObject("http://localhost:24105/Item/", returnModel, Void.class);		
		}
		catch(Exception e)
		{
			
		}
		
	}

	public void FindItem(String itemName) {
		BlockPos position = Minecraft.getMinecraft().thePlayer.getPosition();
		FindClosest find = new FindClosest();
		find.X = position.getX();
		find.Y = position.getY();
		find.Z = position.getZ();
		find.ItemName = itemName;
		ResponseEntity<Chest> chestResponse = rest.postForEntity("http://localhost:24105/find/closest", find, Chest.class);
		Chest chest = chestResponse.getBody();		
		renderService.AddWaypoint(chest.X, chest.Y, chest.Z, itemName);
		
	}
	public void CountAll()
	{
		ItemCount[] countedItems = rest.getForObject("http://localhost:24105/count/all/", ItemCount[].class);
	}
	
	

}
