package com.soch.de.dao;

import java.util.List;

import com.soch.de.domain.ContractCompanyEntity;
import com.soch.de.domain.DeptEntity;
import com.soch.de.domain.DocumentsEntity;
import com.soch.de.domain.OnboardApprovalPendingEntity;
import com.soch.de.domain.OnboardingApprovalEntity;
import com.soch.de.domain.OnboardingUserNotesEntity;
import com.soch.de.domain.PolicyConfigEntity;
import com.soch.de.domain.PolicyGrpEntity;
import com.soch.de.domain.PolicySrcEntity;
import com.soch.de.domain.PolicySrcNotesEntity;
import com.soch.de.domain.RoleMappingEntity;
import com.soch.de.domain.RolesEntity;
import com.soch.de.domain.SystemEntity;
import com.soch.de.domain.UserFileEntity;
import com.soch.de.domain.UserNotesEntity;
import com.soch.de.domain.UserTypeEntity;

public interface CommonDAO {
	
	public List<PolicyConfigEntity> getAllPolicies();
	
	public void updatePolicy(PolicyConfigEntity policyConfigEntity);
	
	public void updatePolicy(PolicySrcEntity policySrcEntity);
	
	public PolicyConfigEntity getPolicy(String name);
	
	public List<PolicySrcEntity> getDefaultPolicies();

	public void updateIdentityCMSPolicies(int src, String policy);
	
	public void savePolicySrc(PolicyGrpEntity policySrcEntity);
	
	public void savePolicySrc(PolicySrcEntity policySrcEntity);
	
	public void savePolicyConfig(PolicyConfigEntity policyConfigEntity);
	
	public Integer getMaxPolicyGRPId();
	
	public Integer getMaxPolicySrcId();
	
	public PolicyGrpEntity getPolicyGrpEntity(String policyName);
	
	public List<DeptEntity> getDepartments();
	
	public List<SystemEntity> getSystems(int deptId);
	
	public SystemEntity getSystemEntity(int systemId);
	
	public List<RolesEntity> getRoles(int systemId);
	
	public RolesEntity getRoleById(int rileId);
	
	public  List<OnboardingApprovalEntity> getOnboardApproval(int roleId);
	
	public void saveOnboardApprovalPendingEntity(OnboardApprovalPendingEntity onboardApprovalPendingEntity);
	
	public void saveOnboardingNotes(OnboardingUserNotesEntity onboardingUserNotesEntity);
	
	void insertUserFile(UserFileEntity userFileEntity);
	
	public List<PolicySrcEntity> getPolicies(int source);

	List<PolicyGrpEntity> getPolicyGrpEntity(int userType);

	public PolicySrcEntity getPolicySrcEntity(int policyId);

	public List<UserTypeEntity> getUserTypes();

	public PolicyGrpEntity getPolicyGrpEntityOnId(Integer policyGrpId);

	UserTypeEntity getUserType(Integer userTypeId);

	public void savePolicyGrpEntity(PolicyGrpEntity policyGrpEntity);

	public void saveOnboardingApprovalEntity(OnboardingApprovalEntity onboardingApprovalEntity);

	void updateOnboardingApprovalEntity(OnboardingApprovalEntity onboardingApprovalEntity);

	public void saveRolesEntity(RolesEntity rolesEntity);

	public List<ContractCompanyEntity> getContractCompanyList();

	public ContractCompanyEntity getContractCompanyEntity(int companyName);

	public  List<OnboardApprovalPendingEntity>  getAllPendingRequests();

	public int getOnboardApprovalMaxLevel(Integer roleID);

	public OnboardingApprovalEntity getOnboardApproval(Integer roleID, int approveLevel);

	public void saveuserNotes(UserNotesEntity userNotesEntity);

	public List<RoleMappingEntity> getRoleMapping();

	public void savePolicySrcNotes(PolicySrcNotesEntity policySrcNotesEntity);

	public List<PolicySrcNotesEntity> getPolicyNotes(Integer policyId);

	public List<RolesEntity> getAllRoles();
	

	public List<DocumentsEntity> getDashboardFiles();

	public DocumentsEntity getDashboardFiles(int id);

	public void updatedDashboardFile(DocumentsEntity documentsEntity) ;

	public int saveDashboardFile(DocumentsEntity documentsEntity);

	public DeptEntity getDepartment(String departmentName);


}
