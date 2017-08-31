package com.soch.uam.serviceimpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.transaction.Transactional;
import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soch.uam.dao.CommonDAO;
import com.soch.uam.dao.RoleDAO;
import com.soch.uam.dao.UserDAO;
import com.soch.uam.domain.ContractCompanyEntity;
import com.soch.uam.domain.DeptEntity;
import com.soch.uam.domain.DocumentsEntity;
import com.soch.uam.domain.ExternalSourceRoleEntity;
import com.soch.uam.domain.OnboardApprovalAuditEntity;
import com.soch.uam.domain.OnboardApprovalPendingEntity;
import com.soch.uam.domain.OnboardingApprovalEntity;
import com.soch.uam.domain.PolicyConfigEntity;
import com.soch.uam.domain.PolicyGrpEntity;
import com.soch.uam.domain.PolicySrcEntity;
import com.soch.uam.domain.PolicySrcNotesEntity;
import com.soch.uam.domain.RolesEntity;
import com.soch.uam.domain.SystemEntity;
import com.soch.uam.domain.TempUserEntity;
import com.soch.uam.domain.UserEntity;
import com.soch.uam.domain.UserRoleEntity;
import com.soch.uam.domain.UserTypeEntity;
import com.soch.uam.dto.ContractCompayDTO;
import com.soch.uam.dto.DepartmentDTO;
import com.soch.uam.dto.DocumentsDTO;
import com.soch.uam.dto.ExternalSourceRoleDTO;
import com.soch.uam.dto.OnboardApprovalPendingDTO;
import com.soch.uam.dto.OnboardApprovedDTO;
import com.soch.uam.dto.OnboardingApprovalDTO;
import com.soch.uam.dto.PolicySrcNotesDTO;
import com.soch.uam.dto.RolesDTO;
import com.soch.uam.dto.RolesMappingDTO;
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
import com.soch.uam.response.CommonResp;
import com.soch.uam.response.ReportResp;
import com.soch.uam.response.RoleMappingResp;
import com.soch.uam.response.UAMBaseResp;
import com.soch.uam.service.CommonService;

@Service("commonService")
public class CommonServiceImpl implements CommonService{
	
	@Autowired
	private CommonDAO commonDAO ;
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private RoleDAO roleDAO;

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
			if(policySrcDTO.getPolicyDesc() != null)
			policySrcEntity.setPolicyDesc(policySrcDTO.getPolicyDesc());
			policySrcEntity.setModifiedBy(policySrcDTO.getCreatedBy());
			/*policyConfigEntity.setPolicyName(policyConfigDTO.getPolicyName());
			policyConfigEntity.setValue(policyConfigDTO.getValue());
			policyConfigEntity.setNotes(policyConfigDTO.getNotes());
			policyConfigEntity.setModifiedTs(new Date());*/
			PolicySrcNotesEntity policySrcNotesEntity = new PolicySrcNotesEntity();
			policySrcNotesEntity.setNotes(policySrcDTO.getNotes());
			policySrcNotesEntity.setCreatedBy(policySrcDTO.getCreatedBy());
			policySrcNotesEntity.setCreatedTs(new Date());
			policySrcNotesEntity.setPolicySrcEntity(policySrcEntity);
			Set<PolicySrcNotesEntity> policySrcNotesEntities = policySrcEntity.getPolicySrcNotesEntities();
					policySrcNotesEntities.add(policySrcNotesEntity);
			policySrcEntity.setPolicySrcNotesEntities(policySrcNotesEntities);
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
			if(policySrcEntity.getPolicyConfigEntity() != null)
			{
				policySrcDTO.setPolicyName(policySrcEntity.getPolicyConfigEntity().getPolicyName());
				policySrcDTO.setCmsVal(policySrcEntity.getPolicyConfigEntity().getFederal());
				policySrcDTO.setVitaVal(policySrcEntity.getPolicyConfigEntity().getState());
			}
			else
			{
				policySrcDTO.setPolicyName(policySrcEntity.getPolicyName());
			}
			policySrcDTO.setPolicyDesc(policySrcEntity.getPolicyDesc());
			policySrcDTO.setCustomVal(policySrcEntity.getCustomVal());
			
