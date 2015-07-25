package dsd.partool.tests.functional;

import play.mvc.Http.Response;
import play.mvc.Http.Request;

import org.junit.Test;

import com.google.gson.Gson;

import dsd.partool.ClientResponse;
import dsd.partool.tests.PartoolFunctionalTest;

public class CdrControllerTest extends PartoolFunctionalTest {

    @Test
    public void testControllerSecurity() {
	Response response = GET("/profile");
	assertStatus(302, response);
	assertHeaderEquals("Location", "/login", response);
    }

    @Test
    public void testInvalidAggregationType() {

	Request request = newRequest();
	request.cookies = login();
	request.url = "/profile";
	request.path = "/profile";
	request.params.put("searchValue", "420012300140177");
	request.params.put("searchType", "imsi");
	request.params.put("startDate", "2010-09-15");
	request.params.put("endDate", "2010-09-22");
	request.params.put("aggregateType", "COUNT");
	Response response = makeRequest(request);

	assertStatus(404, response);
	assertContentEquals(
		"Invalid aggregation type. Should be 'SUM' or 'AVG'.", response);
    }

    @Test
    public void testInvalidSearchType() {

	Request request = newRequest();
	request.cookies = login();
	request.url = "/profile";
	request.path = "/profile";
	request.params.put("searchValue", "420012300140177");
	request.params.put("searchType", "MIMSI");
	request.params.put("startDate", "2010-09-15");
	request.params.put("endDate", "2010-09-22");
	request.params.put("aggregateType", "sum");
	Response response = makeRequest(request);

	assertStatus(404, response);
	assertContentEquals(
		"Invalid Search Type. Should be 'IMSI' or 'MSISDN'.", response);
    }

    @Test
    public void testInvalidImsiLength() {

	Request request = newRequest();
	request.cookies = login();
	request.url = "/profile";
	request.path = "/profile";
	request.params.put("searchValue", "42001");
	request.params.put("searchType", "imsi");
	request.params.put("startDate", "2010-09-15");
	request.params.put("endDate", "2010-09-22");
	request.params.put("aggregateType", "sum");
	Response response = makeRequest(request);

	assertStatus(404, response);
	assertContentEquals("IMSI is invalid!", response);
    }

    @Test
    public void testInvalidImsiDigits() {

	Request request = newRequest();
	request.cookies = login();
	request.url = "/profile";
	request.path = "/profile";
	request.params.put("searchValue", "A42B001A109347H");
	request.params.put("searchType", "imsi");
	request.params.put("startDate", "2010-09-15");
	request.params.put("endDate", "2010-09-22");
	request.params.put("aggregateType", "sum");
	Response response = makeRequest(request);

	assertStatus(404, response);
	assertContentEquals("IMSI should be a number!", response);
    }

    @Test
    public void testInvalidMsisdnLength() {

	Request request = newRequest();
	request.cookies = login();
	request.url = "/profile";
	request.path = "/profile";
	request.params.put("searchValue", "434123200341");
	request.params.put("searchType", "msisdn");
	request.params.put("startDate", "2010-09-15");
	request.params.put("endDate", "2010-09-22");
	request.params.put("aggregateType", "sum");
	Response response = makeRequest(request);

	assertStatus(404, response);
	assertContentEquals("MSISDN is invalid!", response);
    }

    @Test
    public void testInvalidMsisdnDigits() {

	Request request = newRequest();
	request.cookies = login();
	request.url = "/profile";
	request.path = "/profile";
	request.params.put("searchValue", "A42B001AH");
	request.params.put("searchType", "msisdn");
	request.params.put("startDate", "2010-09-15");
	request.params.put("endDate", "2010-09-22");
	request.params.put("aggregateType", "sum");
	Response response = makeRequest(request);

	assertStatus(404, response);
	assertContentEquals("MSISDN should be a number!", response);
    }

    @Test
    public void testImsiNullOrEmpty() {

	Request request = newRequest();
	request.cookies = login();
	request.url = "/profile";
	request.path = "/profile";
	request.params.put("searchType", "imsi");
	request.params.put("startDate", "2010-09-15");
	request.params.put("endDate", "2010-09-22");
	request.params.put("aggregateType", "sum");
	Response response = makeRequest(request);

	assertStatus(404, response);
	assertContentEquals("IMSI not found!", response);
    }

