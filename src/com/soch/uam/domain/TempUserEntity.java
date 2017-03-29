package com.soch.uam.domain;
// default package
// Generated Feb 10, 2017 3:31:12 PM by Hibernate Tools 4.3.5.Final

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
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
 * TempUserTb generated by hbm2java
 */
@Entity
@Table(name = "temp_user_tb", catalog = "sochdb")
public class TempUserEntity implements java.io.Serializable {

	private String employeeId;
	private String firstName;
	private String middleName;
	private String lastName;
	private String emailId;
	private String dateOfBirth;
	private String phoneNumber;
	private String workEmailId;
	private String workPhoneNumber;
	private String startDate;
	private Integer approvalLevel;
	private String createdBy;
	private Date createdTs;
	private boolean pendingApproval;
	private Set<OnboardingUserNotesEntity> onboardingUserNotesEntities = new HashSet<OnboardingUserNotesEntity>(0);
	private Set<OnboardApprovalPendingEntity> onboardApprovalPendingEntities = new HashSet<OnboardApprovalPendingEntity>(0);
	private Set<UserFileEntity> userFileEntities = new HashSet<UserFileEntity>(0);
	private Set<TempUserRoleEntity> tempUserRoleEntities = new HashSet<TempUserRoleEntity>(0);
	private String accountType;
	private ContractCompanyEntity contractCompanyEntity;
	private String endDate;
	
	private String reportingTo;
	

	
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tempUserEntity",cascade=CascadeType.ALL)
	public Set<OnboardingUserNotesEntity> getOnboardingUserNotesEntities() {
		return onboardingUserNotesEntities;
	}

	public void setOnboardingUserNotesEntities(Set<OnboardingUserNotesEntity> onboardingUserNotesEntities) {
		this.onboardingUserNotesEntities = onboardingUserNotesEntities;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tempUserEntity",cascade=CascadeType.ALL)
	public Set<OnboardApprovalPendingEntity> getOnboardApprovalPendingEntities() {
		return onboardApprovalPendingEntities;
	}

	public void setOnboardApprovalPendingEntities(Set<OnboardApprovalPendingEntity> onboardApprovalPendingEntities) {
		this.onboardApprovalPendingEntities = onboardApprovalPendingEntities;
	}

	@Column(name = "pending_approval", length = 1)
	public boolean isPendingApproval() {
		return pendingApproval;
	}

	public void setPendingApproval(boolean pendingApproval) {
		this.pendingApproval = pendingApproval;
	}
	
	@Column(name = "created_by", length = 100)
	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public TempUserEntity() {
	}

	public TempUserEntity(String employeeId) {
		this.employeeId = employeeId;
	}

	public TempUserEntity(String employeeId, String firstName, String middleName, String lastName, String emailId,
			String dateOfBirth, String phoneNumber, String workEmailId, String workPhoneNumber, String startDate,
			Integer userRoleId) {
		this.employeeId = employeeId;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.emailId = emailId;
		this.dateOfBirth = dateOfBirth;
		this.phoneNumber = phoneNumber;
		this.workEmailId = workEmailId;
		this.workPhoneNumber = workPhoneNumber;
		this.startDate = startDate;
	}

	@Id
	@Column(name = "employee_id", unique = true, nullable = false, length = 50)
	public String getEmployeeId() {
		return this.employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	@Column(name = "firstName", length = 100)
	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(name = "middleName", length = 100)
	public String getMiddleName() {
		return this.middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	@Column(name = "lastName", length = 100)
	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Column(name = "emailId", length = 100)
	public String getEmailId() {
		return this.emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	@Column(name = "dateOfBirth", length = 10)
	public String getDateOfBirth() {
		return this.dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	@Column(name = "phoneNumber", length = 45)
	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Column(name = "work_emailId", length = 100)
	public String getWorkEmailId() {
		return this.workEmailId;
	}

	public void setWorkEmailId(String workEmailId) {
		this.workEmailId = workEmailId;
	}

	@Column(name = "work_PhoneNumber", length = 45)
	public String getWorkPhoneNumber() {
		return this.workPhoneNumber;
	}

	public void setWorkPhoneNumber(String workPhoneNumber) {
		this.workPhoneNumber = workPhoneNumber;
	}

	@Column(name = "startDate", length = 10)
	public String getStartDate() {
		return this.startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}


	@Column(name = "approval_level")
	public Integer getApprovalLevel() {
		return this.approvalLevel;
	}

	public void setApprovalLevel(Integer approvalLevel) {
	        this.approvalLevel = approvalLevel;
	}
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="tempUserEntity",cascade=CascadeType.ALL)
	public Set<UserFileEntity> getUserFileEntities() {
		return userFileEntities;
	}

	public void setUserFileEntities(Set<UserFileEntity> userFileEntities) {
		this.userFileEntities = userFileEntities;
	}
	
	@Column(name = "account_type", length = 45)
	public String getAccountType() {
		return this.accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tempUserEntity",cascade=CascadeType.ALL)
	public Set<TempUserRoleEntity> getTempUserRoleEntities() {
		return tempUserRoleEntities;
	}

	public void setTempUserRoleEntities(Set<TempUserRoleEntity> tempUserRoleEntities) {
		this.tempUserRoleEntities = tempUserRoleEntities;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_ts", length = 19)
	public Date getCreatedTs() {
		return this.createdTs;
	}

	public void setCreatedTs(Date createdTs) {
		this.createdTs = createdTs;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CONTRACT_COMPANY_ID")
	public ContractCompanyEntity getContractCompanyEntity() {
		return contractCompanyEntity;
	}

	public void setContractCompanyEntity(ContractCompanyEntity contractCompanyEntity) {
		this.contractCompanyEntity = contractCompanyEntity;
	}
	
	@Column(name = "endDate", length = 45)
	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	@Column(name="REPORTING_TO")
	public String getReportingTo() {
		return reportingTo;
	}

	public void setReportingTo(String reportingTo) {
		this.reportingTo = reportingTo;
	}
	
	
	

}
