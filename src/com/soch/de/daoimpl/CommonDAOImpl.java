package com.soch.de.daoimpl;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.soch.de.dao.CommonDAO;
import com.soch.de.domain.ContractCompanyEntity;
import com.soch.de.domain.DeptEntity;
import com.soch.de.domain.DocumentsEntity;
import com.soch.de.domain.OnboardApprovalPendingEntity;
import com.soch.de.domain.OnboardingApprovalEntity;
import com.soch.de.domain.OnboardingUserNotesEntity;
import com.soch.de.domain.PolicyConfigEntity;
import com.soch.de.domain.PolicyGrpEntity;
import com.soch.de.domain.PolicySrcEntity;
import com.soch.de.domain.PolicySrcNotesEntity;
import com.soch.de.domain.RoleMappingEntity;
import com.soch.de.domain.RolesEntity;
import com.soch.de.domain.SystemEntity;
import com.soch.de.domain.UserFileEntity;
import com.soch.de.domain.UserNotesEntity;
import com.soch.de.domain.UserTypeEntity;
import com.soch.de.svc.constants.APPConstants;

@Component
public class CommonDAOImpl implements CommonDAO{
	
	@Autowired
    private SessionFactory sessionFactory;

	@Override
	public List<PolicyConfigEntity> getAllPolicies() {
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(PolicyConfigEntity.class);
		criteria.addOrder(Order.asc("policyName"));
		List<PolicyConfigEntity> entities = criteria.list();
/*		for(PolicyConfigEntity policyConfigEntity : entities)
			this.sessionFactory.getCurrentSession().evict(policyConfigEntity);*/
		return entities;
	}

	@Override
	public void updatePolicy(PolicyConfigEntity policyConfigEntity) {
		// TODO Auto-generated method stub
		this.sessionFactory.getCurrentSession().saveOrUpdate(policyConfigEntity);
	}

	@Override
	public PolicyConfigEntity getPolicy(String name) {
		PolicyConfigEntity policyConfigEntity = null;
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(PolicyConfigEntity.class);
		criteria.add(Restrictions.eq("policyName", name));
		List<PolicyConfigEntity> entities = criteria.list();
		if(!entities.isEmpty())
			 policyConfigEntity = entities.get(0);
		return policyConfigEntity;
	}

	@Override
	public List<PolicySrcEntity> getDefaultPolicies() {
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(PolicySrcEntity.class);
		criteria.addOrder(Order.asc("policyName"));
		List<PolicySrcEntity> entities = criteria.list();
		return entities;
	}

	@Override
	public void updateIdentityCMSPolicies(int src, String policy) {
		
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(PolicySrcEntity.class);
		criteria.add(Restrictions.eq("policyGrpEntity.policyGrpId", src));
		
		List<PolicySrcEntity> policySrcEntities = criteria.list();
		for(PolicySrcEntity policySrcEntity : policySrcEntities)
		{
			if(policy.equalsIgnoreCase("CMS"))
				policySrcEntity.setCustomVal(getPolicy(policySrcEntity.getPolicyName()).getFederal());
			else if(policy.equalsIgnoreCase("VITA"))
				policySrcEntity.setCustomVal(getPolicy(policySrcEntity.getPolicyName()).getState());
			policySrcEntity.setCreatedTs(new Date());
			policySrcEntity.setModifiedTs(new Date());
			this.sessionFactory.getCurrentSession().saveOrUpdate(policySrcEntity);
		}
		
		/*if(src.equalsIgnoreCase("Identity"))
			criteria.add(Restrictions.eq("policyGrpEntity.policyGrpId", APPConstants.IDENTITY_GRP_ID));
		else if(src.equalsIgnoreCase("Access"))
			criteria.add(Restrictions.eq("policyGrpEntity.policyGrpId", APPConstants.ACCESS_GRP_ID));
		
		
		@SuppressWarnings("unchecked")
		List<PolicySrcEntity> entities = criteria.list();
		PolicyConfigEntity policyConfigEntity = null;
		for(PolicySrcEntity policySrcEntity : entities)
		{
			policyConfigEntity = new PolicyConfigEntity();
			policyConfigEntity.setPolicyName(policySrcEntity.getPolicyName());
			if(policy.equalsIgnoreCase("CMS"))
				policyConfigEntity.setValue(policySrcEntity.getCmsVal());
			if(policy.equalsIgnoreCase("VITA"))
				policyConfigEntity.setValue(policySrcEntity.getVitaVal());
			policyConfigEntity.setCreatedTs(new Date());
			policyConfigEntity.setModifiedTs(new Date());
			this.sessionFactory.getCurrentSession().saveOrUpdate(policyConfigEntity);
		}*/
	}

