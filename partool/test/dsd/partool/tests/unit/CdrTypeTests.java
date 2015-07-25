package dsd.partool.tests.unit;

import java.util.ArrayList;
import java.util.List;

import models.CdrTypes;
import models.CdrUsage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import dsd.partool.TestDB;
import play.test.Fixtures;
import play.test.UnitTest;

public class CdrTypeTests extends UnitTest {
    @Before
    public void setUp() {
	Fixtures.deleteAllModels();
	TestDB testData = new TestDB();
	testData.fillTestDB();
    }

    @Test
    public void getAllTypes() {
	Assert.assertEquals(CdrTypes.count(), 9);
    }

    @Test
    public void getByType() {
	Integer type = 600;

	CdrTypes cdrType = CdrTypes.find("cdrType", type).first();
	List<CdrUsage> usages = cdrType.getCdrUsages();

	Assert.assertEquals(cdrType.getCdrType(), type);
	Assert.assertEquals(cdrType.getTypeDesc(), "NRTRDE");
	Assert.assertEquals(usages.get(0).getUsageType(), "N1");
	Assert.assertEquals(usages.get(0).getUsageDesc(), "NRTRDE MOC");
	Assert.assertEquals(usages.get(1).getUsageType(), "N2");
	Assert.assertEquals(usages.get(1).getUsageDesc(), "NRTRDE MTC");
	Assert.assertEquals(usages.get(2).getUsageType(), "N3");
	Assert.assertEquals(usages.get(2).getUsageDesc(), "NRTRDE MOSMS");
	Assert.assertEquals(usages.get(3).getUsageType(), "N4");
	Assert.assertEquals(usages.get(3).getUsageDesc(), "NRTRDE MTSMS");
	Assert.assertEquals(usages.get(4).getUsageType(), "N5");
	Assert.assertEquals(usages.get(4).getUsageDesc(), "NRTRDE GPRS");
    }

    @Test
    public void getByDesc() {
	String desc = "TV STREAM";

	CdrTypes cdrType = CdrTypes.find("typeDesc", desc).first();
	List<CdrUsage> usages = cdrType.getCdrUsages();

	Assert.assertEquals(cdrType.getCdrType(), new Integer(9));
	Assert.assertEquals(cdrType.getTypeDesc(), desc);
	Assert.assertEquals(usages.get(0).getUsageType(), "3T");
	Assert.assertEquals(usages.get(0).getUsageDesc(), "TV Streaming");
    }

    @Test
    public void addType() {
	List<CdrUsage> usages = new ArrayList<CdrUsage>();
	CdrUsage cdrUsage = new CdrUsage("AC", "CABLE TV");
	usages.add(cdrUsage);
	CdrTypes cdrType = new CdrTypes(15, "INTERNET", usages);

	Assert.assertEquals(cdrType.getCdrType(), new Integer(15));
	Assert.assertEquals(cdrType.getTypeDesc(), "INTERNET");
	Assert.assertEquals(usages.get(0).getUsageType(), "AC");
	Assert.assertEquals(usages.get(0).getUsageDesc(), "CABLE TV");
    }

}
