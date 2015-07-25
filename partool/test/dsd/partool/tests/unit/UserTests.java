package dsd.partool.tests.unit;

import org.junit.*;

import dsd.partool.TestDB;

import java.util.*;

import javax.persistence.EntityManager;

import play.db.jpa.JPA;
import play.test.*;
import models.*;

public class UserTests extends UnitTest {

    EntityManager em;

    @Before
    public void setUp() {
	Fixtures.deleteAllModels();
	TestDB testData = new TestDB();
	testData.fillTestDB();
	em = JPA.em();
    }

    @Test
    public void getUserCount() {
	Assert.assertEquals(5, User.count());

    }

    @Test
    public void getUserByName() {
	User user = User.find("login", "admin").first();
	Assert.assertEquals(user.getLogin(), "admin");
	Assert.assertEquals(user.getPassword(), "admin");
    }

    @Test
    public void addUser() {    
	User user = new User("marko", "20896663fgc");
	em.persist(user);
	user.save();
	User newUser = User.find("login", "marko").first();
	Assert.assertEquals(newUser.getLogin(), "marko");
	Assert.assertEquals(newUser.getPassword(), "20896663fgc");
    }

    @Test
    public void getUsersByFirstLetter() {
	List<User> users = User.find("login LIKE 'z%'").fetch();
	Assert.assertEquals(users.size(), 3);
    }

    @Test
    public void getUserByPassword() {
	User user = User.find("password", "tamy").first();
	Assert.assertEquals(user.getLogin(), "tamara");
	Assert.assertEquals(user.getPassword(), "tamy");
    }

}
