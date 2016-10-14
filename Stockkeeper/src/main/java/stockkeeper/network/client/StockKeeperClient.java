package stockkeeper.network.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import stockkeeper.network.StockKeeperMessage;

public class StockKeeperClient {

	ExecutorService messageService = Executors.newSingleThreadExecutor();
	public StockKeeperClient()
	{

	}

	public void sendMessage(StockKeeperMessage message)
	{
		messageService.submit(new MessageThread(message));
	}

}
