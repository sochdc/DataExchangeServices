package com.soch.uam.serviceimpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.transaction.Transactional;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.soch.uam.dao.CommonDAO;
import com.soch.uam.dao.UserDAO;
import com.soch.uam.domain.AddressEntity;
import com.soch.uam.domain.ConfigEntity;
import com.soch.uam.domain.DemoUserEntity;
import com.soch.uam.domain.LoginEntity;
import com.soch.uam.domain.OTPEntity;
import com.soch.uam.domain.OnboardApprovalPendingEntity;
import com.soch.uam.domain.OnboardingApprovalEntity;
import com.soch.uam.domain.OnboardingUserNotesEntity;
import com.soch.uam.domain.PolicyConfigEntity;
import com.soch.uam.domain.QuestionaireEntity;
import com.soch.uam.domain.RolesEntity;
import com.soch.uam.domain.SecauthtokenEntity;
import com.soch.uam.domain.SecurityQAEntity;
import com.soch.uam.domain.TempUserEntity;
import com.soch.uam.domain.UserEntity;
import com.soch.uam.domain.UserFileEntity;
import com.soch.uam.domain.UserRoleEntity;
import com.soch.uam.domain.UserWorkEntity;
import com.soch.uam.dto.AddressDTO;
import com.soch.uam.dto.ConfigDTO;
import com.soch.uam.dto.OnboardApprovalPendingDTO;
import com.soch.uam.dto.OnboardingUserNotesDTO;
import com.soch.uam.dto.SecurityQADTO;
import com.soch.uam.dto.TempUserDTO;
import com.soch.uam.dto.UserDTO;
import com.soch.uam.exception.InternalErrorException;
import com.soch.uam.request.ContactUsReq;
import com.soch.uam.response.OnboardingReq;
import com.soch.uam.response.OnboardingUserFile;
import com.soch.uam.response.UserReq;
import com.soch.uam.response.UserSVCResp;
import com.soch.uam.service.UserService;
import com.soch.uam.svc.constants.APPConstants;
import com.soch.uam.util.AppUtil;
import com.soch.uam.util.CopyBeanProperties;
import com.soch.uam.util.OTPGenerator;
import com.soch.uam.util.POJOCacheUtil;
import com.soch.uam.util.SendEmail;

@Service("userService")
@PropertySource(value = { "classpath:application.properties"})
public class UserServiceImpl implements UserService{
	
	@Autowired
	private UserDAO userDAO ;
	
	@Autowired
	private CommonDAO commonDAO ;
	
	@Autowired
	private POJOCacheUtil pojoCacheUtil ;
	
	@Autowired
	private MessageSource messageSource;
	
	 @Autowired
	 private Environment environment;
	 
	 private static Map<String, Set<SecurityQADTO>> userMap = new HashMap<String, Set<SecurityQADTO>>();
	 
	 private static Map<Integer, QuestionaireEntity> questionaireMap = new HashMap<Integer, QuestionaireEntity>();
	 
	 private static Map<String, Object> policyMap = new HashMap<String, Object>();
	 
	 private static Map<String, Object> fgtPWDCount = new HashMap<String, Object>();
	 
	@Override
	@Transactional
	public UserDTO signUpUser(UserDTO userDTO) {
		
		UserEntity userEntity = new UserEntity(); 
		Set<AddressEntity> addressEntityList = new HashSet<AddressEntity>();
		Set<SecurityQAEntity> securityQAEntities = new HashSet<SecurityQAEntity>();
		AddressEntity addressEntity = null;
		SecurityQAEntity securityQAEntity = null;
		QuestionaireEntity questionaireEntity = null;
		String authToken = null;
		
		if(questionaireMap.isEmpty())
		{
			getQuestionaire();
		}
		
		try {
			if(userDTO.getId() == 0)
				BeanUtils.copyProperties(userEntity, userDTO);
			if(userDTO.getSecurityQA() != null && !userDTO.getSecurityQA().isEmpty())
			{
				Set<SecurityQADTO> securityQADTOs = userDTO.getSecurityQA(); 
				
				for(SecurityQADTO securityQADTO : securityQADTOs)
				{
					
					securityQAEntity = new SecurityQAEntity();
					questionaireEntity = questionaireMap.get(new Integer(securityQADTO.getQuestion()));
					
					BeanUtils.copyProperties(securityQAEntity, securityQADTO);
					
					securityQAEntity.setQuestion(questionaireEntity.getQuestionDescription());
					securityQAEntity.setUserEntity(userEntity);
					securityQAEntity.setCreatedTs(getCurrentDate());
					securityQAEntity.setCreatedBy(userDTO.getUserId());
					securityQAEntity.setQuestionaireEntity(questionaireEntity);
					
					securityQAEntity.setUpdatedTS(getCurrentDate());
					securityQAEntity.setUpdatedBy(userDTO.getUserId());
					
					securityQAEntities.add(securityQAEntity);
					
				}
				userEntity.setSecurityQA(securityQAEntities);
			}
			
			if(userDTO.getAddress() != null)
			{
				Set<AddressDTO> addressDTOs = userDTO.getAddress();
				
				for(AddressDTO address : addressDTOs)
				{
					userEntity.setId(userDTO.getId());
					addressEntity = new AddressEntity();
					BeanUtils.copyProperties(addressEntity, address);
					addressEntity.setUserEntity(userEntity);
					
					addressEntity.setCreatedTs(getCurrentDate());
					addressEntity.setCreatedBy(userEntity.getUserId());
					
					addressEntity.setUpdatedTs(getCurrentDate());
					addressEntity.setUpdatedBy(userEntity.getUserId());
					
					addressEntityList.add(addressEntity);
				}
					
				userEntity.setAddress(addressEntityList);	
			}
				
			userEntity.setCreatedTs(getCurrentDate());
			userEntity.setCreatedBy(userEntity.getUserId());
			
			userEntity.setUpdatedTs(getCurrentDate());
			userEntity.setUpdatedBy(userEntity.getUserId());
			
			//userEntity.setAddress(UASBeanUtl.convertAddressDomainSetToEntitySet(userDTO.getAddress()));
			if(userDTO.getSource().equalsIgnoreCase(APPConstants.SOURCE_INTERNAL))
			{
				userEntity.setUpdatedBy(userEntity.getUserId());
				userEntity.setUpdatedTs(getCurrentDate());
				userDAO.updateUser(userEntity);
				authToken =generateAuthToken(userEntity);
				sendRegEmail(userEntity, authToken);
			}
			else if(userDTO.getId() == 0) {
				userEntity.setActiveFlag(false);
				userEntity.setLockFlag(false);	
			Integer id = userDAO.saveUser(userEntity);
			//userEntity = userDAO.getUser(userDTO.getUserId());
			userDTO.setId(id);
			}
			else {
				userEntity = userDAO.getUser(userDTO.getUserId());
				userEntity.setFirstName(userDTO.getFirstName());
				userEntity.setLastName(userDTO.getLastName());
				userEntity.setEmailId(userDTO.getEmailId());
				userEntity.setUpdatedBy(userEntity.getUserId());
				userEntity.setUpdatedTs(getCurrentDate());
				userEntity.setDateOfBirth(convertDate(userDTO.getDateOfBirth()));
				userEntity.setAddress(addressEntityList);
				userDAO.updateUser(userEntity);
				authToken =generateAuthToken(userEntity);
				sendRegEmail(userEntity, authToken);
			}
			
		} catch (IllegalAccessException  e) {
			throw new InternalErrorException(Integer.parseInt(messageSource.getMessage("INTERNAL.SYSTEM.ERROR.CODE",null, Locale.getDefault())), 
					messageSource.getMessage("INTERNAL.SYSTEM.ERROR.MSG",null, Locale.getDefault()));
		} catch (InvocationTargetException e) {
			throw new InternalErrorException(Integer.parseInt(messageSource.getMessage("INTERNAL.SYSTEM.ERROR.CODE",null, Locale.getDefault())), 
					messageSource.getMessage("INTERNAL.SYSTEM.ERROR.MSG",null, Locale.getDefault()));
		}
		return userDTO;
	}
	
