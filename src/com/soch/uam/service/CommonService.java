package com.soch.uam.service;

import java.util.Set;

import com.soch.uam.dto.PolicyConfigDTO;
import com.soch.uam.dto.PolicySrcDTO;

public interface CommonService {
	
	public Set<PolicyConfigDTO> getALLPolicies();
	
	public void updatePolicies(Set<PolicyConfigDTO> policyConfigDTOs);
	
	public Set<PolicySrcDTO> gePolicySrcDTOs();
	
	public void updateIdentityCMSPolicies();

	public void addPolicy(Set<PolicyConfigDTO> policyConfigDTOs);
	

}
