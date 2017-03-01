package com.soch.uam.dao;

import java.util.List;

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

public interface CommonDAO {
	
	public List<PolicyConfigEntity> getAllPolicies();
	
	public void updatePolicy(PolicyConfigEntity policyConfigEntity);
	
	public PolicyConfigEntity getPolicy(String name);
	
	public List<PolicySrcEntity> getDefaultPolicies();

	public void updateIdentityCMSPolicies(String src, String policy);
	
	public void savePolicySrc(PolicyGrpEntity policySrcEntity);
	
	public void savePolicyConfig(PolicyConfigEntity policyConfigEntity);
	
	public Integer getMaxPolicyGRPId();
	
	public Integer getMaxPolicySrcId();
	
	public PolicyGrpEntity getPolicyGrpEntity(String policyName);
	
	public List<DeptEntity> getDepartments();
	
	public List<SystemEntity> getSystems(int deptId);
	
	public List<RolesEntity> getRoles(int systemId);
	
	public RolesEntity getRoleById(int rileId);
	
	public  List<OnboardingApprovalEntity> getOnboardApproval(int roleId);
	
	public void saveOnboardApprovalPendingEntity(OnboardApprovalPendingEntity onboardApprovalPendingEntity);
	
	public void saveOnboardingNotes(OnboardingUserNotesEntity onboardingUserNotesEntity);
	
	void insertUserFile(UserFileEntity userFileEntity);
	

}