	private Date convertDate(String javaDate)
	{
		SimpleDateFormat javaDateFormat = new SimpleDateFormat("MM-dd-yyyy");
		SimpleDateFormat JSONDateFormat = new SimpleDateFormat("yyy-MM-dd");
		try {
			Date date = javaDateFormat.parse(javaDate);
			return date;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 
	 * @param userEntity
	 * @param authToken
	 * @return
	 */
	private boolean sendRegEmail(UserEntity userEntity, String authToken)
	{
		String toEmail = null;
		boolean returnVal = true;
		if(userEntity.getEmailId() != null)
			toEmail = userEntity.getEmailId();
		else
			toEmail = userEntity.getUserId();
		String emailBody = APPConstants.REG_EMAIL_TEXT;
		String url = environment.getRequiredProperty("hostName")+APPConstants.REG_EMAIL_URL.replace("tokenParam", authToken);
		emailBody = emailBody.replace("url", url);
		
		SendEmail.sendHTMLEmail(toEmail, APPConstants.REG_EMAIL_SUB, emailBody);
		return returnVal;
	}


	@Override
	@Transactional
	public boolean isUserIdAvailable(String userId) {
		boolean returnValue = false;
		UserEntity userEntity = userDAO.getUser(userId);
		if(userEntity == null)
			returnValue = true;
		return returnValue;
	}
	
	public String generateAuthToken(UserEntity userEntity)
	{
		String authtokenID = UUID.randomUUID().toString().replaceAll("-", "");
		SecauthtokenEntity secauthtokenEntity = new SecauthtokenEntity();
		secauthtokenEntity.setAuthToken(authtokenID);
		secauthtokenEntity.setLastAccessTs(getCurrentDate());
		secauthtokenEntity.setUserEntity(userEntity);
		secauthtokenEntity.setCreatedTs(getCurrentDate());
		secauthtokenEntity.setStatus(true);
		secauthtokenEntity.setCreatedBy(userEntity.getUserId());
		userDAO.saveAuthToken(secauthtokenEntity);
		return authtokenID;
	}

	@Override
	@Transactional
	public boolean validateRegister(String authToken) {
		boolean returnValue = false;
		SecauthtokenEntity secauthtokenEntity = userDAO.getAuthToken(authToken);
		Date createdTS = null;
		if(secauthtokenEntity != null)
		{
			createdTS = secauthtokenEntity.getCreatedTs();
			getPolicies();
			//int regHours = Integer.parseInt((String) policyMap.get("FGT_PWD_ATEMPTS"));
			if(secauthtokenEntity.getStatus() && AppUtil.validateTime(Integer.parseInt(environment.getRequiredProperty("regValidinMins")), createdTS))
			{
					secauthtokenEntity.setStatus(false);
					UserEntity userEntity = secauthtokenEntity.getUserEntity();
					userEntity.setActiveFlag(true);
					userDAO.updateAuthToken(secauthtokenEntity);
					userEntity.setUpdatedTs(getCurrentDate());
					userDAO.updateUser(userEntity);
					returnValue =  true;
			}
		}
		return returnValue;
	}

	@Override
	@Transactional
	public UserDTO signInUser(UserDTO userDTO) {
		
		UserEntity userEntity = null;
		UserDTO returnUserDTO = new UserDTO();
		userEntity = userDAO.getUser(userDTO.getUserId());
		returnUserDTO.setId(userEntity.getId());
		System.out.println(returnUserDTO.getId());
		//Map<String, String> cacheMap = pojoCacheUtil.getAppConfig();
		if(userEntity!= null )
		{
			returnUserDTO.setActiveFlag(userEntity.getActiveFlag());
			if(userEntity.getLockFlag())
			{
				returnUserDTO.setLockFlag(userEntity.getLockFlag());
				returnUserDTO.setActiveFlag(userEntity.getActiveFlag());
				returnUserDTO.setUserId(userEntity.getUserId());
			}else if(!userEntity.getActiveFlag())
			{
				returnUserDTO.setActiveFlag(userEntity.getActiveFlag());
				returnUserDTO.setUserId(userEntity.getUserId());
			}
			else if(userEntity.isPwdChangeFlag())
			{
				returnUserDTO.setPwdChangeFlag(userEntity.isPwdChangeFlag());
				returnUserDTO.setUserId(userEntity.getUserId());
				
				String authToken =generateAuthToken(userEntity);
				returnUserDTO.setToken(authToken);
				returnUserDTO.setUserId(userEntity.getUserId());
			}
			else if(userEntity.getPassowrd().equals(userDTO.getPassowrd()))
			{
		
			
				
			String authToken =generateAuthToken(userEntity);
			returnUserDTO = CopyBeanProperties.copyUserPerperties(userEntity);
			returnUserDTO.setToken(authToken);
			
			LoginEntity loginEntity = new LoginEntity();
			loginEntity.setUserEntity(userEntity);
			loginEntity.setLoginTs(getCurrentDate());
			loginEntity.setLoginStatus(true);
			
			userDAO.saveUserLogin(loginEntity);
			
			if(userEntity.getLogintEntity()!= null && userEntity.getLogintEntity().iterator().hasNext())
				returnUserDTO.setLastLoggedin(userEntity.getLogintEntity().iterator().next().getLoginTs().toString());
			if(userEntity.getLoginFailureCount() >0 )
			{
				userEntity.setLoginFailureCount(0);
				userDAO.updateUser(userEntity);
			}
				
			}
			else
			{
				userEntity.setLoginFailureCount(userEntity.getLoginFailureCount()+1);
				getPolicies();
				if(userEntity.getLoginFailureCount() >= Integer.parseInt((String) policyMap.get("Invalid User Login Attempt Limit")))
				{
					userEntity.setLockFlag(true);
					returnUserDTO.setLockFlag(userEntity.getLockFlag());
					returnUserDTO.setActiveFlag(userEntity.getActiveFlag());
					returnUserDTO.setMaxAttemptReached(true);
					returnUserDTO.setUserId(userEntity.getUserId());
					returnUserDTO.setId(userEntity.getId());
				}
				userDAO.updateUser(userEntity);
				
			}
		}
		else
		{
			
		}
		
		return returnUserDTO;
	}
	
	
	private Date getCurrentDate()
	{
			Calendar cal = Calendar.getInstance();
		    cal.setTimeZone(TimeZone.getTimeZone("est"));
		    return cal.getTime();
	}

	@Override
	public List<ConfigDTO> getAppConfig() {
		// TODO Auto-generated method stub
		List<ConfigEntity> configEntities = userDAO.getAppConfig();
		List<ConfigDTO> configDTOs = new ArrayList<ConfigDTO>();
		ConfigDTO configDTO = null;
		
		
		
		for(ConfigEntity configEntity : configEntities)
		{
			configDTO = new ConfigDTO();
			try {
				BeanUtils.copyProperties(configDTO, configEntity);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			
			configDTOs.add(configDTO);
		}
		return configDTOs;
	}

	@Override
	@Transactional
	public boolean forgotUserId(String emailId) {
		boolean returnVal = false;
		String userId = null;
		userId = userDAO.getUserIdOnEmail(emailId);
		if(userId!= null)
		{
			returnVal = true;
			sendForgotUserIDEmail(userId, emailId);
		}
		
		return returnVal;
	}
	
	private void sendForgotUserIDEmail(String userId, String emailId)
	{
		String toEmail = null;
		boolean returnVal = true;
		
		String emailBody = APPConstants.FORGOT_USERID_TEXT;
		emailBody = emailBody.replace("userid", userId);
		System.out.println(emailBody);
		
		SendEmail.sendHTMLEmail(emailId, APPConstants.FORGOT_USERID_SUB, emailBody);
		
	}

	@Override
	@Transactional
	public UserDTO forgotPassword(String userId) {
		UserDTO userDTO = new UserDTO();
		Set<SecurityQADTO> securityQADTOs = new HashSet<SecurityQADTO>();
		Set<SecurityQADTO> securityQADTOs2 = new HashSet<SecurityQADTO>();
		
		//userDTO = userMap.get(userId);
		
			
			
			UserEntity userEntity = userDAO.getUser(userId);
			if(userEntity != null)
			{
				if(userEntity.getLockFlag())
				{
					userDTO.setLockFlag(true);
					return userDTO;
				}
				
				userDTO = CopyBeanProperties.copyUserPerperties(userEntity);
				userMap.put(userId,  userDTO.getSecurityQA());
				
				securityQADTOs = userDTO.getSecurityQA();
				
				SecurityQADTO[] securityQADTOArray = securityQADTOs.toArray(new SecurityQADTO[securityQADTOs.size()]);
				int index = getInex(securityQADTOs.size());
				
				SecurityQADTO securityQADTO = securityQADTOArray[index];
				
				securityQADTOs2.add(securityQADTO);
				
				userDTO.setSecurityQA(securityQADTOs2);
			}
		
		
		
		return userDTO;
	}
	
	private int getInex(int maxNum)
	{
		Random rand = new Random(); 
		int value = rand.nextInt(maxNum);
		return value;
	}
	
	
	private void getQuestionaire()
	{
		List<QuestionaireEntity> questionaireEntities = userDAO.getQuestionnaire();
		
		for(QuestionaireEntity questionaireEntity : questionaireEntities)
		{
			questionaireMap.put(questionaireEntity.getQuestionId(), questionaireEntity);
		}
	}

	@Override
	@Transactional
	public UserDTO validateQA(UserDTO userDTO) {
		
		Set<SecurityQADTO> securityQADTOs = userDTO.getSecurityQA();
		
		Set<SecurityQADTO> securityQADTOs2 = userMap.get(userDTO.getUserId());
		
		int maxAttempts = 0;
		
		int failureCount = 1;
		
		getPolicies();
		maxAttempts = Integer.parseInt((String) policyMap.get("Forgot Password Attempts Limit"));
	
		/*if( policyMap.isEmpty())
		{
			getPolicies();
			maxAttempts = Integer.parseInt((String) policyMap.get("FGT_PWD_ATEMPTS"));
		}
		else
		{
			maxAttempts = Integer.parseInt((String) policyMap.get("FGT_PWD_ATEMPTS"));
		}*/
		
		System.out.println("maxAttempts "+Integer.parseInt((String) policyMap.get("Forgot Password Attempts Limit")));
		if(securityQADTOs != null && !securityQADTOs.isEmpty())
		{
			for(SecurityQADTO qadto : securityQADTOs)
			{
				for(SecurityQADTO qadto2 : securityQADTOs2)
				{
					if(qadto2.getAnswer().equalsIgnoreCase(qadto.getAnswer()) &&
							qadto2.getQuestion().equalsIgnoreCase(qadto.getQuestion()))
					{
						sendPWDEmail(userDTO.getUserId());
						return null;
					}
				}
			}
		}
		
		SecurityQADTO[] securityQADTOArray = securityQADTOs2.toArray(new SecurityQADTO[securityQADTOs2.size()]);
		int index = getInex(securityQADTOs2.size());
		
		SecurityQADTO securityQADTO = securityQADTOArray[index];
		
		securityQADTOs2 = new HashSet<SecurityQADTO>();
		
		securityQADTOs2.add(securityQADTO);
		
		userDTO.setSecurityQA(securityQADTOs2);
		
		Integer failureCountInt = (Integer) fgtPWDCount.get(userDTO.getUserId());
				
				//(Integer) CacheUtil.getInstance().getCache("FGTPWD/"+userDTO.getUserId());
		if(failureCountInt == null)
		{
			failureCountInt = failureCount;
			fgtPWDCount.put(userDTO.getUserId(), failureCountInt);
			//CacheUtil.getInstance().detachCache("FGTPWD/"+userDTO.getUserId());
			//CacheUtil.getInstance().putCache("FGTPWD/"+userDTO.getUserId(), failureCountInt);
		}
		else {
			failureCountInt++;
			fgtPWDCount.put(userDTO.getUserId(), failureCountInt);
			//CacheUtil.getInstance().detachCache("FGTPWD/"+userDTO.getUserId());
			//CacheUtil.getInstance().putCache("FGTPWD/"+userDTO.getUserId(), failureCountInt);
			
		}
		
		if(failureCountInt >= maxAttempts)
		{
			
			userDTO.setToken("MAXATTEMPTS");
			fgtPWDCount.remove(userDTO.getUserId());
			UserEntity userEntity = userDAO.getUser(userDTO.getUserId());
			userEntity.setLockFlag(true);
			userDAO.updateUser(userEntity);
			//CacheUtil.getInstance().detachCache("FGTPWD/"+userDTO.getUserId());
		}
		System.out.println("failureCountInt " +failureCountInt);
		return userDTO;
	}
	
	
	/**
	 * 
	 * @param userEntity
	 * @param authToken
	 * @return
	 */
	private boolean sendPWDEmail(String UserId)
	{
		boolean returnVal = true;
		
		UserEntity userEntity = userDAO.getUser(UserId);
		String authToken =generateAuthToken(userEntity);
		
		String emailBody = APPConstants.FORGOT_PWD_TEXT;
		String url = environment.getRequiredProperty("hostName")+APPConstants.PWD_RESET_URL+ "?token="+authToken;;
		emailBody = emailBody.replace("url", url);
		emailBody = emailBody.replace("UserId", url);
		SendEmail.sendHTMLEmail(UserId, APPConstants.FORGOT_PWD_SUB, emailBody);
		return returnVal;
	}
	
	private void getPolicies()
	{
		List<PolicyConfigEntity> policyConfigEntities = userDAO.getPolicies();
		
			for(PolicyConfigEntity policyConfigEntity : policyConfigEntities)
			{
				policyMap.put(policyConfigEntity.getPolicyName(), policyConfigEntity.getValue());
			}
		
	}

	@Override
	@Transactional
	public void sendOTP(String userId) {
		
		UserEntity userEntity = userDAO.getUser(userId);
		
		OTPEntity otpEntity = new OTPEntity();
		String OTP = OTPGenerator.random(6);
		
		otpEntity.setOneTimePwd(OTP);
		otpEntity.setActiveFlag(true);
		otpEntity.setCreatedTs(getCurrentDate());
		otpEntity.setUserEntity(userEntity);
		userDAO.saveOTP(otpEntity);
		sendOTPEmail(userEntity, OTP);
	}
	
	/**
	 * 
	 * @param userEntity
	 * @param authToken
	 * @return
	 */
	private boolean sendOTPEmail(UserEntity userEntity, String OTP)
	{
		String toEmail = null;
		boolean returnVal = true;
		if(userEntity.getEmailId() != null)
			toEmail = userEntity.getEmailId();
		else
			toEmail = userEntity.getUserId();
		String emailBody = APPConstants.OTP_EMAIL_TEXT;
		emailBody = emailBody.replaceAll("otpwd", OTP);
		getPolicies();
		Integer validMins = new Integer(0);
		if(policyMap.size() > 0)
		{
			validMins = Integer.parseInt((String) policyMap.get("For One Time Passwords (OTP) set expiry to (x) hours"));
		}
		
		emailBody = emailBody.replaceAll("30", validMins.toString());
		
		SendEmail.sendHTMLEmail(toEmail, APPConstants.OTP_EMAIL_SUB, emailBody);
		return returnVal;
	}

	@Override
	@Transactional
	public boolean validateOTP(String otp) {
		getPolicies();
		OTPEntity otpEntity = userDAO.getOTP(otp);
		if(otpEntity == null)
			return false;
		
		int validMins = 0;
		if(policyMap.size() > 0)
		{
			validMins = Integer.parseInt((String) policyMap.get("For One Time Passwords (OTP) set expiry to (x) hours"));
		}
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); 
		
		SimpleDateFormat format1 = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy",
                Locale.ENGLISH); 
		
		SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.ENGLISH); 

		Date d1 = null;
		Date d2 = null;
		long diff = 0;
		try {
		    d1 = format1.parse(Calendar.getInstance().getTime().toString());
		    d2 = format2.parse(otpEntity.getCreatedTs().toString());
		    diff = new Date(format.format(d1)).getTime() - new Date(format.format(d2)).getTime() ;
		} catch (ParseException e) {
		    e.printStackTrace();
		}
		
		 
		if(TimeUnit.MILLISECONDS.toMinutes(diff) <= validMins )
			return true;
		return false;
	}

