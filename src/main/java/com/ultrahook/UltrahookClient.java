package com.ultrahook;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.BasicHttpParams;

import com.ultrahook.dtos.InitResponse;
import com.ultrahook.internal.InitializationUtil;
import com.ultrahook.internal.StreamConnection;
import com.ultrahook.internal.StreamProcessor;
import com.ultrahook.processors.POSTProcessor;
import com.ultrahook.processors.SysoutMessageProcessor;


public class UltrahookClient {
	public static final String VERSION_0_1_2 = "0.1.2"; 

	private final String key; 
	private final String host; 
	private final String version = VERSION_0_1_2;

	private StreamConnection streamConnection;
	private UltrahookMessageProcessor processor;
	public UltrahookClient(String key, String host, UltrahookMessageProcessor processor) {
		super();
		this.key = key;
		this.processor = processor; 
		this.host = host;
	}
	
	public void connect() throws IOException {
		InitResponse initResponse = InitializationUtil.sendInitRequest(key, host, version);
		streamConnection = new StreamConnection(initResponse.getUrl(),new StreamProcessor(processor));
		streamConnection.start(); 
	}

	public void disconnect() throws IOException {
		streamConnection.stop(); 
	}
	
	public static void main(String[] args) throws IOException {
		if (args.length<4) {
			System.err.println("usage: --key <key> <subdomain> <destinationPort>");
		}
		String key = args[1]; 
		String subdomain = args[2]; 
		int destinationPort = Integer.parseInt(args[3]);
		
		UltrahookClient client = buildDefault(key, subdomain, destinationPort); 
		client.connect(); 
	}
	
	public static UltrahookClient buildDefault(String key, String subdomain, int destinationPort) throws IOException {
		UltrahookClient client = new UltrahookClient(key, subdomain,new POSTProcessor(destinationPort));
		client.connect();
		return client; 
	}
	
	
}
