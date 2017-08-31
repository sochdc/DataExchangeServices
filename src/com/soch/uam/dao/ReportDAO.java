package com.soch.uam.dao;

import java.util.Date;
import java.util.List;

import com.soch.uam.domain.LoginEntity;
import com.soch.uam.domain.OnboardApprovalPendingEntity;
import com.soch.uam.domain.UserActivityEntity;

public interface ReportDAO {
	
	public List<LoginEntity> getActiveLogin();

	public List<UserActivityEntity> userActivityReport( int userId, String beginDate, String endDate);
	public List<LoginEntity> getInactiveUsersForReport();
}
