package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
@Table(name = "rtx_types")
public class CdrTypes extends GenericModel {

    @Id
    @Column(name = "rtx_type")
    private Integer cdrType;
    @Column(name = "type_desc")
    private String typeDesc;
    @OneToMany(mappedBy = "cdrType", fetch = FetchType.EAGER)
    private List<CdrUsage> cdrUsages;

    public CdrTypes(Integer cdrType, String typeDesc, List<CdrUsage> cdrUsages) {
	this.setCdrType(cdrType);
	this.setTypeDesc(typeDesc);
	this.setCdrUsages(cdrUsages);
    }

    public CdrTypes() {
	// TODO Auto-generated constructor stub
    }

    // getters and setters
    public Integer getCdrType() {
	return cdrType;
    }

    public void setCdrType(Integer cdrType) {
	this.cdrType = cdrType;
    }

    public String getTypeDesc() {
	return typeDesc;
    }

    public void setTypeDesc(String typeDesc) {
	this.typeDesc = typeDesc;
    }

    public List<CdrUsage> getCdrUsages() {
	return cdrUsages;
    }

    public void setCdrUsages(List<CdrUsage> cdrUsages) {
	this.cdrUsages = cdrUsages;
    }
}
