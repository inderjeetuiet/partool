package models;

import java.util.*;
import java.util.Date;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
@Table(name = "leap_customers")
public class SubscriberInformation extends GenericModel {

    @Column(name = "subscriber_id")
    private String subscriberId;
    @Column(name = "subscriber_name")
    private String subscriberName;
    @Column(name = "official_address")
    private String officialAddress;
    @Column(name = "subscriber_type")
    private String subscriberType;

    @Column(name = "customer_pid")
    private String customerId;

    @Id
    @Column(name = "imsi")
    private String imsi;
    @Id
    @Column(name = "msisdn")
    private String msisdn;

    public SubscriberInformation() {

    }

    public SubscriberInformation(String subscriberId, String subscriberName,
	    String officialAddress, String customerId, String msisdn,
	    String imsi, String subscriberType) {
	this.setSubscriberId(subscriberId);
	this.setSubscriberName(subscriberName);
	this.setOfficialAddress(officialAddress);
	this.setCustomerId(customerId);
	this.setMsisdn(msisdn);
	this.setImsi(imsi);
	this.setSubscriberType(subscriberType);
    }

    // getters and setters
    public String getSubscriberId() {
	return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
	this.subscriberId = subscriberId;
    }

    public String getSubscriberName() {
	return subscriberName;
    }

    public void setSubscriberName(String subscriberName) {
	this.subscriberName = subscriberName;
    }

    public String getOfficialAddress() {
	return officialAddress;
    }

    public void setOfficialAddress(String officialAddress) {
	this.officialAddress = officialAddress;
    }

    public String getCustomerId() {
	return customerId;
    }

    public void setCustomerId(String customerId) {
	this.customerId = customerId;
    }

    public String getSubscriberType() {
	return subscriberType;
    }

    public void setSubscriberType(String subscriberType) {
	this.subscriberType = subscriberType;
    }

    public String getMsisdn() {
	return msisdn;
    }

    public void setMsisdn(String msisdn) {
	this.msisdn = msisdn;
    }

    public String getImsi() {
	return imsi;
    }

    public void setImsi(String imsi) {
	this.imsi = imsi;
    }
}
