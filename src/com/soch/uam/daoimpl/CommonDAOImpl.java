package com.soch.uam.daoimpl;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.soch.uam.dao.CommonDAO;
import com.soch.uam.domain.PolicyConfigEntity;
import com.soch.uam.domain.PolicyGrpEntity;
import com.soch.uam.domain.PolicySrcEntity;
import com.soch.uam.dto.PolicySrcDTO;
import com.soch.uam.svc.constants.APPConstants;

@Component
public class CommonDAOImpl implements CommonDAO{
	
	@Autowired
    private SessionFactory sessionFactory;

	@Override
	public List<PolicyConfigEntity> getAllPolicies() {
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(PolicyConfigEntity.class);
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
		List<PolicySrcEntity> entities = criteria.list();
		return entities;
	}

	@Override
	public void updateIdentityCMSPolicies() {
		
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(PolicySrcEntity.class);
		criteria.add(Restrictions.eq("policyGrpEntity.policyGrpId", APPConstants.IDENTITY_GRP_ID));
		List<PolicySrcEntity> entities = criteria.list();
		PolicyConfigEntity policyConfigEntity = null;
		for(PolicySrcEntity policySrcEntity : entities)
		{
			policyConfigEntity = new PolicyConfigEntity();
			policyConfigEntity.setPolicyName(policySrcEntity.getPolicyName());
			policyConfigEntity.setValue(policySrcEntity.getCmsVal());
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
	
	

}
