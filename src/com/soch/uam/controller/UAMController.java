package com.soch.uam.controller;

import java.util.Locale;
import java.util.Set;

import javax.validation.Valid;

import org.hibernate.exception.ConstraintViolationException;
import org.jboss.security.auth.spi.Users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.soch.uam.dao.UserDAO;
import com.soch.uam.domain.PolicyConfigEntity;
import com.soch.uam.dto.PolicyConfigDTO;
import com.soch.uam.dto.PolicySrcDTO;
import com.soch.uam.dto.UserDTO;
import com.soch.uam.exception.InvalidDataException;
import com.soch.uam.request.UserSVCReq;
import com.soch.uam.response.UserSVCResp;
import com.soch.uam.service.CommonService;
import com.soch.uam.service.UserService;
import com.soch.uam.util.POJOCacheUtil;

@Controller
@RequestMapping("/")
public class UAMController {
	
	@Autowired
	MessageSource messageSource;
	
	@RequestMapping(value="/register/{userId}", method = RequestMethod.GET)
	public String registerUser( String userId){
		
		System.out.println("Test");
		return "Test";

    }
	
	@Autowired
	UserService userService;
	
	@Autowired
	CommonService commonService;
	
	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	 @ResponseBody
	 public UserSVCResp signupUser(@RequestBody  UserSVCReq userSVCReq)
		{
			System.out.println(userSVCReq.getUser().getEmailId());
			
			UserDTO userDTO = userService.signUpUser(userSVCReq.getUser());
			
			UserSVCResp userSVCResp = new UserSVCResp();
			userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.REGISTRATION.SUCCUESS.CODE",null, Locale.getDefault())));
			userSVCResp.setresultString(messageSource.getMessage("USER.REGISTRATION.SUCCUESS.STATUS",null, Locale.getDefault()));
			userSVCResp.setUser(userDTO);
			return userSVCResp;
		}
	
	 @RequestMapping(value = "/validateDemoUserSVC", method = RequestMethod.POST)
	 @ResponseBody
	 public UserSVCResp validateDemoUser(@RequestBody  UserSVCReq userSVCReq)
		{
			UserSVCResp userSVCResp = new UserSVCResp();
			
			 if(userService.validateDemoUser(userSVCReq.getUser()))
			 {
					userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.ID.AVAILABLE.CODE",null, Locale.getDefault())));
					userSVCResp.setresultString(messageSource.getMessage("USER.ID.AVAILABLE.STRING",null, Locale.getDefault()));
				}
				else {
					userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.ID.NOTAVAILABLE.CODE",null, Locale.getDefault())));
					userSVCResp.setresultString(messageSource.getMessage("USER.ID.NOTAVAILABLE.STRING",null, Locale.getDefault()));
				}
			return userSVCResp;
		}
	 
