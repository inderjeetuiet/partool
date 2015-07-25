package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

import com.google.gson.*;
import controllers.*;

@Entity
@Table(name = "rtx_usage_types")
public class CdrUsage extends GenericModel {

    @Id
    @Column(name = "usage_type")
    private String usageType;
    @Column(name = "description")
    private String usageDesc;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rtx_type")
    @JsonExclude
    private CdrTypes cdrType;

    public CdrUsage(String usagetype, String usageDesc) {
	this.setUsageType(usagetype);
	this.setUsageDesc(usageDesc);
    }

    public CdrUsage() {
	// TODO Auto-generated constructor stub
    }

    // getters and setters
    public String getUsageType() {
	return usageType;
    }

    public void setUsageType(String usageType) {
	this.usageType = usageType;
    }

    public String getUsageDesc() {
	return usageDesc;
    }

    public void setUsageDesc(String usageDesc) {
	this.usageDesc = usageDesc;
    }

	public CdrTypes getCdrType() {
		return cdrType;
	}

	public void setCdrType(CdrTypes cdrType) {
		this.cdrType = cdrType;
	}
}
