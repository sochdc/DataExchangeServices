package com.soch.uam.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;
import javax.xml.bind.DatatypeConverter;
import org.jboss.resteasy.plugins.interceptors.RoleBasedSecurityFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soch.uam.dao.CommonDAO;
import com.soch.uam.dao.UserDAO;
import com.soch.uam.domain.ContractCompanyEntity;
import com.soch.uam.domain.DeptEntity;
import com.soch.uam.domain.OnboardApprovalPendingEntity;
import com.soch.uam.domain.OnboardingApprovalEntity;
import com.soch.uam.domain.PolicyConfigEntity;
import com.soch.uam.domain.PolicyGrpEntity;
import com.soch.uam.domain.PolicySrcEntity;
import com.soch.uam.domain.RolesEntity;
import com.soch.uam.domain.SystemEntity;
import com.soch.uam.domain.UserEntity;
import com.soch.uam.domain.UserTypeEntity;
import com.soch.uam.dto.ContractCompayDTO;
import com.soch.uam.dto.DepartmentDTO;
import com.soch.uam.dto.OnboardingApprovalDTO;
import com.soch.uam.dto.RolesDTO;
import com.soch.uam.dto.SystemDTO;
import com.soch.uam.dto.UserTypeDTO;
import com.soch.uam.dto.policy.AddNewPolicyDTO;
import com.soch.uam.dto.policy.PolicyConfigDTO;
import com.soch.uam.dto.policy.PolicyGrpDTO;
import com.soch.uam.dto.policy.PolicySrcDTO;
import com.soch.uam.request.AddRoleReq;
import com.soch.uam.service.CommonService;

@Service("commonService")
public class CommonServiceImpl implements CommonService{
	
	@Autowired
	private CommonDAO commonDAO ;
	
	@Autowired
	private UserDAO userDAO;

	@Override
	@Transactional
	public Set<PolicyConfigDTO> getALLPolicies() {
		
		Set<PolicyConfigDTO> policyConfigDTOs = null;
		PolicyConfigDTO policyConfigDTO = null;
		List<PolicyConfigEntity> policyConfigEntities = commonDAO.getAllPolicies();
		
		if(!policyConfigEntities.isEmpty())
		{
			policyConfigDTOs = new HashSet<PolicyConfigDTO>();
			for(PolicyConfigEntity policyConfigEntity : policyConfigEntities)
			{
				policyConfigDTO = new PolicyConfigDTO();
				policyConfigDTO.setPolicyName(policyConfigEntity.getPolicyName());
				policyConfigDTO.setPolicyConfigID(policyConfigEntity.getPolicyConfigId());
				policyConfigDTO.setFederal(policyConfigEntity.getFederal());
				policyConfigDTO.setState(policyConfigEntity.getState());
				/*if(policyConfigEntity.getValue() != null)
					policyConfigDTO.setValue(policyConfigEntity.getValue());*/
				policyConfigDTOs.add(policyConfigDTO);
			}
		}
		
		return policyConfigDTOs;
	}

	@Override
	@Transactional
	public void updatePolicies(Set<PolicySrcDTO> policySrcDTOs) {
		
		PolicySrcEntity policySrcEntity = null;
		for(PolicySrcDTO policySrcDTO : policySrcDTOs)
		{
			policySrcEntity = commonDAO.getPolicySrcEntity(policySrcDTO.getPolicyId());
			
			policySrcEntity.setCustomVal(policySrcDTO.getCustomVal());
			policySrcEntity.setNotes(policySrcDTO.getNotes());
			policySrcEntity.setModifiedTs(new Date());
			policySrcEntity.setModifiedBy("TEST");
			/*policyConfigEntity.setPolicyName(policyConfigDTO.getPolicyName());
			policyConfigEntity.setValue(policyConfigDTO.getValue());
			policyConfigEntity.setNotes(policyConfigDTO.getNotes());
			policyConfigEntity.setModifiedTs(new Date());*/
			commonDAO.updatePolicy(policySrcEntity);
			
			/*policyConfigEntity = commonDAO.getPolicy(policyConfigDTO.getPolicyName());
			if(policyConfigEntity != null && !policyConfigEntity.getValue().equals(policyConfigDTO.getValue()))
			{
				System.out.println("policyConfigDTO.getPolicyName() " +policyConfigDTO.getPolicyName());
				policyConfigEntity.setPolicyName(policyConfigDTO.getPolicyName());
				policyConfigEntity.setValue(policyConfigDTO.getValue());
				policyConfigEntity.setNotes(policyConfigDTO.getNotes());
				policyConfigEntity.setModifiedTs(new Date());
				commonDAO.updatePolicy(policyConfigEntity);
			}*/
			//CacheUtil.getInstance().putCache(policyConfigDTO.getPolicyName(), policyConfigDTO.getValue());
		}
		
	}

