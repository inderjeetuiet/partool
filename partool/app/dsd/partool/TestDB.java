package dsd.partool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import play.db.jpa.JPA;
import play.vfs.VirtualFile;

import models.CdrInformation;
import models.CdrTypes;
import models.CdrUsage;
import models.SubscriberInformation;
import models.User;

public class TestDB {

    private EntityManager em;

    public TestDB() {
	em = JPA.em();
    }

    public void fillTestDB() {

	loadShemaFromFile("public/testData/shema.txt");
	loadDataFromFile("public/testData/users.txt");
	loadDataFromFile("public/testData/subscribers.txt");
	loadDataFromFile("public/testData/types.txt");
	loadDataFromFile("public/testData/usages.txt");
	loadDataFromFile("public/testData/cdrs.txt");

    }

    private void loadShemaFromFile(String fileName) {
	File file = null;
	Scanner scanner = null;
	StringBuilder script = new StringBuilder(1000);

	try {
	    VirtualFile vf = VirtualFile.fromRelativePath(fileName);
	    file = vf.getRealFile();

	    scanner = new Scanner(new FileReader(file));
	    while (scanner.hasNextLine()) {
		String line = scanner.nextLine();
		if (!line.trim().isEmpty() && !line.trim().startsWith("--")) {
		    script.append(line);
		}
	    }

	    executeCommand(script.toString());
	} catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} finally {
	    scanner.close();
	}
    }

    private void loadDataFromFile(String fileName) {

	File file = null;
	Scanner scanner = null;

	try {
	    VirtualFile vf = VirtualFile.fromRelativePath(fileName);
	    file = vf.getRealFile();
	    scanner = new Scanner(new FileReader(file));
	    while (scanner.hasNextLine()) {
		this.executeCommand(scanner.nextLine());
	    }

	} catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} finally {
	    scanner.close();
	}
    }

    private void executeCommand(String command) {
	Query query = em.createNativeQuery(command);
	try {
	    query.executeUpdate();
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

}
