package com.soch.uam.controller;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.soch.uam.dto.ContractCompayDTO;
import com.soch.uam.dto.DepartmentDTO;
import com.soch.uam.dto.OnboardApprovalPendingDTO;
import com.soch.uam.dto.OnboardingApprovalDTO;
import com.soch.uam.dto.PendingApprovalResp;
import com.soch.uam.dto.RolesDTO;
import com.soch.uam.dto.SystemDTO;
import com.soch.uam.dto.TempUserDTO;
import com.soch.uam.dto.UserDTO;
import com.soch.uam.dto.policy.AddNewPolicyDTO;
import com.soch.uam.dto.policy.PolicyConfigDTO;
import com.soch.uam.dto.policy.PolicySrcDTO;
import com.soch.uam.request.AddRoleReq;
import com.soch.uam.request.ContactUsReq;
import com.soch.uam.request.UserSVCReq;
import com.soch.uam.response.CommonResp;
import com.soch.uam.response.DeptSysRoleResp;
import com.soch.uam.response.OnboardingReq;
import com.soch.uam.response.PolicySVCResp;
import com.soch.uam.response.UserReq;
import com.soch.uam.response.UserSVCResp;
import com.soch.uam.service.CommonService;
import com.soch.uam.service.UserService;

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
			System.out.println(userDTO.isLockFlag());
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
			else if(userDTO!= null && userDTO.isMaxAttemptReached())
			{
				
				userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.ACCOUNT.MAXATTEMPTS.CODE",null, Locale.getDefault())));
				userSVCResp.setresultString(messageSource.getMessage("USER.ACCOUNT.MAXATTEMPTS.STRING",null, Locale.getDefault()));
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
			}else 	if(userDTO.isLockFlag())
			{
				userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.ACCOUNT.LOCK.CODE",null, Locale.getDefault())));
				userSVCResp.setresultString(messageSource.getMessage("USER.ACCOUNT.LOCK.STRING",null, Locale.getDefault()));
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
			
			Set<PolicySrcDTO> policySrcDTOsl = userSVCReq.getPolicySrcDTOs();
			
			commonService.updatePolicies(policySrcDTOsl);
			
			return userSVCResp;
		}
	 
	 
	 @RequestMapping(value = "/addPolicySVC", method = RequestMethod.POST)
	 @ResponseBody	
	 public UserSVCResp addPolicySVC(@RequestBody  UserSVCReq userSVCReq)
		{
			UserSVCResp userSVCResp = new UserSVCResp();
			
			Set<PolicySrcDTO> policySrcDTOs = userSVCReq.getPolicySrcDTOs();
			
			
			commonService.addPolicy(policySrcDTOs);
			
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
	 public UserSVCResp getDefaultPoliciesSVC(@RequestParam(value="src") int src)
		{
			UserSVCResp userSVCResp = new UserSVCResp();
			
			userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("FORGOT.USER.ID.FOUND.CODE",null, Locale.getDefault())));
			userSVCResp.setresultString(messageSource.getMessage("FORGOT.USER.ID.FOUND.STRING",null, Locale.getDefault()));
			Set<PolicySrcDTO> policySrcDTOs = commonService.gePolicySrcDTOs(src);
			//Set<PolicyConfigDTO> policyConfigDTOs = commonService.getALLPolicies();
			//userSVCResp.setPolicyConfigDTOs(policyConfigDTOs);
			userSVCResp.setPolicySrcDTOs(policySrcDTOs);
			
			return userSVCResp;
		}
	 
	 @RequestMapping(value = "/updIdentityCMSPoliciesSVC", method = RequestMethod.GET)
	 @ResponseBody	
	 public UserSVCResp updIdentityCMSPoliciesSVC(@RequestParam(value="src") int src, @RequestParam(value="policy") String policy) {
		 UserSVCResp userSVCResp = new UserSVCResp();
			
		 commonService.updateIdentityCMSPolicies(src, policy);
		 
		 	userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("POLICY.UPDATE.SUCCESS.CODE",null, Locale.getDefault())));
			userSVCResp.setresultString(messageSource.getMessage("POLICY.UPDATE.SUCCESS.STRING",null, Locale.getDefault()));
			
			return userSVCResp;
	 }
	 
	 
	 @RequestMapping(value = "/updatePoliciesSVC", method = RequestMethod.GET)
	 @ResponseBody	
	 public UserSVCResp updatePoliciesSVC(@RequestParam(value="src") int src, @RequestParam(value="policy") String policy) {
		 UserSVCResp userSVCResp = new UserSVCResp();
			
		 commonService.updateIdentityCMSPolicies(src, policy);
		 
		 	userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("POLICY.UPDATE.SUCCESS.CODE",null, Locale.getDefault())));
			userSVCResp.setresultString(messageSource.getMessage("POLICY.UPDATE.SUCCESS.STRING",null, Locale.getDefault()));
			
			return userSVCResp;
	 }
	 
	 @RequestMapping(value = "/getUserTypesSVC", method = RequestMethod.GET)
	 @ResponseBody	
	 public PolicySVCResp getUserTypesSVC() {
		 PolicySVCResp policySVCResp = new PolicySVCResp();
			
		     policySVCResp.setUserTypeDTOs(commonService.getUserTypes());
			
			return policySVCResp;
	 }
	 
	 //
	 
	 @RequestMapping(value = "/changeUserStatusSVC", method = RequestMethod.GET)
	 @ResponseBody	
	 public UserSVCResp changeUserStatusSVC(@RequestParam(value="userId") String userId) {
		 UserSVCResp userSVCResp = new UserSVCResp();
			
		 	UserDTO userDTO = userService.changeUserStatus(userId);
		 	
		 	userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.UPDATE.SUCCESS.CODE",null, Locale.getDefault())));
			userSVCResp.setresultString(messageSource.getMessage("USER.UPDATE.SUCCESS.STRING",null, Locale.getDefault()));
			userSVCResp.setUser(userDTO);
			return userSVCResp;
	 }
	 
	 
	 @RequestMapping(value = "/contactUsSVC", method = RequestMethod.POST)
	 @ResponseBody	
	 public UserSVCResp contactUsSVC(@RequestBody  ContactUsReq ContactUsReq)
		{
			UserSVCResp userSVCResp = new UserSVCResp();
			
			
			return userSVCResp;
		}
	 
	 @RequestMapping(value = "/searchUserSVC", method = RequestMethod.POST)
	 @ResponseBody	
	 public UserSVCResp searchUserSVC(@RequestBody  UserReq userReq)
		{
			UserSVCResp userSVCResp = new UserSVCResp();
			
			UserDTO userDTO = userService.searchUser(userReq);
			
			if(userDTO != null)
			{
				userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.ID.AVAILABLE.CODE",null, Locale.getDefault())));
				userSVCResp.setresultString(messageSource.getMessage("USER.ID.AVAILABLE.STRING",null, Locale.getDefault()));
				userSVCResp.setUser(userDTO);
			}
			else {
				userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.ID.NOTAVAILABLE.CODE",null, Locale.getDefault())));
				userSVCResp.setresultString(messageSource.getMessage("USER.ID.NOTAVAILABLE.STRING",null, Locale.getDefault()));
			}
			
			return userSVCResp;
		}
	 
	 @RequestMapping(value = "/changeLockStatusSVC", method = RequestMethod.GET)
	 @ResponseBody	
	 public UserSVCResp changeLockStatusSVC(@RequestParam(value="userId") String userId) {
		 UserSVCResp userSVCResp = new UserSVCResp();
			
		 	UserDTO userDTO = userService.changeLockStatus(userId);
		 	
		 	userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.UPDATE.SUCCESS.CODE",null, Locale.getDefault())));
			userSVCResp.setresultString(messageSource.getMessage("USER.UPDATE.SUCCESS.STRING",null, Locale.getDefault()));
			userSVCResp.setUser(userDTO);
			return userSVCResp;
	 }
	 
	 //resetPasswordSVC
	 @RequestMapping(value = "/resetPasswordSVC", method = RequestMethod.GET)
	 @ResponseBody	
	 public UserSVCResp resetPasswordSVC(@RequestParam(value="userId") String userId) {
		 UserSVCResp userSVCResp = new UserSVCResp();
			
		 	UserDTO userDTO = userService.resetUserPwd(userId);
		 	
		 	userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.UPDATE.SUCCESS.CODE",null, Locale.getDefault())));
			userSVCResp.setresultString(messageSource.getMessage("USER.UPDATE.SUCCESS.STRING",null, Locale.getDefault()));
			userSVCResp.setUser(userDTO);
			return userSVCResp;
	 }
	 
	 //resetPasswordSVC
	 @RequestMapping(value = "/logOutSVC", method = RequestMethod.GET)
	 @ResponseBody	
	 public UserSVCResp logOutSVC(@RequestParam(value="token") String token) {
		 UserSVCResp userSVCResp = new UserSVCResp();
			
		 	userService.logOut(token);
		 	
		 	userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.LOGGEDOUT.CODE",null, Locale.getDefault())));
			userSVCResp.setresultString(messageSource.getMessage("USER.LOGGEDOUT.STRING",null, Locale.getDefault()));
			return userSVCResp;
	 }
	 
	 @RequestMapping(value = "/passwordPolicySVC", method = RequestMethod.GET)
	 @ResponseBody	
	 public UserSVCResp passwordPolicySVC() {
		 UserSVCResp userSVCResp = new UserSVCResp();
			
		 	
		 userSVCResp = userService.getPasswordPolicy();
		 	userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.LOGGEDOUT.CODE",null, Locale.getDefault())));
			userSVCResp.setresultString(messageSource.getMessage("USER.LOGGEDOUT.STRING",null, Locale.getDefault()));
			return userSVCResp;
	 }
	 
	 @RequestMapping(value = "/getDepartmentSVC", method = RequestMethod.GET)
	 @ResponseBody	
	 public DeptSysRoleResp getDepartment() {
		 DeptSysRoleResp deptSysRoleResp = new DeptSysRoleResp();
			
		 	Set<DepartmentDTO> departmentDTOs = null;
		 	departmentDTOs = commonService.getDepartments();
		 	deptSysRoleResp.setDepartmentDTOs(departmentDTOs);
		 	deptSysRoleResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.LOGGEDOUT.CODE",null, Locale.getDefault())));
		 	deptSysRoleResp.setresultString(messageSource.getMessage("USER.LOGGEDOUT.STRING",null, Locale.getDefault()));
			return deptSysRoleResp;
	 }
	 
	 @RequestMapping(value = "/getSystemSVC", method = RequestMethod.GET)
	 @ResponseBody	
	 public DeptSysRoleResp getSystemSVC(@RequestParam(value="deptId") Integer deptId) {
		 DeptSysRoleResp deptSysRoleResp = new DeptSysRoleResp();
			
		 	Set<SystemDTO> systemDTOs = null;
		 	systemDTOs = commonService.getDeptSystems(deptId);
		 	deptSysRoleResp.setSystemDTOs(systemDTOs);
		 	deptSysRoleResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.LOGGEDOUT.CODE",null, Locale.getDefault())));
		 	deptSysRoleResp.setresultString(messageSource.getMessage("USER.LOGGEDOUT.STRING",null, Locale.getDefault()));
			return deptSysRoleResp;
	 }
	 
	 @RequestMapping(value = "/getSystemRolesSVC", method = RequestMethod.GET)
	 @ResponseBody	
	 public DeptSysRoleResp getSystemRolesSVC(@RequestParam(value="sysId") Integer sysId) {
		 DeptSysRoleResp deptSysRoleResp = new DeptSysRoleResp();
			
		 	Set<RolesDTO> rolesDTOs = null;
		 	rolesDTOs = commonService.getSystemRoles(sysId);
		 	deptSysRoleResp.setRolesDTOs(rolesDTOs);
		 	deptSysRoleResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.LOGGEDOUT.CODE",null, Locale.getDefault())));
		 	deptSysRoleResp.setresultString(messageSource.getMessage("USER.LOGGEDOUT.STRING",null, Locale.getDefault()));
			return deptSysRoleResp;
	 }
	 //getSystemSVC
	 
	 @RequestMapping(value = "/onBoardingSVC", method = RequestMethod.POST)
	 @ResponseBody	
	 public UserSVCResp onBoardingSVC(@RequestBody  OnboardingReq onboardingReq)
		{
		 	UserSVCResp userSVCResp = new UserSVCResp();
		    Integer response = userService.onBoardingUser(onboardingReq);
		    if(response == 0)
		    {
				userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.REGISTRATION.SUCCUESS.CODE",null, Locale.getDefault())));
				userSVCResp.setresultString(messageSource.getMessage("USER.REGISTRATION.SUCCUESS.STATUS",null, Locale.getDefault()));
		    }
		    else
		    {
		    	userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.ONBOARD.ROLES.CONFILT.CODE",null, Locale.getDefault())));
				userSVCResp.setresultString(messageSource.getMessage("USER.ONBOARD.ROLES.CONFILT.STRING",null, Locale.getDefault()));
		    }
		    
			return userSVCResp;
		}
	 
	 @RequestMapping(value = "/getPendingRequestsSVC", method = RequestMethod.GET)
	 @ResponseBody	
	 public PendingApprovalResp getPendingRequestsSVC(@RequestParam(value="id") int userId)
		{
		 PendingApprovalResp pendingApprovalResp = new PendingApprovalResp();
		 	 Set<OnboardApprovalPendingDTO> approvalPendingDTOs = userService.getPendingReq(userId);
		 	pendingApprovalResp.setOnboardApprovalPendingDTOs(approvalPendingDTOs);
		 	pendingApprovalResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.REGISTRATION.SUCCUESS.CODE",null, Locale.getDefault())));
		 	pendingApprovalResp.setresultString(messageSource.getMessage("USER.REGISTRATION.SUCCUESS.STATUS",null, Locale.getDefault()));
			return pendingApprovalResp;
		}
	 
	 @RequestMapping(value = "/getTempUserSVC", method = RequestMethod.GET)
	 @ResponseBody	
	 public UserSVCResp getTempUserSVC(@RequestParam(value="id") String userId)
		{
		 UserSVCResp userSVCResp = new UserSVCResp();
		 	 TempUserDTO tempUserDTO = userService.getTempUser(userId);
		 	userSVCResp.setTempUserDTO(tempUserDTO);
		 	userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("USER.LOGIN.SUCCESS.CODE",null, Locale.getDefault())));
			userSVCResp.setresultString(messageSource.getMessage("USER.LOGIN.SUCCESS.STRING",null, Locale.getDefault()));
			return userSVCResp;
		}
	 
	 @RequestMapping(value = "/approveRejectUserSVC", method = RequestMethod.POST)
	 @ResponseBody	
	 public UserSVCResp approveRejectUserSVC(@RequestBody TempUserDTO tempUserDTO)
		{
		 	UserSVCResp userSVCResp = new UserSVCResp();
		 	userSVCResp = userService.approveRejectUser(tempUserDTO);
			return userSVCResp;
		}
	 
	 @RequestMapping(value = "/getOnboardUserSVC", method = RequestMethod.GET)
	 @ResponseBody	
	 public UserSVCResp getOnboardUserSVC(@RequestParam(value="token") String token)
		{
		 UserSVCResp userSVCResp = new UserSVCResp();
		 userSVCResp = userService.getOnboardUser(token);
		 return userSVCResp;
		}
	 
	 
	 @RequestMapping(value = "/fetchTempUserNotesSVC", method = RequestMethod.GET)
	 @ResponseBody	
	 public UserSVCResp fetchTempUserNotesSVC(@RequestParam(value="id") String id)
		{
		 UserSVCResp userSVCResp = new UserSVCResp();
		 return userSVCResp;
		}
	 
	 
	 @RequestMapping(value = "/getPolicyGroupSVC", method = RequestMethod.GET)
	 @ResponseBody	
	 public UserSVCResp getPolicyGroupSVC(@RequestParam(value="userType") int userType)
		{
		 UserSVCResp userSVCResp = new UserSVCResp();
		 userSVCResp.setPolicyGrpDTOs(commonService.getPolicyGroup(userType));
		 return userSVCResp;
		}
	 
	 @RequestMapping(value = "/addNewPolicySVC", method = RequestMethod.POST)
	 @ResponseBody	
	 public UserSVCResp addNewPolicySVC(@RequestBody AddNewPolicyDTO addNewPolicyDTO)
		{
		 UserSVCResp userSVCResp = new UserSVCResp();
		 commonService.addNewPolicyDTO(addNewPolicyDTO);
		 userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("GENERAL.SUCCESS.CODE",null, Locale.getDefault())));
		 userSVCResp.setresultString(messageSource.getMessage("GENERAL.SUCCESS.STRING",null, Locale.getDefault()));
		 return userSVCResp;
		}
	 
	 @RequestMapping(value = "/validateTokenSVC", method = RequestMethod.GET)
	 @ResponseBody	
	 public UserSVCResp validateTokenSVC(@RequestParam(value="token") String token,@RequestParam(value="userId") Integer userId)
		{
		 UserSVCResp userSVCResp = new UserSVCResp();
		 if(userService.validateToken(token, userId))
		 {
			 userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("GENERAL.SUCCESS.CODE",null, Locale.getDefault())));
			 userSVCResp.setresultString(messageSource.getMessage("GENERAL.SUCCESS.STRING",null, Locale.getDefault()));
		 }
		 else
		 {
			 userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("INVALID.TOKEN.CODE",null, Locale.getDefault())));
			 userSVCResp.setresultString(messageSource.getMessage("INVALID.TOKEN.STRING",null, Locale.getDefault()));
		 }
	
		 return userSVCResp;
		}
	 
	 
	 @RequestMapping(value = "/getApprovalListSVC", method = RequestMethod.GET)
	 @ResponseBody	
	 public UserSVCResp getApprovalListSVC(@RequestParam(value="roleId") Integer roleId)
		{
		 UserSVCResp userSVCResp = new UserSVCResp();
		 
		 Set<OnboardingApprovalDTO>  approvalDTOs =userService.getApprovalList(roleId);
		 
		 userSVCResp.setOnboardingApprovalDTOs(approvalDTOs);
		 
		/* if(userService.validateToken(token, userId))
		 {
			 userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("GENERAL.SUCCESS.CODE",null, Locale.getDefault())));
			 userSVCResp.setresultString(messageSource.getMessage("GENERAL.SUCCESS.STRING",null, Locale.getDefault()));
		 }
		 else
		 {
			 userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("INVALID.TOKEN.CODE",null, Locale.getDefault())));
			 userSVCResp.setresultString(messageSource.getMessage("INVALID.TOKEN.STRING",null, Locale.getDefault()));
		 }*/
	
		 return userSVCResp;
		}
	 
	 
	 @RequestMapping(value = "/addApprovalSVC", method = RequestMethod.POST)
	 @ResponseBody	
	 public UserSVCResp addApprovalSVC(@RequestBody  OnboardingApprovalDTO onboardingApprovalDTO)
		{
			 UserSVCResp userSVCResp = new UserSVCResp();
			 int status = commonService.addOnboardApproval(onboardingApprovalDTO);
			 System.out.println("status "+status);
			 if(status == 1)
			 {
				 userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("DUPLICATE.ONBOARD.APPROVAL.CODE",null, Locale.getDefault())));
				 userSVCResp.setresultString(messageSource.getMessage("DUPLICATE.ONBOARD.APPROVAL.STRING",null, Locale.getDefault()));
				 //DUPLICATE.ONBOARD.APPROVAL.STRING
			 }
			 else {
				 userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("GENERAL.SUCCESS.CODE",null, Locale.getDefault())));
				 userSVCResp.setresultString(messageSource.getMessage("GENERAL.SUCCESS.STRING",null, Locale.getDefault()));
			 }
			 return userSVCResp;
		}
	 
	 @RequestMapping(value = "/addNewRoleSVC", method = RequestMethod.POST)
	 @ResponseBody	
	 public UserSVCResp addNewRoleSVC(@RequestBody  AddRoleReq roleReq)
		{
			 UserSVCResp userSVCResp = new UserSVCResp();
			 int status = commonService.addRole(roleReq);
			 if(status == 1)
			 {
				 userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("DUPLICATE.ONBOARD.APPROVAL.CODE",null, Locale.getDefault())));
				 userSVCResp.setresultString(messageSource.getMessage("DUPLICATE.ONBOARD.APPROVAL.STRING",null, Locale.getDefault()));
				 //DUPLICATE.ONBOARD.APPROVAL.STRING
			 }
			 else {
				 userSVCResp.setResultCode(Integer.parseInt(messageSource.getMessage("GENERAL.SUCCESS.CODE",null, Locale.getDefault())));
				 userSVCResp.setresultString(messageSource.getMessage("GENERAL.SUCCESS.STRING",null, Locale.getDefault()));
			 }
			 return userSVCResp;
		}
	 
	 @RequestMapping(value = "/getContractCompanyListSVC", method = RequestMethod.GET)
	 @ResponseBody	
	 public CommonResp getContractCompanyListSVC()
		{
		 CommonResp commonResp = new CommonResp();
		 
		 List<ContractCompayDTO> compayDTOs =commonService.getContractCompanyList();
		 
		 commonResp.setCompayDTOs(compayDTOs);
	
		 return commonResp;
		}
	 
	 //getContractCompanyListSVC
	 
	 //fetchTempUserNotes
	 
	 //getOnboardUser
	 
}
