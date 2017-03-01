package com.soch.uam.serviceimpl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soch.uam.dao.CommonDAO;
import com.soch.uam.domain.DeptEntity;
import com.soch.uam.domain.PolicyConfigEntity;
import com.soch.uam.domain.PolicyGrpEntity;
import com.soch.uam.domain.PolicySrcEntity;
import com.soch.uam.domain.RolesEntity;
import com.soch.uam.domain.SystemEntity;
import com.soch.uam.dto.DepartmentDTO;
import com.soch.uam.dto.PolicyConfigDTO;
import com.soch.uam.dto.PolicyGrpDTO;
import com.soch.uam.dto.PolicySrcDTO;
import com.soch.uam.dto.RolesDTO;
import com.soch.uam.dto.SystemDTO;
import com.soch.uam.service.CommonService;

@Service("commonService")
public class CommonServiceImpl implements CommonService{
	
	@Autowired
	private CommonDAO commonDAO ;

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
				if(policyConfigEntity.getValue() != null)
					policyConfigDTO.setValue(policyConfigEntity.getValue());
				policyConfigDTOs.add(policyConfigDTO);
			}
		}
		
		return policyConfigDTOs;
	}

	@Override
	@Transactional
	public void updatePolicies(Set<PolicyConfigDTO> policyConfigDTOs) {
		
		PolicyConfigEntity policyConfigEntity = null;
		for(PolicyConfigDTO policyConfigDTO : policyConfigDTOs)
		{
			policyConfigEntity = new PolicyConfigEntity();
			policyConfigEntity.setPolicyName(policyConfigDTO.getPolicyName());
			policyConfigEntity.setValue(policyConfigDTO.getValue());
			policyConfigEntity.setNotes(policyConfigDTO.getNotes());
			policyConfigEntity.setModifiedTs(new Date());
			commonDAO.updatePolicy(policyConfigEntity);
			
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
	public void updateIdentityCMSPolicies(String src, String policy) {
		commonDAO.updateIdentityCMSPolicies(src, policy);
	}

	@Override
	@Transactional
	public void addPolicy(Set<PolicyConfigDTO> policyConfigDTOs) {
		PolicyConfigEntity policyConfigEntity ;  
		PolicySrcEntity policySrcEntity;
		System.out.println(policyConfigDTOs.size());
		for(PolicyConfigDTO policyConfigDTO : policyConfigDTOs)
		{
			Integer grpId = commonDAO.getMaxPolicyGRPId();
			Integer srcId = commonDAO.getMaxPolicySrcId();
			policySrcEntity = new PolicySrcEntity();
			PolicyGrpEntity policyGrpEntity =commonDAO.getPolicyGrpEntity(policyConfigDTO.getGrpName());
			policySrcEntity.setPolicyId(srcId+1);
			policySrcEntity.setPolicyName(policyConfigDTO.getPolicyName());
			policySrcEntity.setPolicyDesc(policyConfigDTO.getDescription());
			Set<PolicySrcEntity> policyGrpEntities = new HashSet<PolicySrcEntity>();
			policyGrpEntities.add(policySrcEntity);
			policyGrpEntity.setPolicySrcTbs(policyGrpEntities);
			policySrcEntity.setPolicyGrpEntity(policyGrpEntity);
			commonDAO.savePolicySrc(policyGrpEntity);
			
			policyConfigEntity = new PolicyConfigEntity();
			policyConfigEntity.setPolicyName(policyConfigDTO.getPolicyName());
			policyConfigEntity.setValue(policyConfigDTO.getValue());
			policyConfigEntity.setNotes(policyConfigDTO.getNotes());
			policyConfigEntity.setModifiedTs(new Date());
			policyConfigEntity.setCreatedTs(new Date());
			commonDAO.savePolicyConfig(policyConfigEntity);
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


}
