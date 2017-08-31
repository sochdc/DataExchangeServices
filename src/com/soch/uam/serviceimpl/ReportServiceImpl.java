package com.soch.uam.serviceimpl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soch.uam.dao.CommonDAO;
import com.soch.uam.dao.ReportDAO;
import com.soch.uam.dao.UserDAO;
import com.soch.uam.domain.LoginEntity;
import com.soch.uam.domain.PwdHistoryEntity;
import com.soch.uam.domain.RolesEntity;
import com.soch.uam.domain.UserActivityEntity;
import com.soch.uam.domain.UserEntity;
import com.soch.uam.domain.UserRoleEntity;
import com.soch.uam.dto.ActivityReportDTO;
import com.soch.uam.dto.PasswordResetReport;
import com.soch.uam.dto.UserDTO;
import com.soch.uam.dto.UserLoginReport;
import com.soch.uam.request.UserReportReq;
import com.soch.uam.request.UserReportResp;
import com.soch.uam.service.ReportService;
import com.soch.uam.svc.constants.APPConstants;

@Service("reportService")
public class ReportServiceImpl implements ReportService{
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private CommonDAO commonDAO;
	
	@Autowired
	private ReportDAO reportDAO;

	@Override
	@Transactional
	public UserReportResp getUserLogin(UserReportReq userReportReq) {
		UserEntity userEntity = userDAO.getUser(userReportReq.getUserId());
		Set<LoginEntity> loginEntities = null;
		Date beginDate = null;
		Date endDate = null;
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR , -45);
		if(userReportReq.getBeginDate() != null)
		{
			beginDate = convertStringtoDate(userReportReq.getBeginDate());
			beginDate.setHours(0);
			beginDate.setMinutes(0);
			beginDate.setSeconds(0);
		}
		else
			beginDate = cal.getTime();
		if(userReportReq.getEndDate() != null)
		{
			endDate =  convertStringtoDate(userReportReq.getEndDate());
			endDate.setHours(24);
			endDate.setMinutes(59);
			endDate.setSeconds(59);
		}
		else
			endDate =  beginDate;
		
		UserLoginReport userLoginReport = null;
		List<UserLoginReport> userLoginReports = null;
		UserReportResp userReportResp = null;
		UserDTO userDTO = null;
		Set<PwdHistoryEntity> pwdHistoryEntities = null;
		List<PasswordResetReport> passwordResetReports = null;
		PasswordResetReport passwordResetReport = null;
		Date previousDate = null;
		
		if(userEntity != null)
		{
			userReportResp = new UserReportResp();
			userDTO = new UserDTO();
			passwordResetReports = new ArrayList<PasswordResetReport>(0);
			userLoginReports = new ArrayList<UserLoginReport>(0);
			
			userDTO.setFirstName(userEntity.getFirstName());
			userDTO.setLastName(userEntity.getLastName());
			userDTO.setMiddleName(userEntity.getMiddleName());
			
			userDTO.setEmailId(userEntity.getUserId());
			userDTO.setDateOfBirth(userEntity.getDateOfBirth().toString());
			
			
			loginEntities = userEntity.getLogintEntity();
			for(LoginEntity loginEntity : loginEntities)
			{
				if((loginEntity.getLoginTs().before(endDate) || loginEntity.getLoginTs().equals(endDate))&& 
						(loginEntity.getLoginTs().after(beginDate) || loginEntity.getLoginTs().equals(beginDate)))
				{
					userLoginReport = new UserLoginReport();
					userLoginReport.setLoginDate(getLocalTime(loginEntity.getLoginTs()));
					if(loginEntity.getLogOutTs() != null)
						userLoginReport.setLogoutDate(getLocalTime(loginEntity.getLogOutTs()));
					else
					{
						long curTimeInMs= loginEntity.getLoginTs().getTime();
						
						userLoginReport.setLogoutDate(getLocalTime(new Date(curTimeInMs + (30 * 60000))));
					}
					userLoginReports.add(userLoginReport);
				}
			}
			
			pwdHistoryEntities = userEntity.getPwdHistoryEntities();
			System.out.println("pwdHistoryEntities "+pwdHistoryEntities.size());
			for(PwdHistoryEntity pwdHistoryEntity : pwdHistoryEntities)
			{
				 passwordResetReport = new PasswordResetReport();
				 passwordResetReport.setResetTimeStamp(convertDateToString(pwdHistoryEntity.getCreatedTs()));
				 passwordResetReport.setSpan(calculateSpan(previousDate, pwdHistoryEntity.getCreatedTs()));
				 passwordResetReports.add(passwordResetReport);
				 previousDate =  pwdHistoryEntity.getCreatedTs();
			}
			
			userReportResp.setUserDTO(userDTO);
			userReportResp.setPasswordResetReports(passwordResetReports);
			userReportResp.setUserLoginReports(userLoginReports);
		}
		
