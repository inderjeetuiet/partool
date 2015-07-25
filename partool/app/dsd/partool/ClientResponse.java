package dsd.partool;

import java.util.Hashtable;

import models.SubscriberInformation;

public class ClientResponse {

    private String startDate;
    private String endDate;
    private SubscriberInformation subscriber;
    private Hashtable<String, VisualizationData> usageData = new Hashtable<String, VisualizationData>();

    // getters and setters
    public String getImsi() {
	return subscriber.getImsi();
    }

    public String getMsisdn() {
	return subscriber.getMsisdn();
    }

    public String getStartDate() {
	return startDate;
    }

    public void setStartDate(String startDate) {
	this.startDate = startDate;
    }

    public String getEndDate() {
	return endDate;
    }

    public void setEndDate(String endDate) {
	this.endDate = endDate;
    }

    public Hashtable<String, VisualizationData> getUsageData() {
	return usageData;
    }

    public void setUsageData(Hashtable<String, VisualizationData> usageData) {
	this.usageData = usageData;
    }

    public SubscriberInformation getSubscriber() {
	return subscriber;
    }

    public void setSubscriber(SubscriberInformation subscriber) {
	this.subscriber = subscriber;
    }

}