	@Override
	public void savePolicySrc(PolicyGrpEntity policySrcEntity) {
		// TODO Auto-generated method stub
		this.sessionFactory.getCurrentSession().save(policySrcEntity);
	}

	@Override
	public void savePolicyConfig(PolicyConfigEntity policyConfigEntity) {
		// TODO Auto-generated method stub
		this.sessionFactory.getCurrentSession().saveOrUpdate(policyConfigEntity);
	}

	@Override
	public Integer getMaxPolicyGRPId() {
		Criteria maxIdCriteria =  this.sessionFactory.getCurrentSession().createCriteria(PolicyGrpEntity.class)
			    .setProjection( Projections.max("id") );
		return (Integer) maxIdCriteria.list().get(0);
	}

	@Override
	public Integer getMaxPolicySrcId() {
		Criteria maxIdCriteria =  this.sessionFactory.getCurrentSession().createCriteria(PolicySrcEntity.class)
			    .setProjection( Projections.max("id") );
		return (Integer) maxIdCriteria.list().get(0);
	}

	@Override
	public PolicyGrpEntity getPolicyGrpEntity(String policyName) {
		// TODO Auto-generated method stub
		Criteria criteria =  this.sessionFactory.getCurrentSession().createCriteria(PolicyGrpEntity.class);
				criteria.add(Restrictions.eq("policyGrpName", policyName));
		return (PolicyGrpEntity) criteria.list().get(0);
	}
	
	@Override
	public List<PolicyGrpEntity> getPolicyGrpEntity(int  userType) {
		// TODO Auto-generated method stub
		Criteria criteria =  this.sessionFactory.getCurrentSession().createCriteria(PolicyGrpEntity.class);
				criteria.add(Restrictions.eq("userTypeEntity.userTypeId", userType));
		return criteria.list();
	}

	@Override
	public List<DeptEntity> getDepartments() {
		Criteria criteria =  this.sessionFactory.getCurrentSession().createCriteria(DeptEntity.class);
		
		return criteria.list();
	}
	

	@Override
	public List<SystemEntity> getSystems(int deptId) {
		Criteria criteria =  this.sessionFactory.getCurrentSession().createCriteria(SystemEntity.class);
		if(deptId != 0)
			criteria.add(Restrictions.eq("deptEntity.deptId", deptId));
		return criteria.list();
	}

	@Override
	public List<RolesEntity> getRoles(int systemId) {
		Criteria criteria =  this.sessionFactory.getCurrentSession().createCriteria(RolesEntity.class);
		criteria.add(Restrictions.eq("systemEntity.systemId", systemId));
		return criteria.list();
	}

	@Override
	public RolesEntity getRoleById(int rileId) {
		Criteria criteria =  this.sessionFactory.getCurrentSession().createCriteria(RolesEntity.class);
		criteria.add(Restrictions.eq("roleId", rileId));
		return (RolesEntity) criteria.list().get(0);
	}

	@Override
	public List<OnboardingApprovalEntity> getOnboardApproval(int roleId) {
		Criteria criteria =  this.sessionFactory.getCurrentSession().createCriteria(OnboardingApprovalEntity.class);
		criteria.addOrder(Order.asc("level"));
		criteria.add(Restrictions.eq("rolesEntity.roleId", roleId));
		criteria.add(Restrictions.eq("activeStatus", true));
		return criteria.list();
	}

	@Override
	public void saveOnboardApprovalPendingEntity(OnboardApprovalPendingEntity onboardApprovalPendingEntity)
	{
			this.sessionFactory.getCurrentSession().saveOrUpdate(onboardApprovalPendingEntity);
	}