	return userReportResp;
	}
	
	private Date convertStringtoDate(String dateStr)
	{
		DateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
		Date date = null;
		try {
			date = format.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		System.out.println(date);
		return date;
	}
	
	private String convertDateToString (Date date)
	{

		DateFormat format = new SimpleDateFormat("MM/d/yyyy HH:mm:ss", Locale.ENGLISH);
		String dateStr = null;
			dateStr = format.format(date);
	
		return dateStr;
	}
	
	private long calculateSpan(Date prevDate, Date currentDate)
	{
		long diff = 0;
		long noOfDays = 0;
		if(prevDate !=null)
		{
			diff = currentDate.getTime() - prevDate.getTime();
			noOfDays = ( (prevDate.getTime() - currentDate.getTime()) / (1000 * 60 * 60 * 24));
		}
		//long noOfDays = TimeUnit.DAYS.convert(diff, TimeUnit.HOURS);
				
		return noOfDays;
	}
	
	private String getLocalTime(Date date)
	{
		SimpleDateFormat sdf= new SimpleDateFormat("MM-dd-yyyy HH:mm");
		Calendar estDate = Calendar.getInstance();
		
		estDate.setTime(date);
		sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		System.out.println( sdf.format(estDate.getTime()));
		return sdf.format(estDate.getTime());
	}

	@Override
	@Transactional
	public UserReportResp fetchAciveLoginsSVC() {
		
		UserReportResp userReportResp = null;
		List<UserLoginReport> userLoginReports = null;
		UserLoginReport userLoginReport = null;
		Calendar cal = Calendar.getInstance();
		List<LoginEntity> loginEntities = reportDAO.getActiveLogin();
		if(!loginEntities.isEmpty())
		{
			userReportResp = new UserReportResp();
			userLoginReports = new ArrayList<UserLoginReport>(0);
			
			for(LoginEntity loginEntity : loginEntities)
			{
				if(compareToday(loginEntity.getLoginTs()))
				{
					userLoginReport = new UserLoginReport();
					
					userLoginReport.setLoginDate(convertDateToString(loginEntity.getLoginTs()));
					userLoginReport.setUserName(loginEntity.getUserEntity().getFirstName()+" "+loginEntity.getUserEntity().getLastName());
					userLoginReports.add(userLoginReport);
				}
			}
			userReportResp.setUserLoginReports(userLoginReports);
		}
		
		return userReportResp;
	}
	
	private boolean compareToday(Date date)
	{
		String inputDateStr, currentDateString;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		
		inputDateStr = simpleDateFormat.format(date);
		
		currentDateString = simpleDateFormat.format(new Date());
		
		if(currentDateString.equals(inputDateStr))
			return true;
		else 
			return false;
		
	}

	@Override
	@Transactional
	public UserReportResp userActivityReport(UserReportReq userReportReq) {
		
		UserReportResp userReportResp = null;
		
		List<ActivityReportDTO> loginFail = new ArrayList<ActivityReportDTO>(0);
		List<ActivityReportDTO> forgotPassword = new ArrayList<ActivityReportDTO>(0);
		List<ActivityReportDTO> forgotUserID = new ArrayList<ActivityReportDTO>(0);
		List<ActivityReportDTO> loginActivity = new ArrayList<ActivityReportDTO>(0);
		ActivityReportDTO activityReportDTO;
		String endDate = null;
		long DAY_IN_MS = 1000 * 60 * 60 * 24;
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		if(userReportReq.getEndDate() == null)
			endDate = formatter.format(new Date());
		else
			endDate = userReportReq.getEndDate();
		
		String  beginDate  = null;
		if(userReportReq.getBeginDate() == null)
			beginDate = formatter.format(new Date(new Date().getTime() - (7 * DAY_IN_MS)));
		else
			beginDate = userReportReq.getBeginDate();
		
		
		
		
		List<UserActivityEntity> userActivityEntities = reportDAO.userActivityReport(userReportReq.getId(),beginDate, endDate);
		
		if(userActivityEntities.size() >0)
		{
			userReportResp = new UserReportResp();
			
			for(UserActivityEntity activityEntity : userActivityEntities)
			{
				activityReportDTO = new ActivityReportDTO();
				if(activityEntity.getActivityType().equals(APPConstants.LOGIN_FAIL_ACTIVITY))
				{
					activityReportDTO.setActivityTime(getLocalTime(activityEntity.getActivityCreateTs()));
					activityReportDTO.setActivityType(activityEntity.getActivityType());
					activityReportDTO.setUserName(activityEntity.getUserEntity().getFirstName()+" "+activityEntity.getUserEntity().getLastName());
					loginFail.add(activityReportDTO);
				}
				else if(activityEntity.getActivityType().equals(APPConstants.FORGOT_USER_ACTIVITY))
				{
					activityReportDTO.setActivityTime(getLocalTime(activityEntity.getActivityCreateTs()));
					activityReportDTO.setActivityType(activityEntity.getActivityType());
					activityReportDTO.setUserName(activityEntity.getUserEntity().getFirstName()+" "+activityEntity.getUserEntity().getLastName());
					forgotUserID.add(activityReportDTO);
				}
				else if(activityEntity.getActivityType().equals(APPConstants.FORGOT_USER_PASSWORD))
				{
					activityReportDTO.setActivityTime(getLocalTime(activityEntity.getActivityCreateTs()));
					activityReportDTO.setActivityType(activityEntity.getActivityType());
					activityReportDTO.setUserName(activityEntity.getUserEntity().getFirstName()+" "+activityEntity.getUserEntity().getLastName());
					forgotPassword.add(activityReportDTO);
				}
				else if(activityEntity.getActivityType().equals(APPConstants.LOGIN_SUCCESS_ACTIVITY))
				{
					activityReportDTO.setActivityTime(getLocalTime(activityEntity.getActivityCreateTs()));
					activityReportDTO.setActivityType(activityEntity.getActivityType());
					activityReportDTO.setUserName(activityEntity.getUserEntity().getFirstName()+" "+activityEntity.getUserEntity().getLastName());
					loginActivity.add(activityReportDTO);
				}
			}
			userReportResp.setForgotPassword(forgotPassword);
			userReportResp.setForgotUserID(forgotUserID);
			userReportResp.setLoginFail(loginFail);
			userReportResp.setLoginActivity(loginActivity);
		}
		return userReportResp;
	}

	@Override
	@Transactional
	public UserReportResp getInactiveUsers(int days) {
		UserReportResp userReportResp = new UserReportResp();
		List<UserLoginReport> userLoginReports = null;
		UserLoginReport userLoginReport = null;
		Calendar cal = Calendar.getInstance();
		List<UserEntity> userEntities = userDAO.getUsers();
		userLoginReports = new ArrayList<>();
		cal.add(Calendar.DAY_OF_MONTH, -days);
		System.out.println(cal.getTime());
		for(UserEntity userEntity : userEntities)
		{
			Set<LoginEntity> loginEntities = userEntity.getLogintEntity();
			
			if(!loginEntities.isEmpty())
			{
				for(LoginEntity loginEntity : loginEntities)
				{
					if(loginEntity.getLoginTs().before(cal.getTime()))
					{
						userLoginReport = new UserLoginReport();
						
						userLoginReport.setLoginDate(convertDateToString(loginEntity.getLoginTs()));
						userLoginReport.setUserName(loginEntity.getUserEntity().getFirstName()+" "+loginEntity.getUserEntity().getLastName());
						userLoginReport.setEmailId(userEntity.getEmailId());
						userLoginReports.add(userLoginReport);
					}
					break;
				}
			}
			else
			{
				userLoginReport = new UserLoginReport();
				userLoginReport.setLoginDate("Never logged in");
				userLoginReport.setUserName(userEntity.getFirstName()+" "+userEntity.getLastName());
				userLoginReport.setEmailId(userEntity.getEmailId());
				userLoginReports.add(userLoginReport);
			}
		}
		userReportResp.setUserLoginReports(userLoginReports);
		return userReportResp;
	}

	@Override
	@Transactional
	public UserReportResp getUsersOnRole(int roleID) {
		RolesEntity rolesEntity = commonDAO.getRoleById(roleID);
		Set<UserRoleEntity> userRoleEntities = rolesEntity.getUserRoleEntitys();
		List<UserDTO> userDTOs = new ArrayList<>();
		UserDTO userDTO = null;
		UserReportResp userReportResp = new UserReportResp();
		for(UserRoleEntity userRoleEntity : userRoleEntities)
		{
			userDTO = new UserDTO();
			userDTO.setFirstName(userRoleEntity.getUserEntity().getFirstName());
			userDTO.setLastName(userRoleEntity.getUserEntity().getLastName());
			userDTO.setEmailId(userRoleEntity.getUserEntity().getEmailId());
			userDTOs.add(userDTO);
		}
		userReportResp.setUserDTOs(userDTOs);
		return userReportResp;
	}
	
	
}
