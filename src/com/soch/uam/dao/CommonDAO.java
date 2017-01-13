package com.soch.uam.dao;

import java.util.List;

import com.soch.uam.domain.PolicyConfigEntity;
import com.soch.uam.domain.PolicyGrpEntity;
import com.soch.uam.domain.PolicySrcEntity;

public interface CommonDAO {
	
	public List<PolicyConfigEntity> getAllPolicies();
	
	public void updatePolicy(PolicyConfigEntity policyConfigEntity);
	
	public PolicyConfigEntity getPolicy(String name);
	
	public List<PolicySrcEntity> getDefaultPolicies();

	public void updateIdentityCMSPolicies();
	
	public void savePolicySrc(PolicyGrpEntity policySrcEntity);
	
	public void savePolicyConfig(PolicyConfigEntity policyConfigEntity);
	
	public Integer getMaxPolicyGRPId();
	
	public Integer getMaxPolicySrcId();
	
	public PolicyGrpEntity getPolicyGrpEntity(String policyName);
}