    @Test
    public void testMsisdnNullOrEmpty() {

	Request request = newRequest();
	request.cookies = login();
	request.url = "/profile";
	request.path = "/profile";
	request.params.put("searchType", "msisdn");
	request.params.put("startDate", "2010-09-15");
	request.params.put("endDate", "2010-09-22");
	request.params.put("aggregateType", "sum");
	Response response = makeRequest(request);

	assertStatus(404, response);
	assertContentEquals("MSISDN not found!", response);
    }

    @Test
    public void testSearchTypeNull() {

	Request request = newRequest();
	request.cookies = login();
	request.url = "/profile";
	request.path = "/profile";
	request.params.put("searchValue", "503107771");
	request.params.put("startDate", "2010-09-15");
	request.params.put("endDate", "2010-09-22");
	request.params.put("aggregateType", "sum");
	Response response = makeRequest(request);

	assertStatus(404, response);
	assertContentEquals("Search Type not found!", response);
    }
    
    @Test
    public void testSearchTypeEmpty() {

	Request request = newRequest();
	request.cookies = login();
	request.url = "/profile";
	request.path = "/profile";
	request.params.put("searchType", " ");
	request.params.put("searchValue", "503107771");
	request.params.put("startDate", "2010-09-15");
	request.params.put("endDate", "2010-09-22");
	request.params.put("aggregateType", "sum");
	Response response = makeRequest(request);

	assertStatus(404, response);
	assertContentEquals("Invalid Search Type. Should be 'IMSI' or 'MSISDN'.", response);
    }
   
    @Test
    public void testAggregationTypeEmpty() {
    	Request request = newRequest();
    	request.cookies = login();
    	request.url = "/profile";
    	request.path = "/profile";
    	request.params.put("searchType", "msisdn");
    	request.params.put("searchValue", "503107771");
    	request.params.put("startDate", "2010-09-15");
    	request.params.put("endDate", "2010-09-22");
    	request.params.put("aggregateType", " ");
    	Response response = makeRequest(request);

    	assertStatus(404, response);
    	assertContentEquals("Aggregation Type not found. Should be 'SUM' or 'AVG'.", response);
        }

    @Test
    public void testAggregationTypeNull() {

	Request request = newRequest();
	request.cookies = login();
	request.url = "/profile";
	request.path = "/profile";
	request.params.put("searchValue", "503107771");
	request.params.put("searchType", "msisdn");
	request.params.put("startDate", "2010-09-15");
	request.params.put("endDate", "2010-09-22");
	Response response = makeRequest(request);

	assertStatus(404, response);
	assertContentEquals(
		"Aggregation Type not found. Should be 'SUM' or 'AVG'.",
		response);
    }

    @Test
    public void testStartDateNull() {

	Request request = newRequest();
	request.cookies = login();
	request.url = "/profile";
	request.path = "/profile";
	request.params.put("searchValue", "503107771");
	request.params.put("searchType", "msisdn");
	request.params.put("endDate", "2010-09-22");
	request.params.put("aggregateType", "sum");
	Response response = makeRequest(request);

	assertStatus(404, response);
	assertContentEquals("Start Date not found!", response);
    }
    @Test
    public void testStartDateEmpty() {

    	Request request = newRequest();
    	request.cookies = login();
    	request.url = "/profile";
    	request.path = "/profile";
    	request.params.put("searchValue", "503107771");
    	request.params.put("searchType", "msisdn");
    	request.params.put("startDate", " ");
    	request.params.put("endDate", "2010-09-22");
    	request.params.put("aggregateType", "sum");
    	Response response = makeRequest(request);

    	assertStatus(404, response);
    	assertContentEquals("Start Date not found!", response);
        }
    
    @Test
    public void testEndDateEmpty() {

    	Request request = newRequest();
    	request.cookies = login();
    	request.url = "/profile";
    	request.path = "/profile";
    	request.params.put("searchValue", "503107771");
    	request.params.put("searchType", "msisdn");
    	request.params.put("startDate", "2010-09-22");
    	request.params.put("endDate", " ");
    	request.params.put("aggregateType", "sum");
    	Response response = makeRequest(request);

    	assertStatus(404, response);
    	assertContentEquals("End Date not found!", response);
        }

    @Test
    public void testEndDateNull() {

	Request request = newRequest();
	request.cookies = login();
	request.url = "/profile";
	request.path = "/profile";
	request.params.put("searchValue", "503107771");
	request.params.put("searchType", "msisdn");
	request.params.put("startDate", "2010-09-22");
	request.params.put("aggregateType", "sum");
	Response response = makeRequest(request);

	assertStatus(404, response);
	assertContentEquals("End Date not found!", response);
    }

