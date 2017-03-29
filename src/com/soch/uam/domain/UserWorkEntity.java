// default package
// Generated Feb 9, 2017 12:28:59 PM by Hibernate Tools 4.3.5.Final
package com.soch.uam.domain;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * UserWorkTb generated by hbm2java
 */
@Entity
@Table(name = "user_work_tb", catalog = "sochdb")
public class UserWorkEntity implements java.io.Serializable {

	private UserEntity userEntity;
	private String phoneNumber;
	private Date startDate;
	private Date endDate;
	private int deptId;
	private String empId;
	private String emailAddress;
	private String reportingTo;
	private ContractCompanyEntity contractCompanyEntity;

	public UserWorkEntity() {
	}

	
	@Id
	@Column(name = "emp_id", unique = true, nullable = false, length = 45)
	public String getEmpId() {
		return this.empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id", nullable = false)
	public UserEntity getUserEntity() {
		return this.userEntity;
	}

	public void setUserEntity(UserEntity userEntity) {
		this.userEntity = userEntity;
	}

	@Column(name = "phone_number", length = 45)
	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "start_date", nullable = false, length = 10)
	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "end_date", length = 10)
	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Column(name = "dept_id", nullable = false)
	public int getDeptId() {
		return this.deptId;
	}

	public void setDeptId(int deptId) {
		this.deptId = deptId;
	}

	@Column(name = "email_address", nullable = false, length = 100)
	public String getEmailAddress() {
		return this.emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	@Column(name = "REPORTING_TO", length = 50)
	public String getReportingTo() {
		return reportingTo;
	}


	public void setReportingTo(String reportingTo) {
		this.reportingTo = reportingTo;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CONTRACT_COMPANY_ID")
	public ContractCompanyEntity getContractCompanyEntity() {
		return contractCompanyEntity;
	}


	public void setContractCompanyEntity(ContractCompanyEntity contractCompanyEntity) {
		this.contractCompanyEntity = contractCompanyEntity;
	}
	
	

}
