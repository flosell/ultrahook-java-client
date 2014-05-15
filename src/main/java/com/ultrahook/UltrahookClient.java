package com.ultrahook;

import java.io.IOException;

import com.ultrahook.dtos.InitResponse;
import com.ultrahook.internal.InitializationUtil;
import com.ultrahook.internal.StreamConnection;
import com.ultrahook.internal.StreamProcessor;
import com.ultrahook.processors.POSTProcessor;

public class UltrahookClient {
	public static final String VERSION_0_1_2 = "0.1.2";

	private final String key;
	private final String host;
	private final String version = VERSION_0_1_2;

	private StreamConnection streamConnection;
	private final UltrahookMessageProcessor processor;

	private InitResponse initResponse;

	public UltrahookClient(String key, String host, UltrahookMessageProcessor processor) {
		super();
		this.key = key;
		this.processor = processor;
		this.host = host;
	}

	public void connect() throws IOException {
		initResponse = InitializationUtil.sendInitRequest(key, host, version);
		streamConnection = new StreamConnection(initResponse.getUrl(), new StreamProcessor(processor));
		streamConnection.start();
	}

	public void disconnect() throws IOException {
		streamConnection.stop();
	}

	public static void main(String[] args) throws IOException {
		if (args.length < 4) {
			System.err.println("usage: --key <key> <subdomain> <destinationPort>");
		}
		String key = args[1];
		String subdomain = args[2];
		int destinationPort = Integer.parseInt(args[3]);

		UltrahookClient client = buildDefault(key, subdomain, destinationPort);
		client.connect();
		if (client.initResponse != null) {
			System.out.println("Authenticated as " + client.initResponse.getNamespace() + "\nForwarding activated...\nhttp://" + subdomain
					+ "." + client.initResponse.getNamespace() + ".ultrahook.com -> http://localhost:" + destinationPort);
		} else {
			System.err.println("Init Response is NULL!");
		}
	}

	public static UltrahookClient buildDefault(String key, String subdomain, int destinationPort) throws IOException {
		UltrahookClient client = new UltrahookClient(key, subdomain, new POSTProcessor(destinationPort));
		client.connect();
		return client;
	}

}