	@Override
	@Transactional
	public Set<PolicySrcDTO> gePolicySrcDTOs(int src) {
		Set<PolicySrcDTO> policySrcDTOs = new HashSet<PolicySrcDTO>();
		PolicySrcDTO policySrcDTO = null;
		
		List<PolicySrcEntity> policySrcEntities = commonDAO.getPolicies(src);
		
		for(PolicySrcEntity policySrcEntity : policySrcEntities)
		{
			policySrcDTO = new PolicySrcDTO();
			policySrcDTO.setPolicyId(policySrcEntity.getPolicyId());
			policySrcDTO.setPolicyName(policySrcEntity.getPolicyConfigEntity().getPolicyName());
			policySrcDTO.setCmsVal(policySrcEntity.getPolicyConfigEntity().getFederal());
			policySrcDTO.setVitaVal(policySrcEntity.getPolicyConfigEntity().getState());
			policySrcDTO.setPolicyDesc(policySrcEntity.getPolicyDesc());
			policySrcDTO.setCustomVal(policySrcEntity.getCustomVal());
			policySrcDTOs.add(policySrcDTO);
			System.out.println(policySrcEntity.getPolicyConfigEntity().getPolicyName() +" "+policySrcDTO.getPolicyName());
		}
		
		return policySrcDTOs;
	}

	@Override
	@Transactional
	public void updateIdentityCMSPolicies(int src, String policy) {
		commonDAO.updateIdentityCMSPolicies(src, policy);
	}

	@Override
	@Transactional
	public void addPolicy(Set<PolicySrcDTO> policySrcDTOs) {
		PolicyConfigEntity policyConfigEntity ;  
		PolicySrcEntity policySrcEntity;
		for(PolicySrcDTO policySrcDTO : policySrcDTOs)
		{
			Integer srcId = commonDAO.getMaxPolicySrcId();
			PolicyGrpEntity policyGrpEntity =  commonDAO.getPolicyGrpEntityOnId(policySrcDTO.getPolicyGrpId());
			policyConfigEntity =  commonDAO.getPolicy(policySrcDTO.getPolicyName());
			policySrcEntity = new PolicySrcEntity();
			policySrcEntity.setPolicyId(srcId+1);
			policySrcEntity.setPolicyConfigEntity(policyConfigEntity);
			policySrcEntity.setPolicyName(policySrcDTO.getPolicyName());
			policySrcEntity.setPolicyDesc(policySrcDTO.getPolicyDesc());
			policySrcEntity.setCustomVal(policySrcDTO.getCustomVal());
			policySrcEntity.setPolicyGrpEntity(policyGrpEntity);
			policySrcEntity.setModifiedBy(policySrcDTO.getCreatedBy());
			policySrcEntity.setCreatedBy(policySrcDTO.getCreatedBy());
			policySrcEntity.setNotes(policySrcDTO.getNotes());
			policySrcEntity.setCreatedTs(new Date());
			policySrcEntity.setModifiedTs(new Date());
			policySrcEntity.setFileContent(DatatypeConverter.parseBase64Binary(policySrcDTO.getPolicyFileContent()));
			//Base64.getDecoder().decode(encoded);
			commonDAO.savePolicySrc(policySrcEntity);
			
			
			/*PolicyGrpEntity policyGrpEntity =commonDAO.getPolicyGrpEntity(policyConfigDTO.getGrpName());
			policySrcEntity.setPolicyId(srcId+1);
			policySrcEntity.setPolicyName(policyConfigDTO.getPolicyName());
			policySrcEntity.setPolicyDesc(policyConfigDTO.getDescription());
			Set<PolicySrcEntity> policyGrpEntities = new HashSet<PolicySrcEntity>();
			policyGrpEntities.add(policySrcEntity);
			//policyGrpEntity.setPolicySrcTbs(policyGrpEntities);
			policySrcEntity.setPolicyGrpEntity(policyGrpEntity);
			commonDAO.savePolicySrc(policyGrpEntity);
			
			policyConfigEntity = new PolicyConfigEntity();
			policyConfigEntity.setPolicyName(policyConfigDTO.getPolicyName());
			policyConfigEntity.setValue(policyConfigDTO.getValue());
			policyConfigEntity.setNotes(policyConfigDTO.getNotes());
			policyConfigEntity.setModifiedTs(new Date());
			policyConfigEntity.setCreatedTs(new Date());
			commonDAO.savePolicyConfig(policyConfigEntity);*/
			//commonDAO.updatePolicy(policyConfigEntity);
			
			/*policyConfigEntity = commonDAO.getPolicy(policyConfigDTO.getPolicyName());
			if(policyConfigEntity != null && !policyConfigEntity.getValue().equals(policyConfigDTO.getValue()))
			{
				System.out.println("policyConfigDTO.getPolicyName() " +policyConfigDTO.getPolicyName());
				policyConfigEntity.setPolicyName(policyConfigDTO.getPolicyName());
				policyConfigEntity.setValue(policyConfigDTO.getValue());
				policyConfigEntity.setNotes(policyConfigDTO.getNotes());
				policyConfigEntity.setModifiedTs(new Date());
				commonDAO.updatePolicy(policyConfigEntity);
			}*/
			//CacheUtil.getInstance().putCache(policyConfigDTO.getPolicyName(), policyConfigDTO.getValue());
		}
		
	}

