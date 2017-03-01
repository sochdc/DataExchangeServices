package com.soch.uam.service;

import java.util.List;
import java.util.Set;

import com.soch.uam.domain.DeptEntity;
import com.soch.uam.domain.OnboardingUserNotesEntity;
import com.soch.uam.domain.RolesEntity;
import com.soch.uam.domain.SystemEntity;
import com.soch.uam.dto.DepartmentDTO;
import com.soch.uam.dto.OnboardingUserNotesDTO;
import com.soch.uam.dto.PolicyConfigDTO;
import com.soch.uam.dto.PolicySrcDTO;
import com.soch.uam.dto.RolesDTO;
import com.soch.uam.dto.SystemDTO;
import com.soch.uam.response.UserSVCResp;

public interface CommonService {
	
	public Set<PolicyConfigDTO> getALLPolicies();
	
	public void updatePolicies(Set<PolicyConfigDTO> policyConfigDTOs);
	
	public Set<PolicySrcDTO> gePolicySrcDTOs();
	
	public void updateIdentityCMSPolicies(String src, String policy);

	public void addPolicy(Set<PolicyConfigDTO> policyConfigDTOs);
	
	public Set<DepartmentDTO> getDepartments();
	
	public Set<SystemDTO> getDeptSystems(int deptId);
	
	public Set<RolesDTO> getSystemRoles(int systemId);
	
}
