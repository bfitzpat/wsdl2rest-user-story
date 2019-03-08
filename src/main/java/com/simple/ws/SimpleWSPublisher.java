package com.simple.ws;

import javax.xml.ws.Endpoint;
import com.simple.ws.SimpleWSImpl;

//Endpoint publisher
public class SimpleWSPublisher{
	
	public static void main(String[] args) {
	   Endpoint.publish("http://localhost:9999/ws/hello", new SimpleWSImpl());
    }

}