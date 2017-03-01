// default package
// Generated Feb 9, 2017 12:28:59 PM by Hibernate Tools 4.3.5.Final
package com.soch.uam.domain;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * SystemTb generated by hbm2java
 */
@Entity
@Table(name = "system_tb", catalog = "sochdb")
public class SystemEntity implements java.io.Serializable {

	private int systemId;
	private DeptEntity deptEntity;
	private String systemName;
	private String description;
	private String createdBy;
	private Date createdTs;
	private String updatedBy;
	private Date updatedTs;
	private Set<RolesEntity> rolesEntitys = new HashSet<RolesEntity>(0);

	public SystemEntity() {
	}

	public SystemEntity(int systemId) {
		this.systemId = systemId;
	}

	public SystemEntity(int systemId, DeptEntity deptEntity, String systemName, String description, String createdBy,
			Date createdTs, String updatedBy, Date updatedTs, Set<RolesEntity> rolesEntitys) {
		this.systemId = systemId;
		this.deptEntity = deptEntity;
		this.systemName = systemName;
		this.description = description;
		this.createdBy = createdBy;
		this.createdTs = createdTs;
		this.updatedBy = updatedBy;
		this.updatedTs = updatedTs;
		this.rolesEntitys = rolesEntitys;
	}

	@Id

	@Column(name = "system_id", unique = true, nullable = false)
	public int getSystemId() {
		return this.systemId;
	}

	public void setSystemId(int systemId) {
		this.systemId = systemId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dept_id")
	public DeptEntity getDeptEntity() {
		return this.deptEntity;
	}

	public void setDeptEntity(DeptEntity deptEntity) {
		this.deptEntity = deptEntity;
	}

	@Column(name = "system_name", length = 100)
	public String getSystemName() {
		return this.systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	@Column(name = "description", length = 200)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "created_by", length = 50)
	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_ts", length = 19)
	public Date getCreatedTs() {
		return this.createdTs;
	}

	public void setCreatedTs(Date createdTs) {
		this.createdTs = createdTs;
	}

	@Column(name = "updated_by", length = 50)
	public String getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_ts", length = 19)
	public Date getUpdatedTs() {
		return this.updatedTs;
	}

	public void setUpdatedTs(Date updatedTs) {
		this.updatedTs = updatedTs;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "systemEntity")
	public Set<RolesEntity> getRolesEntitys() {
		return this.rolesEntitys;
	}

	public void setRolesEntitys(Set<RolesEntity> rolesEntitys) {
		this.rolesEntitys = rolesEntitys;
	}

}
