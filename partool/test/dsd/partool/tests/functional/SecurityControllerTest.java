package dsd.partool.tests.functional;

import java.util.HashMap;
import java.util.Map;

import play.mvc.Http.Response;
import play.mvc.Http.Request;
import org.junit.Test;

import dsd.partool.tests.PartoolFunctionalTest;


public class SecurityControllerTest extends PartoolFunctionalTest {
	
	@Test
    public void testLoginPageAlive() {
	Response response = GET("/login");
	assertStatus(200, response);
	assertContentMatch("Login", response);
	assertContentMatch("User name", response);
	assertContentMatch("Password", response);	
    }
	
	
	@Test
    public void testLoginWithNoParameters() {
		
		Map<String, String> loginUserParams = new HashMap<String, String>();
		loginUserParams.put("username", "");
		loginUserParams.put("password", "");
		Response response = POST("/login", loginUserParams);	
		
		assertStatus(302, response);
		Request request = newRequest();
		request.cookies=response.cookies;
		request.path="/login";
		response=makeRequest(request);		
		
	assertStatus(200, response);
	assertContentMatch("Login", response);
	assertContentMatch("User name", response);
	assertContentMatch("Password", response);	
	assertContentMatch("unknown username or password.", response);	
    }
	
	@Test
    public void testLoginWithInvalidPassword() {
		
		Map<String, String> loginUserParams = new HashMap<String, String>();
		loginUserParams.put("username", "admin");
		loginUserParams.put("password", "wrong");
		Response response = POST("/login", loginUserParams);	
		
		assertStatus(302, response);
		Request request = newRequest();
		request.cookies=response.cookies;
		request.path="/login";
		response=makeRequest(request);		
		
	assertStatus(200, response);
	assertContentMatch("Login", response);
	assertContentMatch("User name", response);
	assertContentMatch("Password", response);	
	assertContentMatch("unknown username or password.", response);		
    }
	
	@Test
    public void testLoginWithInvalidUsername() {
		
		Map<String, String> loginUserParams = new HashMap<String, String>();
		loginUserParams.put("username", "wrong");
		loginUserParams.put("password", "admin");
		Response response = POST("/login", loginUserParams);	
		
		assertStatus(302, response);
		Request request = newRequest();
		request.cookies=response.cookies;
		request.path="/login";
		response=makeRequest(request);		
		
	assertStatus(200, response);
	assertContentMatch("Login", response);
	assertContentMatch("User name", response);
	assertContentMatch("Password", response);	
	assertContentMatch("unknown username or password.", response);	
    }
	
	@Test
    public void testLoginSuccess() {
		
		Map<String, String> loginUserParams = new HashMap<String, String>();
		loginUserParams.put("username", "admin");
		loginUserParams.put("password", "admin");
		Response response = POST("/login", loginUserParams);	
		
		assertStatus(302, response);
		Request request = newRequest();
		request.cookies=response.cookies;
		request.path="/";
		request.url="/";
		response=makeRequest(request);		
		
	assertStatus(200, response);
	assertContentMatch("Visualization", response);
		
    }	

}
