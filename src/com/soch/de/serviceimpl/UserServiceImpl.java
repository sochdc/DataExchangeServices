package com.soch.de.serviceimpl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.soch.de.dao.CommonDAO;
import com.soch.de.dao.UserDAO;
import com.soch.de.domain.AddressEntity;
import com.soch.de.domain.ConfigEntity;
import com.soch.de.domain.DemoUserEntity;
import com.soch.de.domain.LoginEntity;
import com.soch.de.domain.OTPEntity;
import com.soch.de.domain.OnboardApprovalAuditEntity;
import com.soch.de.domain.OnboardApprovalPendingEntity;
import com.soch.de.domain.OnboardingApprovalEntity;
import com.soch.de.domain.OnboardingUserNotesEntity;
import com.soch.de.domain.PolicySrcEntity;
import com.soch.de.domain.PwdHistoryEntity;
import com.soch.de.domain.QuestionaireEntity;
import com.soch.de.domain.RolesEntity;
import com.soch.de.domain.SecauthtokenEntity;
import com.soch.de.domain.SecurityQAEntity;
import com.soch.de.domain.TempUserEntity;
import com.soch.de.domain.TempUserRoleEntity;
import com.soch.de.domain.UserActivityEntity;
import com.soch.de.domain.UserEntity;
import com.soch.de.domain.UserFileEntity;
import com.soch.de.domain.UserNotesEntity;
import com.soch.de.domain.UserRoleEntity;
import com.soch.de.domain.UserWorkEntity;
import com.soch.uam.dto.AddressDTO;
import com.soch.uam.dto.ConfigDTO;
import com.soch.uam.dto.OnboardApprovalPendingDTO;
import com.soch.uam.dto.OnboardApprovedDTO;
import com.soch.uam.dto.OnboardingApprovalDTO;
import com.soch.uam.dto.OnboardingUserNotesDTO;
import com.soch.uam.dto.RoleDTO;
import com.soch.uam.dto.SecurityQADTO;
import com.soch.uam.dto.TempUserDTO;
import com.soch.uam.dto.UserDTO;
import com.soch.de.exception.InternalErrorException;
import com.soch.uam.request.AddUserRoleReq;
import com.soch.uam.request.ContactUsReq;
import com.soch.uam.request.UserProfileResp;
import com.soch.uam.response.OnboardingReq;
import com.soch.uam.response.OnboardingUserFile;
import com.soch.uam.response.UserReq;
import com.soch.uam.response.UserSVCResp;
import com.soch.de.service.UserService;
import com.soch.de.svc.constants.APPConstants;
import com.soch.de.util.AppUtil;
import com.soch.de.util.CopyBeanProperties;
import com.soch.de.util.EncryptUtil;
import com.soch.de.util.OTPGenerator;
import com.soch.de.util.POJOCacheUtil;
import com.soch.de.util.SendEmail;

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
	 private static Map<String, Object> externalPolicyMap = new HashMap<String, Object>();
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
			if(APPConstants.SOURCE_INTERNAL.equalsIgnoreCase(userDTO.getSource()))
			{
				userEntity = userDAO.getUser(userDTO.getUserId());
				userEntity.setPassowrd(userDTO.getPassowrd());
			}
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
			if(APPConstants.SOURCE_INTERNAL.equalsIgnoreCase(userDTO.getSource()))
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
		try {
			Date date = javaDateFormat.parse(javaDate);
			return date;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private Date convertCalDate(String javaDate)
	{
		SimpleDateFormat javaDateFormat = new SimpleDateFormat("MM/dd/yyyy");
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
		UserActivityEntity userActivityEntity = new UserActivityEntity();
		
		//Map<String, String> cacheMap = pojoCacheUtil.getAppConfig();
		if(userEntity!= null )
		{
			returnUserDTO.setId(userEntity.getId());
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
			} else
				try {
					if(EncryptUtil.decrypt(userEntity.getPassowrd()).equals(userDTO.getPassowrd()))
					{	
						if(policyMap.isEmpty())
						{
							getInternalPolicies();
						}
						
						Integer expDays =  Integer.parseInt(policyMap.get("Password Maximum Age (x) Days").toString());
						returnUserDTO = CopyBeanProperties.copyUserPerperties(userEntity);
						/*if(userEntity.isPwdChangeFlag())
						{
							returnUserDTO.setPwdChangeFlag(true);
						}
						else*/ 
						long lastPwdChange = AppUtil.calculateDaysTillToday(userEntity.getPwdHistoryEntities().iterator().next().getCreatedTs());
						if(expDays < lastPwdChange)
						{
							returnUserDTO.setPwdChangeFlag(true);
							userEntity.setPwdChangeFlag(true);
							userDAO.updateUser(userEntity);
						}else
						{
						String authToken =generateAuthToken(userEntity);
					
					returnUserDTO.setToken(authToken);
					Set<UserRoleEntity> userRoleEntities = userEntity.getUserRoleEntities();
					for(UserRoleEntity userRoleEntity :userRoleEntities)
					{
						String appName = null;
						if(userDTO.getAppName() != null)
							appName = environment.getRequiredProperty(userDTO.getAppName());
						if(appName != null && userRoleEntity.getRolesEntity().getSystemEntity().getSystemName().equalsIgnoreCase(appName) && 
								userRoleEntity.isActiveStatus()){
							returnUserDTO.setRoleName(userRoleEntity.getRolesEntity().getRoleName());
							
							if(userRoleEntity.getRolesEntity().getRoleName().equalsIgnoreCase("Admin"))
							{
								returnUserDTO.setRoleName(userRoleEntity.getRolesEntity().getRoleName());
									returnUserDTO.setAccess(1);
									break;
							}
							else if(userRoleEntity.getRolesEntity().getRoleName().equalsIgnoreCase("Requestor"))
							{
								returnUserDTO.setAccess(2);
								break;
							}
							else if(userRoleEntity.getRolesEntity().getRoleName().equalsIgnoreCase("Approve"))
							{
								returnUserDTO.setAccess(3);
								break;
							}
							else if(userRoleEntity.getRolesEntity().getRoleName().equalsIgnoreCase("Implementor"))
							{
								returnUserDTO.setAccess(3);
								break;
							}
							else if(userRoleEntity.getRolesEntity().getRoleName().equalsIgnoreCase("Report"))
							{
								returnUserDTO.setAccess(4);
								returnUserDTO.setDepartment(userRoleEntity.getRolesEntity().getSystemEntity().getDeptEntity().getDeptName());
								returnUserDTO.setRoleName(userRoleEntity.getRolesEntity().getRoleName());
								break;
							}
							
						}
						else if(userRoleEntity.isActiveStatus()){
						if(userRoleEntity.getRolesEntity().getRoleName().equalsIgnoreCase("Admin"))
						{
							returnUserDTO.setRoleName(userRoleEntity.getRolesEntity().getRoleName());
								returnUserDTO.setAccess(1);
								break;
						}
						else if(userRoleEntity.getRolesEntity().getRoleName().equalsIgnoreCase("Requestor"))
						{
							returnUserDTO.setAccess(2);
							break;
						}
						else if(userRoleEntity.getRolesEntity().getRoleName().equalsIgnoreCase("Approve"))
						{
							returnUserDTO.setAccess(3);
							break;
						}
						else if(userRoleEntity.getRolesEntity().getRoleName().equalsIgnoreCase("Implementor"))
						{
							returnUserDTO.setAccess(3);
							break;
						}
						else if(userRoleEntity.getRolesEntity().getRoleName().equalsIgnoreCase("Report"))
						{
							returnUserDTO.setDepartment(userRoleEntity.getRolesEntity().getSystemEntity().getDeptEntity().getDeptName());
							returnUserDTO.setRoleName(userRoleEntity.getRolesEntity().getRoleName());
							break;
						}
						}
						
					}
						
					LoginEntity loginEntity = new LoginEntity();
					loginEntity.setUserEntity(userEntity);
					loginEntity.setLoginTs(getCurrentDate());
					loginEntity.setLoginStatus(true);
					
					userDAO.saveUserLogin(loginEntity);
					
					if(userEntity.getLogintEntity()!= null && userEntity.getLogintEntity().size() > 1)
					{
						Iterator<LoginEntity> loginIter = userEntity.getLogintEntity().iterator();
						LoginEntity loginEntity2 = loginIter.next();
						loginEntity2 = loginIter.next();
						returnUserDTO.setLastLoggedin(getLocalTime(loginEntity2.getLoginTs()));
					}
					if(userEntity.getLoginFailureCount() >0 )
					{
						userEntity.setLoginFailureCount(0);
						userDAO.updateUser(userEntity);
					}
					userActivityEntity.setUserEntity(userEntity);
					userActivityEntity.setActivityType(APPConstants.LOGIN_SUCCESS_ACTIVITY);
					userActivityEntity.setActivityCreateTs(new Date());
					userActivityEntity.setActivityCreatedBy(userEntity.getUserId());
					userActivityEntity.setActivityStatus(true);
					userDAO.saveUserActivity(userActivityEntity);
						}
					}
					else
					{
						userEntity.setLoginFailureCount(userEntity.getLoginFailureCount()+1);
						getInternalPolicies();
						if(userEntity.getLoginFailureCount() >= Integer.parseInt((String) policyMap.get("Consecutive Login Failure Limit (x)")))
						{
							userEntity.setLockFlag(true);
							returnUserDTO.setLockFlag(userEntity.getLockFlag());
							returnUserDTO.setActiveFlag(userEntity.getActiveFlag());
							returnUserDTO.setMaxAttemptReached(true);
							returnUserDTO.setUserId(userEntity.getUserId());
							returnUserDTO.setId(userEntity.getId());
						}
						userDAO.updateUser(userEntity);
						userActivityEntity.setUserEntity(userEntity);
						userActivityEntity.setActivityType(APPConstants.LOGIN_FAIL_ACTIVITY);
						userActivityEntity.setActivityCreateTs(new Date());
						userActivityEntity.setActivityCreatedBy(userEntity.getUserId());
						userActivityEntity.setActivityStatus(false);
						userDAO.saveUserActivity(userActivityEntity);
					}
				} catch (NumberFormatException | UnsupportedEncodingException | GeneralSecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
		UserActivityEntity userActivityEntity = null;
		if(userId!= null)
		{
			returnVal = true;
			sendForgotUserIDEmail(userId, emailId);
			UserEntity userEntity = userDAO.getUser(userId);
			userActivityEntity = new UserActivityEntity();
			userActivityEntity.setUserEntity(userEntity);
			userActivityEntity.setActivityType(APPConstants.FORGOT_USER_ACTIVITY);
			userActivityEntity.setActivityCreateTs(new Date());
			userActivityEntity.setActivityCreatedBy(userEntity.getUserId());
			userDAO.saveUserActivity(userActivityEntity);
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
	public UserDTO forgotPassword(String userId,boolean tempPwd) {
		UserDTO userDTO = new UserDTO();
		Set<SecurityQADTO> securityQADTOs = new HashSet<SecurityQADTO>();
		Set<SecurityQADTO> securityQADTOs2 = new HashSet<SecurityQADTO>();
		UserActivityEntity userActivityEntity = null;
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
				if(!tempPwd) 
				{
					if(securityQADTOs!= null && !securityQADTOs.isEmpty())
					{
					SecurityQADTO[] securityQADTOArray = securityQADTOs.toArray(new SecurityQADTO[securityQADTOs.size()]);
					int index = getInex(securityQADTOs.size());
					
					SecurityQADTO securityQADTO = securityQADTOArray[index];
					
					securityQADTOs2.add(securityQADTO);
					
					userDTO.setSecurityQA(securityQADTOs2);
					userDTO.setSecurityQASelected(true);
					}
					else
					{
						userDTO.setSecurityQASelected(false);
					}
				}
				else
				{
					userDTO.setTempPassword(true);
					String tempPassword = generatePassword();
					try {
						String encryptedPWD = EncryptUtil.encrypt(tempPassword);
						userEntity.setPassowrd(encryptedPWD);
						userEntity.setPwdChangeFlag(true);
						userDAO.updateUser(userEntity);
						sendTempPassword(userEntity,tempPassword);
					} catch (UnsupportedEncodingException | GeneralSecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
				userActivityEntity = new UserActivityEntity();
				userActivityEntity.setUserEntity(userEntity);
				userActivityEntity.setActivityType(APPConstants.FORGOT_USER_PASSWORD);
				userActivityEntity.setActivityCreateTs(new Date());
				userActivityEntity.setActivityCreatedBy(userEntity.getUserId());
				userDAO.saveUserActivity(userActivityEntity);
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
		maxAttempts = Integer.parseInt((String) policyMap.get("Forgot Password Process Limit (x)"));
	
		/*if( policyMap.isEmpty())
		{
			getPolicies();
			maxAttempts = Integer.parseInt((String) policyMap.get("FGT_PWD_ATEMPTS"));
		}
		else
		{
			maxAttempts = Integer.parseInt((String) policyMap.get("FGT_PWD_ATEMPTS"));
		}*/
		
		System.out.println("maxAttempts "+Integer.parseInt((String) policyMap.get("Forgot Password Process Limit (x)")));
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
		List<PolicySrcEntity> policyConfigEntities = userDAO.getPolicies();
		
			for(PolicySrcEntity policyConfigEntity : policyConfigEntities)
			{
				policyMap.put(policyConfigEntity.getPolicyName(), policyConfigEntity.getCustomVal());
			}
		
	}
	
	
	private void getInternalPolicies()
	{
		List<PolicySrcEntity> policyConfigEntities = userDAO.getPolicies();
		
			for(PolicySrcEntity policyConfigEntity : policyConfigEntities)
			{
				if(policyConfigEntity.getPolicyGrpEntity().getUserTypeEntity().getTypeName().equalsIgnoreCase("internal"))
					policyMap.put(policyConfigEntity.getPolicyName(), policyConfigEntity.getCustomVal());
				if(policyConfigEntity.getPolicyGrpEntity().getUserTypeEntity().getTypeName().equalsIgnoreCase("External"))
					externalPolicyMap.put(policyConfigEntity.getPolicyName(), policyConfigEntity.getCustomVal());
				
			}
			
			
		
	}
	
	/*private void getExternalPolicies()
	{
		List<PolicySrcEntity> policyConfigEntities = userDAO.getPolicies();
		
			for(PolicySrcEntity policyConfigEntity : policyConfigEntities)
			{
				if(policyConfigEntity.getPolicyGrpEntity().getUserTypeEntity().getTypeName().equalsIgnoreCase("External"))
					externalPolicyMap.put(policyConfigEntity.getPolicyName(), policyConfigEntity.getCustomVal());
			}
		
	}*/

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
			validMins = Integer.parseInt((String) policyMap.get("One Time Password (OTP) Expiry (x) Hours"));
		}
		
		emailBody = emailBody.replaceAll("30", validMins.toString());
		
		SendEmail.sendHTMLEmail(toEmail, APPConstants.OTP_EMAIL_SUB, emailBody);
		return returnVal;
	}
	
	
	private boolean sendTempPassword(UserEntity userEntity,String tempPwd)
	{
		
		String toEmail = null;
		boolean returnVal = true;
		if(userEntity.getEmailId() != null)
			toEmail = userEntity.getEmailId();
		else
			toEmail = userEntity.getUserId();
		String emailBody = APPConstants.TEMP_PWD_EMAIL_TEXT;
		emailBody = emailBody.replaceAll("otpwd", tempPwd);
		getPolicies();
		Integer validMins = new Integer(0);
		if(policyMap.size() > 0)
		{
			validMins = Integer.parseInt((String) policyMap.get("One Time Password (OTP) Expiry (x) Hours"));
		}
		
		emailBody = emailBody.replaceAll("30", validMins.toString());
		
		SendEmail.sendHTMLEmail(toEmail, APPConstants.OTP_EMAIL_SUB, emailBody);
		return true;
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
			validMins = Integer.parseInt((String) policyMap.get("One Time Password (OTP) Expiry (x) Hours"));
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
	public int resetPwd(UserDTO user) {
		PwdHistoryEntity pwdHistoryEntity = null;
		Set<PwdHistoryEntity> pwdHistoryEntities = null;
		SecauthtokenEntity secauthtokenEntity = userDAO.getAuthToken(user.getToken());
		System.out.println("secauthtokenEntity "+secauthtokenEntity);
		if(secauthtokenEntity != null) {
		UserEntity userEntity = secauthtokenEntity.getUserEntity();
		UserActivityEntity userActivityEntity = null;
		getPolicies();
		int count =  10;
		if(policyMap.get("New Password Cannot Contain (x) Characters From Any Previous Password") != null)
		count = Integer.parseInt(policyMap.get("New Password Cannot Contain (x) Characters From Any Previous Password").toString());
		try {
		if(userDAO.checkPreviousPassword(EncryptUtil.encrypt(user.getPassowrd()),userEntity.getId(),count))
		{
			String passwordStr;
			
				passwordStr = EncryptUtil.encrypt(user.getPassowrd());
				userEntity.setPwdChangeFlag(false);
			userEntity.setPassowrd(passwordStr);
			secauthtokenEntity.setUserEntity(userEntity);
			pwdHistoryEntity =  new PwdHistoryEntity();
			pwdHistoryEntity.setCreatedTs(new Date());
			pwdHistoryEntity.setPassword(passwordStr);
			pwdHistoryEntity.setUserEntity(userEntity);
			pwdHistoryEntity.setCreatedBy(userEntity.getUserId());
			secauthtokenEntity.setStatus(false);
			pwdHistoryEntities = userEntity.getPwdHistoryEntities();
			pwdHistoryEntities.add(pwdHistoryEntity);
			userDAO.updateUser(userEntity);
			userDAO.updateAuthToken(secauthtokenEntity);
			
			userActivityEntity = new UserActivityEntity();
			userActivityEntity.setUserEntity(userEntity);
			userActivityEntity.setActivityType(APPConstants.PASSWORD_RESET_ACTIVITY);
			userActivityEntity.setActivityCreateTs(new Date());
			userActivityEntity.setActivityCreatedBy(userEntity.getUserId());
			userDAO.saveUserActivity(userActivityEntity);
			
			
			return 0;
			}
			else
			{
				return 1;
			}
		} catch (UnsupportedEncodingException | GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 2;
		}
		}
		else
		{
			return 2;
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
	public UserProfileResp searchUser(UserReq userReq) {
		UserEntity userEntity =  userDAO.getUser(userReq.getUserId());
		UserProfileResp userProfileResp = new UserProfileResp();
		if(userEntity != null)
		{
			userProfileResp = CopyBeanProperties.createUserProfile(userEntity);
		}
		return userProfileResp;
	}

	@Override
	@Transactional
	public UserDTO changeUserStatus(UserReq userReq) {
		UserDTO userDTO = null;
		
		UserEntity userEntity= userDAO.getUser(userReq.getEditUserId());
		if(userEntity.getActiveFlag())
			userEntity.setActiveFlag(false);
		else
			userEntity.setActiveFlag(true);
		
		
		UserNotesEntity userNotesEntity = new UserNotesEntity();
		userNotesEntity.setNotes(userReq.getNotes());
		userNotesEntity.setCreatedBy(userReq.getNotesuserId());
		userNotesEntity.setCreatedTs(new Date());
		userNotesEntity.setUserEntity(userEntity);
		
		Set<UserNotesEntity> userNotesEntities = new HashSet<UserNotesEntity>(0);
		userNotesEntities.add(userNotesEntity);
		userEntity.setUserNotesEntities(userNotesEntities);
		
		
		userDAO.updateUser(userEntity);
		userDTO = CopyBeanProperties.copyUserPerperties(userEntity);
		return userDTO;
	}

	@Override
	@Transactional
	public UserDTO changeLockStatus(UserReq userReq) {
		UserDTO userDTO = null;

		UserEntity userEntity = userDAO.getUser(userReq.getEditUserId());
		if (userEntity.getLockFlag())
		{
			userEntity.setLockFlag(false);
			userEntity.setLoginFailureCount(0);
		}
		else
			userEntity.setLockFlag(true);
		
		UserNotesEntity userNotesEntity = new UserNotesEntity();
		userNotesEntity.setNotes(userReq.getNotes());
		userNotesEntity.setCreatedBy(userReq.getNotesuserId());
		userNotesEntity.setCreatedTs(new Date());
		userNotesEntity.setUserEntity(userEntity);
		
		Set<UserNotesEntity> userNotesEntities = new HashSet<UserNotesEntity>(0);
		userNotesEntities.add(userNotesEntity);
		userEntity.setUserNotesEntities(userNotesEntities);
		
		userDAO.updateUser(userEntity);
		userDTO = CopyBeanProperties.copyUserPerperties(userEntity);
		return userDTO;
	}

	@Override
	@Transactional
	public UserDTO resetUserPwd(UserReq userReq) {
		UserDTO userDTO = null;
		try {
		UserActivityEntity userActivityEntity = null;
		UserEntity userEntity = userDAO.getUser(userReq.getEditUserId());
		String pwd =generatePassword();
		UserNotesEntity userNotesEntity = new UserNotesEntity();
		userNotesEntity.setNotes(userReq.getNotes());
		userNotesEntity.setCreatedBy(userReq.getNotesuserId());
		userNotesEntity.setCreatedTs(new Date());
		userNotesEntity.setUserEntity(userEntity);
		
		Set<UserNotesEntity> userNotesEntities = new HashSet<UserNotesEntity>(0);
		userNotesEntities.add(userNotesEntity);
		userEntity.setUserNotesEntities(userNotesEntities);
		
		userActivityEntity = new UserActivityEntity();
		userActivityEntity.setUserEntity(userEntity);
		userActivityEntity.setActivityType(APPConstants.PASSWORD_RESET_ACTIVITY);
		userActivityEntity.setActivityCreateTs(new Date());
		userActivityEntity.setActivityCreatedBy(userEntity.getUserId());
		Set<UserActivityEntity> userActivityEntities = new HashSet<UserActivityEntity>(0);
		userActivityEntities.add(userActivityEntity);
		userEntity.setUserActivityEntities(userActivityEntities);
		
		Set<PwdHistoryEntity> pwdHistoryEntities = new HashSet<PwdHistoryEntity>(0);
		
		PwdHistoryEntity pwdHistoryEntity =  new PwdHistoryEntity();
		pwdHistoryEntity.setCreatedTs(new Date());
		pwdHistoryEntity.setPassword(userEntity.getPassowrd());
		pwdHistoryEntity.setUserEntity(userEntity);
		pwdHistoryEntity.setCreatedBy(userEntity.getUserId());
		pwdHistoryEntities = userEntity.getPwdHistoryEntities();
		pwdHistoryEntities.add(pwdHistoryEntity);
		userEntity.setPwdHistoryEntities(pwdHistoryEntities);
		pwd = EncryptUtil.encrypt(pwd);
		userEntity.setPassowrd(pwd);
		userEntity.setPwdChangeFlag(true);
		userEntity.setUserNotesEntities(userNotesEntities);
		userDAO.updateUser(userEntity);
		//userDAO.saveUserActivity(userActivityEntity);
		userDTO = CopyBeanProperties.copyUserPerperties(userEntity);
		} catch (UnsupportedEncodingException | GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userDTO;
	}
	
	private String generatePassword()
	{
		 char[] potential = new char[10]; // 32768 is not huge....
		    int size = 0;
		    //final Pattern pattern = Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$");  
		    final Pattern pattern = Pattern.compile(getPasswordPolicy().getPwdRegExp());
		    
		   /* for (int c = 0; c <= 10; c++) {
		        if (pattern.matcher(String.valueOf((char)c)).matches()) {
		            potential[size++] = (char)c;
		        }
		    }*/
		    
			StringBuffer sb = new StringBuffer();
			
			String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
			
			String lower = "abcdefghijklmnopqrstuvwxyz";
			
			String numbers = "0123456789";
			
			String splChars = "@#$%^&+=.-_*!";
			
			Integer maxLen =Integer.parseInt((String) policyMap.get("Password Minimum Length (x) Characters"));
			
			int number = 0;
			Random rd = new Random();
			for(int index = 0; index < maxLen ; index++) {
				
				 number = getRandomNumberInRange(1,4);
					
					if(policyMap.get("Allow Upper Case Letters").toString().equalsIgnoreCase("YES") && number == 1)
					{
						sb.append(upper.charAt(rd.nextInt(upper.length())));
					}
					
					if(policyMap.get("Allow Lower Case Letters").toString().equalsIgnoreCase("YES") && number == 2)
					{
						sb.append(lower.charAt(rd.nextInt(lower.length())));
					}
					if(policyMap.get("Allow Special Characters (@#$%^&+=-_*!)").toString().equalsIgnoreCase("YES") && number == 3)
					{
						sb.append(splChars.charAt(rd.nextInt(splChars.length())));
					}
					if(policyMap.get("Allow Numeric Characters").toString().equalsIgnoreCase("YES") && number == 4)
					{
						sb.append(numbers.charAt(rd.nextInt(numbers.length())));
					}
			
			}
		    
		    return sb.toString();
		    
	}
	
	private static int getRandomNumberInRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
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
		passwordString.append("Password must contain ");
		if(policyMap.get("Allow Special Characters (@#$%^&+=-_*!)").toString().equalsIgnoreCase("YES"))
		{
			passwordRegExpr =  passwordRegExpr.append("@#$%^&+=.\\-_*!");
			passwordString.append("At least one special character");
		}
		
		if(policyMap.get("Allow Upper Case Letters").toString().equalsIgnoreCase("YES"))
		{
			passwordRegExpr =  passwordRegExpr.append("A-Z");
			if(passwordString.length() > 0)
				passwordString.append(", ");
			passwordString.append(" At least one upper case letter");
		}
		
		if(policyMap.get("Allow Lower Case Letters").toString().equalsIgnoreCase("YES"))
		{
			passwordRegExpr =  passwordRegExpr.append("a-z");
			if(passwordString.length() > 0)
				passwordString.append(", ");
			passwordString.append("At least one lower case letter");
		}
		
		if(policyMap.get("Allow Numeric Characters").toString().equalsIgnoreCase("YES"))
		{
			passwordRegExpr = passwordRegExpr.append("0-9");
			if(passwordString.length() > 0)
				passwordString.append(", ");
			passwordString.append("at least one number ");
		}
		
		//([a-zA-Z0-9@#$%^&+=*.\-_])
	
		passwordRegExpr = passwordRegExpr.append("])");
		//passwordRegExpr = passwordRegExpr.append("([a-zA-Z0-9])");
		passwordRegExpr = passwordRegExpr.append("{");
		if(passwordString.length() > 0)
			passwordString.append(", ");
		passwordString.append(" Must contain minimum "+policyMap.get("Password Minimum Length (x) Characters")+" characters, maximum "+policyMap.get("Password Maximum Length (x) Characters")+" characters.");
		passwordRegExpr = passwordRegExpr.append(policyMap.get("Password Minimum Length (x) Characters")+",");
		passwordRegExpr = passwordRegExpr.append(policyMap.get("Password Maximum Length (x) Characters")+"}$");
		userSVCResp.setPwdRegExp(passwordRegExpr.toString());
		userSVCResp.setPwdString(passwordString.toString());
		
		
		 //getExternalPolicies();
			 passwordRegExpr = new StringBuffer();
			 passwordString = new StringBuffer();
			//^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=.\-_*!()])([a-zA-Z0-9@#$%^&+=*.\-_!()]){3,}$
			passwordRegExpr = passwordRegExpr.append("^([");
			passwordString.append("Password must contain ");
			/*if(externalPolicyMap!=null && externalPolicyMap.get("Allow Special Characters (@#$%^&+=-_*!)") != null &&
					externalPolicyMap.get("Allow Special Characters (@#$%^&+=-_*!)").toString().equalsIgnoreCase("YES"))
			{
				passwordRegExpr =  passwordRegExpr.append("@#$%^&+=.\\-_*!");
				passwordString.append("At least one special character");
			}
			
			if(externalPolicyMap!=null && externalPolicyMap.get("Allow Upper Case Letters").toString().equalsIgnoreCase("YES"))
			{
				passwordRegExpr =  passwordRegExpr.append("A-Z");
				if(passwordString.length() > 0)
					passwordString.append(", ");
				passwordString.append(" At least one upper case letter");
			}
			
			if(externalPolicyMap!=null && externalPolicyMap.get("Allow Lower Case Letters").toString().equalsIgnoreCase("YES"))
			{
				passwordRegExpr =  passwordRegExpr.append("a-z");
				if(passwordString.length() > 0)
					passwordString.append(", ");
				passwordString.append("At least one lower case letter");
			}
			
			if(externalPolicyMap!=null && externalPolicyMap.get("Allow Numeric Characters").toString().equalsIgnoreCase("YES"))
			{
				passwordRegExpr = passwordRegExpr.append("0-9");
				if(passwordString.length() > 0)
					passwordString.append(", ");
				passwordString.append("at least one number ");
			}*/
			
			//([a-zA-Z0-9@#$%^&+=*.\-_])
		
			passwordRegExpr = passwordRegExpr.append("])");
			//passwordRegExpr = passwordRegExpr.append("([a-zA-Z0-9])");
			passwordRegExpr = passwordRegExpr.append("{");
			if(passwordString.length() > 0)
				passwordString.append(", ");
			passwordString.append(" Must contain minimum "+externalPolicyMap.get("Password Minimum Length (x) Characters")+" characters, maximum "+externalPolicyMap.get("Password Maximum Length (x) Characters")+" characters.");
			passwordRegExpr = passwordRegExpr.append(externalPolicyMap.get("Password Minimum Length (x) Characters")+",");
			passwordRegExpr = passwordRegExpr.append(externalPolicyMap.get("Password Maximum Length (x) Characters")+"}$");
			userSVCResp.setExternalPwdRegExp(passwordRegExpr.toString());
			userSVCResp.setExternalPwdString(passwordString.toString());
			
		
		userSVCResp.setUserInactivateTime( Integer.parseInt(policyMap.get("User Session Inactivity (x) Minutes").toString()));
		return userSVCResp;
	}

	@Override
	@Transactional
	public Integer onBoardingUser(OnboardingReq onboardingReq) {
		
		
		
		Integer role[] = onboardingReq.getRole();
		
		List<String> roles = new ArrayList<String>(0);
		
		for(Integer roleId : role)
		{
			//roles[index++] = roleDTO.getRoleName();
			RolesEntity rolesEntity= commonDAO.getRoleById(roleId);
			roles.add(rolesEntity.getRoleName());
		}
		
		if(roles.contains(APPConstants.SR_AP_SPECIALIST) && roles.contains(APPConstants.SR_AR_SPECIALIST))
		{
			return 1;
		}
		
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
		tempUserEntity.setReportingTo(onboardingReq.getReportingTo());
		tempUserEntity.setPendingApproval(true);
		tempUserEntity.setAccountType(onboardingReq.getAccountType());
		tempUserEntity.setAccountSubType(onboardingReq.getAccountSubType());
		tempUserEntity.setCreatedTs(new Date());
		tempUserEntity.setSendingTo(onboardingReq.getSendingTo());
		tempUserEntity.setReceivingFrom(onboardingReq.getReceivingFrom());
		tempUserEntity.setBusinessJustification(onboardingReq.getBusinessJustification());
		tempUserEntity.setTypeOfDataExchange(onboardingReq.getTypeOfDataExchange());
		tempUserEntity.setEndDate(onboardingReq.getEndDate());
		tempUserEntity.setContractCompanyName(onboardingReq.getContractCompanyName());
		if(onboardingReq.getPhiData() != null)
			tempUserEntity.setPhiData(true);
		else
			tempUserEntity.setPhiData(false);
		if(onboardingReq.getCompanyName() != 0)
			tempUserEntity.setContractCompanyEntity(commonDAO.getContractCompanyEntity(onboardingReq.getCompanyName()));
		
		//tempUserEntity.setUserRoleId(onboardingReq.getRole());
		tempUserEntity.setTempUserRoleEntities(getTempUSerRole(onboardingReq,tempUserEntity));
		OnboardingUserNotesEntity onboardingUserNotesEntity = new OnboardingUserNotesEntity();
		
		onboardingUserNotesEntity.setTempUserEntity(tempUserEntity);
		onboardingUserNotesEntity.setNotes(onboardingReq.getNotes());
		onboardingUserNotesEntity.setCreatedBy(onboardingReq.getCreatedBy());
		onboardingUserNotesEntity.setCreatedTs(new Date());
		Set<OnboardingUserNotesEntity> notesEntities = new HashSet<OnboardingUserNotesEntity>();
		notesEntities.add(onboardingUserNotesEntity);
		tempUserEntity.setOnboardingUserNotesEntities(notesEntities) ;
		/*commonDAO.saveOnboardingNotes(onboardingUserNotesEntity);*/
		
		/*Iterator<OnboardingUserFile> userFileIterator =  onboardingReq.getUserFile().iterator();
		OnboardingUserFile userFile;
		FileInputStream inputStream;
		File fileContent;
		
		String fileName;*/
		UserFileEntity userFileEntity = null;
		Set<UserFileEntity> userFileEntities = new HashSet<UserFileEntity>();
		int fileLength =  0;
		if(onboardingReq.getFileContent() != null)
		fileLength = onboardingReq.getFileContent().length;
		String[] nameArray = null;
		for(int index = 0; index < fileLength ; index++)
		{
			userFileEntity = new UserFileEntity();
			String fileName = onboardingReq.getAttachment()[index];
			if(fileName.contains("\\"))
			{
				nameArray = fileName.split("\\\\");
				fileName = nameArray[nameArray.length-1];
			}
			else
			{
				nameArray = fileName.split("/");
				fileName = nameArray[nameArray.length-1];
			}
			userFileEntity.setContent(DatatypeConverter.parseBase64Binary(onboardingReq.getFileContent()[index]));
			userFileEntity.setFileName(fileName);
			userFileEntity.setTempUserEntity(tempUserEntity);
			userFileEntities.add(userFileEntity);
			//commonDAO.insertUserFile(userFileEntity);
		}
		tempUserEntity.setUserFileEntities(userFileEntities);
	/*	while(userFileIterator.hasNext())
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
		}*/
		userDAO.addTempUser(tempUserEntity);
		createApprovalReq(onboardingReq.getRole(), tempUserEntity);
		return 0;
	}
	
	private Set<TempUserRoleEntity> getTempUSerRole(OnboardingReq onboardingReq,TempUserEntity tempUserEntity)
	{
		Set<RoleDTO> roleDTOs = onboardingReq.getRoles();
		Set<TempUserRoleEntity> tempUserRoleEntities = new HashSet<TempUserRoleEntity>();
		TempUserRoleEntity tempUserRoleEntity ;
		for(int index = 0; index < onboardingReq.getRole().length; index++)
		{
			
			tempUserRoleEntity = new TempUserRoleEntity();
			tempUserRoleEntity.setRolesEntity(commonDAO.getRoleById(onboardingReq.getRole()[index]));
			tempUserRoleEntity.setTempUserEntity(tempUserEntity);
			//userDAO.saveTempUserRole(tempUserRoleEntity);
			tempUserRoleEntities.add(tempUserRoleEntity);
		}
		return tempUserRoleEntities;
	}
	
	/**
	 * 
	 * @param roleId
	 */
	/*private void createApprovalReq(int roleId, TempUserEntity tempUserEntity)
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
	}*/
	
	private void createApprovalReq(Integer[] roles, TempUserEntity tempUserEntity)
	{
		for(int index=0; index < roles.length;index++){
			//List<OnboardingApprovalEntity> onboardingApprovalEntities = commonDAO.getOnboardApproval(roles[index],1);
			OnboardingApprovalEntity onboardingApprovalEntity = commonDAO.getOnboardApproval(roles[index],1);
			UserEntity userEntity = null;
			
			userEntity = onboardingApprovalEntity.getUserEntity();
			OnboardApprovalPendingEntity onboardApprovalPendingEntity = new OnboardApprovalPendingEntity();
			onboardApprovalPendingEntity.setUserEntity(userEntity);
			onboardApprovalPendingEntity.setTempUserEntity(tempUserEntity);
			onboardApprovalPendingEntity.setRequestDate(new Date());
			onboardApprovalPendingEntity.setRequestType("Onboarding Approval");
			onboardApprovalPendingEntity.setPendingApproval(true);
			onboardApprovalPendingEntity.setLevel(1);
			onboardApprovalPendingEntity.setRoleID(roles[index]);
			commonDAO.saveOnboardApprovalPendingEntity(onboardApprovalPendingEntity);
			sendApproveEmail(userEntity, tempUserEntity); 
			
			
			/*for(OnboardingApprovalEntity onboardingApprovalEntity :onboardingApprovalEntities )
			{
				if(onboardingApprovalEntity.getLevel() == 1 && onboardingApprovalEntity.getApprovalType() ==1) 
				{
					
					break;
				}
			}*/
		}
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
	private boolean sendApproveEmail(UserEntity approver, UserEntity requestor)
	{
		String toEmail = null;
		boolean returnVal = true;
		if(approver.getEmailId() != null)
			toEmail = approver.getEmailId();
		else
			toEmail = approver.getUserId();
		String emailBody = APPConstants.ROLE_CHANGE_REQ_MAIL_TEXT;
		emailBody = emailBody.replace("apprFname", approver.getFirstName());
		emailBody = emailBody.replace("apprLastName", approver.getLastName());
		emailBody = emailBody.replace("reqFirstName", requestor.getFirstName());
		emailBody = emailBody.replace("reqLastName", requestor.getLastName());
		System.out.println(emailBody);
		SendEmail.sendHTMLEmail(toEmail, APPConstants.ROLE_CHANGE_REQ_EMAIL_SUB, emailBody);
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
		 OnboardApprovedDTO onboardApprovedDTO = null;
		 boolean supervisor = false;
		 boolean requestor = false;
		 List<OnboardApprovalPendingEntity> onboardApprovalPendingEntities = null;
		 
		 SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		 
		 	Calendar estDate = Calendar.getInstance();
			
		 UserEntity userEntity = userDAO.getUser(userId);
		 
		 Set<UserRoleEntity> roleEntities = userEntity.getUserRoleEntities();
		 
		 Iterator<UserRoleEntity> roleEntityIter = roleEntities.iterator();
		 
		 UserRoleEntity userRoleEntity;
		 while(roleEntityIter.hasNext())
		 {
			 userRoleEntity = roleEntityIter.next();
			 if(userRoleEntity.getRolesEntity().getRoleName().equalsIgnoreCase("ADMIN"))
			 {
				 supervisor = true;
				 break;
			 }
			 else  if(userRoleEntity.getRolesEntity().getRoleName().equalsIgnoreCase("Requestor"))
			 {
				 requestor = true;
				 break;
			 }
		 }
		 
		 if(!supervisor) 
		 {
				 onboardApprovalPendingEntities = userDAO.fetchUserPendingRequest(userId);
		 }
			
		 else
			 onboardApprovalPendingEntities = userDAO.fetchUserPendingRequest(null);
		 
		
		if(!requestor)
		{
		for(OnboardApprovalPendingEntity approvalPendingEntity : onboardApprovalPendingEntities)
		{
			System.out.println(approvalPendingEntity.isPendingApproval()+""+approvalPendingEntity.getOnboardPendingId());
			if(approvalPendingEntity.isPendingApproval()) {
				onboardApprovalPendingDTO = new OnboardApprovalPendingDTO();
				onboardApprovalPendingDTO.setOnboardPendingId(approvalPendingEntity.getOnboardPendingId());
				onboardApprovalPendingDTO.setReqType(approvalPendingEntity.getRequestType());
				/*estDate.setTime(approvalPendingEntity.getRequestDate());
				sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
				System.out.println(estDate.getTime());
				sdf.format(estDate.getTime());*/
				onboardApprovalPendingDTO.setRequestDate(getLocalTime(approvalPendingEntity.getRequestDate()));
				onboardApprovalPendingDTO.setUserId(approvalPendingEntity.getUserEntity().getId());
				if(approvalPendingEntity.getTempUserEntity() != null )
				{
					onboardApprovalPendingDTO.setTempUserId(approvalPendingEntity.getTempUserEntity().getEmployeeId());
					onboardApprovalPendingDTO.setTempFname(approvalPendingEntity.getTempUserEntity().getFirstName());
					onboardApprovalPendingDTO.setTempLname(approvalPendingEntity.getTempUserEntity().getLastName());
					onboardApprovalPendingDTO.setPendingWith(approvalPendingEntity.getUserEntity().getFirstName()+" "+approvalPendingEntity.getUserEntity().getLastName());
					if(approvalPendingEntity.getTempUserEntity().getCreatedBy() != null)
					{
					UserEntity createdBy = userDAO.getUser(approvalPendingEntity.getTempUserEntity().getCreatedBy());
					onboardApprovalPendingDTO.setSubmittedBy(createdBy.getFirstName()+" "+createdBy.getLastName());
					}
					approvalPendingDTOs.add(onboardApprovalPendingDTO);
				}
				/*else
					if(approvalPendingEntity.getRequestUserEntity()!=null)
					{
						onboardApprovalPendingDTO.setTempUserId(approvalPendingEntity.getRequestUserEntity().getId().toString());
						onboardApprovalPendingDTO.setTempFname(approvalPendingEntity.getRequestUserEntity().getFirstName());
						onboardApprovalPendingDTO.setTempLname(approvalPendingEntity.getRequestUserEntity().getLastName());
						onboardApprovalPendingDTO.setPendingWith(approvalPendingEntity.getUserEntity().getFirstName()+" "+approvalPendingEntity.getUserEntity().getLastName());
						approvalPendingDTOs.add(onboardApprovalPendingDTO);
					}*/
				
			}
			
		}
		}
		else
		{
			UserEntity userEntity2 = userDAO.getUser(userId);
			List<TempUserEntity> tempUserEntities = userDAO.getTempUsersCreatedBy(userEntity2.getUserId());
			
			for(TempUserEntity tempUserEntity : tempUserEntities)
			{
				onboardApprovalPendingDTO = new OnboardApprovalPendingDTO();
				
				for(OnboardApprovalPendingEntity approvalPendingEntity : onboardApprovalPendingEntities)
				{
					System.out.println(approvalPendingEntity.isPendingApproval()+""+approvalPendingEntity.getOnboardPendingId());
					if(approvalPendingEntity.isPendingApproval()) {
						onboardApprovalPendingDTO = new OnboardApprovalPendingDTO();
						onboardApprovalPendingDTO.setOnboardPendingId(approvalPendingEntity.getOnboardPendingId());
						onboardApprovalPendingDTO.setReqType(approvalPendingEntity.getRequestType());
						/*estDate.setTime(approvalPendingEntity.getRequestDate());
						sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
						System.out.println(estDate.getTime());
						sdf.format(estDate.getTime());*/
						onboardApprovalPendingDTO.setRequestDate(getLocalTime(approvalPendingEntity.getRequestDate()));
						onboardApprovalPendingDTO.setUserId(approvalPendingEntity.getUserEntity().getId());
						if(approvalPendingEntity.getTempUserEntity() != null )
						{
							onboardApprovalPendingDTO.setTempUserId(approvalPendingEntity.getTempUserEntity().getEmployeeId());
							onboardApprovalPendingDTO.setTempFname(approvalPendingEntity.getTempUserEntity().getFirstName());
							onboardApprovalPendingDTO.setTempLname(approvalPendingEntity.getTempUserEntity().getLastName());
							onboardApprovalPendingDTO.setPendingWith(approvalPendingEntity.getUserEntity().getFirstName()+" "+approvalPendingEntity.getUserEntity().getLastName());
							if(approvalPendingEntity.getTempUserEntity().getCreatedBy() != null)
							{
							UserEntity createdBy = userDAO.getUser(approvalPendingEntity.getTempUserEntity().getCreatedBy());
							onboardApprovalPendingDTO.setSubmittedBy(createdBy.getFirstName()+" "+createdBy.getLastName());
							}
							approvalPendingDTOs.add(onboardApprovalPendingDTO);
						}
					}
				}
			}
		}
			
		return approvalPendingDTOs;
	}
	
	
	@Override
	@Transactional
	public Set<OnboardApprovalPendingDTO> getCreatedBy(int userId) {
		 Set<OnboardApprovalPendingDTO> approvalPendingDTOs = new HashSet<OnboardApprovalPendingDTO>(0);
		 OnboardApprovalPendingDTO onboardApprovalPendingDTO = null;
		 OnboardApprovedDTO onboardApprovedDTO = null;
		 boolean supervisor = false;
		 boolean requestor = false;
		 List<OnboardApprovalPendingEntity> onboardApprovalPendingEntities = null;
		 
		 SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		 
		 	Calendar estDate = Calendar.getInstance();
			
		 UserEntity userEntity = userDAO.getUser(userId);
		 
		 Set<UserRoleEntity> roleEntities = userEntity.getUserRoleEntities();
		 
 		onboardApprovalPendingEntities = userDAO.fetchUserRequestedBy(userDAO.getUser(userId).getUserId());
		
		for(OnboardApprovalPendingEntity approvalPendingEntity : onboardApprovalPendingEntities)
		{
			System.out.println(approvalPendingEntity.isPendingApproval()+""+approvalPendingEntity.getOnboardPendingId());
			if(approvalPendingEntity.isPendingApproval()) {
				onboardApprovalPendingDTO = new OnboardApprovalPendingDTO();
				onboardApprovalPendingDTO.setOnboardPendingId(approvalPendingEntity.getOnboardPendingId());
				onboardApprovalPendingDTO.setReqType(approvalPendingEntity.getRequestType());
				/*estDate.setTime(approvalPendingEntity.getRequestDate());
				sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
				System.out.println(estDate.getTime());
				sdf.format(estDate.getTime());*/
				onboardApprovalPendingDTO.setRequestDate(getLocalTime(approvalPendingEntity.getRequestDate()));
				onboardApprovalPendingDTO.setUserId(approvalPendingEntity.getUserEntity().getId());
				if(approvalPendingEntity.getTempUserEntity() != null )
				{
					onboardApprovalPendingDTO.setTempUserId(approvalPendingEntity.getTempUserEntity().getEmployeeId());
					onboardApprovalPendingDTO.setTempFname(approvalPendingEntity.getTempUserEntity().getFirstName());
					onboardApprovalPendingDTO.setTempLname(approvalPendingEntity.getTempUserEntity().getLastName());
					onboardApprovalPendingDTO.setPendingWith(approvalPendingEntity.getUserEntity().getFirstName()+" "+approvalPendingEntity.getUserEntity().getLastName());
					if(approvalPendingEntity.getTempUserEntity().getCreatedBy() != null)
					{
					UserEntity createdBy = userDAO.getUser(approvalPendingEntity.getTempUserEntity().getCreatedBy());
					onboardApprovalPendingDTO.setSubmittedBy(createdBy.getFirstName()+" "+createdBy.getLastName());
					}
				}
				else
					if(approvalPendingEntity.getRequestUserEntity()!=null)
					{
						onboardApprovalPendingDTO.setTempUserId(approvalPendingEntity.getRequestUserEntity().getId().toString());
						onboardApprovalPendingDTO.setTempFname(approvalPendingEntity.getRequestUserEntity().getFirstName());
						onboardApprovalPendingDTO.setTempLname(approvalPendingEntity.getRequestUserEntity().getLastName());
						onboardApprovalPendingDTO.setPendingWith(approvalPendingEntity.getUserEntity().getFirstName()+" "+approvalPendingEntity.getUserEntity().getLastName());
					}
				approvalPendingDTOs.add(onboardApprovalPendingDTO);
			}
			
		}
			
		return approvalPendingDTOs;
	}
	
	
	@Override
	@Transactional
	public List<OnboardApprovedDTO> getApprovedRequests(int userId) {
		List<OnboardApprovalAuditEntity> onboardApprovalAuditEntities = null;
		UserEntity userEntity = userDAO.getUser(userId);
		Set<UserRoleEntity> roleEntities = userEntity.getUserRoleEntities();
		 
		 Iterator<UserRoleEntity> roleEntityIter = roleEntities.iterator();
		 
		 UserRoleEntity userRoleEntity;
		 boolean supervisor = false;
		 while(roleEntityIter.hasNext())
		 {
			 userRoleEntity = roleEntityIter.next();
			 System.out.println("userRoleEntity.getRolesEntity() "+userRoleEntity.getRolesEntity().getRoleName());
			 if(userRoleEntity.getRolesEntity().getRoleName().equalsIgnoreCase("ADMIN"))
			 {
				 supervisor = true;
				 break;
			 }
		 }
		 
		 if(!supervisor) 
			 onboardApprovalAuditEntities = userDAO.getApprovedRequests(userEntity.getUserId());
		 else
			 onboardApprovalAuditEntities = userDAO.getApprovedRequests(null);
		 
		OnboardApprovedDTO onboardApprovedDTO = null;
		List<OnboardApprovedDTO> onboardApprovedDTOs = new ArrayList<OnboardApprovedDTO>();
		TempUserEntity tempUserEntity = null;
		UserEntity approvedBy = null;
		 
		if(!onboardApprovalAuditEntities.isEmpty())
		{
			for(OnboardApprovalAuditEntity onboardApprovalAuditEntity : onboardApprovalAuditEntities)
			{
				tempUserEntity = userDAO.getApprovedTempUser(onboardApprovalAuditEntity.getTempUserTbEmployeeId());
				if(tempUserEntity != null)
				{
					onboardApprovedDTO = new OnboardApprovedDTO();
					onboardApprovedDTO.setTempUserId(tempUserEntity.getEmployeeId());
					onboardApprovedDTO.setTempFname(tempUserEntity.getFirstName());
					onboardApprovedDTO.setTempLname(tempUserEntity.getLastName());
					onboardApprovedDTO.setReqType("Onboarding");
					if(onboardApprovalAuditEntity.getAprovedOn() != null)
						onboardApprovedDTO.setApprovedOn(getLocalTime(onboardApprovalAuditEntity.getAprovedOn()));
					approvedBy = userDAO.getUser(onboardApprovalAuditEntity.getUserTbId());
					onboardApprovedDTO.setApprovedBy(approvedBy.getFirstName()+" "+approvedBy.getLastName());
					if(tempUserEntity.getCreatedBy() != null)
					{
					UserEntity createdBy = userDAO.getUser(tempUserEntity.getCreatedBy());
					if(createdBy != null)
					onboardApprovedDTO.setSubmittedBy(createdBy.getFirstName()+" "+createdBy.getLastName());
				}
				onboardApprovedDTOs.add(onboardApprovedDTO);
				}
			}
			
		}
		return onboardApprovedDTOs;
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
		Set<RoleDTO> roleDTOs = new HashSet<RoleDTO>();
		RoleDTO roleDTO = null;
		/*RolesEntity rolesEntity = commonDAO.getRoleById(tempUserEntity.getUserRoleId());
		String roleName = rolesEntity.getRoleName();
		String systemName = rolesEntity.getSystemEntity().getSystemName();
		String department = rolesEntity.getSystemEntity().getDeptEntity().getDeptName();*/
		tempUserDTO.setCreatedTs(getLocalTime(tempUserEntity.getCreatedTs()));
		tempUserDTO.setAccountType(tempUserEntity.getAccountType());
		tempUserDTO.setAccountSubType(tempUserEntity.getAccountSubType());
		tempUserDTO.setBusinessJustification(tempUserEntity.getBusinessJustification());
		tempUserDTO.setSendingTo(tempUserEntity.getSendingTo());
		tempUserDTO.setReceivingFrom(tempUserEntity.getReceivingFrom());
		tempUserDTO.setTypeOfDataExchange(tempUserEntity.getTypeOfDataExchange());
		tempUserDTO.setPhiData(tempUserEntity.isPhiData());
		UserEntity createdBy = userDAO.getUser(tempUserEntity.getCreatedBy());
		
		
		System.out.println("Created By::"+tempUserDTO.getCreatedBy());
		Set<OnboardApprovalPendingEntity> onboardApprovalPendingEntities = tempUserEntity.getOnboardApprovalPendingEntities();
		int level = 0;
		for(OnboardApprovalPendingEntity onboardApprovalPendingEntity : onboardApprovalPendingEntities)
		{
			if(level < onboardApprovalPendingEntity.getLevel())
				level = onboardApprovalPendingEntity.getLevel();
		}
		tempUserDTO.setApproveLevel(level);
		try {
			BeanUtils.copyProperties(tempUserDTO, tempUserEntity);
			tempUserDTO.setCreatedBy(createdBy.getFirstName()+" "+createdBy.getLastName());
			if(!tempUserEntity.getOnboardingUserNotesEntities().isEmpty())
			{
				Set<OnboardingUserNotesEntity> onboardingUserNotesEntities =  tempUserEntity.getOnboardingUserNotesEntities();
				for(OnboardingUserNotesEntity onboardingUserNotesEntity : onboardingUserNotesEntities)
				{
					onboardingUserNotesDTO = new OnboardingUserNotesDTO();
					onboardingUserNotesDTO.setNotes(onboardingUserNotesEntity.getNotes());
					onboardingUserNotesDTO.setCreatedBy(onboardingUserNotesEntity.getCreatedBy());
					UserEntity userEntity = userDAO.getUser(onboardingUserNotesEntity.getCreatedBy());
					onboardingUserNotesDTO.setApprovedBy(userEntity.getFirstName()+" "+userEntity.getLastName());
					onboardingUserNotesDTO.setCreatedTs(onboardingUserNotesEntity.getCreatedTs());
					onboardingUserNotesDTOs.add(onboardingUserNotesDTO);
				}
				tempUserDTO.setOnboardingUserNotesDTOs(onboardingUserNotesDTOs);
			}
			if(tempUserEntity.getContractCompanyEntity() != null)
			tempUserDTO.setCompanyName(tempUserEntity.getContractCompanyEntity().getCompanyName());
			
			if(!tempUserEntity.getUserFileEntities().isEmpty())
			{
				Set<UserFileEntity> userFileEntities =  tempUserEntity.getUserFileEntities();
				
				for(UserFileEntity userFileEntity : userFileEntities)
				{
					onboardingUserFile = new OnboardingUserFile();
					onboardingUserFile.setFileName(userFileEntity.getFileName());
					onboardingUserFile.setFileContent(DatatypeConverter.printBase64Binary(userFileEntity.getContent()));
					if(onboardingUserFile.getFileContent() != null)
						onboardingUserFile.setFileContent(onboardingUserFile.getFileContent().split("base64")[1]);
					onboardingUserFiles.add(onboardingUserFile);
				}
				tempUserDTO.setOnboardingUserFiles(onboardingUserFiles);
			}
			if(tempUserEntity.getTempUserRoleEntities().size() > 0)
			{
				for(TempUserRoleEntity tempUserRoleEntity : tempUserEntity.getTempUserRoleEntities())
				{
					roleDTO = new RoleDTO();
					roleDTO.setRoleId(tempUserRoleEntity.getRolesEntity().getRoleId());
					roleDTO.setRoleName(tempUserRoleEntity.getRolesEntity().getRoleName());
					roleDTO.setSystemName(tempUserRoleEntity.getRolesEntity().getSystemEntity().getSystemName());
					//roleDTO.setDepartmentName(tempUserRoleEntity.getRolesEntity().getSystemEntity().getDeptEntity().getDeptName());
					roleDTOs.add(roleDTO);
				}
				tempUserDTO.setRoleDTOs(roleDTOs);
			}
			
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		/*tempUserDTO.setDepartment(department);
		tempUserDTO.setSystem(systemName);
		tempUserDTO.setRole(roleName);*/
		return tempUserDTO;
	}

	@Override
	@Transactional
	public UserSVCResp approveRejectUser(TempUserDTO tempUserDTO) {
		// TODO Auto-generated method stub
		
		UserSVCResp userSVCResp = new UserSVCResp();
		if(tempUserDTO.getReqType().equalsIgnoreCase("On boarding"))
		{
		TempUserEntity tempUserEntity = userDAO.getTempUser(tempUserDTO.getEmployeeId());
		
		OnboardApprovalAuditEntity onboardApprovalAuditEntity = new OnboardApprovalAuditEntity();
		Set<UserRoleEntity> userRoleEntities = new HashSet<UserRoleEntity>();
		if(tempUserDTO.getOperation().equalsIgnoreCase("REJECT"))
		{
			UserEntity userEntity = userDAO.getUser(tempUserDTO.getApprovedBy());
			OnboardApprovalPendingEntity onboardApprovalPendingEntity = userDAO.getOnboardApprovalPendingEntity(tempUserEntity.getEmployeeId(),0,userEntity.getId());
			onboardApprovalAuditEntity.setUserTbId(tempUserDTO.getApprovedBy());
			onboardApprovalAuditEntity.setTempUserTbEmployeeId(tempUserEntity.getEmployeeId());
			onboardApprovalAuditEntity.setMessage("Request  has rejected.");
			
			
			System.out.println("onboardApprovalPendingEntity "+onboardApprovalPendingEntity.isPendingApproval());
			//onboardApprovalPendingEntity.setUserEntity(userEntity);
			userDAO.updateOnboardApprovalPendingEntity(onboardApprovalPendingEntity);
			OnboardingUserNotesEntity onboardingUserNotesEntity = new OnboardingUserNotesEntity();
			onboardingUserNotesEntity.setTempUserEntity(tempUserEntity);
			onboardingUserNotesEntity.setNotes(tempUserDTO.getNotes());
			onboardingUserNotesEntity.setCreatedBy(tempUserDTO.getApprovedBy());
			onboardingUserNotesEntity.setCreatedTs(new Date());
			commonDAO.saveOnboardingNotes(onboardingUserNotesEntity);
			onboardApprovalPendingEntity.setPendingApproval(false);
			userDAO.updateOnboardApprovalPendingEntity(onboardApprovalPendingEntity);
			
			
			userEntity = userDAO.getUser(tempUserEntity.getCreatedBy());
			int roleId = onboardApprovalPendingEntity.getRoleID();
			onboardApprovalPendingEntity = new OnboardApprovalPendingEntity();
			onboardApprovalPendingEntity.setRequestDate(new Date());
			onboardApprovalPendingEntity.setTempUserEntity(tempUserEntity);
			onboardApprovalPendingEntity.setPendingApproval(true);
			onboardApprovalPendingEntity.setRequestType("Onboarding Approval");
			onboardApprovalPendingEntity.setUserEntity(userEntity);
			onboardApprovalPendingEntity.setRoleID(roleId);
			onboardApprovalPendingEntity.setLevel(0);
			commonDAO.saveOnboardApprovalPendingEntity(onboardApprovalPendingEntity);
			sendRejectEmail(userEntity, tempUserEntity);
			
			userDAO.updateTempUser(tempUserEntity);
			
			userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.ONBOARD.REJECT.CODE",null, Locale.getDefault())));
			userSVCResp.setresultString(messageSource.getMessage("USER.ONBOARD.REJECT.STRING",null, Locale.getDefault()));
			
		}
		else {
			
				UserEntity userEntity = userDAO.getUser(tempUserDTO.getApprovedBy());
				OnboardApprovalPendingEntity onboardApprovalPendingEntity = userDAO.getOnboardApprovalPendingEntity(tempUserDTO.getEmployeeId(),0,userEntity.getId());
				onboardApprovalAuditEntity.setUserTbId(onboardApprovalPendingEntity.getUserEntity().getUserId());
				onboardApprovalAuditEntity.setTempUserTbEmployeeId(tempUserEntity.getEmployeeId());
				onboardApprovalAuditEntity.setMessage(onboardApprovalPendingEntity.getUserEntity().getLastName()+" has approved.");
				onboardApprovalAuditEntity.setAprovedOn(new Date());
				userDAO.saveOnboardingApprovalAudit(onboardApprovalAuditEntity);
				
				OnboardingUserNotesEntity onboardingUserNotesEntity = new OnboardingUserNotesEntity();
				onboardingUserNotesEntity.setTempUserEntity(tempUserEntity);
				onboardingUserNotesEntity.setNotes(tempUserDTO.getNotes());
				onboardingUserNotesEntity.setCreatedBy(tempUserDTO.getApprovedBy());
				onboardingUserNotesEntity.setCreatedTs(new Date());
				commonDAO.saveOnboardingNotes(onboardingUserNotesEntity);
				
				onboardApprovalPendingEntity.setPendingApproval(false);
				userDAO.updateOnboardApprovalPendingEntity(onboardApprovalPendingEntity);
				
				
				int approveLevel = onboardApprovalPendingEntity.getLevel();
				System.out.println("approveLevel "+approveLevel);
				//List<OnboardingApprovalEntity> onboardingApprovalEntities = commonDAO.getOnboardApproval(onboardApprovalPendingEntity.getRoleID(),approveLevel);
			
				
				int maxLevel = commonDAO.getOnboardApprovalMaxLevel(onboardApprovalPendingEntity.getRoleID());
				
				int roleId = onboardApprovalPendingEntity.getRoleID();
				System.out.println("maxLevel" +maxLevel);
				if(maxLevel > approveLevel) {
					OnboardingApprovalEntity onboardingApprovalEntity = commonDAO.getOnboardApproval(onboardApprovalPendingEntity.getRoleID(),approveLevel+1);
					onboardApprovalPendingEntity = new OnboardApprovalPendingEntity();
					onboardApprovalPendingEntity.setRequestDate(new Date());
					onboardApprovalPendingEntity.setTempUserEntity(tempUserEntity);
					onboardApprovalPendingEntity.setPendingApproval(true);
					onboardApprovalPendingEntity.setUserEntity(onboardingApprovalEntity.getUserEntity());
					onboardApprovalPendingEntity.setRoleID(roleId);
					onboardApprovalPendingEntity.setRequestType("Onboarding Approval");
					onboardApprovalPendingEntity.setLevel(approveLevel+1);
					sendApproveEmail(onboardingApprovalEntity.getUserEntity(), tempUserEntity);
					commonDAO.saveOnboardApprovalPendingEntity(onboardApprovalPendingEntity);
				}
				/*else
				{
					onboardApprovalPendingEntity.setPendingApproval(false);
				}*/
				
				
				
				userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.ONBOARD.APPROVAL.NEXTLEVEL.CODE",null, Locale.getDefault())));
				userSVCResp.setresultString(messageSource.getMessage("USER.ONBOARD.APPROVAL.NEXTLEVEL.STRING",null, Locale.getDefault()));
				
				
				List<OnboardApprovalPendingEntity> onboardApprovalPendingEntities = userDAO.getOnboardApprovalPendingEntity(tempUserDTO.getEmployeeId());
				System.out.println(onboardApprovalPendingEntities.isEmpty());
				if(onboardApprovalPendingEntities.isEmpty())
				{
					tempUserEntity.setPendingApproval(false);
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
					userEntity = new UserEntity();
					UserWorkEntity userWorkEntity = new UserWorkEntity();
					userWorkEntity.setEmpId(tempUserEntity.getEmployeeId());
					userWorkEntity.setEmailAddress(tempUserEntity.getWorkEmailId());
					userWorkEntity.setPhoneNumber(tempUserEntity.getWorkPhoneNumber());
					userWorkEntity.setUserEntity(userEntity);
					if(tempUserEntity.getContractCompanyEntity() != null)
						userWorkEntity.setContractCompanyEntity(tempUserEntity.getContractCompanyEntity());
					
					try {
						userWorkEntity.setStartDate(sdf.parse(tempUserEntity.getStartDate()));
						userEntity.setDateOfBirth(sdf.parse(tempUserEntity.getDateOfBirth()));
						if(tempUserEntity.getEndDate() != null)
							userWorkEntity.setEndDate(sdf.parse(tempUserEntity.getEndDate()));
						
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
					
					UserRoleEntity userRoleEntity = null;
					
					Set<TempUserRoleEntity> userRoleEntities2 = tempUserEntity.getTempUserRoleEntities();
					for(TempUserRoleEntity tempUserRoleEntity : userRoleEntities2)
					{
						userRoleEntity = new UserRoleEntity();
						System.out.println("tempUserRoleEntity.getTempUserRoleId() "+tempUserRoleEntity.getRolesEntity().getRoleId());
						userRoleEntity.setRolesEntity(tempUserRoleEntity.getRolesEntity());
						userRoleEntity.setUserEntity(userEntity);
						userRoleEntity.setCreatedBy(APPConstants.SYSTEM_STR);
						userRoleEntity.setCreatedTs(new Date());
						userRoleEntities.add(userRoleEntity);
					}
					userEntity.setUserRoleEntities(userRoleEntities);
					userDAO.saveUser(userEntity);
					userDAO.updateTempUser(tempUserEntity);
					
					String token = generateAuthToken(userEntity);
					sendOnboardingEmail(userEntity,token);
					
					userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.ONBOARD.APPROVAL.COMPLETED.CODE",null, Locale.getDefault())));
					userSVCResp.setresultString(messageSource.getMessage("USER.ONBOARD.APPROVAL.COMPLETED.STRING",null, Locale.getDefault()));
				}
				else
				{
					
				
				}
		} 
		}else if(tempUserDTO.getReqType().equalsIgnoreCase("Add Role"))
		{
			OnboardApprovalAuditEntity onboardApprovalAuditEntity = new OnboardApprovalAuditEntity();
			if(tempUserDTO.getOperation().equalsIgnoreCase("REJECT"))
			{
				UserEntity userEntity = userDAO.getUser(tempUserDTO.getApprovedBy());
				OnboardApprovalPendingEntity onboardApprovalPendingEntity = userDAO.getOnboardApprovalPendingEntity(null,new Integer(tempUserDTO.getEmployeeId()),userEntity.getId());
				
				UserEntity reqUserEntity = userDAO.getUser(tempUserDTO.getEmployeeId());
				
				System.out.println("onboardApprovalPendingEntity "+onboardApprovalPendingEntity.isPendingApproval());
				//onboardApprovalPendingEntity.setUserEntity(userEntity);
				userDAO.updateOnboardApprovalPendingEntity(onboardApprovalPendingEntity);
				
				/*OnboardingUserNotesEntity onboardingUserNotesEntity = new OnboardingUserNotesEntity();
				onboardingUserNotesEntity.setTempUserEntity(reqUserEntity);
				onboardingUserNotesEntity.setNotes(tempUserDTO.getNotes());
				onboardingUserNotesEntity.setCreatedBy(tempUserDTO.getApprovedBy());
				onboardingUserNotesEntity.setCreatedTs(new Date());
				commonDAO.saveOnboardingNotes(onboardingUserNotesEntity);*/
				
				onboardApprovalPendingEntity.setPendingApproval(false);
				userDAO.updateOnboardApprovalPendingEntity(onboardApprovalPendingEntity);
				
				
				/*userEntity = userDAO.getUser(tempUserEntity.getCreatedBy());
				int roleId = onboardApprovalPendingEntity.getRoleID();
				onboardApprovalPendingEntity = new OnboardApprovalPendingEntity();
				onboardApprovalPendingEntity.setRequestDate(new Date());
				onboardApprovalPendingEntity.setTempUserEntity(tempUserEntity);
				onboardApprovalPendingEntity.setPendingApproval(true);
				onboardApprovalPendingEntity.setRequestType("Onboarding Approval");
				onboardApprovalPendingEntity.setUserEntity(userEntity);
				onboardApprovalPendingEntity.setRoleID(roleId);
				onboardApprovalPendingEntity.setLevel(0);
				commonDAO.saveOnboardApprovalPendingEntity(onboardApprovalPendingEntity);
				sendRejectEmail(userEntity, tempUserEntity);
				
				userDAO.updateTempUser(tempUserEntity);*/
				
				userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.ONBOARD.REJECT.CODE",null, Locale.getDefault())));
				userSVCResp.setresultString(messageSource.getMessage("USER.ONBOARD.REJECT.STRING",null, Locale.getDefault()));
				
			}
			else {

				UserNotesEntity userNotesEntity = new UserNotesEntity();
				UserEntity userEntity = userDAO.getUser(tempUserDTO.getApprovedBy());
				
				UserEntity requestUserEntity = userDAO.getUser(new Integer(tempUserDTO.getEmployeeId()));
				OnboardApprovalPendingEntity onboardApprovalPendingEntity = userDAO.getOnboardApprovalPendingEntity(null,new Integer(tempUserDTO.getEmployeeId()),userEntity.getId());
				/*onboardApprovalAuditEntity.setUserTbId(onboardApprovalPendingEntity.getUserEntity().getUserId());
				onboardApprovalAuditEntity.setTempUserTbEmployeeId(tempUserEntity.getEmployeeId());
				onboardApprovalAuditEntity.setMessage(onboardApprovalPendingEntity.getUserEntity().getLastName()+" has approved.");
				onboardApprovalAuditEntity.setAprovedOn(new Date());
				userDAO.saveOnboardingApprovalAudit(onboardApprovalAuditEntity);*/
				
				/*OnboardingUserNotesEntity onboardingUserNotesEntity = new OnboardingUserNotesEntity();
				onboardingUserNotesEntity.setTempUserEntity(tempUserEntity);
				onboardingUserNotesEntity.setNotes(tempUserDTO.getNotes());
				onboardingUserNotesEntity.setCreatedBy(tempUserDTO.getApprovedBy());
				onboardingUserNotesEntity.setCreatedTs(new Date());
				commonDAO.saveOnboardingNotes(onboardingUserNotesEntity)*/;
				
				
				
				userNotesEntity.setUserEntity(requestUserEntity);
				userNotesEntity.setNotes(tempUserDTO.getNotes());
				userNotesEntity.setCreatedBy(tempUserDTO.getApprovedBy());
				userNotesEntity.setCreatedTs(new Date());
				commonDAO.saveuserNotes(userNotesEntity);
				
				
				onboardApprovalPendingEntity.setPendingApproval(false);
				userDAO.updateOnboardApprovalPendingEntity(onboardApprovalPendingEntity);
				
				
				int approveLevel = onboardApprovalPendingEntity.getLevel();
				System.out.println("approveLevel "+approveLevel);
				//List<OnboardingApprovalEntity> onboardingApprovalEntities = commonDAO.getOnboardApproval(onboardApprovalPendingEntity.getRoleID(),approveLevel);
			
				
				int maxLevel = commonDAO.getOnboardApprovalMaxLevel(onboardApprovalPendingEntity.getRoleID());
				
				int roleId = onboardApprovalPendingEntity.getRoleID();
				System.out.println("maxLevel" +maxLevel);
				if(maxLevel > approveLevel) {
					OnboardingApprovalEntity onboardingApprovalEntity = commonDAO.getOnboardApproval(onboardApprovalPendingEntity.getRoleID(),approveLevel+1);
					onboardApprovalPendingEntity = new OnboardApprovalPendingEntity();
					onboardApprovalPendingEntity.setRequestDate(new Date());
					onboardApprovalPendingEntity.setRequestUserEntity(requestUserEntity);
					onboardApprovalPendingEntity.setPendingApproval(true);
					onboardApprovalPendingEntity.setUserEntity(onboardingApprovalEntity.getUserEntity());
					onboardApprovalPendingEntity.setRoleID(roleId);
					onboardApprovalPendingEntity.setRequestType("Add Role Request");
					onboardApprovalPendingEntity.setLevel(approveLevel+1);
					sendApproveEmail(onboardingApprovalEntity.getUserEntity(), requestUserEntity);
					commonDAO.saveOnboardApprovalPendingEntity(onboardApprovalPendingEntity);
				}
				/*else
				{
					onboardApprovalPendingEntity.setPendingApproval(false);
				}*/
				
				
				
				userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.ONBOARD.APPROVAL.NEXTLEVEL.CODE",null, Locale.getDefault())));
				userSVCResp.setresultString(messageSource.getMessage("USER.ONBOARD.APPROVAL.NEXTLEVEL.STRING",null, Locale.getDefault()));
				
				
				List<OnboardApprovalPendingEntity> onboardApprovalPendingEntities = userDAO.getOnboardApprovalPendingEntity(tempUserDTO.getEmployeeId());
				System.out.println(onboardApprovalPendingEntities.isEmpty());
				if(onboardApprovalPendingEntities.isEmpty())
				{
					Set<UserRoleEntity> userRoleEntities = requestUserEntity.getUserRoleEntities();
					
					for(UserRoleEntity userRoleEntity : userRoleEntities)
					{
						if(!userRoleEntity.isActiveStatus())
							userRoleEntity.setActiveStatus(true);
					}
					userDAO.updateUser(requestUserEntity);
					userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.ONBOARD.APPROVAL.COMPLETED.CODE",null, Locale.getDefault())));
					userSVCResp.setresultString(messageSource.getMessage("USER.ONBOARD.APPROVAL.COMPLETED.STRING",null, Locale.getDefault()));
				}
				else
				{
					
				
				}
			}
			
		}
				
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

	@Override
	@Transactional
	public boolean validateToken(String token, Integer userId) {
		
		SecauthtokenEntity secauthtokenEntity = userDAO.getAuthToken(token);
		UserEntity userEntity = secauthtokenEntity.getUserEntity();
		Date accessTime;
		if(userEntity.getId().intValue() == userId.intValue())
		{
			accessTime = secauthtokenEntity.getLastAccessTs();
			getPolicies();
			if(secauthtokenEntity.getStatus() && AppUtil.validateTime(new Integer(policyMap.get("User Session Inactivity (x) Minutes").toString()), accessTime))
			{
				return true;
			}
			else
				return false;
			
		}
		else
		{
			return false;
		}
	}
	
	@Override
	@Transactional
	public boolean refreshToken(String token, Integer userId) {
		
		SecauthtokenEntity secauthtokenEntity = userDAO.getAuthToken(token);
		UserEntity userEntity = secauthtokenEntity.getUserEntity();
		if(userEntity.getId().intValue() == userId.intValue())
		{
			secauthtokenEntity.setLastAccessTs(new Date());
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
	public Set<OnboardingApprovalDTO>  getApprovalList(Integer roleId) {
		
		List<OnboardingApprovalEntity> onboardingApprovalEntities = commonDAO.getOnboardApproval(roleId);
		OnboardingApprovalDTO onboardingApprovalDTO ; 
		Set<OnboardingApprovalDTO> onboardingApprovalDTOs = new HashSet<OnboardingApprovalDTO>(0);
		for(OnboardingApprovalEntity onboardingApprovalEntity : onboardingApprovalEntities)
		{
			onboardingApprovalDTO = new OnboardingApprovalDTO();
			onboardingApprovalDTO.setId(onboardingApprovalEntity.getOnboardApprovalId());
			onboardingApprovalDTO.setFirstName(onboardingApprovalEntity.getUserEntity().getFirstName());
			onboardingApprovalDTO.setLastName(onboardingApprovalEntity.getUserEntity().getLastName());
			onboardingApprovalDTO.setEmailId(onboardingApprovalEntity.getUserEntity().getEmailId());
			onboardingApprovalDTO.setLevel(onboardingApprovalEntity.getLevel());
			onboardingApprovalDTO.setSla(onboardingApprovalEntity.getSla());
			onboardingApprovalDTO.setApprovalType(onboardingApprovalEntity.getApprovalType());
			onboardingApprovalDTOs.add(onboardingApprovalDTO);
		}
		
		return onboardingApprovalDTOs;
	}
	
	private String getLocalTime(Date date)
	{
		SimpleDateFormat sdf= new SimpleDateFormat("MM-dd-yyyy HH:mm");
		Calendar estDate = Calendar.getInstance();
		
		estDate.setTime(date);
		sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		return sdf.format(estDate.getTime());
	}

	@Override
	@Transactional
	public UserProfileResp addRole(AddUserRoleReq addUserRoleReq) {
		UserEntity userEntity = userDAO.getUser(addUserRoleReq.getId());
		
		RolesEntity rolesEntity = commonDAO.getRoleById(addUserRoleReq.getRole());
		
		Set<UserRoleEntity> userRoleEntities = userEntity.getUserRoleEntities();
		
		UserRoleEntity userRoleEntity = new UserRoleEntity();
		userRoleEntity.setRolesEntity(rolesEntity);
		userRoleEntity.setUserEntity(userEntity);
		userRoleEntity.setStartDate(convertCalDate(addUserRoleReq.getStartDate()));
		userRoleEntity.setEndDate(convertCalDate(addUserRoleReq.getStartDate()));
		userRoleEntity.setActiveStatus(false);
		userRoleEntity.setCreatedBy(addUserRoleReq.getCreatedBy());
		userRoleEntity.setCreatedTs(new Date());
		userRoleEntity.setModifiedBy(addUserRoleReq.getCreatedBy());
		userRoleEntity.setModifiedTs(new Date());
		userRoleEntities.add(userRoleEntity);
		userEntity.setUserRoleEntities(userRoleEntities);
		
		userDAO.updateUser(userEntity);
		
		createRoleChangeApprovalReq(addUserRoleReq.getRole(), userEntity);
		
		userEntity =  userDAO.getUser(addUserRoleReq.getId());
		UserProfileResp userProfileResp = new UserProfileResp();
		if(userEntity != null)
		{
			userProfileResp = CopyBeanProperties.createUserProfile(userEntity);
		}
		return userProfileResp;
	}
	
	private void createRoleChangeApprovalReq(int roleID, UserEntity userEntity)
	{
		OnboardingApprovalEntity onboardingApprovalEntity = commonDAO.getOnboardApproval(roleID,1);
		UserEntity approveUserEntity = null;
		
		approveUserEntity = onboardingApprovalEntity.getUserEntity();
		OnboardApprovalPendingEntity onboardApprovalPendingEntity = new OnboardApprovalPendingEntity();
		onboardApprovalPendingEntity.setUserEntity(approveUserEntity);
		onboardApprovalPendingEntity.setRequestUserEntity(userEntity);
		onboardApprovalPendingEntity.setRequestDate(new Date());
		onboardApprovalPendingEntity.setRequestType("Add Role Request");
		onboardApprovalPendingEntity.setPendingApproval(true);
		onboardApprovalPendingEntity.setLevel(1);
		onboardApprovalPendingEntity.setRoleID(roleID);
		commonDAO.saveOnboardApprovalPendingEntity(onboardApprovalPendingEntity);
		sendApproveEmail(approveUserEntity, userEntity); 
		
	}

	@Override
	@Transactional
	public UserProfileResp getUserProfile(int id) {
		UserEntity userEntity =  userDAO.getUser(id);
		UserProfileResp userProfileResp = new UserProfileResp();
		if(userEntity != null)
		{
			userProfileResp = CopyBeanProperties.createUserProfile(userEntity);
		}
		return userProfileResp;
	}

	@Override
	@Transactional
	public UserProfileResp deleteRole(Integer userRoleId) {
		
		UserRoleEntity userRoleEntity = userDAO.getUserRoleOnUserRoleId(userRoleId);
		UserProfileResp userProfileResp = null;
		if(userRoleEntity != null){
			userRoleEntity.setActiveStatus(false);
			userRoleEntity.setModifiedBy("TEST");
			userRoleEntity.setModifiedTs(new Date());
			
			UserEntity userEntity =  userRoleEntity.getUserEntity();
			if(userEntity != null)
			{
				userProfileResp = CopyBeanProperties.createUserProfile(userEntity);
				userProfileResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.ROLE.DELETE.CODE",null, Locale.getDefault())));
				userProfileResp.setresultString(messageSource.getMessage("USER.ROLE.DELETE.STRING",null, Locale.getDefault()));
			}
		}
		
		return userProfileResp;
	}

	@Override
	@Transactional
	public List<UserDTO> getUsers() {
		// TODO Auto-generated method stub
		List<UserDTO> userDTOs = new ArrayList<UserDTO>();
		UserDTO userDTO = null;
		List<UserEntity> userEntities = userDAO.getUsers();
		for(UserEntity userEntity : userEntities)
		{
			userDTO = new UserDTO();
			userDTO.setId(userEntity.getId());
			userDTO.setFirstName(userEntity.getFirstName());
			userDTO.setLastName(userEntity.getLastName());
			userDTOs.add(userDTO);
		}
		return userDTOs;
	}

}