    @Test
    public void testInvalidDateFormat() {

	Request request = newRequest();
	request.cookies = login();
	request.url = "/profile";
	request.path = "/profile";
	request.params.put("searchValue", "503107771");
	request.params.put("searchType", "msisdn");
	request.params.put("startDate", "2010-19-22");
	request.params.put("endDate", "2010-09-25");
	request.params.put("aggregateType", "sum");
	Response response = makeRequest(request);

	assertStatus(404, response);
	assertContentEquals(
		"Date is not valid according to YYYY-MM-DD pattern.", response);
    }

    @Test
    public void testInvalidDateInterval() {

	Request request = newRequest();
	request.cookies = login();
	request.url = "/profile";
	request.path = "/profile";
	request.params.put("searchValue", "503107771");
	request.params.put("searchType", "msisdn");
	request.params.put("startDate", "2010-09-25");
	request.params.put("endDate", "2010-09-22");
	request.params.put("aggregateType", "sum");
	Response response = makeRequest(request);

	assertStatus(404, response);
	assertContentEquals(
		"Invalid request, start date must be before end date!",
		response);
    }

    @Test
    public void testResultData() {

	Request request = newRequest();
	request.cookies = login();
	request.url = "/profile";
	request.path = "/profile";
	request.params.put("searchValue", "503107771");
	request.params.put("searchType", "msisdn");
	request.params.put("startDate", "2010-09-15");
	request.params.put("endDate", "2010-09-20");
	request.params.put("aggregateType", "sum");
	Response response = makeRequest(request);

	assertStatus(200, response);
	assertContentType("application/json", response);

	ClientResponse clientResponse = new Gson().fromJson(
		getContent(response), ClientResponse.class);
	assertEquals("503107771", clientResponse.getSubscriber().getMsisdn());
	assertEquals("503107771", clientResponse.getMsisdn());
	assertEquals("420017100266274", clientResponse.getImsi());
	assertEquals("20100915000000", clientResponse.getStartDate());
	assertEquals("20100920000000", clientResponse.getEndDate());
    }
    
    @Test
    public void testResultDataSum() {

	Request request = newRequest();
	request.cookies = login();
	request.url = "/profile";
	request.path = "/profile";
	request.params.put("searchValue", "420012000283814");
	request.params.put("searchType", "imsi");
	request.params.put("startDate", "2010-09-17");
	request.params.put("endDate", "2010-09-25");
	request.params.put("aggregateType", "sum");
	Response response = makeRequest(request);

	assertStatus(200, response);
	assertContentType("application/json", response);

	ClientResponse clientResponse = new Gson().fromJson(
		getContent(response), ClientResponse.class);
	
	Long[][] count=clientResponse.getUsageData().get("S1").getCount();
	
	assertEquals(new Long(10), count[1][10]);
	assertEquals(new Long(11), count[1][11]);
	assertEquals(new Long(5), count[1][12]);
	assertEquals(new Long(3), count[1][13]);
	
	assertEquals(new Long(4), count[5][11]);
	assertEquals(new Long(2), count[5][12]);
	assertEquals(new Long(4), count[5][13]);
	
	
	
    }
    
    @Test
    public void testResultDataAvg() {

    	Request request = newRequest();
    	request.cookies = login();
    	request.url = "/profile";
    	request.path = "/profile";
    	request.params.put("searchValue", "420012000283814");
    	request.params.put("searchType", "imsi");
    	request.params.put("startDate", "2010-09-17");
    	request.params.put("endDate", "2010-09-25");
    	request.params.put("aggregateType", "avg");
    	Response response = makeRequest(request);

    	assertStatus(200, response);
    	assertContentType("application/json", response);

    	ClientResponse clientResponse = new Gson().fromJson(
    		getContent(response), ClientResponse.class);
    	
    	Long[][] count=clientResponse.getUsageData().get("S1").getCount();
    	
    	assertEquals(new Long(5), count[1][10]);
    	assertEquals(new Long(5), count[1][11]);
    	assertEquals(new Long(2), count[1][12]);
    	assertEquals(new Long(1), count[1][13]);
    	
    	assertEquals(new Long(4), count[5][11]);
    	assertEquals(new Long(2), count[5][12]);
    	assertEquals(new Long(4), count[5][13]);
    }

}
