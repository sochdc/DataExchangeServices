package com.soch.uam.dao;

import com.soch.uam.domain.ExternalSourceRoleEntity;

public interface RoleDAO {

	public ExternalSourceRoleEntity getExternalRole(String roleID);
}
