package dsd.partool.tests.unit;

import org.junit.*;

import dsd.partool.TestDB;

import java.util.*;
import play.test.*;
import models.*;

public class CdrUsageTests extends UnitTest {
    @Before
    public void setUp() {
	Fixtures.deleteAllModels();
	TestDB testData = new TestDB();
	testData.fillTestDB();
    }

    @Test
    public void getAllUsages() {
	Assert.assertEquals(CdrUsage.count(), 122);
    }

    @Test
    public void getDescByUsageType() {
	String usageType = "S1";

	CdrUsage cdrUsage = CdrUsage.find("usageType", usageType).first();
	Assert.assertEquals(cdrUsage.getUsageDesc(),
		"SMS National from AlJawal to AlJawal");
    }

    @Test
    public void getTypeByFirstLetter() {
	List<CdrUsage> usages = CdrUsage.find("usageType LIKE 'B%'").fetch();
	Assert.assertEquals(usages.size(), 12);
    }

    @Test
    public void getTypeByDesc() {
	String usageDesc = "Received Directory thru Fax";

	CdrUsage cdrUsage = CdrUsage.find("usageDesc", usageDesc).first();
	Assert.assertEquals(cdrUsage.getUsageType(), "D4");
	Assert.assertEquals(cdrUsage.getUsageDesc(), usageDesc);
    }

    @Test
    public void getUsageByDescPart() {
	List<CdrUsage> usages = CdrUsage.find("usageDesc LIKE '%AlJawal%'")
		.fetch();
	Assert.assertEquals(usages.size(), 67);
    }

    @Test
    public void addCdrUsage() {
	CdrUsage cdrUsage = new CdrUsage("AB", "IP TV");

	Assert.assertEquals(cdrUsage.getUsageType(), "AB");
	Assert.assertEquals(cdrUsage.getUsageDesc(), "IP TV");
    }

}
