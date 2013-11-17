package com.ultrahook;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import com.ultrahook.test.utils.TestProperties;

public class UltrahookClientTest {
	@Test
	@Ignore
	public void testCompleteClient() throws IOException {
		UltrahookClient client = new UltrahookClient(TestProperties.getAPIKey(), "testhost");
		client.connect();
		// TODO: do something
		client.disconnect(); 
	}
}
