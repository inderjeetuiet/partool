package dsd.partool.tests.functional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import play.mvc.Http.Response;
import play.mvc.Http.Request;

import org.junit.Test;

import com.google.gson.Gson;

import models.CdrTypes;
import models.CdrUsage;
import dsd.partool.ClientResponse;
import dsd.partool.tests.PartoolFunctionalTest;

public class CdrTypesControllerTest extends PartoolFunctionalTest {
	
		@Test
	    public void testControllerSecurity() {
		Response response = GET("/getTypes");
		assertStatus(302, response);
		assertHeaderEquals("Location", "/login", response);
	    }
		
		@Test
	    public void getTypesTest() {

		Request request = newRequest();
		request.cookies = login();
		request.url = "/getTypes";
		request.path = "/getTypes";
		
		Response response = makeRequest(request);

		assertStatus(200, response);
		assertContentType("application/json", response);

		CdrTypes[] types= new Gson().fromJson(
			getContent(response), CdrTypes[].class);

		assertEquals(9, types.length);	
		assertEquals(0, types[0].getCdrUsages().size());
		assertEquals(0, types[1].getCdrUsages().size());
		assertEquals(0, types[2].getCdrUsages().size());
	    }
		
		@Test
	    public void getAllTest() {

		Request request = newRequest();
		request.cookies = login();
		request.url = "/getTypesWithUsage";
		request.path = "/getTypesWithUsage";
		
		Response response = makeRequest(request);

		assertStatus(200, response);
		assertContentType("application/json", response);

		CdrTypes[] types= new Gson().fromJson(
			getContent(response), CdrTypes[].class);

		assertEquals(9, types.length);
		assertEquals("VOICE", types[0].getTypeDesc());
		assertEquals(56, types[0].getCdrUsages().size());
		assertEquals("SMS", types[1].getTypeDesc());
		assertEquals(26, types[1].getCdrUsages().size());		
	    }
		
		@Test
	    public void getUsageTypesTest() {

		Request request = newRequest();
		request.cookies = login();
		request.url = "/getUsageTypes";
		request.path = "/getUsageTypes";
		request.params.put("type", "1");
		
		Response response = makeRequest(request);

		assertStatus(200, response);
		assertContentType("application/json", response);

		 CdrTypes type= new Gson().fromJson(
			getContent(response), CdrTypes.class);
		
		
		assertEquals("VOICE", type.getTypeDesc());		
	    }
	 
		
		
	 

}
