package com.soch.uam.serviceimpl;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.transaction.Transactional;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.soch.uam.dao.UserDAO;
import com.soch.uam.domain.AddressEntity;
import com.soch.uam.domain.ConfigEntity;
import com.soch.uam.domain.DemoUserEntity;
import com.soch.uam.domain.LoginEntity;
import com.soch.uam.domain.OTPEntity;
import com.soch.uam.domain.PolicyConfigEntity;
import com.soch.uam.domain.QuestionaireEntity;
import com.soch.uam.domain.SecauthtokenEntity;
import com.soch.uam.domain.SecurityQAEntity;
import com.soch.uam.domain.UserEntity;
import com.soch.uam.domain.UserRoleEntity;
import com.soch.uam.dto.AddressDTO;
import com.soch.uam.dto.ConfigDTO;
import com.soch.uam.dto.SecurityQADTO;
import com.soch.uam.dto.UserDTO;
import com.soch.uam.exception.InternalErrorException;
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
					securityQAEntity.setCreatedTs(new Date());
					securityQAEntity.setCreatedBy(userDTO.getUserId());
					securityQAEntity.setQuestionaireEntity(questionaireEntity);
					
					securityQAEntity.setUpdatedTS(new Date());
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
					
					addressEntity.setCreatedTs(new Date());
					addressEntity.setCreatedBy(userEntity.getUserId());
					
					addressEntity.setUpdatedTs(new Date());
					addressEntity.setUpdatedBy(userEntity.getUserId());
					
					addressEntityList.add(addressEntity);
				}
					
				userEntity.setAddress(addressEntityList);	
			}
				
			userEntity.setCreatedTs(new Date());
			userEntity.setCreatedBy(userEntity.getUserId());
			
			userEntity.setUpdatedTs(new Date());
			userEntity.setUpdatedBy(userEntity.getUserId());
			
			//userEntity.setAddress(UASBeanUtl.convertAddressDomainSetToEntitySet(userDTO.getAddress()));
			if(userDTO.getId() == 0) {
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
				userEntity.setUpdatedTs(new Date());
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
		secauthtokenEntity.setLastAccessTs(new Date());
		secauthtokenEntity.setUserEntity(userEntity);
		secauthtokenEntity.setCreatedTs(new Date());
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
					userEntity.setUpdatedTs(new Date());
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
		//Map<String, String> cacheMap = pojoCacheUtil.getAppConfig();
		if(userEntity!= null )
		{
			returnUserDTO.setActiveFlag(userEntity.getActiveFlag());
			System.out.println(userEntity.getLockFlag());
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
			LoginEntity loginEntity = new LoginEntity();
			loginEntity.setUserEntity(userEntity);
			loginEntity.setLoginTs(new Date());
			loginEntity.setLoginStatus(true);
			userDAO.saveUserLogin(loginEntity);
			returnUserDTO = CopyBeanProperties.copyUserPerperties(userEntity);
				if(userEntity.getLogintEntity().iterator().hasNext())
					returnUserDTO.setLastLoggedin(userEntity.getLogintEntity().iterator().next().getLoginTs().toString());
			}
			else
			{
				userEntity.setLoginFailureCount(userEntity.getLoginFailureCount()+1);
				getPolicies();
				if(userEntity.getLoginFailureCount() == Integer.parseInt((String) policyMap.get("LOGIN_FAILURE_COUNT")))
					userEntity.setLockFlag(true);
				userDAO.updateUser(userEntity);
			}
		}
		else
		{
			
		}
		
		return returnUserDTO;
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
		System.out.println("userId " +userId);
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
		UserDTO userDTO = null;
		Set<SecurityQADTO> securityQADTOs = new HashSet<SecurityQADTO>();
		Set<SecurityQADTO> securityQADTOs2 = new HashSet<SecurityQADTO>();
		
		//userDTO = userMap.get(userId);
		
		if(userDTO == null) {
			
			
			UserEntity userEntity = userDAO.getUser(userId);
			if(userEntity != null)
			{
				userDTO = CopyBeanProperties.copyUserPerperties(userEntity);
				userMap.put(userId,  userDTO.getSecurityQA());
				
				securityQADTOs = userDTO.getSecurityQA();
				
				SecurityQADTO[] securityQADTOArray = securityQADTOs.toArray(new SecurityQADTO[securityQADTOs.size()]);
				int index = getInex(securityQADTOs.size());
				
				SecurityQADTO securityQADTO = securityQADTOArray[index];
				
				securityQADTOs2.add(securityQADTO);
				
				userDTO.setSecurityQA(securityQADTOs2);
			}
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
		maxAttempts = Integer.parseInt((String) policyMap.get("FGT_PWD_ATEMPTS"));
	
		/*if( policyMap.isEmpty())
		{
			getPolicies();
			maxAttempts = Integer.parseInt((String) policyMap.get("FGT_PWD_ATEMPTS"));
		}
		else
		{
			maxAttempts = Integer.parseInt((String) policyMap.get("FGT_PWD_ATEMPTS"));
		}*/
		
		System.out.println("maxAttempts "+Integer.parseInt((String) policyMap.get("FGT_PWD_ATEMPTS")));
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
		System.out.println(policyMap);
		
	}

	@Override
	@Transactional
	public void sendOTP(String userId) {
		
		UserEntity userEntity = userDAO.getUser(userId);
		
		OTPEntity otpEntity = new OTPEntity();
		String OTP = OTPGenerator.random(6);
		
		otpEntity.setOneTimePwd(OTP);
		otpEntity.setActiveFlag(true);
		otpEntity.setCreatedTs(new Date());
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
		
		SendEmail.sendHTMLEmail(toEmail, APPConstants.OTP_EMAIL_SUB, emailBody);
		return returnVal;
	}

	@Override
	@Transactional
	public boolean validateOTP(String otp) {
System.out.println("OTP " + otp);
getPolicies();
		OTPEntity otpEntity = userDAO.getOTP(otp);
		if(otpEntity == null)
			return false;
		
		int validMins = 0;
		if(policyMap.size() > 0)
		{
			validMins = Integer.parseInt((String) policyMap.get("OTP_MINS"));
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

}
