package com.soch.de.dao;

import java.util.Date;
import java.util.List;

import com.soch.de.domain.LoginEntity;
import com.soch.de.domain.OnboardApprovalPendingEntity;
import com.soch.de.domain.UserActivityEntity;

public interface ReportDAO {
	
	public List<LoginEntity> getActiveLogin();

	public List<UserActivityEntity> userActivityReport( int userId, String beginDate, String endDate);
	public List<LoginEntity> getInactiveUsersForReport();
}
