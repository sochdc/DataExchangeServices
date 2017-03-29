package com.soch.uam.dao;

import java.util.List;

import com.soch.uam.domain.ContractCompanyEntity;
import com.soch.uam.domain.DeptEntity;
import com.soch.uam.domain.OnboardApprovalPendingEntity;
import com.soch.uam.domain.OnboardingApprovalEntity;
import com.soch.uam.domain.OnboardingUserNotesEntity;
import com.soch.uam.domain.PolicyConfigEntity;
import com.soch.uam.domain.PolicyGrpEntity;
import com.soch.uam.domain.PolicySrcEntity;
import com.soch.uam.domain.RolesEntity;
import com.soch.uam.domain.SystemEntity;
import com.soch.uam.domain.UserFileEntity;
import com.soch.uam.domain.UserTypeEntity;

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


}
