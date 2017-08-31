package com.soch.uam.service;

import java.util.Set;

import com.soch.uam.dto.UserLoginReport;
import com.soch.uam.request.UserReportReq;
import com.soch.uam.request.UserReportResp;

public interface ReportService {

	UserReportResp getUserLogin(UserReportReq userReportReq);

	UserReportResp fetchAciveLoginsSVC();

	UserReportResp userActivityReport(UserReportReq userReportReq);
	
	UserReportResp getInactiveUsers(int days);
	
	UserReportResp getUsersOnRole(int roleID);
}