	 @RequestMapping(value = "/resetPwdSVC", method = RequestMethod.POST)
	 @ResponseBody
	 public UserSVCResp resetPwdSVC(@RequestBody  UserSVCReq userSVCReq)
		{
			UserSVCResp userSVCResp = new UserSVCResp();
			
			
			if(userService.resetPwd(userSVCReq.getUser())) {
			userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.ID.AVAILABLE.CODE",null, Locale.getDefault())));
			userSVCResp.setresultString(messageSource.getMessage("USER.ID.AVAILABLE.STRING",null, Locale.getDefault()));
			}
			else
			{
				userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("INVALID.TOKEN.CODE",null, Locale.getDefault())));
				userSVCResp.setresultString(messageSource.getMessage("INVALID.TOKEN.STRING",null, Locale.getDefault()));
			}
			
			/*\\ if(userService.validateDemoUser(userSVCReq.getUser()))
			 {
					userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.ID.AVAILABLE.CODE",null, Locale.getDefault())));
					userSVCResp.setresultString(messageSource.getMessage("USER.ID.AVAILABLE.STRING",null, Locale.getDefault()));
				}
				else {
					userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.ID.NOTAVAILABLE.CODE",null, Locale.getDefault())));
					userSVCResp.setresultString(messageSource.getMessage("USER.ID.NOTAVAILABLE.STRING",null, Locale.getDefault()));
				}*/
			return userSVCResp;
		}
	
	
	 @RequestMapping(value = "/checkDuplicateId", method = RequestMethod.GET)
	 @ResponseBody
	 public UserSVCResp checkDuplicateId(@RequestParam(value="userId") String userId)
		{
			UserSVCResp userSVCResp = new UserSVCResp();
			
			if(userService.isUserIdAvailable(userId))
			{
				userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.ID.AVAILABLE.CODE",null, Locale.getDefault())));
				userSVCResp.setresultString(messageSource.getMessage("USER.ID.AVAILABLE.STRING",null, Locale.getDefault()));
			}
			else {
				userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.ID.NOTAVAILABLE.CODE",null, Locale.getDefault())));
				userSVCResp.setresultString(messageSource.getMessage("USER.ID.NOTAVAILABLE.STRING",null, Locale.getDefault()));
			}
			
			return userSVCResp;
		}
	 
	 
	 
	 @RequestMapping(value = "/validateRegisterSVC", method = RequestMethod.GET)
	 @ResponseBody
	 public UserSVCResp validateRegisterSVC(@RequestParam(value="token") String token)
		{
			UserSVCResp userSVCResp = new UserSVCResp();
			if(userService.validateRegister(token))
			{
				userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.ID.ACTIVATION.SUCCESS.CODE",null, Locale.getDefault())));
				userSVCResp.setresultString(messageSource.getMessage("USER.ID.ACTIVATION.SUCCESS.STRING",null, Locale.getDefault()));
			}
			else
			{
				
				userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.ID.ACTIVATION.FAIL.CODE",null, Locale.getDefault())));
				userSVCResp.setresultString(messageSource.getMessage("USER.ID.ACTIVATION.FAIL.STRING",null, Locale.getDefault()));
			}
			
			return userSVCResp;
		}
	 
	 //signIn
	 
	 @RequestMapping(value = "/signIn", method = RequestMethod.POST)
	 @ResponseBody	
	 public UserSVCResp signIn(@RequestBody  UserSVCReq userSVCReq)
		{
			UserSVCResp userSVCResp = new UserSVCResp();
			UserDTO userDTO =  userService.signInUser(userSVCReq.getUser());
			if(userDTO.getUserId() == null)
			{
				userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.LOGIN.FAILURE.CODE",null, Locale.getDefault())));
				userSVCResp.setresultString(messageSource.getMessage("USER.LOGIN.FAILURE.STRING",null, Locale.getDefault()));
			}
			else if(userDTO!= null && userDTO.isLockFlag())
			{
				
				userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.ACCOUNT.LOCK.CODE",null, Locale.getDefault())));
				userSVCResp.setresultString(messageSource.getMessage("USER.ACCOUNT.LOCK.STRING",null, Locale.getDefault()));
				userSVCResp.setUser(userDTO);
			}
			else if(userDTO!= null && !userDTO.isActiveFlag())
			{
				
				userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.ACCOUNT.INACTIVE.CODE",null, Locale.getDefault())));
				userSVCResp.setresultString(messageSource.getMessage("USER.ACCOUNT.INACTIVE.STRING",null, Locale.getDefault()));
				userSVCResp.setUser(userDTO);
			}
			else if(userDTO!= null && userDTO.isPwdChangeFlag())
			{
				
				userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.PASSWORD.RESET.CODE",null, Locale.getDefault())));
				userSVCResp.setresultString(messageSource.getMessage("USER.PASSWORD.RESET.STRING",null, Locale.getDefault()));
				userSVCResp.setUser(userDTO);
			}
			else if(userDTO!= null && userDTO.isActiveFlag())
			{
				
				userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.LOGIN.SUCCESS.CODE",null, Locale.getDefault())));
				userSVCResp.setresultString(messageSource.getMessage("USER.LOGIN.SUCCESS.STRING",null, Locale.getDefault()));
				userSVCResp.setUser(userDTO);
			}
			return userSVCResp;
		}
	 	
	 @RequestMapping(value = "/forgotUserIdSvc", method = RequestMethod.GET)
	 @ResponseBody
	 public UserSVCResp forgotUserIdSVC(@RequestParam(value="emailId") String emailId)
		{
			UserSVCResp userSVCResp = new UserSVCResp();
			
			if(userService.forgotUserId(emailId))
			{
				userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("FORGOT.USER.ID.FOUND.CODE",null, Locale.getDefault())));
				userSVCResp.setresultString(messageSource.getMessage("FORGOT.USER.ID.FOUND.STRING",null, Locale.getDefault()));
			}
			else {
				userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("FORGOT.USER.ID.NOTFOUND.CODE",null, Locale.getDefault())));
				userSVCResp.setresultString(messageSource.getMessage("FORGOT.USER.ID.NOTFOUND.STRING",null, Locale.getDefault()));
			}
			
			return userSVCResp;
		}
	 
	 @RequestMapping(value = "/forgotPWDSvc", method = RequestMethod.GET)
	 @ResponseBody
	 public UserSVCResp forgotPWDSvc(@RequestParam(value="userId") String userId)
		{
			UserSVCResp userSVCResp = new UserSVCResp();
			UserDTO userDTO = null;
			
			userDTO = userService.forgotPassword(userId);
			
			if(userDTO != null)
			{
				userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("FORGOT.USER.ID.FOUND.CODE",null, Locale.getDefault())));
				userSVCResp.setresultString(messageSource.getMessage("FORGOT.USER.ID.FOUND.STRING",null, Locale.getDefault()));
				userSVCResp.setUser(userDTO);
			}
			else {
				userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("FORGOT.USER.ID.NOTFOUND.CODE",null, Locale.getDefault())));
				userSVCResp.setresultString(messageSource.getMessage("FORGOT.USER.ID.NOTFOUND.STRING",null, Locale.getDefault()));
			}
			
			return userSVCResp;
		}
	
	 
	 //validateQA
	 
	 
	 @RequestMapping(value = "/validateQA", method = RequestMethod.POST)
	 @ResponseBody
	 public UserSVCResp validateQA(@RequestBody  UserSVCReq userSVCReq)
		{
		 	UserSVCResp userSVCResp = new UserSVCResp();
			
		 	UserDTO userDTO = userService.validateQA(userSVCReq.getUser());
			if(userDTO != null)
			{
				if(userDTO.getToken()!= null && userDTO.getToken().equals("MAXATTEMPTS"))
				{
					userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("FORGOT.PWD.MAXATTEMPTS.CODE",null, Locale.getDefault())));
					userSVCResp.setresultString(messageSource.getMessage("FORGOT.PWD.MAXATTEMPTS.STRING",null, Locale.getDefault()));
					userSVCResp.setUser(userDTO);
				}else {
				userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("FORGOT.USER.ID.FOUND.CODE",null, Locale.getDefault())));
				userSVCResp.setresultString(messageSource.getMessage("FORGOT.USER.ID.FOUND.STRING",null, Locale.getDefault()));
				userSVCResp.setUser(userDTO);
				}
			}
			else {
				userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("FORGOT.USER.ID.SUCC.CODE",null, Locale.getDefault())));
				userSVCResp.setresultString(messageSource.getMessage("FORGOT.USER.ID.NOTFOUND.STRING",null, Locale.getDefault()));
			}
			return userSVCResp;
		}
	 
	 
	 @RequestMapping(value = "/retreivePolicies", method = RequestMethod.GET)
	 @ResponseBody
	 public UserSVCResp retreivePolicies()
		{
			
		 	UserSVCResp userSVCResp = new UserSVCResp();	
		 	Set<PolicyConfigDTO> policyConfigDTOs = commonService.getALLPolicies();
			
			userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("FORGOT.USER.ID.FOUND.CODE",null, Locale.getDefault())));
			userSVCResp.setresultString(messageSource.getMessage("FORGOT.USER.ID.FOUND.STRING",null, Locale.getDefault()));
			userSVCResp.setPolicyConfigDTOs(policyConfigDTOs);
			
			
			return userSVCResp;
		}
	 
	 @RequestMapping(value = "/updatePolicySVC", method = RequestMethod.POST)
	 @ResponseBody	
	 public UserSVCResp updatePolicySVC(@RequestBody  UserSVCReq userSVCReq)
		{
			UserSVCResp userSVCResp = new UserSVCResp();
			
			Set<PolicyConfigDTO> policyConfigDTOs = userSVCReq.getPolicyConfigDTOs();
			
			commonService.updatePolicies(policyConfigDTOs);
			
			return userSVCResp;
		}
	 
	 
	 @RequestMapping(value = "/addPolicySVC", method = RequestMethod.POST)
	 @ResponseBody	
	 public UserSVCResp addPolicySVC(@RequestBody  UserSVCReq userSVCReq)
		{
			UserSVCResp userSVCResp = new UserSVCResp();
			
			Set<PolicyConfigDTO> policyConfigDTOs = userSVCReq.getPolicyConfigDTOs();
			
			commonService.addPolicy(policyConfigDTOs);
			
			return userSVCResp;
		}
	 
	 
	 @RequestMapping(value = "/forcePWDChangeSVC", method = RequestMethod.GET)
	 @ResponseBody	
	 public UserSVCResp forcePWDChange()
		{
			UserSVCResp userSVCResp = new UserSVCResp();
			
			userService.forcePWDChange();
			
			return userSVCResp;
		}
	 
	 
	 @RequestMapping(value = "/OTPSVC", method = RequestMethod.GET)
	 @ResponseBody	
	 public UserSVCResp OTPSVC(@RequestParam(value="userId") String userId)
		{
			UserSVCResp userSVCResp = new UserSVCResp();
			
			userService.sendOTP(userId);
			
			//OTP.CREATED
			userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("OTP.CREATED",null, Locale.getDefault())));
			return userSVCResp;
		}
	 
	 @RequestMapping(value = "/validateOTPSVC", method = RequestMethod.GET)
	 @ResponseBody	
	 public UserSVCResp validateOTPSVC(@RequestParam(value="otp") String otp)
		{
			UserSVCResp userSVCResp = new UserSVCResp();
			
			if(userService.validateOTP(otp))
			{
				userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("OTP.VERIFICATION.SUCCESS",null, Locale.getDefault())));
				userSVCResp.setresultString(messageSource.getMessage("OTP.VERIFICATION.SUCCESSS.STRING",null, Locale.getDefault()));;
			}else
			{
				userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("OTP.VERIFICATION.FAIL",null, Locale.getDefault())));
				//OTP.VERIFICATION.SUCCESSS.STRING
				userSVCResp.setresultString(messageSource.getMessage("OTP.VERIFICATION.FAIL.STRING",null, Locale.getDefault()));
			}
			
			//OTP.CREATED
			return userSVCResp;
		}
	 
	 @RequestMapping(value = "/fetchRoleSVC", method = RequestMethod.GET)
	 @ResponseBody	
	 public UserSVCResp fetchRoleSVC(@RequestParam(value="userId") String userId)
		{
			UserSVCResp userSVCResp = new UserSVCResp();
			
			Set<Integer> userRoleIds = userService.gerUserRole(userId);
			
			userSVCResp.setRoleIds(userRoleIds);
			
			//OTP.CREATED
			//userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("OTP.CREATED",null, Locale.getDefault())));
			return userSVCResp;
		}
	 
	 
	 @RequestMapping(value = "/getDefaultPoliciesSVC", method = RequestMethod.GET)
	 @ResponseBody	
	 public UserSVCResp getDefaultPoliciesSVC()
		{
			UserSVCResp userSVCResp = new UserSVCResp();
			
			userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("FORGOT.USER.ID.FOUND.CODE",null, Locale.getDefault())));
			userSVCResp.setresultString(messageSource.getMessage("FORGOT.USER.ID.FOUND.STRING",null, Locale.getDefault()));
			Set<PolicySrcDTO> policySrcDTOs = commonService.gePolicySrcDTOs();
			Set<PolicyConfigDTO> policyConfigDTOs = commonService.getALLPolicies();
			userSVCResp.setPolicyConfigDTOs(policyConfigDTOs);
			userSVCResp.setPolicySrcDTOs(policySrcDTOs);
			
			return userSVCResp;
		}
	 
	 @RequestMapping(value = "/updIdentityCMSPoliciesSVC", method = RequestMethod.GET)
	 @ResponseBody	
	 public UserSVCResp updIdentityCMSPoliciesSVC() {
		 UserSVCResp userSVCResp = new UserSVCResp();
			
		 commonService.updateIdentityCMSPolicies();
		 
		 	userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("POLICY.UPDATE.SUCCESS.CODE",null, Locale.getDefault())));
			userSVCResp.setresultString(messageSource.getMessage("POLICY.UPDATE.SUCCESS.STRING",null, Locale.getDefault()));
			
			return userSVCResp;
	 }
	 
	 
	 
}
