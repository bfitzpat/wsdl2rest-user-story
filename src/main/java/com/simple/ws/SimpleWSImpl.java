/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.simple.ws;

import javax.jws.WebService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.nio.file.Files;
 
//Service Implementation
@WebService(endpointInterface = "com.simple.ws.SimpleWS")
public class SimpleWSImpl implements SimpleWS{
 
	@Override
	public String getHelloWorldAsString(String name) {
		return "Hello World JAX-WS " + name;
	}

	@Override
	public String[] getRandomCityAndCountry() {
		// parse json file, grab a city/country combination
		String[] returnArray = { "Colorado Springs", "us" };
		JSONParser jsonParser = new JSONParser();

		
		try {
			String fileName = "com/simple/ws/thin.city.list.json";
			ClassLoader classLoader = this.getClass().getClassLoader();
			URL resourceUrl = classLoader.getResource(fileName);
			File file = new File(resourceUrl.getFile());
			String content = new String(Files.readAllBytes(file.toPath()));

			//Read JSON file
			Object obj = jsonParser.parse(content);
 
			JSONArray cityList = (JSONArray) obj;
			JSONObject cityObject = getRandomObject(cityList);
			parseCityObject( cityObject );
			String name = (String) cityObject.get("name");
			String country = (String) cityObject.get("country");
			returnArray = new String[] { name, country };
 		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return returnArray;
	}
 
	private static void parseCityObject(JSONObject cityObject) {
		Long id = (Long) cityObject.get("id");
		System.out.println(id);
		String name = (String) cityObject.get("name");
		System.out.println(name);
		String country = (String) cityObject.get("country");
		System.out.println(country);
	}

	public JSONObject getRandomObject(JSONArray jsonArray) {
		int randomIndex = (int) (Math.random() * jsonArray.size());
		JSONObject jsonObject = (JSONObject) jsonArray.get(randomIndex);
		return jsonObject;
	}	
}