			policySrcDTO.setModifiedTs(policySrcEntity.getModifiedTs());
			UserEntity userEntity = userDAO.getUser(policySrcEntity.getModifiedBy());
			if(userEntity != null)
			policySrcDTO.setModifiedBy(userEntity.getFirstName()+" "+userEntity.getLastName());
			policySrcDTOs.add(policySrcDTO);
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
			if(policySrcDTO.getPolicyFileContent() != null)
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
		rolesEntity.setExternalSourceRoleEntity(roleDAO.getExternalRole(roleReq.getMapId()));
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

	@Override
	@Transactional
	public RoleMappingResp getRoleMapping() {
		List<RolesEntity> rolesEntities = commonDAO.getAllRoles();
		RolesMappingDTO rolesMappingDTO;
		Set<RolesMappingDTO> rolesMappingDTOs = new HashSet<RolesMappingDTO>(0);
		RoleMappingResp roleMappingResp = new RoleMappingResp();
		
		for(RolesEntity rolesEntity : rolesEntities)
		{
			rolesMappingDTO = new RolesMappingDTO();
			rolesMappingDTO.setIamRoleName(rolesEntity.getRoleName());
			rolesMappingDTO.setRoleId(new Integer(rolesEntity.getRoleId()).toString());
			if(rolesEntity.getExternalSourceRoleEntity() != null)
			{
				rolesMappingDTO.setOtherRoleName(rolesEntity.getExternalSourceRoleEntity().getRoleName());
				rolesMappingDTO.setOtherId(rolesEntity.getExternalSourceRoleEntity().getExternalSourceRoleId());
			}
			rolesMappingDTOs.add(rolesMappingDTO);
		}
		if(rolesMappingDTOs.size() > 0)
			roleMappingResp.setRolesMappingDTOs(rolesMappingDTOs);
		return roleMappingResp;
	}

	@Override
	@Transactional
	public void addPolicyNotes(PolicyConfigVO policyConfigVO) {
		PolicySrcNotesEntity policySrcNotesEntity = new PolicySrcNotesEntity();
		policySrcNotesEntity.setNotes(policyConfigVO.getNotes());
		policySrcNotesEntity.setCreatedBy(policyConfigVO.getPolicyNotesuserId());
		policySrcNotesEntity.setCreatedTs(new Date());
		policySrcNotesEntity.setPolicySrcEntity(commonDAO.getPolicySrcEntity(policyConfigVO.getPolicyNotesPolicyId()));
		commonDAO.savePolicySrcNotes(policySrcNotesEntity);
		
	}

	@Override
	@Transactional
	public Set<PolicySrcNotesDTO> getPolicyNotes(Integer policyId) {
		// TODO Auto-generated method stub
		List<PolicySrcNotesEntity> policySrcNotesEntities = commonDAO.getPolicyNotes(policyId);
		PolicySrcNotesDTO policySrcNotesDTO = null;
		Set<PolicySrcNotesDTO> policySrcNotesDTOs = new HashSet<PolicySrcNotesDTO>(0);		
		
		for(PolicySrcNotesEntity policySrcNotesEntity : policySrcNotesEntities)
		{
			policySrcNotesDTO = new PolicySrcNotesDTO();
			policySrcNotesDTO.setNotes(policySrcNotesEntity.getNotes());
			if(policySrcNotesEntity.getCreatedBy() != null)
			{
				UserEntity userEntity = userDAO.getUser(policySrcNotesEntity.getCreatedBy());
				policySrcNotesDTO.setCreatedBy(userEntity.getFirstName()+" "+userEntity.getLastName());
			}
			policySrcNotesDTO.setCreatedTs(policySrcNotesEntity.getCreatedTs());
			policySrcNotesDTOs.add(policySrcNotesDTO);
		}
		
		return policySrcNotesDTOs;
	}

