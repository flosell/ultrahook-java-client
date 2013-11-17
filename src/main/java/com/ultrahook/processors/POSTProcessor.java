package com.ultrahook.processors;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map.Entry;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.ultrahook.UltrahookMessageProcessor;
import com.ultrahook.dtos.UltrahookMessage;

public class POSTProcessor implements UltrahookMessageProcessor {
	private int destPort; 
	
	public POSTProcessor(int destPort) {
		super();
		this.destPort = destPort;
	}



	@Override
	public void process(UltrahookMessage msg) {
		CloseableHttpClient client = HttpClients.custom().build();
		String queryPart;
		if (msg.getQuery()!=null && !msg.getQuery().trim().isEmpty()) {
			queryPart = "?"+msg.getQuery();			
		}else {
			queryPart = ""; 
		}
		HttpPost post = new HttpPost("http://localhost:" + destPort + msg.getPath() + queryPart);
		if (msg.getHeaders()!=null) {
			for (Entry<String,String> header : msg.getHeaders().entrySet()) {
				post.addHeader(header.getKey(),header.getValue());
			}			
		}
		try {
			post.setEntity(new StringEntity(msg.getBody()));
			CloseableHttpResponse resp = client.execute(post); 
			EntityUtils.consume(resp.getEntity());
		} catch (UnsupportedEncodingException | ClientProtocolException  e) {
			throw new RuntimeException(e); // this shouldn't happen
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	}

}
