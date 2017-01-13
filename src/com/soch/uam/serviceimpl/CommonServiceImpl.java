package com.soch.uam.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soch.uam.dao.CommonDAO;
import com.soch.uam.domain.PolicyConfigEntity;
import com.soch.uam.domain.PolicyGrpEntity;
import com.soch.uam.domain.PolicySrcEntity;
import com.soch.uam.dto.PolicyConfigDTO;
import com.soch.uam.dto.PolicyGrpDTO;
import com.soch.uam.dto.PolicySrcDTO;
import com.soch.uam.service.CommonService;
import com.soch.uam.util.CacheUtil;

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
			policySrcDTO.setPolicyName(policySrcEntity.getPolicyName());
			policySrcDTO.setCmsVal(policySrcEntity.getCmsVal());
			policySrcDTO.setVitaVal(policySrcEntity.getVitaVal());
			policyGrpDTO.setPolicyGrpName(policySrcEntity.getPolicyGrpEntity().getPolicyGrpName());
			policySrcDTO.setPolicyGrpDTO(policyGrpDTO);
			policySrcDTO.setPolicyId(policySrcEntity.getPolicyId());
			policySrcDTOs.add(policySrcDTO);
		}
		
		return policySrcDTOs;
	}

	@Override
	@Transactional
	public void updateIdentityCMSPolicies() {
		commonDAO.updateIdentityCMSPolicies();
	}

	@Override
	@Transactional
	public void addPolicy(Set<PolicyConfigDTO> policyConfigDTOs) {
		PolicyConfigEntity policyConfigEntity ;  
		PolicySrcEntity policySrcEntity;
		System.out.println(policyConfigDTOs.size());
		for(PolicyConfigDTO policyConfigDTO : policyConfigDTOs)
		{
			System.out.println(policyConfigDTO.getPolicyName());
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
	
	

}