	@Override
	@Transactional
	public Set<Integer> gerUserRole(String userId) {
		Set<Integer>  roleIds = new HashSet<Integer>();
		Set<UserRoleEntity> userRoleEntities =  userDAO.getUserRole(userId);
		if(userRoleEntities != null) {
			for(UserRoleEntity userRoleEntity : userRoleEntities)
			{
				roleIds.add(userRoleEntity.getRolesEntity().getRoleId());
			}
		}
		return roleIds;
	}

	@Transactional
	@Override
	public boolean validateDemoUser(UserDTO user) {
		boolean returnVal = false;
		
		DemoUserEntity demoUserEntity = userDAO.validateDemoUser(user.getUserId(), user.getPassowrd());
	
		if(demoUserEntity != null)
			return true;
		return false;
	}

	@Override
	@Transactional
	public boolean resetPwd(UserDTO user) {
		SecauthtokenEntity secauthtokenEntity = userDAO.getAuthToken(user.getToken());
		if(secauthtokenEntity != null) {
		UserEntity userEntity = secauthtokenEntity.getUserEntity();
		userEntity.setPwdChangeFlag(false);
		userEntity.setPassowrd(user.getPassowrd());
		secauthtokenEntity.setUserEntity(userEntity);
		secauthtokenEntity.setStatus(false);
		//userDAO.updateUser(userEntity);
		userDAO.updateAuthToken(secauthtokenEntity);
		return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	@Transactional
	public boolean forcePWDChange() {
		boolean returnVal = false;
		returnVal = userDAO.forcePWDChange();
		return returnVal;
	}
	
	
	/**
	 * 
	 * @param userEntity
	 * @param authToken
	 * @return
	 */
	private boolean contactUsEMail(ContactUsReq contactUsReq)
	{
		String toEmail = null;
		
		SendEmail.sendHTMLEmail("info@soch-inc.com", APPConstants.REG_EMAIL_SUB, contactUsReq.getMessage());
		return true;
	}

	@Override
	@Transactional
	public UserDTO searchUser(UserReq userReq) {
		UserEntity userEntity =  userDAO.getUser(userReq.getUserId());
		UserDTO userDTO = null;
		if(userEntity != null)
		{
			userDTO = CopyBeanProperties.copyUserPerperties(userEntity);
		}
		return userDTO;
	}

	@Override
	@Transactional
	public UserDTO changeUserStatus(String userId) {
		UserDTO userDTO = null;
		
		UserEntity userEntity= userDAO.getUser(userId);
		if(userEntity.getActiveFlag())
			userEntity.setActiveFlag(false);
		else
			userEntity.setActiveFlag(true);
		userDAO.updateUser(userEntity);
		userDTO = CopyBeanProperties.copyUserPerperties(userEntity);
		return userDTO;
	}

	@Override
	@Transactional
	public UserDTO changeLockStatus(String userId) {
		UserDTO userDTO = null;

		UserEntity userEntity = userDAO.getUser(userId);
		if (userEntity.getLockFlag())
		{
			userEntity.setLockFlag(false);
			userEntity.setLoginFailureCount(0);
		}
		else
			userEntity.setLockFlag(true);
		userDAO.updateUser(userEntity);
		userDTO = CopyBeanProperties.copyUserPerperties(userEntity);
		return userDTO;
	}

	@Override
	@Transactional
	public UserDTO resetUserPwd(String userId) {
		UserDTO userDTO = null;

		UserEntity userEntity = userDAO.getUser(userId);
		String pwd =generatePassword();
		System.out.println(pwd);
		userEntity.setPassowrd(generatePassword());
		userEntity.setPwdChangeFlag(true);
		userDAO.updateUser(userEntity);
		userDTO = CopyBeanProperties.copyUserPerperties(userEntity);
		return userDTO;
	}
	
	private String generatePassword()
	{
		 char[] potential = new char[10]; // 32768 is not huge....
		    int size = 0;
		    final Pattern pattern = Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$");
		    for (int c = 0; c <= 10; c++) {
		        if (pattern.matcher(String.valueOf((char)c)).matches()) {
		            potential[size++] = (char)c;
		        }
		    }
		    return Arrays.copyOf(potential, size).toString();
		    
	}

	@Override
	@Transactional
	public void logOut(String token) {
		
		SecauthtokenEntity secauthtokenEntity = userDAO.getAuthToken(token);
		if(secauthtokenEntity != null)
		{
			UserEntity userEntity = secauthtokenEntity.getUserEntity();
			Set<LoginEntity> loginEntitySet = userEntity.getLogintEntity();
			/*for(LoginEntity loginEntity : loginEntitySet)
			{
				loginEntity.setLogOutTs(getCurrentDate());
				loginEntity.setLoginStatus(false);
			}*/
			
			LoginEntity loginEntity = (LoginEntity) loginEntitySet.iterator().next();
			loginEntity.setLogOutTs(getCurrentDate());
			loginEntity.setLoginStatus(false);
			secauthtokenEntity.setLastAccessTs(getCurrentDate());
			secauthtokenEntity.setStatus(false);
			userDAO.updateAuthToken(secauthtokenEntity);
		}
		
		
		
	}

	@Override
	@Transactional
	public UserSVCResp getPasswordPolicy() {
		 UserSVCResp userSVCResp = new UserSVCResp();
		getPolicies();
		StringBuffer passwordRegExpr = new StringBuffer();
		StringBuffer passwordString = new StringBuffer();
		//^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=.\-_*!()])([a-zA-Z0-9@#$%^&+=*.\-_!()]){3,}$
		passwordRegExpr = passwordRegExpr.append("^([");
		passwordString.append("Password shoud contain ");
		System.out.println(policyMap);
		if(policyMap.get("Allow special letters (@#$%^&+=.-_*!)").toString().equalsIgnoreCase("YES"))
		{
			passwordRegExpr =  passwordRegExpr.append("@#$%^&+=.\\-_*!");
			passwordString.append("at least one special character, ");
		}
		
		if(policyMap.get("Allow upper case letters").toString().equalsIgnoreCase("YES"))
		{
			passwordRegExpr =  passwordRegExpr.append("A-Z");
			passwordString.append(" at least one upper case letter, ");
		}
		
		if(policyMap.get("Allow lower case letters").toString().equalsIgnoreCase("YES"))
		{
			passwordRegExpr =  passwordRegExpr.append("a-z");
			passwordString.append("at least one lower case letter, ");
		}
		
		if(policyMap.get("Allow numeric characters").toString().equalsIgnoreCase("YES"))
		{
			passwordRegExpr = passwordRegExpr.append("0-9");
			passwordString.append("at least one number");
		}
		
		//([a-zA-Z0-9@#$%^&+=*.\-_])
		passwordRegExpr = passwordRegExpr.append("])");
		//passwordRegExpr = passwordRegExpr.append("([a-zA-Z0-9])");
		passwordRegExpr = passwordRegExpr.append("{");
		passwordString.append(". Should contain minimum "+policyMap.get("Enforce minimum password length")+" characters and maximum "+policyMap.get("Enforce maximum password length")+" characters.");
		passwordRegExpr = passwordRegExpr.append(policyMap.get("Enforce minimum password length")+",");
		passwordRegExpr = passwordRegExpr.append(policyMap.get("Enforce maximum password length")+"}$");
		userSVCResp.setPwdRegExp(passwordRegExpr.toString());
		userSVCResp.setPwdString(passwordString.toString());
		return userSVCResp;
	}

	@Override
	@Transactional
	public void onBoardingUser(OnboardingReq onboardingReq) {
		
		TempUserEntity tempUserEntity = new TempUserEntity();
		tempUserEntity.setEmployeeId(onboardingReq.getEmployeeID());
		tempUserEntity.setFirstName(onboardingReq.getFirstName());
		tempUserEntity.setLastName(onboardingReq.getLastName());
		tempUserEntity.setMiddleName(onboardingReq.getMiddleName());
		tempUserEntity.setEmailId(onboardingReq.getEmailId());
		tempUserEntity.setPhoneNumber(onboardingReq.getPhoneNumber());
		tempUserEntity.setDateOfBirth(onboardingReq.getDateOfBirth());
		tempUserEntity.setCreatedBy(onboardingReq.getCreatedBy());
		tempUserEntity.setApprovalLevel(1);
		tempUserEntity.setWorkEmailId(onboardingReq.getWorkEmail());
		tempUserEntity.setWorkPhoneNumber(onboardingReq.getWorkPhone());
		tempUserEntity.setStartDate(onboardingReq.getStartDate());
		tempUserEntity.setPendingApproval(true);
		
		tempUserEntity.setUserRoleId(onboardingReq.getRole());
		OnboardingUserNotesEntity onboardingUserNotesEntity = new OnboardingUserNotesEntity();
		userDAO.addTempUser(tempUserEntity);
		onboardingUserNotesEntity.setTempUserEntity(tempUserEntity);
		onboardingUserNotesEntity.setNotes(onboardingReq.getNotes());
		onboardingUserNotesEntity.setCreatedBy(onboardingReq.getCreatedBy());
		onboardingUserNotesEntity.setCreatedTs(new Date());
		commonDAO.saveOnboardingNotes(onboardingUserNotesEntity);
		
		Iterator<OnboardingUserFile> userFileIterator =  onboardingReq.getUserFile().iterator();
		OnboardingUserFile userFile;
		FileInputStream inputStream;
		File fileContent;
		UserFileEntity userFileEntity = null;
		String fileName;
		while(userFileIterator.hasNext())
		{
			userFileEntity = new UserFileEntity();
			userFile = userFileIterator.next();
			fileName = userFile.getFileName();
			userFileEntity.setTempUserEntity(tempUserEntity);
			userFileEntity.setFileName(fileName);
			  try {
				  fileContent = new File(userFile.getFileName());
				  inputStream = new FileInputStream(userFile.getFileName());
				  
				  byte[] fileBytes = new byte[(int) fileContent.length()];
			      inputStream.read(fileBytes);
			      inputStream.close();
			        
				  userFileEntity.setContent(fileBytes);
				  commonDAO.insertUserFile(userFileEntity);
				  //fileContent.delete();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		createApprovalReq(onboardingReq.getRole(), tempUserEntity);
	}
	
	/**
	 * 
	 * @param roleId
	 */
	private void createApprovalReq(int roleId, TempUserEntity tempUserEntity)
	{
		List<OnboardingApprovalEntity> OnboardingApprovalEntity = commonDAO.getOnboardApproval(roleId);
		UserEntity userEntity = OnboardingApprovalEntity.get(0).getUserEntity();
		OnboardApprovalPendingEntity onboardApprovalPendingEntity = new OnboardApprovalPendingEntity();
		onboardApprovalPendingEntity.setUserEntity(userEntity);
		onboardApprovalPendingEntity.setTempUserEntity(tempUserEntity);
		onboardApprovalPendingEntity.setRequestDate(new Date());
		onboardApprovalPendingEntity.setRequestType("NEW");
		onboardApprovalPendingEntity.setPendingApproval(true);
		commonDAO.saveOnboardApprovalPendingEntity(onboardApprovalPendingEntity);
		sendApproveEmail(userEntity, tempUserEntity);
	}
	
	/**
	 * 
	 * @param userEntity
	 * @param authToken
	 * @return
	 */
	private boolean sendApproveEmail(UserEntity userEntity, TempUserEntity tempUserEntity)
	{
		String toEmail = null;
		boolean returnVal = true;
		if(userEntity.getEmailId() != null)
			toEmail = userEntity.getEmailId();
		else
			toEmail = userEntity.getUserId();
		String emailBody = APPConstants.REQ_APPROVE_MAIL_TEXT;
		emailBody = emailBody.replace("apprFname", userEntity.getFirstName());
		emailBody = emailBody.replace("apprLastName", userEntity.getLastName());
		emailBody = emailBody.replace("tempFirstName", tempUserEntity.getFirstName());
		emailBody = emailBody.replace("tempLastName", tempUserEntity.getLastName());
		System.out.println(emailBody);
		SendEmail.sendHTMLEmail(toEmail, APPConstants.REQ_APPROVE_EMAIL_SUB, emailBody);
		return returnVal;
	}
	
	/**
	 * 
	 * @param userEntity
	 * @param authToken
	 * @return
	 */
	private boolean sendRejectEmail(UserEntity userEntity, TempUserEntity tempUserEntity)
	{
		String toEmail = null;
		boolean returnVal = true;
		if(userEntity.getEmailId() != null)
			toEmail = userEntity.getEmailId();
		else
			toEmail = userEntity.getUserId();
		String emailBody = APPConstants.REQ_REJECT_MAIL_TEXT;
		emailBody = emailBody.replace("apprFname", userEntity.getFirstName());
		emailBody = emailBody.replace("apprLastName", userEntity.getLastName());
		emailBody = emailBody.replace("tempFirstName", tempUserEntity.getFirstName());
		emailBody = emailBody.replace("tempLastName", tempUserEntity.getLastName());
		System.out.println(emailBody);
		SendEmail.sendHTMLEmail(toEmail, APPConstants.REQ_REJECT_EMAIL_SUB, emailBody);
		return returnVal;
	}
	
	@Override
	@Transactional
	public Set<OnboardApprovalPendingDTO> getPendingReq(int userId) {
		 Set<OnboardApprovalPendingDTO> approvalPendingDTOs = new HashSet<OnboardApprovalPendingDTO>(0);
		 OnboardApprovalPendingDTO onboardApprovalPendingDTO = null;
		 
		 SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		 
		List<OnboardApprovalPendingEntity> onboardApprovalPendingEntities = userDAO.fetchUserPendingRequest(userId);
		
		for(OnboardApprovalPendingEntity approvalPendingEntity : onboardApprovalPendingEntities)
		{
			if(approvalPendingEntity.getTempUserEntity().isPendingApproval()) {
				onboardApprovalPendingDTO = new OnboardApprovalPendingDTO();
				onboardApprovalPendingDTO.setOnboardPendingId(approvalPendingEntity.getOnboardPendingId());
				onboardApprovalPendingDTO.setReqType(approvalPendingEntity.getRequestType());
				onboardApprovalPendingDTO.setRequestDate(sdf.format(approvalPendingEntity.getRequestDate()));
				onboardApprovalPendingDTO.setUserId(approvalPendingEntity.getUserEntity().getId());
				onboardApprovalPendingDTO.setTempUserId(approvalPendingEntity.getTempUserEntity().getEmployeeId());
				onboardApprovalPendingDTO.setTempFname(approvalPendingEntity.getTempUserEntity().getFirstName());
				onboardApprovalPendingDTO.setTempLname(approvalPendingEntity.getTempUserEntity().getLastName());
				approvalPendingDTOs.add(onboardApprovalPendingDTO);
			}
		}
			
		return approvalPendingDTOs;
	}

	@Override
	@Transactional
	public TempUserDTO getTempUser(String userId) {
		
		TempUserDTO tempUserDTO = new TempUserDTO();
		TempUserEntity tempUserEntity = userDAO.getTempUser(userId);
		Set<OnboardingUserNotesDTO> onboardingUserNotesDTOs = new HashSet<OnboardingUserNotesDTO>();
		OnboardingUserNotesDTO onboardingUserNotesDTO = null;
		Set<OnboardingUserFile> onboardingUserFiles = new HashSet<OnboardingUserFile>();
		OnboardingUserFile onboardingUserFile = null;
		RolesEntity rolesEntity = commonDAO.getRoleById(tempUserEntity.getUserRoleId());
		String roleName = rolesEntity.getRoleName();
		String systemName = rolesEntity.getSystemEntity().getSystemName();
		String department = rolesEntity.getSystemEntity().getDeptEntity().getDeptName();
		try {
			BeanUtils.copyProperties(tempUserDTO, tempUserEntity);
			if(!tempUserEntity.getOnboardingUserNotesEntities().isEmpty())
			{
				Set<OnboardingUserNotesEntity> onboardingUserNotesEntities =  tempUserEntity.getOnboardingUserNotesEntities();
				for(OnboardingUserNotesEntity onboardingUserNotesEntity : onboardingUserNotesEntities)
				{
					onboardingUserNotesDTO = new OnboardingUserNotesDTO();
					onboardingUserNotesDTO.setNotes(onboardingUserNotesEntity.getNotes());
					onboardingUserNotesDTO.setCreatedBy(onboardingUserNotesEntity.getCreatedBy());
					onboardingUserNotesDTOs.add(onboardingUserNotesDTO);
				}
				tempUserDTO.setOnboardingUserNotesDTOs(onboardingUserNotesDTOs);
			}
			
			if(!tempUserEntity.getUserFileEntities().isEmpty())
			{
				Set<UserFileEntity> userFileEntities =  tempUserEntity.getUserFileEntities();
				
				for(UserFileEntity userFileEntity : userFileEntities)
				{
					onboardingUserFile = new OnboardingUserFile();
					onboardingUserFile.setFileName(userFileEntity.getFileName());
					onboardingUserFiles.add(onboardingUserFile);
				}
				tempUserDTO.setOnboardingUserFiles(onboardingUserFiles);
			}
			
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		tempUserDTO.setDepartment(department);
		tempUserDTO.setSystem(systemName);
		tempUserDTO.setRole(roleName);
		return tempUserDTO;
	}

	@Override
	@Transactional
	public UserSVCResp approveRejectUser(TempUserDTO tempUserDTO) {
		// TODO Auto-generated method stub
		
		UserSVCResp userSVCResp = new UserSVCResp();
		TempUserEntity tempUserEntity = userDAO.getTempUser(tempUserDTO.getEmployeeId());
		List<OnboardingApprovalEntity> onboardingApprovalEntities = commonDAO.getOnboardApproval(tempUserEntity.getUserRoleId());
		OnboardingApprovalEntity approvalEntity = null;
		if(tempUserDTO.getOperation().equalsIgnoreCase("REJECT"))
		{
			UserEntity userEntity = userDAO.getUser(tempUserEntity.getCreatedBy());
			
			OnboardApprovalPendingEntity onboardApprovalPendingEntity = userDAO.getOnboardApprovalPendingEntity(tempUserEntity.getEmployeeId());
			onboardApprovalPendingEntity.setUserEntity(userEntity);
			userDAO.updateOnboardApprovalPendingEntity(onboardApprovalPendingEntity);
			sendRejectEmail(userEntity, tempUserEntity);
			userDAO.updateTempUser(tempUserEntity);
			
		}
		else {
		if(tempUserEntity.getApprovalLevel()  < onboardingApprovalEntities.size() && onboardingApprovalEntities.get(tempUserEntity.getApprovalLevel()) != null)
		{
			approvalEntity = onboardingApprovalEntities.get(tempUserEntity.getApprovalLevel());
			tempUserEntity.setApprovalLevel(approvalEntity.getLevel());
			OnboardApprovalPendingEntity onboardApprovalPendingEntity = userDAO.getOnboardApprovalPendingEntity(tempUserEntity.getEmployeeId());
			onboardApprovalPendingEntity.setUserEntity(approvalEntity.getUserEntity());
			userDAO.updateOnboardApprovalPendingEntity(onboardApprovalPendingEntity);
			sendApproveEmail(approvalEntity.getUserEntity(), tempUserEntity);
			userDAO.updateTempUser(tempUserEntity);
			
			userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.ONBOARD.APPROVAL.NEXTLEVEL.CODE",null, Locale.getDefault())));
			userSVCResp.setresultString(messageSource.getMessage("USER.ONBOARD.APPROVAL.NEXTLEVEL.STRING",null, Locale.getDefault()));
			
		}
		else
		{
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			UserEntity userEntity = new UserEntity();
			UserWorkEntity userWorkEntity = new UserWorkEntity();
			userWorkEntity.setEmpId(tempUserEntity.getEmployeeId());
			userWorkEntity.setEmailAddress(tempUserEntity.getWorkEmailId());
			try {
				userWorkEntity.setStartDate(sdf.parse(tempUserEntity.getStartDate()));
				userEntity.setDateOfBirth(sdf.parse(tempUserEntity.getDateOfBirth()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			userWorkEntity.setPhoneNumber(tempUserEntity.getWorkPhoneNumber());
			
			userEntity.setUserWorkEntity(userWorkEntity);
			userEntity.setFirstName(tempUserEntity.getFirstName());
			userEntity.setLastName(tempUserEntity.getLastName());
			userEntity.setEmailId(tempUserEntity.getEmailId());
			userEntity.setUserId(tempUserEntity.getWorkEmailId());
			userEntity.setActiveFlag(false);
			userEntity.setLockFlag(false);
			userEntity.setCreatedBy(APPConstants.SYSTEM_STR);
			userEntity.setCreatedTs(new Date());
			userEntity.setUpdatedBy(APPConstants.SYSTEM_STR);
			userEntity.setUpdatedTs(new Date());
			userEntity.setPwdChangeFlag(false);
			userEntity.setMiddleName(tempUserEntity.getMiddleName());
			// Creating user Roles
			UserRoleEntity userRoleEntity = new UserRoleEntity();
			userRoleEntity.setRolesEntity(commonDAO.getRoleById(tempUserEntity.getUserRoleId()));
			userRoleEntity.setUserEntity(userEntity);
			userRoleEntity.setCreatedBy(APPConstants.SYSTEM_STR);
			userRoleEntity.setCreatedTs(new Date());
			Set<UserRoleEntity> userRoleEntities = new HashSet<UserRoleEntity>();
			userRoleEntities.add(userRoleEntity);
			userEntity.setUserRoleEntities(userRoleEntities);
			
			userDAO.saveUser(userEntity);
			tempUserEntity.setPendingApproval(false);
			userDAO.updateTempUser(tempUserEntity);
			
			String token = generateAuthToken(userEntity);
			sendOnboardingEmail(userEntity,token);
			userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.ONBOARD.APPROVAL.NEXTLEVEL.CODE",null, Locale.getDefault())));
			userSVCResp.setresultString(messageSource.getMessage("USER.ONBOARD.APPROVAL.COMPLETED.STRING",null, Locale.getDefault()));
		} }
		
		OnboardingUserNotesEntity onboardingUserNotesEntity = new OnboardingUserNotesEntity();
		System.out.println(tempUserEntity.getEmployeeId());
		onboardingUserNotesEntity.setTempUserEntity(tempUserEntity);
		onboardingUserNotesEntity.setNotes(tempUserDTO.getNotes());
		onboardingUserNotesEntity.setCreatedBy(tempUserDTO.getCreatedBy());
		onboardingUserNotesEntity.setCreatedTs(new Date());
		commonDAO.saveOnboardingNotes(onboardingUserNotesEntity);
		
		return userSVCResp;
	}
	
	private boolean sendOnboardingEmail(UserEntity userEntity,String token)
	{
		String toEmail = null;
		boolean returnVal = true;
		if(userEntity.getEmailId() != null)
			toEmail = userEntity.getEmailId();
		else
			toEmail = userEntity.getUserId();
		String emailBody = APPConstants.ONBOARD_EMAIL_TEXT;
		String url = environment.getRequiredProperty("hostName")+environment.getRequiredProperty("complete.onboard.url").replace("tokenParam", token);
		emailBody = emailBody.replace("url", url);
		emailBody = emailBody.replace("Orgname", environment.getRequiredProperty("org.name"));
		
		SendEmail.sendHTMLEmail(toEmail, APPConstants.ONBOARD_EMAIL_SUB, emailBody);
		return returnVal;
	}

	@Override
	@Transactional
	public UserSVCResp getOnboardUser(String token) {
		SecauthtokenEntity secauthtokenEntity = userDAO.getAuthToken(token);
		UserSVCResp userSVCResp = new UserSVCResp();
		UserEntity userEntity = null;
		if(secauthtokenEntity != null)
		{
			userEntity =  secauthtokenEntity.getUserEntity();
			userSVCResp.setUser(CopyBeanProperties.copyUserPerperties(userEntity));
			userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.ID.AVAILABLE.CODE",null, Locale.getDefault())));
			userSVCResp.setresultString(messageSource.getMessage("USER.ID.AVAILABLE.STRING",null, Locale.getDefault()));
		}
		else
		{
			userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.ID.NOTAVAILABLE.CODE",null, Locale.getDefault())));
			userSVCResp.setresultString(messageSource.getMessage("USER.ID.NOTAVAILABLE.STRING",null, Locale.getDefault()));
		}
		return userSVCResp;
	}

	@Override
	public Set<OnboardingUserNotesDTO> getTempUserNotes(String id) {
		Set<OnboardingUserNotesDTO> onboardingUserNotesDTOs = new HashSet<OnboardingUserNotesDTO>();
		
		//List<OnboardingUserNotesEntity> onboardingUserNotesEntities =  userDAO.
		
		return null;
	}

}
