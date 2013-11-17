package com.ultrahook.internal.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import com.ultrahook.UltrahookClient;
import com.ultrahook.dtos.InitResponse;
import com.ultrahook.internal.InitializationUtil;
import com.ultrahook.test.utils.TestProperties;

public class InitializationUtilTest {
	@Test
	public void testInitialization() throws IOException {
		InitResponse response = InitializationUtil.sendInitRequest(TestProperties.getAPIKey(), "testhost", UltrahookClient.VERSION_0_1_2);
		assertEquals(true,response.isSuccess());
		// TODO: detailed assertions?
		assertNotNull(response.getNamespace()); 
		assertNotNull(response.getUrl()); 
	}
	
}
