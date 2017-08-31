package com.soch.uam.domain;
// default package
// Generated Dec 26, 2016 4:27:28 PM by Hibernate Tools 4.3.5.Final

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

import org.hibernate.annotations.Cascade;

/**
 * PolicySrcTb generated by hbm2java
 */
@Entity
@Table(name = "POLICY_SRC_TB")
public class PolicySrcEntity implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5943600606740905055L;
	private int policyId;
	private PolicyGrpEntity policyGrpEntity;
	private PolicyConfigEntity policyConfigEntity;
	private String policyName;
	private String policyDesc;
	private String vitaVal;
	private String cmsVal;
	private String customVal;
	private String createdBy;
	private Date createdTs;
	private String modifiedBy;
	private Date modifiedTs;
	private String notes;
	private byte[] fileContent;
	private Set<PolicySrcNotesEntity> policySrcNotesEntities = new HashSet<PolicySrcNotesEntity>(0);
	
	
/*	private Set<PolicyConfigEntity> policyConfigEntities;
*/	
	public PolicySrcEntity() {
	}
	
	public PolicySrcEntity(int policyId) {
		this.policyId = policyId;
	}

	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="SeqGen")
    @TableGenerator(
		        name="SeqGen", 
		        table="SEQ_TB", 
		        pkColumnName="seq_name", 
		        valueColumnName="seq_val", 
		        pkColumnValue="policySrcTB_ID", 
		        allocationSize=1)
	@Column(name = "POLICY_ID", unique = true, nullable = false)
	public int getPolicyId() {
		return this.policyId;
	}

	public void setPolicyId(int policyId) {
		this.policyId = policyId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "POLICY_GRP_ID", insertable = true, updatable = true, nullable = false)
	public PolicyGrpEntity getPolicyGrpEntity() {
		return this.policyGrpEntity;
	}

	public void setPolicyGrpEntity(PolicyGrpEntity policyGrpEntity) {
		this.policyGrpEntity = policyGrpEntity;
	}

	@Column(name = "POLICY_NAME", length = 100)
	public String getPolicyName() {
		return this.policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	@Column(name = "POLICY_DESC", length = 500)
	public String getPolicyDesc() {
		return this.policyDesc;
	}

	public void setPolicyDesc(String policyDesc) {
		this.policyDesc = policyDesc;
	}

	@Column(name = "VITA_VAL", length = 45)
	public String getVitaVal() {
		return this.vitaVal;
	}

	public void setVitaVal(String vitaVal) {
		this.vitaVal = vitaVal;
	}

	@Column(name = "CMS_VAL", length = 45)
	public String getCmsVal() {
		return this.cmsVal;
	}

	public void setCmsVal(String cmsVal) {
		this.cmsVal = cmsVal;
	}
	
	@Column(name = "CUSTOM_VAL", length = 45)
	public String getCustomVal() {
		return this.customVal;
	}

	public void setCustomVal(String customVal) {
		this.customVal = customVal;
	}

	@Column(name = "CREATED_BY", length = 45)
	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_TS", length = 19)
	public Date getCreatedTs() {
		return this.createdTs;
	}

	public void setCreatedTs(Date createdTs) {
		this.createdTs = createdTs;
	}

	@Column(name = "MODIFIED_BY", length = 45)
	public String getModifiedBy() {
		return this.modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFIED_TS", length = 19)
	public Date getModifiedTs() {
		return this.modifiedTs;
	}

	public void setModifiedTs(Date modifiedTs) {
		this.modifiedTs = modifiedTs;
	}

	@Column(name = "NOTES", length = 500)
	public String getNotes() {
		return this.notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	@Column(name = "fileContent")
	public byte[] getFileContent() {
		return this.fileContent;
	}

	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "POLICY_CONFIG_ID", nullable=false, insertable=true, updatable=true)
	public PolicyConfigEntity getPolicyConfigEntity() {
		return policyConfigEntity;
	}

	public void setPolicyConfigEntity(PolicyConfigEntity policyConfigEntity) {
		this.policyConfigEntity = policyConfigEntity;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "policySrcEntity",cascade = CascadeType.ALL)
	public Set<PolicySrcNotesEntity> getPolicySrcNotesEntities() {
		return policySrcNotesEntities;
	}

	public void setPolicySrcNotesEntities(Set<PolicySrcNotesEntity> policySrcNotesEntities) {
		this.policySrcNotesEntities = policySrcNotesEntities;
	}
	
}
