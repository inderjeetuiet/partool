package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;

import java.util.*;

@Entity
@Table(name = "users")
public class User extends GenericModel {

    @Id
    @Column(name = "login")
    private String login;
    @Column(name = "password")
    private String password;

    public User(String login, String password) {
	this.setLogin(login);
	this.setPassword(password);
    }

    // getters and setters
    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    public String getLogin() {
	return login;
    }

    public void setLogin(String login) {
	this.login = login;
    }
}
