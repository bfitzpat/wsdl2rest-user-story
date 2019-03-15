package com.simple.ws.test;

import static org.junit.Assert.*;
import org.junit.Test;
import com.simple.ws.SimpleWSImpl;

public class JSONLoadTest {

	@Test
	public void test() {
		SimpleWSImpl impl = new SimpleWSImpl();
		String[] response = impl.getRandomCityAndCountry();
		assertTrue("Successfully retrieved a city/country pair", response.length == 2);
	}

}