	@Override
	@Transactional
	public ExternalSourceRoleDTO getExternalRole(String roleId) {
		ExternalSourceRoleDTO externalSourceRoleDTO = null;
		ExternalSourceRoleEntity externalSourceRoleEntity =  roleDAO.getExternalRole(roleId);
		if(externalSourceRoleEntity != null)
		{
			externalSourceRoleDTO = new ExternalSourceRoleDTO();
			externalSourceRoleDTO.setExternalRoleId(externalSourceRoleEntity.getExternalSourceRoleId());
			externalSourceRoleDTO.setExternalRoleName(externalSourceRoleEntity.getRoleName());
			externalSourceRoleDTO.setExternalRoleDesc(externalSourceRoleEntity.getDescription());
		}
		return externalSourceRoleDTO;
	}
	
	@Override
	@Transactional
	public CommonResp getDashboardFiles() {
		List<DocumentsEntity> documentsEntities = commonDAO.getDashboardFiles();
		List<DocumentsDTO> internalDocs = new ArrayList<DocumentsDTO>(0);
		List<DocumentsDTO> externalDocs = new ArrayList<DocumentsDTO>(0);
		List<DocumentsDTO> otherDocs = new ArrayList<DocumentsDTO>(0);
		List<DocumentsDTO> workPlanDocs = new ArrayList<DocumentsDTO>(0);
		
		DocumentsDTO documentsDTO = null;
		CommonResp commonResp = new CommonResp();
		
		for(DocumentsEntity documentsEntity : documentsEntities)
		{
			documentsDTO = new DocumentsDTO();
			documentsDTO.setDocumentId(documentsEntity.getDocumentId());
			documentsDTO.setName(documentsEntity.getName());
			documentsDTO.setType(documentsEntity.getType());
			documentsDTO.setUpdatedBy(documentsEntity.getUpdatedBy());
			if(documentsEntity.getUpdatedTs() != null)
			documentsDTO.setUpdatedDate(getLocalTime(documentsEntity.getUpdatedTs()));
			if(documentsEntity.getStartDate() != null)
				documentsDTO.setStartDate(getLocalTime(documentsEntity.getStartDate()));
			if(documentsEntity.getEndDate() != null)
				documentsDTO.setStartDate(getLocalTime(documentsEntity.getEndDate()));
			if(documentsEntity.getType().equalsIgnoreCase("internal"))
				internalDocs.add(documentsDTO);
			else if(documentsEntity.getType().equalsIgnoreCase("external"))
				externalDocs.add(documentsDTO);
			else if(documentsEntity.getType().equalsIgnoreCase("workplan"))
				workPlanDocs.add(documentsDTO);
			else 
				otherDocs.add(documentsDTO);
		}
		commonResp.setInternalDocs(internalDocs);
		commonResp.setExternalDocs(externalDocs);
		commonResp.setOtherDocs(otherDocs);
		commonResp.setWorkPlanDocs(workPlanDocs);
		return commonResp;
	}

	@Override
	@Transactional
	public String getFile(int id) {
		String content = null;
		
		DocumentsEntity documentsEntity = commonDAO.getDashboardFiles(id);
		if(documentsEntity != null)
		{
			content = DatatypeConverter.printBase64Binary(documentsEntity.getContent());
		}
		return content;
	}
	
	private String getLocalTime(Date date)
	{
		SimpleDateFormat sdf= new SimpleDateFormat("MM-dd-yyyy HH:mm");
		Calendar estDate = Calendar.getInstance();
		
		estDate.setTime(date);
		sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		return sdf.format(estDate.getTime());
	}

