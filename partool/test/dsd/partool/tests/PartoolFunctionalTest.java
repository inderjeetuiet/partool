package dsd.partool.tests;

import play.test.Fixtures;
import play.test.FunctionalTest;

import play.mvc.Http.Response;
import play.mvc.Http.Request;
import play.mvc.Http.Cookie;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import dsd.partool.TestDB;

@Ignore
public class PartoolFunctionalTest extends FunctionalTest {

    @Before
    public void setUp() {
	Fixtures.deleteAllModels();
	TestDB testData = new TestDB();
	testData.fillTestDB();
    }

    protected Map<String, Cookie> login() {
	Map<String, String> loginUserParams = new HashMap<String, String>();
	loginUserParams.put("username", "admin");
	loginUserParams.put("password", "admin");
	Response loginResponse = POST("/login", loginUserParams);
	return loginResponse.cookies;
    }

}
