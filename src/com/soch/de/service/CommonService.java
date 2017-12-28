package com.soch.de.service;

import java.util.List;
import java.util.Set;

import com.soch.de.domain.DeptEntity;
import com.soch.de.domain.OnboardingUserNotesEntity;
import com.soch.de.domain.RolesEntity;
import com.soch.de.domain.SystemEntity;
import com.soch.uam.dto.ContractCompayDTO;
import com.soch.uam.dto.DepartmentDTO;
import com.soch.uam.dto.ExternalSourceRoleDTO;
import com.soch.uam.dto.OnboardingApprovalDTO;
import com.soch.uam.dto.OnboardingUserNotesDTO;
import com.soch.uam.dto.PolicySrcNotesDTO;
import com.soch.uam.dto.RolesDTO;
import com.soch.uam.dto.SystemDTO;
import com.soch.uam.dto.UserDTO;
import com.soch.uam.dto.UserTypeDTO;
import com.soch.uam.dto.policy.AddNewPolicyDTO;
import com.soch.uam.dto.policy.PolicyConfigDTO;
import com.soch.uam.dto.policy.PolicyConfigVO;
import com.soch.uam.dto.policy.PolicyGrpDTO;
import com.soch.uam.dto.policy.PolicySrcDTO;
import com.soch.uam.request.AddRoleReq;
import com.soch.uam.request.FileUploadReq;
import com.soch.uam.request.ReportReq;
import com.soch.uam.request.UserProfileResp;
import com.soch.uam.response.CommonResp;
import com.soch.uam.response.ReportResp;
import com.soch.uam.response.RoleMappingResp;
import com.soch.uam.response.UserSVCResp;

public interface CommonService {
	
	public Set<PolicyConfigDTO> getALLPolicies();
	
	public void updatePolicies(Set<PolicySrcDTO> PolicySrcDTO);
	
	public Set<PolicySrcDTO> gePolicySrcDTOs();
	
	public void updateIdentityCMSPolicies(int src, String policy);

	public void addPolicy(Set<PolicySrcDTO> policySrcDTOs);
	
	public Set<DepartmentDTO> getDepartments();
	
	public Set<SystemDTO> getDeptSystems(int deptId);
	
	public Set<RolesDTO> getSystemRoles(int systemId);

	Set<PolicySrcDTO> gePolicySrcDTOs(int src);
	
	Set<PolicyGrpDTO> getPolicyGroup(int userType);

	public Set<UserTypeDTO> getUserTypes();

	public void addNewPolicyDTO(AddNewPolicyDTO addNewPolicyDTO);

	public Integer addOnboardApproval(OnboardingApprovalDTO onboardingApprovalDTO);

	public int addRole(AddRoleReq roleReq);

	public List<ContractCompayDTO> getContractCompanyList();

	public RoleMappingResp getRoleMapping();

	public void addPolicyNotes(PolicyConfigVO policyConfigVO);

	public Set<PolicySrcNotesDTO> getPolicyNotes(Integer policyId);

	public ExternalSourceRoleDTO getExternalRole(String roleId);
	
	public CommonResp getDashboardFiles();

	public String getFile(int id);

	public ReportResp retReport(ReportReq reportReq);

	public int deleteFile(int fileId, int id);

	public int uploadDashboardFile(FileUploadReq fileUploadReq);

	public List<UserDTO> getDepartmentUsers(String departmentName);
	
	//Set<UserType> getPolicyGroup(int userType);
	//getUserTypesSVC
}
