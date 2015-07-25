package dsd.partool.tests.unit;

import org.junit.*;

import dsd.partool.TestDB;

import java.util.*;

import javax.persistence.EntityManager;

import play.db.jpa.JPA;
import play.test.*;
import models.*;

public class CdrInformationTest extends UnitTest {

    @Before
    public void setUp() {
	Fixtures.deleteAllModels();
	TestDB testData = new TestDB();
	testData.fillTestDB();
    }

    @Test
    public void getAllCDRs() {
	Assert.assertEquals(CdrInformation.count(), 301);
    }

    @Test
    public void getCdrById() {
	CdrInformation cdrInfo = CdrInformation.find("callStartDate",
		"2010-09-15").first();
	Assert.assertEquals(cdrInfo.getImsi(), "420017100266274");
	Assert.assertEquals(cdrInfo.getMsisdn(), "503107771");
	Assert.assertEquals(cdrInfo.getCallStartDate(), "2010-09-15");
	Assert.assertEquals(cdrInfo.getCallDateTime(), "20100915041102");
	Assert.assertEquals(cdrInfo.getCallDuration().toString(), "80.0");
	Assert.assertEquals(cdrInfo.getRatedAmount().toString(), "0.40000001");
	Assert.assertEquals(cdrInfo.getUsageType().getUsageType(), "GG2");
	Assert.assertEquals(cdrInfo.getUsageType().getUsageDesc(),
		"2G Voice call from AlJawal to AlJawal");
    }

    @Test
    public void getCdrsInTimeSpan() {
	String startDate = "2010-08-15";
	String endDate = "2010-09-15";

	List<CdrInformation> cdrInfos = CdrInformation.find(
		"callStartDate BETWEEN ? AND ?", startDate, endDate).fetch();
	Assert.assertEquals(cdrInfos.size(), 124);

    }

    @Test
    public void getCdrsByCallDuration() {
	Double duration = 80.0;

	List<CdrUsage> cdrInfo = CdrInformation.find("callDuration < ?",
		duration).fetch();
	Assert.assertEquals(cdrInfo.size(), 258);
    }

    @Test
    public void getCdrByRatedAmount() {
	Double rated = 0.155;

	CdrInformation cdrInfo = CdrInformation.find("ratedAmount", rated)
		.first();
	Assert.assertEquals(cdrInfo.getImsi(), "420017100266274");
	Assert.assertEquals(cdrInfo.getMsisdn(), "503107771");
	Assert.assertEquals(cdrInfo.getCallStartDate(), "2010-09-16");
	Assert.assertEquals(cdrInfo.getCallDateTime(), "20100916035000");
	Assert.assertEquals(cdrInfo.getCallDuration().toString(), "62.0");
	Assert.assertEquals(cdrInfo.getRatedAmount().toString(), "0.155");
	Assert.assertEquals(cdrInfo.getUsageType().getUsageType(), "GG2");
	Assert.assertEquals(cdrInfo.getUsageType().getUsageDesc(),
		"2G Voice call from AlJawal to AlJawal");
    }

    @Test
    public void addCdrInfo() {
	CdrInformation cdrInfo = new CdrInformation("503107769",
		"420017100261234", "2011-01-21", "20110121093324", 34.0, 0.234,
		0.0);

	Assert.assertEquals(cdrInfo.getImsi(), "420017100261234");
	Assert.assertEquals(cdrInfo.getMsisdn(), "503107769");
	Assert.assertEquals(cdrInfo.getCallStartDate(), "2011-01-21");
	Assert.assertEquals(cdrInfo.getCallDateTime(), "20110121093324");
	Assert.assertEquals(cdrInfo.getCallDuration().toString(), "34.0");
	Assert.assertEquals(cdrInfo.getRatedAmount().toString(), "0.234");
	Assert.assertEquals(cdrInfo.getDiscountedAmount().toString(), "0.0");
    }
}