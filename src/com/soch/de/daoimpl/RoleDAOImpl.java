package com.soch.de.daoimpl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.soch.de.dao.RoleDAO;
import com.soch.de.domain.ExternalSourceRoleEntity;
import com.soch.de.domain.PolicyConfigEntity;

@Component
public class RoleDAOImpl implements RoleDAO{
	
	@Autowired
    private SessionFactory sessionFactory;
	
	@Override
	public ExternalSourceRoleEntity getExternalRole(String roleID) {
		List<ExternalSourceRoleEntity> externalSourceRoleEntities = null;
		ExternalSourceRoleEntity externalSourceRoleEntity = null;
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(ExternalSourceRoleEntity.class);
		criteria.add(Restrictions.eq("externalSourceRoleId", roleID));
		externalSourceRoleEntities = criteria.list();
		if(externalSourceRoleEntities.size() > 0)
		{
			externalSourceRoleEntity = externalSourceRoleEntities.get(0);
		}
		return externalSourceRoleEntity;
	}

}
