package com.ultrahook.internal;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.ultrahook.dtos.InitResponse;

public class InitializationUtil {
	public static InitResponse sendInitRequest(String key, String host, String version) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost post = new HttpPost("http://www.ultrahook.com/init?");
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("key", key));
		params.add(new BasicNameValuePair("host", host)); 
		params.add(new BasicNameValuePair("version", version)); 
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params);
		post.setEntity(entity);
		CloseableHttpResponse resp = client.execute(post);
		String respString = EntityUtils.toString(resp.getEntity());
		InitResponse initResponse = new Gson().fromJson(respString, InitResponse.class);
		return initResponse;
	}
}