	@Override
	@Transactional
	public Set<DepartmentDTO> getDepartments() {
		
		List<DeptEntity> deptEntities = commonDAO.getDepartments();
		Set<DepartmentDTO> departmentDTOs = new HashSet<DepartmentDTO>();
		DepartmentDTO departmentDTO;
		for(DeptEntity deptEntity : deptEntities)
		{
			departmentDTO = new DepartmentDTO();
			departmentDTO.setDeptId(deptEntity.getDeptId());
			departmentDTO.setDeptName(deptEntity.getDeptName());
			departmentDTOs.add(departmentDTO);
		}
		
		return departmentDTOs;
	}

	@Override
	@Transactional
	public Set<SystemDTO> getDeptSystems(int deptId) {
		
		List<SystemEntity> systemEntities  = commonDAO.getSystems(deptId);
		Set<SystemDTO> systemDTOs = new HashSet<SystemDTO>();
		SystemDTO systemDTO;
		for(SystemEntity systemEntity: systemEntities)
		{
			systemDTO = new SystemDTO();
			systemDTO.setSystemId(systemEntity.getSystemId());
			systemDTO.setSystemName(systemEntity.getSystemName());
			systemDTOs.add(systemDTO);
		}
		
		return systemDTOs;
		
	}

	@Override
	@Transactional
	public Set<RolesDTO> getSystemRoles(int systemId) {
		List<RolesEntity> rolesEntities = commonDAO.getRoles(systemId);
		Set<RolesDTO> rolesDTOs = new HashSet<RolesDTO>();
		RolesDTO rolesDTO;
		for(RolesEntity rolesEntity : rolesEntities)
		{
			rolesDTO = new RolesDTO();
			rolesDTO.setRoleId(rolesEntity.getRoleId());
			rolesDTO.setRoleName(rolesEntity.getRoleName());
			rolesDTOs.add(rolesDTO);
		}
		return rolesDTOs;
	}

	@Override
	@Transactional
	public Set<PolicySrcDTO> gePolicySrcDTOs() {
		Set<PolicySrcDTO> policySrcDTOs = new HashSet<PolicySrcDTO>();
		PolicySrcDTO policySrcDTO = null;
		PolicyGrpDTO policyGrpDTO = null;
		
		List<PolicySrcEntity> policySrcEntities = commonDAO.getDefaultPolicies();
		for(PolicySrcEntity policySrcEntity : policySrcEntities)
		{
			policySrcDTO = new PolicySrcDTO();
			policyGrpDTO = new PolicyGrpDTO();
			System.out.println(policySrcEntity.getPolicyName());
			policySrcDTO.setPolicyName(policySrcEntity.getPolicyName());
			if(policySrcEntity.getVitaVal() != null)
				policySrcDTO.setCmsVal(policySrcEntity.getCmsVal());
			else 
				policySrcDTO.setCmsVal("");
			if(policySrcEntity.getVitaVal() != null)
				policySrcDTO.setVitaVal(policySrcEntity.getVitaVal());
			else
				policySrcDTO.setVitaVal("");
			policyGrpDTO.setPolicyGrpName(policySrcEntity.getPolicyGrpEntity().getPolicyGrpName());
			policySrcDTO.setPolicyGrpDTO(policyGrpDTO);
			policySrcDTO.setPolicyId(policySrcEntity.getPolicyId());
			policySrcDTOs.add(policySrcDTO);
		}
		
		return policySrcDTOs;
	}

