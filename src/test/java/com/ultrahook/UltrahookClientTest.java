package com.ultrahook;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Random;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

import com.ultrahook.dtos.UltrahookMessage;
import com.ultrahook.test.utils.TestProperties;
import com.ultrahook.test.utils.TestServer;

public class UltrahookClientTest {
	@Test
	public void testCompleteClient() throws Exception {	
		int port = new Random().nextInt(1000)+8000;
		
		TestServer testServer = new TestServer(port); 
		testServer.start(); 
		
		UltrahookClient client = UltrahookClient.buildDefault(TestProperties.getAPIKey(), "somedomain", port); 
		client.connect();

		postToUltrahookServer(); 
		
		Thread.sleep(3000); // give it some time 
		
		client.disconnect(); 
		
		testServer.stop();

		assertThat(testServer.getPostedContent(),Matchers.hasSize(1));
		UltrahookMessage item = testServer.getPostedContent().iterator().next();
		assertEquals("formKey=formValue",item.getBody());
		assertEquals("/test",item.getPath()); 
		assertEquals("val=10",item.getQuery()); 
	}

	private void postToUltrahookServer() throws ClientProtocolException, IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost post = new HttpPost("http://somedomain.cttest.ultrahook.com/test?val=10");
		post.setEntity(new UrlEncodedFormEntity(Arrays.asList(new BasicNameValuePair("formKey", "formValue"))));
		CloseableHttpResponse resp = client.execute(post);
		EntityUtils.consume(resp.getEntity()); 
		
	}
}
