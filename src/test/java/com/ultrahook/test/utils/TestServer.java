/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package com.ultrahook.test.utils;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLDecoder;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.DefaultHttpServerIODispatch;
import org.apache.http.impl.nio.DefaultNHttpServerConnection;
import org.apache.http.impl.nio.DefaultNHttpServerConnectionFactory;
import org.apache.http.impl.nio.SSLNHttpServerConnectionFactory;
import org.apache.http.impl.nio.reactor.DefaultListeningIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.nio.NHttpConnection;
import org.apache.http.nio.NHttpConnectionFactory;
import org.apache.http.nio.NHttpServerConnection;
import org.apache.http.nio.entity.NFileEntity;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.nio.protocol.BasicAsyncRequestConsumer;
import org.apache.http.nio.protocol.BasicAsyncResponseProducer;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.nio.protocol.HttpAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;
import org.apache.http.nio.protocol.HttpAsyncService;
import org.apache.http.nio.protocol.UriHttpAsyncRequestHandlerMapper;
import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.nio.reactor.ListeningIOReactor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.apache.http.util.EntityUtils;

import com.ultrahook.dtos.UltrahookMessage;

/**
 * HTTP/1.1 file server based on the non-blocking I/O model and capable of direct channel
 * (zero copy) data transfer.
 */
public class TestServer {
	private int port;
	private List<UltrahookMessage> postedContent = new ArrayList<>(); 
	private DefaultListeningIOReactor ioReactor;

	public TestServer(int port) {
		this.port = port; 
	}
	
	public List<UltrahookMessage> getPostedContent() {
		return postedContent;
	}
	
    public void start() throws Exception {
        HttpProcessor httpproc = HttpProcessorBuilder.create()
                .add(new ResponseDate())
                .add(new ResponseServer("Test/1.1"))
                .add(new ResponseContent())
                .add(new ResponseConnControl()).build();
        UriHttpAsyncRequestHandlerMapper reqistry = new UriHttpAsyncRequestHandlerMapper();
        reqistry.register("*", new HttpHandler());
        HttpAsyncService protocolHandler = new HttpAsyncService(httpproc, reqistry); 
        NHttpConnectionFactory<DefaultNHttpServerConnection> connFactory;
        connFactory = new DefaultNHttpServerConnectionFactory(
                ConnectionConfig.DEFAULT);
        final IOEventDispatch ioEventDispatch = new DefaultHttpServerIODispatch(protocolHandler, connFactory);
        IOReactorConfig config = IOReactorConfig.custom()
            .setIoThreadCount(1)
            .setSoTimeout(3000)
            .setConnectTimeout(3000)
            .build();
        ioReactor = new DefaultListeningIOReactor(config);
        ioReactor.listen(new InetSocketAddress(port));
        new Thread() {
        	public void run() {
        		try {
					ioReactor.execute(ioEventDispatch);
				} catch (InterruptedIOException e) {
					e.printStackTrace();
				} catch (IOReactorException e) {
					e.printStackTrace();
				}            		
        	};
        	
        }.start(); 
    }
    
    public void stop() throws IOException {
    	ioReactor.shutdown(); 
    }

    class HttpHandler implements HttpAsyncRequestHandler<HttpRequest> {

        public HttpAsyncRequestConsumer<HttpRequest> processRequest(
                final HttpRequest request,
                final HttpContext context) {
            return new BasicAsyncRequestConsumer();
        }

        public void handle(
                final HttpRequest request,
                final HttpAsyncExchange httpexchange,
                final HttpContext context) throws HttpException, IOException {
            HttpResponse response = httpexchange.getResponse();
            if (request instanceof BasicHttpEntityEnclosingRequest) {
            	if (request.getRequestLine().getMethod().equals("POST")) {
            		BasicHttpEntityEnclosingRequest entityRequest = (BasicHttpEntityEnclosingRequest) request;
            		String content = EntityUtils.toString(entityRequest.getEntity());
            		
            		String uri = request.getRequestLine().getUri();
            		String[] split = uri.split("\\?"); 
					String path=split[0];
					String query;
					if (split.length>1) {
						query = split[1];						
					}else {
						query=""; 
					}
					UltrahookMessage msg = new UltrahookMessage("request", content, query, path, convertHeaders(request.getAllHeaders()));
            		
            		postedContent.add(msg); 
            	}            	
            }
            response.setStatusCode(200); 
            httpexchange.submitResponse(new BasicAsyncResponseProducer(response));
        }

		private Map<String, String> convertHeaders(Header[] allHeaders) {
			HashMap<String, String> hashMap = new HashMap<String, String>();
			for(Header header: allHeaders) {
				hashMap.put(header.getName(), header.getValue()); 
			}
			return hashMap;
		}
    }

    public static void main(String[] args) throws Exception {
		TestServer server = new TestServer(8080); 
		server.start(); 
	}
}