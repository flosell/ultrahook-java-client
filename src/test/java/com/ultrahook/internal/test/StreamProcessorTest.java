package com.ultrahook.internal.test;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import static org.mockito.Mockito.*; 

import com.ultrahook.UltrahookMessageProcessor;
import com.ultrahook.dtos.UltrahookMessage;
import com.ultrahook.internal.StreamProcessor;
public class StreamProcessorTest {
	 @Test
	 public void testProcessStream() throws IOException {
		 UltrahookMessageProcessor msgProcessor = mock(UltrahookMessageProcessor.class);
		 StreamProcessor streamProcessor = new StreamProcessor(msgProcessor);
		 String initData = "eyJ0eXBlIjoiaW5pdCJ9\n\n";
		 String testData = "eyJ0eXBlIjoicmVxdWVzdCIsImJvZHkiOiJoZWxsbyB3b3JsZCIsInF1ZXJ5" + "\n" +
		 		"IjoiIiwicGF0aCI6Ii9ibHViYiIsImhlYWRlcnMiOnsiVXNlci1BZ2VudCI6" + "\n" +
		 		"IldnZXQvMS4xMy40IChsaW51eC1nbnUpIiwiQWNjZXB0IjoiKi8qIiwiQ29u" + "\n" +
		 		"dGVudC1UeXBlIjoiYXBwbGljYXRpb24veC13d3ctZm9ybS11cmxlbmNvZGVk" + "\n" +
		 		"In19";
		 String data = initData+"\n\n"+testData+"\n\n"; 
		 InputStream is = new ByteArrayInputStream(data.getBytes()); 
		 
		 streamProcessor.process(is);
		 
		 verify(msgProcessor,times(2)).process(any(UltrahookMessage.class)); // TODO: more detailed assertions
		 
	 }
}
