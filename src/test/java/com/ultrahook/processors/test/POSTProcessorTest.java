package com.ultrahook.processors.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import com.ultrahook.dtos.UltrahookMessage;
import com.ultrahook.processors.POSTProcessor;
import com.ultrahook.test.utils.TestServer;

public class POSTProcessorTest {
	
	private TestServer server;
	private int port;

	@Before
	public void setUp() throws Exception {
		port = new Random().nextInt(1000)+8000;
		server = new TestServer(port);
		server.start(); 
	}
	
	@After
	public void tearDown() throws IOException {
		server.stop(); 
	}
	
	@Test
	public void postProcessorTest() {
		POSTProcessor processor = new POSTProcessor(port);
		HashMap<String, String> headers = new HashMap<String,String>();
		headers.put("Content-Type", "text/json"); 
		processor.process(new UltrahookMessage("request", "hello", "q", "/somePath", headers));
		
		assertThat(server.getPostedContent(),hasSize(1));
		UltrahookMessage item = server.getPostedContent().iterator().next();
		assertEquals("hello",item.getBody());
		assertEquals("/somePath",item.getPath()); 
		assertEquals("q",item.getQuery()); 
		
		checkHeaders(headers, item);
	}

	
	@Test
	public void postProcessorTestNoQuery() {
		POSTProcessor processor = new POSTProcessor(port);
		processor.process(new UltrahookMessage("request", "hello", "", "/somePath", new HashMap<String,String>()));
		
		assertThat(server.getPostedContent(),hasSize(1));
		UltrahookMessage item = server.getPostedContent().iterator().next();
		assertEquals("hello",item.getBody());
		assertEquals("/somePath",item.getPath()); 
		assertEquals("",item.getQuery());
		
	}
	private void checkHeaders(HashMap<String, String> headers,
			UltrahookMessage item) {
		for (Entry<String, String> header : headers.entrySet())  {
			assertEquals(header.getValue(),item.getHeaders().get(header.getKey()));
		}
	}
}
