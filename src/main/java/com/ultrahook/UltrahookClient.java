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


public class UltrahookClient {
	public static final String VERSION_0_1_2 = "0.1.2"; 

	private final String key; 
	private final String host; 
	private final String version = VERSION_0_1_2;

	private StreamConnection streamConnection;

	public UltrahookClient(String key, String host) {
		super();
		this.key = key;
		this.host = host;
	}
	
	public void connect() throws IOException {
		InitResponse initResponse = InitializationUtil.sendInitRequest(key, host, version);
		streamConnection = new StreamConnection(initResponse.getUrl(),new StreamProcessor(new SysoutMessageProcessor()));
		streamConnection.start(); 
	}

	public void disconnect() throws IOException {
		streamConnection.stop(); 
	}
	
	
}
