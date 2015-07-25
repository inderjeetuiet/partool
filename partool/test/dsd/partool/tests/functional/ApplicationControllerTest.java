package dsd.partool.tests.functional;

import play.mvc.Http.Response;
import play.mvc.Http.Request;
import org.junit.Test;

import dsd.partool.tests.PartoolFunctionalTest;

public class ApplicationControllerTest extends PartoolFunctionalTest {

    @Test
    public void testControllerSecurity() {
	Response response = GET("/profile");
	assertStatus(302, response);
	assertHeaderEquals("Location", "/login", response);
    }

    @Test
    public void testHomePage() {

	Request request = newRequest();
	request.cookies = login();
	request.url = "/";
	Response response = makeRequest(request);

	assertStatus(200, response);
	assertContentType("text/html", response);
	assertContentMatch("Search profile", response);
	assertContentMatch("Visualization", response);

    }
    
    @Test
    public void testChangePropertiesPage() {

	Request request = newRequest();
	request.cookies = login();
	request.url = "/changeProperties.html";
	Response response = makeRequest(request);

	assertStatus(200, response);
	assertContentType("text/html", response);
	assertContentMatch("Search profile", response);
	assertContentMatch("Visualization", response);

    }

}
