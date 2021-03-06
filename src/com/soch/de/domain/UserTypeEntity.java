package com.soch.de.domain;
// default package
// Generated Mar 2, 2017 10:34:25 AM by Hibernate Tools 4.3.5.Final

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * UserTypeTb generated by hbm2java
 */
@Entity
@Table(name = "user_type_tb")
public class UserTypeEntity implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5173341879169507545L;
	private String typeName;
	private int userTypeId;
	private Set<PolicyGrpEntity> policyGrpEntities;

	public UserTypeEntity() {
	}

	public UserTypeEntity(int userTypeId) {
		this.userTypeId = userTypeId;
	}

	@Id
	@Column(name = "USER_TYPE_ID", unique = true, nullable = false)
	public int getUserTypeId() {
		return this.userTypeId;
	}

	public void setUserTypeId(int userTypeId) {
		this.userTypeId = userTypeId;
	}

	@Column(name = "TYPE_NAME", length = 100)
	public String getTypeName() {
		return this.typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "userTypeEntity")
	public Set<PolicyGrpEntity> getPolicyGrpEntities() {
		return policyGrpEntities;
	}

	public void setPolicyGrpEntities(Set<PolicyGrpEntity> policyGrpEntities) {
		this.policyGrpEntities = policyGrpEntities;
	}

	
	

}
