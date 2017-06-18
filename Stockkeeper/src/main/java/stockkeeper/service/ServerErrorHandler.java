package stockkeeper.service;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

public class ServerErrorHandler  implements ResponseErrorHandler{

	private RestTemplate rest;
	public ServerErrorHandler(RestTemplate rest) {
		this.rest= rest;
	}

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		InputStream inputStream = response.getBody();
		String theString = IOUtils.toString(inputStream); 
		
		
		
	}

}