	@Override
	public void saveOnboardingNotes(OnboardingUserNotesEntity onboardingUserNotesEntity) {
		this.sessionFactory.getCurrentSession().saveOrUpdate(onboardingUserNotesEntity);
		
	}

	@Override
	public void insertUserFile(UserFileEntity userFileEntity) {
		
		this.sessionFactory.getCurrentSession().saveOrUpdate(userFileEntity);
	}

	@Override
	public List<PolicySrcEntity> getPolicies(int source) {
		Criteria criteria =  this.sessionFactory.getCurrentSession().createCriteria(PolicySrcEntity.class);
		criteria.add(Restrictions.eq("policyGrpEntity.policyGrpId", source));
		return criteria.list();
	}

	@Override
	public void updatePolicy(PolicySrcEntity policySrcEntity) {
		this.sessionFactory.getCurrentSession().saveOrUpdate(policySrcEntity);
		
	}

	@Override
	public PolicySrcEntity getPolicySrcEntity(int policyId) {
		Criteria criteria =  this.sessionFactory.getCurrentSession().createCriteria(PolicySrcEntity.class);
		criteria.add(Restrictions.eq("policyId", policyId));
		return (PolicySrcEntity) criteria.list().get(0);
	}

	@Override
	public UserTypeEntity getUserType(Integer userTypeId) {
		Criteria criteria =  this.sessionFactory.getCurrentSession().createCriteria(UserTypeEntity.class);
		criteria.add(Restrictions.eq("userTypeId", userTypeId));
		return (UserTypeEntity) criteria.list().get(0);
	}
	
	@Override
	public List<UserTypeEntity> getUserTypes() {
		Criteria criteria =  this.sessionFactory.getCurrentSession().createCriteria(UserTypeEntity.class);
		return criteria.list();
	}

	@Override
	public PolicyGrpEntity getPolicyGrpEntityOnId(Integer policyGrpId) {
		// TODO Auto-generated method stub
				Criteria criteria =  this.sessionFactory.getCurrentSession().createCriteria(PolicyGrpEntity.class);
						criteria.add(Restrictions.eq("policyGrpId", policyGrpId));
		return (PolicyGrpEntity) criteria.list().get(0);
		
	}

	@Override
	public void savePolicySrc(PolicySrcEntity policySrcEntity) {
		this.sessionFactory.getCurrentSession().saveOrUpdate(policySrcEntity);
		
	}

	@Override
	public void savePolicyGrpEntity(PolicyGrpEntity policyGrpEntity) {
		this.sessionFactory.getCurrentSession().saveOrUpdate(policyGrpEntity);
		
	}

	@Override
	public void saveOnboardingApprovalEntity(OnboardingApprovalEntity onboardingApprovalEntity) {
		this.sessionFactory.getCurrentSession().save(onboardingApprovalEntity);
		
	}

	@Override
	public void updateOnboardingApprovalEntity(OnboardingApprovalEntity onboardingApprovalEntity) {
		this.sessionFactory.getCurrentSession().update(onboardingApprovalEntity);
		
	}

	@Override
	public SystemEntity getSystemEntity(int systemId) {
		Criteria criteria =  this.sessionFactory.getCurrentSession().createCriteria(SystemEntity.class);
		criteria.add(Restrictions.eq("systemId", systemId));
		return (SystemEntity)criteria.list().get(0);
	}

	@Override
	public void saveRolesEntity(RolesEntity rolesEntity) {
		this.sessionFactory.getCurrentSession().save(rolesEntity);
		
	}

	@Override
	public List<ContractCompanyEntity> getContractCompanyList() {
		Criteria criteria =  this.sessionFactory.getCurrentSession().createCriteria(ContractCompanyEntity.class);
		return criteria.list();
		
	}

	@Override
	public ContractCompanyEntity getContractCompanyEntity(int companyName) {
		
		Criteria criteria =  this.sessionFactory.getCurrentSession().createCriteria(ContractCompanyEntity.class);
		criteria.add(Restrictions.eq("contractCompanyId", companyName));
		return (ContractCompanyEntity) criteria.list().get(0);
	}