	@Override
	@Transactional
	public ReportResp retReport(ReportReq reportReq) {
		
		Date beginDate = null, endDate = null;
		
		ReportResp reportResp = new ReportResp();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			beginDate = sdf.parse(reportReq.getStartDate());
			if(reportReq.getEndDate() == null)
			{
				endDate = sdf.parse(sdf.format(new Date()));
			}
			else
			{
				endDate = sdf.parse(reportReq.getEndDate());
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		List<OnboardApprovalPendingEntity> onboardApprovalPendingEntities = userDAO.fetchUserPendingRequestsForReport(reportReq.getUserId(), beginDate, endDate);
		OnboardApprovalPendingDTO onboardApprovalPendingDTO = null;
		 List<OnboardApprovalPendingDTO> approvalPendingDTOs = new ArrayList<OnboardApprovalPendingDTO>(0);
		 
		for(OnboardApprovalPendingEntity approvalPendingEntity : onboardApprovalPendingEntities)
		{
			System.out.println(approvalPendingEntity.isPendingApproval()+""+approvalPendingEntity.getOnboardPendingId());
			if(approvalPendingEntity.isPendingApproval()) {
				onboardApprovalPendingDTO = new OnboardApprovalPendingDTO();
				onboardApprovalPendingDTO.setOnboardPendingId(approvalPendingEntity.getOnboardPendingId());
				onboardApprovalPendingDTO.setReqType(approvalPendingEntity.getRequestType());
				/*estDate.setTime(approvalPendingEntity.getRequestDate());
				sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
				System.out.println(estDate.getTime());
				sdf.format(estDate.getTime());*/
				onboardApprovalPendingDTO.setRequestDate(getLocalTime(approvalPendingEntity.getRequestDate()));
				onboardApprovalPendingDTO.setUserId(approvalPendingEntity.getUserEntity().getId());
				if(approvalPendingEntity.getTempUserEntity() != null )
				{
					onboardApprovalPendingDTO.setTempUserId(approvalPendingEntity.getTempUserEntity().getEmployeeId());
					onboardApprovalPendingDTO.setTempFname(approvalPendingEntity.getTempUserEntity().getFirstName());
					onboardApprovalPendingDTO.setTempLname(approvalPendingEntity.getTempUserEntity().getLastName());
					onboardApprovalPendingDTO.setPendingWith(approvalPendingEntity.getUserEntity().getFirstName()+" "+approvalPendingEntity.getUserEntity().getLastName());
					if(approvalPendingEntity.getTempUserEntity().getCreatedBy() != null)
					{
					UserEntity createdBy = userDAO.getUser(approvalPendingEntity.getTempUserEntity().getCreatedBy());
					onboardApprovalPendingDTO.setSubmittedBy(createdBy.getFirstName()+" "+createdBy.getLastName());
					}
				}
				else
					if(approvalPendingEntity.getRequestUserEntity()!=null)
					{
						onboardApprovalPendingDTO.setTempUserId(approvalPendingEntity.getRequestUserEntity().getId().toString());
						onboardApprovalPendingDTO.setTempFname(approvalPendingEntity.getRequestUserEntity().getFirstName());
						onboardApprovalPendingDTO.setTempLname(approvalPendingEntity.getRequestUserEntity().getLastName());
						onboardApprovalPendingDTO.setPendingWith(approvalPendingEntity.getUserEntity().getFirstName()+" "+approvalPendingEntity.getUserEntity().getLastName());
					}
				approvalPendingDTOs.add(onboardApprovalPendingDTO);
			}
			
		}
		
		reportResp.setPendingReqs(approvalPendingDTOs);
		
		List<OnboardApprovalAuditEntity> onboardApprovalAuditEntities  = userDAO.getApprovedRequestsForReport(reportReq.getUserId(),beginDate,endDate);
		OnboardApprovedDTO onboardApprovedDTO = null;
		List<OnboardApprovedDTO> onboardApprovedDTOs = new ArrayList<OnboardApprovedDTO>();
		TempUserEntity tempUserEntity = null;
		UserEntity approvedBy = null;
		
		if(!onboardApprovalAuditEntities.isEmpty())
		{
			for(OnboardApprovalAuditEntity onboardApprovalAuditEntity : onboardApprovalAuditEntities)
			{
				tempUserEntity = userDAO.getTempUser(onboardApprovalAuditEntity.getTempUserTbEmployeeId());
				onboardApprovedDTO = new OnboardApprovedDTO();
				onboardApprovedDTO.setTempUserId(tempUserEntity.getEmployeeId());
				onboardApprovedDTO.setTempFname(tempUserEntity.getFirstName());
				onboardApprovedDTO.setTempLname(tempUserEntity.getLastName());
				onboardApprovedDTO.setReqType("Onboarding");
				onboardApprovedDTO.setApprovedOn(getLocalTime(onboardApprovalAuditEntity.getAprovedOn()));
				approvedBy = userDAO.getUser(onboardApprovalAuditEntity.getUserTbId());
				onboardApprovedDTO.setApprovedBy(approvedBy.getFirstName()+" "+approvedBy.getLastName());
				if(tempUserEntity.getCreatedBy() != null)
				{
				UserEntity createdBy = userDAO.getUser(tempUserEntity.getCreatedBy());
				onboardApprovedDTO.setSubmittedBy(createdBy.getFirstName()+" "+createdBy.getLastName());
				}
				onboardApprovedDTOs.add(onboardApprovedDTO);
			}
			
		}
		reportResp.setApprovedReqs(onboardApprovedDTOs);
		
		 List<OnboardApprovalPendingDTO> createdReqDTOs = new ArrayList<OnboardApprovalPendingDTO>(0);
		 
		List<OnboardApprovalPendingEntity> createdReqEntities = userDAO.fetchUserRequestedByForReport(reportReq.getUserId(),beginDate,endDate);
		
		for(OnboardApprovalPendingEntity approvalPendingEntity : createdReqEntities)
		{
			System.out.println(approvalPendingEntity.isPendingApproval()+""+approvalPendingEntity.getOnboardPendingId());
			if(approvalPendingEntity.isPendingApproval()) {
				onboardApprovalPendingDTO = new OnboardApprovalPendingDTO();
				onboardApprovalPendingDTO.setOnboardPendingId(approvalPendingEntity.getOnboardPendingId());
				onboardApprovalPendingDTO.setReqType(approvalPendingEntity.getRequestType());
				/*estDate.setTime(approvalPendingEntity.getRequestDate());
				sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
				System.out.println(estDate.getTime());
				sdf.format(estDate.getTime());*/
				onboardApprovalPendingDTO.setRequestDate(getLocalTime(approvalPendingEntity.getRequestDate()));
				onboardApprovalPendingDTO.setUserId(approvalPendingEntity.getUserEntity().getId());
				if(approvalPendingEntity.getTempUserEntity() != null )
				{
					onboardApprovalPendingDTO.setTempUserId(approvalPendingEntity.getTempUserEntity().getEmployeeId());
					onboardApprovalPendingDTO.setTempFname(approvalPendingEntity.getTempUserEntity().getFirstName());
					onboardApprovalPendingDTO.setTempLname(approvalPendingEntity.getTempUserEntity().getLastName());
					onboardApprovalPendingDTO.setPendingWith(approvalPendingEntity.getUserEntity().getFirstName()+" "+approvalPendingEntity.getUserEntity().getLastName());
					if(approvalPendingEntity.getTempUserEntity().getCreatedBy() != null)
					{
					UserEntity createdBy = userDAO.getUser(approvalPendingEntity.getTempUserEntity().getCreatedBy());
					onboardApprovalPendingDTO.setSubmittedBy(createdBy.getFirstName()+" "+createdBy.getLastName());
					}
				}
				else
					if(approvalPendingEntity.getRequestUserEntity()!=null)
					{
						onboardApprovalPendingDTO.setTempUserId(approvalPendingEntity.getRequestUserEntity().getId().toString());
						onboardApprovalPendingDTO.setTempFname(approvalPendingEntity.getRequestUserEntity().getFirstName());
						onboardApprovalPendingDTO.setTempLname(approvalPendingEntity.getRequestUserEntity().getLastName());
						onboardApprovalPendingDTO.setPendingWith(approvalPendingEntity.getUserEntity().getFirstName()+" "+approvalPendingEntity.getUserEntity().getLastName());
					}
				createdReqDTOs.add(onboardApprovalPendingDTO);
			}
			
		}
		reportResp.setCreatedyBy(createdReqDTOs);
		return reportResp;
	}

	@Override
	@Transactional
	public int deleteFile(int fileId, int id) {
		DocumentsEntity documentsEntity = commonDAO.getDashboardFiles(fileId);
		documentsEntity.setActiveFlag(false);
		UserEntity userEntity = userDAO.getUser(id);
		documentsEntity.setUpdatedBy(userEntity.getFirstName()+" "+userEntity.getLastName());
		documentsEntity.setUpdatedTs(new Date());
		commonDAO.updatedDashboardFile(documentsEntity);
		return 1;
	}

	@Override
	@Transactional
	public int uploadDashboardFile(FileUploadReq fileUploadReq) {
		DocumentsEntity documentsEntity = new DocumentsEntity();
		
		String fileName = fileUploadReq.getFileName();
		String[] nameArray;
		if(fileName.contains("\\"))
		{
			nameArray = fileName.split("\\\\");
			fileName = nameArray[nameArray.length-1];
		}
		else
		{
			nameArray = fileName.split("/");
			fileName = nameArray[nameArray.length-1];
		}
		
		documentsEntity.setActiveFlag(true);
		documentsEntity.setType(fileUploadReq.getFileType());
		documentsEntity.setCreatedTs(new Date());
		documentsEntity.setUpdatedTs(new Date());
		UserEntity userEntity = userDAO.getUser(fileUploadReq.getCreatedBy());
		documentsEntity.setUpdatedBy(userEntity.getFirstName()+" "+userEntity.getLastName());
		documentsEntity.setCreatedBy(userEntity.getFirstName()+" "+userEntity.getLastName());
		documentsEntity.setName(fileName);
		documentsEntity.setContent(DatatypeConverter.parseBase64Binary(fileUploadReq.getFileContent().split("base64,")[1]));
		commonDAO.saveDashboardFile(documentsEntity);
		return 1;
	}

	@Override
	@Transactional
	public List<UserDTO> getDepartmentUsers(String departmentName) {
		
		List<UserDTO>  userDTOs = new ArrayList<>();
		UserDTO userDTO = null;
		UserEntity userEntity = null;
		DeptEntity deptEntity = commonDAO.getDepartment(departmentName);
		
		Set<SystemEntity> systemEntities = deptEntity.getSystemEntitys();
		
		for(SystemEntity systemEntity :systemEntities )
		{
			if(systemEntity.getSystemName().equalsIgnoreCase("COVAR"))
			{
				
				Set<RolesEntity> rolesEntities = systemEntity.getRolesEntitys();
				for(RolesEntity rolesEntity : rolesEntities)
				{
					Set<UserRoleEntity> userRoleEntities = rolesEntity.getUserRoleEntitys();
					for(UserRoleEntity userRoleEntity : userRoleEntities)
					{
						
						userEntity = userRoleEntity.getUserEntity();
						if(userEntity != null)
						{
							userDTO = new UserDTO();
							userDTO.setFirstName(userEntity.getFirstName());
							userDTO.setLastName(userEntity.getLastName());
							userDTO.setEmailId(userEntity.getEmailId());
							userDTOs.add(userDTO);
						}
					}
				}
			} 
		}
		return userDTOs;
	}

}
