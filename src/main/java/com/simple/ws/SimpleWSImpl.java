package com.simple.ws;

import javax.jws.WebService;
 
//Service Implementation
@WebService(endpointInterface = "com.simple.ws.SimpleWS")
public class SimpleWSImpl implements SimpleWS{
 
	@Override
	public String getHelloWorldAsString(String name) {
		return "Hello World JAX-WS " + name;
	}
 
}