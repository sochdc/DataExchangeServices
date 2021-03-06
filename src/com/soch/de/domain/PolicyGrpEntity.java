package com.soch.de.domain;
// default package
// Generated Dec 26, 2016 4:27:28 PM by Hibernate Tools 4.3.5.Final

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * PolicyGrpTb generated by hbm2java
 */
@Entity
@Table(name = "POLICY_GRP_TB")
public class PolicyGrpEntity implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6353665976745682781L;
	private int policyGrpId;
	private String policyGrpName;
	private String description;
    private String createdBy;
    private Date createdTs;
	private Set<PolicySrcEntity> policySrcEntities = new HashSet<PolicySrcEntity>(0);
	
	private UserTypeEntity userTypeEntity;

	public PolicyGrpEntity() {
	}

	public PolicyGrpEntity(int policyGrpId) {
		this.policyGrpId = policyGrpId;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="SeqGen")
    @TableGenerator(
		        name="SeqGen", 
		        table="SEQ_TB", 
		        pkColumnName="seq_name", 
		        valueColumnName="seq_val", 
		        pkColumnValue="policyGRPTB_ID", 
		        allocationSize=1)
	@Column(name = "POLICY_GRP_ID", unique = true, nullable = false)
	public int getPolicyGrpId() {
		return this.policyGrpId;
	}

	public void setPolicyGrpId(int policyGrpId) {
		this.policyGrpId = policyGrpId;
	}

	@Column(name = "POLICY_GRP_NAME", length = 100)
	public String getPolicyGrpName() {
		return this.policyGrpName;
	}

	public void setPolicyGrpName(String policyGrpName) {
		this.policyGrpName = policyGrpName;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "policyGrpEntity",cascade = CascadeType.ALL)
	public Set<PolicySrcEntity> getPolicySrcEntities() {
		return policySrcEntities;
	}

	public void setPolicySrcEntities(Set<PolicySrcEntity> policySrcEntities) {
		this.policySrcEntities = policySrcEntities;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_TYPE_ID")
	public UserTypeEntity getUserTypeEntity() {
		return userTypeEntity;
	}

	public void setUserTypeEntity(UserTypeEntity userTypeEntity) {
		this.userTypeEntity = userTypeEntity;
	}
	 
	
	@Column(name="CREATED_BY", length=100)
    public String getCreatedBy() {
        return this.createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="CREATED_TS", length=19)
    public Date getCreatedTs() {
        return this.createdTs;
    }
    
    public void setCreatedTs(Date createdTs) {
        this.createdTs = createdTs;
    }
	
    @Column(name="DESCRIPTION", length=500)
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

}
