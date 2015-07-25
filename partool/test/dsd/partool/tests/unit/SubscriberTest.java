package dsd.partool.tests.unit;

import org.junit.*;

import dsd.partool.TestDB;

import java.util.*;
import play.test.*;
import models.*;

public class SubscriberTest extends UnitTest {

    @Before
    public void setUp() {
	Fixtures.deleteAllModels();
	TestDB testData = new TestDB();
	testData.fillTestDB();
    }

    @Test
    public void getAllSubscribers() {
	Assert.assertEquals(SubscriberInformation.count(), 11);
    }

    @Test
    public void getSubscriberById() {
	SubscriberInformation subscriber = SubscriberInformation.find(
		"subscriberId", "503174659").first();
	Assert.assertEquals(subscriber.getSubscriberId(), "503174659");
	Assert.assertEquals(subscriber.getSubscriberName(), "Jowia Zawadzka");
	Assert.assertEquals(subscriber.getOfficialAddress(),
		"Schmarjestrasse 72, 93466 Chamerau");
	Assert.assertEquals(subscriber.getCustomerId(), "S 1-0061-0741-9t ");
	Assert.assertEquals(subscriber.getSubscriberType(),
		"Residential Normal");
	Assert.assertEquals(subscriber.getMsisdn(), "503174659");
	Assert.assertEquals(subscriber.getImsi(), "420012103041905");
    }

    @Test
    public void getSubscribersByType() {
	String type = "Residential Normal";

	List<SubscriberInformation> subscribers = SubscriberInformation.find(
		"subscriberType", type).fetch();
	Assert.assertEquals(subscribers.size(), 11);
    }

    @Test
    public void getSubscriberByName() {
	String name = "Sophia Beckenbauer";

	SubscriberInformation subscriber = SubscriberInformation.find(
		"subscriberName", name).first();
	Assert.assertEquals(subscriber.getSubscriberId(), "555885911");
	Assert.assertEquals(subscriber.getSubscriberName(), name);
	Assert.assertEquals(subscriber.getOfficialAddress(),
		"Schachermairdorf 55, 6075 VOLDERWALD");
	Assert.assertEquals(subscriber.getCustomerId(), "S 1-0212-2794-5 ");
	Assert.assertEquals(subscriber.getSubscriberType(),
		"Residential Normal");
	Assert.assertEquals(subscriber.getMsisdn(), "555885911");
	Assert.assertEquals(subscriber.getImsi(), "420013500565050");
    }

    @Test
    public void getSubscriberByTown() {
	List<SubscriberInformation> subscribers = SubscriberInformation.find(
		"officialAddress LIKE '%WOLFSHOFERAMT%'").fetch();
	Assert.assertEquals(subscribers.size(), 2);
    }

    @Test
    public void addSubscriberInformation() {
	SubscriberInformation subscriber = new SubscriberInformation(
		"555885627", "Marko Maric", "Palinovecka 37, 10000 Zagreb",
		"S 1-0212-3264-1 ", "555885627", "420013500563232",
		"Residential Normal");

	Assert.assertEquals(subscriber.getSubscriberId(), "555885627");
	Assert.assertEquals(subscriber.getSubscriberName(), "Marko Maric");
	Assert.assertEquals(subscriber.getOfficialAddress(),
		"Palinovecka 37, 10000 Zagreb");
	Assert.assertEquals(subscriber.getCustomerId(), "S 1-0212-3264-1 ");
	Assert.assertEquals(subscriber.getSubscriberType(),
		"Residential Normal");
	Assert.assertEquals(subscriber.getMsisdn(), "555885627");
	Assert.assertEquals(subscriber.getImsi(), "420013500563232");
    }

}
