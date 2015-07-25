package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;
import java.util.Date;
import java.sql.Time;

@Entity
@Table(name = "rtx")
// @IdClass(CdrInformationPK.class)
public class CdrInformation extends GenericModel {

    @Id
    @Column(name = "r_p_msisdn")
    private String msisdn;
    @Id
    @Column(name = "imsi")
    private String imsi;

    @Id
    @Column(name = "callstart")
    private String callStartDate;
    @Id
    @Column(name = "calldatetime")
    private String callDateTime;

    @Id
    @Column(name = "duration")
    private Double callDuration;
    @Id
    @Column(name = "rated_amount")
    private Double ratedAmount;
    @Id
    @Column(name = "tap_amount")
    private Double discountedAmount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nr")
    private CdrUsage cdrUsage;

    public CdrInformation(String msisdn, String imsi, String callStartDate,
	    String callDateTime, Double callDuration, Double ratedAmount,
	    Double discountedAmount) {
	this.setMsisdn(msisdn);
	this.setImsi(imsi);
	this.setCallStartDate(callStartDate);
	this.setCallDateTime(callDateTime);
	this.setCallDuration(callDuration);
	this.setRatedAmount(ratedAmount);
	this.setDiscountedAmount(discountedAmount);
    }

    public CdrInformation() {
	// TODO Auto-generated constructor stub
    }

    // getters and setters
    public String getCallStartDate() {
	return callStartDate;
    }

    public void setCallStartDate(String callStartDate) {
	this.callStartDate = callStartDate;
    }

    public String getCallDateTime() {
	return callDateTime;
    }

    public void setCallDateTime(String callDateTime) {
	this.callDateTime = callDateTime;
    }

    public Double getCallDuration() {
	return callDuration;
    }

    public void setCallDuration(Double callDuration) {
	this.callDuration = callDuration;
    }

    public Double getRatedAmount() {
	return ratedAmount;
    }

    public void setRatedAmount(Double ratedAmount) {
	this.ratedAmount = ratedAmount;
    }

    public Double getDiscountedAmount() {
	return discountedAmount;
    }

    public void setDiscountedAmount(Double discountedAmount) {
	this.discountedAmount = discountedAmount;
    }

    public CdrUsage getUsageType() {
	return cdrUsage;
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