	@Override
	@Transactional
	public Set<PolicyGrpDTO> getPolicyGroup(int userType) {
		List<PolicyGrpEntity> policyGrpEntities = commonDAO.getPolicyGrpEntity(userType);
		Set<PolicyGrpDTO> policyGrpDTOs = new HashSet<PolicyGrpDTO>(0);
		PolicyGrpDTO policyGrpDTO = null;
		for(PolicyGrpEntity policyGrpEntity : policyGrpEntities)
		{
			policyGrpDTO = new PolicyGrpDTO();
			policyGrpDTO.setPolicyGrpId(policyGrpEntity.getPolicyGrpId());
			policyGrpDTO.setPolicyGrpName(policyGrpEntity.getPolicyGrpName());
			policyGrpDTOs.add(policyGrpDTO);
		}
		return policyGrpDTOs;
	}

	@Override
	@Transactional
	public Set<UserTypeDTO> getUserTypes() {
		List<UserTypeEntity> userTypeEntities = commonDAO.getUserTypes();
		Set<UserTypeDTO> userTypeDTOs = new HashSet<UserTypeDTO>(0);
		UserTypeDTO userTypeDTO;
		for(UserTypeEntity userTypeEntity : userTypeEntities)
		{
			userTypeDTO = new UserTypeDTO();
			userTypeDTO.setUserTypeId(userTypeEntity.getUserTypeId());
			userTypeDTO.setTypeName(userTypeEntity.getTypeName());
			userTypeDTOs.add(userTypeDTO);
		}
		return userTypeDTOs;
	}

	@Override
	@Transactional
	public void addNewPolicyDTO(AddNewPolicyDTO addNewPolicyDTO) {
		PolicySrcEntity policySrcEntity = null;
		PolicyConfigEntity policyConfigEntity = null;
		Set<PolicySrcEntity> policySrcEntities = new HashSet<PolicySrcEntity>(0);
		PolicyGrpEntity policyGrpEntity = new PolicyGrpEntity();
		policyGrpEntity.setPolicyGrpName(addNewPolicyDTO.getPolicyName());
		policyGrpEntity.setDescription(addNewPolicyDTO.getPolicyDescription());
		policyGrpEntity.setUserTypeEntity(commonDAO.getUserType(addNewPolicyDTO.getPolicyGrpId()));
		policyGrpEntity.setCreatedBy(addNewPolicyDTO.getCreatedBy());
		policyGrpEntity.setCreatedTs(new Date());
		//commonDAO.savePolicyGrpEntity(policyGrpEntity);
		//policyGrpEntity = commonDAO.getPolicyGrpEntity(addNewPolicyDTO.getPolicyName());
		
		for(int index = 0 ; index < addNewPolicyDTO.getNewPolicyDetails().size(); index++)
		{
			policySrcEntity =  new PolicySrcEntity();
			System.out.println(addNewPolicyDTO.getPolicyName());
			policyConfigEntity =  commonDAO.getPolicy(addNewPolicyDTO.getNewPolicyDetails().get(index).getPolicyId());
			if(policyConfigEntity != null)
			System.out.println(policyConfigEntity.getPolicyConfigId());
			policySrcEntity.setPolicyConfigEntity(policyConfigEntity);
			policySrcEntity.setCreatedBy(addNewPolicyDTO.getCreatedBy());
			policySrcEntity.setCreatedTs(new Date());
			policySrcEntity.setModifiedBy(addNewPolicyDTO.getCreatedBy());
			policySrcEntity.setModifiedTs(new Date());
			
			policySrcEntity.setCustomVal(addNewPolicyDTO.getNewPolicyDetails().get(index).getPolicyValue());
			policySrcEntity.setPolicyName(addNewPolicyDTO.getNewPolicyDetails().get(index).getPolicyId());
			policySrcEntity.setPolicyGrpEntity(policyGrpEntity);
			policySrcEntities.add(policySrcEntity);
			System.out.println(policySrcEntity.getPolicyConfigEntity().getPolicyName());
		}
		policyGrpEntity.setPolicySrcEntities(policySrcEntities);
		commonDAO.savePolicyGrpEntity(policyGrpEntity);
		
	}