	@Override
	public  List<OnboardApprovalPendingEntity>  getAllPendingRequests() {
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(OnboardApprovalPendingEntity.class);
		criteria.add(Restrictions.eq("pendingApproval", true));
		List<OnboardApprovalPendingEntity> onboardApprovalPendingEntities = criteria.list();
		return onboardApprovalPendingEntities;
		
	}

	@Override
	public int getOnboardApprovalMaxLevel(Integer roleID) {
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(OnboardingApprovalEntity.class)
			    .setProjection(Projections.max("level"));
		criteria.add(Restrictions.eq("rolesEntity.roleId", roleID));
			Integer maxlevel = (Integer)criteria.uniqueResult();	
		return maxlevel;
	}

	@Override
	public OnboardingApprovalEntity getOnboardApproval(Integer roleID, int approveLevel) {
		Criteria criteria =  this.sessionFactory.getCurrentSession().createCriteria(OnboardingApprovalEntity.class);
		//criteria.addOrder(Order.asc("level"));
		criteria.add(Restrictions.eq("rolesEntity.roleId", roleID));
		criteria.add(Restrictions.eq("level", approveLevel));
		criteria.addOrder(Order.asc("approvalType"));
		criteria.add(Restrictions.eq("activeStatus", true));
		return (OnboardingApprovalEntity)criteria.list().get(0);
	}

	@Override
	public void saveuserNotes(UserNotesEntity userNotesEntity) {
		this.sessionFactory.getCurrentSession().save(userNotesEntity);
		
	}

	@Override
	public List<RoleMappingEntity> getRoleMapping() {
		Criteria criteria =  this.sessionFactory.getCurrentSession().createCriteria(RoleMappingEntity.class);
		return criteria.list();
	}

	@Override
	public void savePolicySrcNotes(PolicySrcNotesEntity policySrcNotesEntity) {
		this.sessionFactory.getCurrentSession().save(policySrcNotesEntity);
		
	}

	@Override
	public List<PolicySrcNotesEntity> getPolicyNotes(Integer policyId) {
		Criteria criteria =  this.sessionFactory.getCurrentSession().createCriteria(PolicySrcNotesEntity.class);
		criteria.add(Restrictions.eq("policySrcEntity.policyId", policyId));
		return criteria.list();
	}

	@Override
	public List<RolesEntity> getAllRoles() {
		Criteria criteria =  this.sessionFactory.getCurrentSession().createCriteria(RolesEntity.class);
		return criteria.list();
	}
	
	
	@Override
	public List<DocumentsEntity> getDashboardFiles() {
		Criteria criteria =  this.sessionFactory.getCurrentSession().createCriteria(DocumentsEntity.class);
		criteria.add(Restrictions.eq("activeFlag", true));
		return criteria.list();
	}

	@Override
	public DocumentsEntity getDashboardFiles(int id) {
		Criteria criteria =  this.sessionFactory.getCurrentSession().createCriteria(DocumentsEntity.class);
		criteria.add(Restrictions.eq("documentId", id));
		criteria.add(Restrictions.eq("activeFlag", true));
		List<DocumentsEntity> documentsEntities = criteria.list();
		DocumentsEntity documentsEntity = null;
		if(!documentsEntities.isEmpty())
		{
			documentsEntity = documentsEntities.get(0);
		}
			
		return documentsEntity;
	}

	@Override
	public void updatedDashboardFile(DocumentsEntity documentsEntity) {
		this.sessionFactory.getCurrentSession().update(documentsEntity);
		
	}

	@Override
	public int saveDashboardFile(DocumentsEntity documentsEntity) {
		this.sessionFactory.getCurrentSession().save(documentsEntity);
		return 0;
	}

	@Override
	public DeptEntity getDepartment(String departmentName) {
		Criteria criteria =  this.sessionFactory.getCurrentSession().createCriteria(DeptEntity.class);
		criteria.add(Restrictions.eq("deptName", departmentName));
		DeptEntity deptEntity =  (DeptEntity) criteria.list().get(0);
		return deptEntity;
	}
	

}
