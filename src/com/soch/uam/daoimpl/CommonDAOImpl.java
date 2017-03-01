package com.soch.uam.daoimpl;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.soch.uam.dao.CommonDAO;
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
import com.soch.uam.svc.constants.APPConstants;

@Component
public class CommonDAOImpl implements CommonDAO{
	
	@Autowired
    private SessionFactory sessionFactory;

	@Override
	public List<PolicyConfigEntity> getAllPolicies() {
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(PolicyConfigEntity.class);
		criteria.addOrder(Order.asc("policyName"));
		List<PolicyConfigEntity> entities = criteria.list();
		for(PolicyConfigEntity policyConfigEntity : entities)
			this.sessionFactory.getCurrentSession().evict(policyConfigEntity);
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
	public void updateIdentityCMSPolicies(String src, String policy) {
		
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(PolicySrcEntity.class);
		if(src.equalsIgnoreCase("Identity"))
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
		}
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
	public List<DeptEntity> getDepartments() {
		Criteria criteria =  this.sessionFactory.getCurrentSession().createCriteria(DeptEntity.class);
		
		return criteria.list();
	}

	@Override
	public List<SystemEntity> getSystems(int deptId) {
		Criteria criteria =  this.sessionFactory.getCurrentSession().createCriteria(SystemEntity.class);
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
	
	

}
