package com.ultrahook.internal.test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.junit.Test;

import com.ultrahook.UltrahookClient;
import com.ultrahook.dtos.InitResponse;
import com.ultrahook.internal.InitializationUtil;
import com.ultrahook.internal.StreamConnection;
import com.ultrahook.internal.StreamProcessor;
import com.ultrahook.test.utils.TestProperties;
public class StreamConnectionTest {
	private static class MockProcessor extends StreamProcessor {
		
		public MockProcessor() {
			super(null);
		}
		
		@Override
		public void process(InputStream is) throws IOException {
			try {
				while (is.read()!=-1); // consume everything there is				
			} catch (SocketTimeoutException | SocketException e) {
				//this is normal, ignore
			}
		}

	}
	
	@Test
	public void testStreamingConnection() throws IOException, InterruptedException {
		InitResponse resp = InitializationUtil.sendInitRequest(TestProperties.getAPIKey(), "testhost", UltrahookClient.VERSION_0_1_2);
		MockProcessor mockProcessor = spy(new MockProcessor()); 
		StreamConnection connection = new StreamConnection(resp.getUrl(), mockProcessor);
		connection.setTimeout(2000);
		connection.start(); 
		
		Thread.sleep(3000); // waiting for one timeout, this should make two process-calls
		connection.stop(); 
		verify(mockProcessor,times(2)).process(any(InputStream.class)); 
	}
}