	@Override
	@Transactional
	public Integer addOnboardApproval(OnboardingApprovalDTO onboardingApprovalDTO) {
		RolesEntity rolesEntity = commonDAO.getRoleById(onboardingApprovalDTO.getRoleId());
		UserEntity  userEntity = userDAO.getUser(userDAO.getUserIdOnEmail(onboardingApprovalDTO.getEmailId()));;
		List<OnboardingApprovalEntity> onboardingApprovalEntities = commonDAO.getOnboardApproval(onboardingApprovalDTO.getRoleId());
		for(OnboardingApprovalEntity onboardingApprovalEntity : onboardingApprovalEntities)
		{
			if(onboardingApprovalEntity.getLevel().intValue() == onboardingApprovalDTO.getLevel().intValue() && 
					onboardingApprovalDTO.getApprovalType().intValue() == onboardingApprovalEntity.getApprovalType().intValue())
			{
				if(onboardingApprovalDTO.isOverWrite())
				{
					
					List<OnboardApprovalPendingEntity> onboardingApprovalEntities2 = userDAO.fetchUserPendingRequest(onboardingApprovalEntity.getUserEntity().getId());
					for(OnboardApprovalPendingEntity onboardApprovalPendingEntity : onboardingApprovalEntities2)
					{
						onboardApprovalPendingEntity.setUserEntity(userEntity);
						commonDAO.saveOnboardApprovalPendingEntity(onboardApprovalPendingEntity);
						
					}
					onboardingApprovalEntity.setActiveStatus(false);
					commonDAO.updateOnboardingApprovalEntity(onboardingApprovalEntity);
					/*for(OnboardingApprovalEntity onboardingApprovalEntity2 : onboardingApprovalEntities2)
					{
						onboardingApprovalEntity2.setUserEntity(userEntity);
					}*/
				}
				else
					return 1;
			}
				
		}
		 
		OnboardingApprovalEntity onboardingApprovalEntity = new OnboardingApprovalEntity();
		onboardingApprovalEntity.setRolesEntity(rolesEntity);
		onboardingApprovalEntity.setUserEntity(userEntity);
		onboardingApprovalEntity.setLevel(onboardingApprovalDTO.getLevel());
		onboardingApprovalEntity.setSla(onboardingApprovalDTO.getSla());
		onboardingApprovalEntity.setApprovalType(onboardingApprovalDTO.getApprovalType());
		onboardingApprovalEntity.setActiveStatus(true);
		commonDAO.saveOnboardingApprovalEntity(onboardingApprovalEntity);
		return 0;
		
	}

	@Override
	@Transactional
	public int addRole(AddRoleReq roleReq) {
		RolesEntity rolesEntity = new RolesEntity();
		rolesEntity.setRoleName(roleReq.getRoleName());
		rolesEntity.setSystemEntity(commonDAO.getSystemEntity(roleReq.getDeptId()));
		rolesEntity.setCreatedBy(roleReq.getUserId());
		rolesEntity.setCreatedTs(new Date());
		rolesEntity.setUpdatedBy(roleReq.getUserId());
		rolesEntity.setUpdatedTs(new Date());
		if(roleReq.getFileContent() != null)
			rolesEntity.setFileContent(DatatypeConverter.parseBase64Binary(roleReq.getFileContent()));
		commonDAO.saveRolesEntity(rolesEntity);
		return 0;
	}

	@Override
	@Transactional
	public List<ContractCompayDTO> getContractCompanyList() {
		
		List<ContractCompanyEntity> companyEntities = commonDAO.getContractCompanyList();
		ContractCompayDTO contractCompayDTO = null;
		List<ContractCompayDTO> contractCompayDTOs = new ArrayList<ContractCompayDTO>(0);
		for(ContractCompanyEntity companyEntity : companyEntities)
		{
			contractCompayDTO = new ContractCompayDTO();
			contractCompayDTO.setCompanyId(companyEntity.getContractCompanyId());
			contractCompayDTO.setName(companyEntity.getCompanyName());
			contractCompayDTOs.add(contractCompayDTO);
		}
		
		return contractCompayDTOs;
		
	}


}
