package com.ultrahook.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.apache.commons.codec.binary.Base64;

import com.google.gson.Gson;
import com.ultrahook.UltrahookMessageProcessor;
import com.ultrahook.dtos.UltrahookMessage;

public class StreamProcessor {

	private UltrahookMessageProcessor msgProcessor;

	public StreamProcessor(UltrahookMessageProcessor msgProcessor) {
		this.msgProcessor = msgProcessor;
	}

	public void process(InputStream is) throws IOException {
		try {
			int s;
			StringBuilder sb = new StringBuilder();
			while ((s = is.read()) != -1) {
				sb.append((char) s);
				if (sb.length() > 2) {
					String substring = sb.substring(sb.length() - 2);
					if (substring.equals("\n\n")) {
						byte[] decoded = new Base64().decode(sb.toString());
						
						UltrahookMessage msg = new Gson().fromJson(new String(
								decoded), UltrahookMessage.class);
						if (msg.getType().equals("request")) {
							msgProcessor.process(msg);						
						}
						sb = new StringBuilder();
					}
				}
			}			
		}catch (SocketTimeoutException | SocketException e) {
			// this is normal, we will be reconnected
		}
	}
}
