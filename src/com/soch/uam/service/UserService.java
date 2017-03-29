package com.soch.uam.service;

import java.util.List;
import java.util.Set;

import com.soch.uam.domain.OnboardApprovalPendingEntity;
import com.soch.uam.domain.TempUserEntity;
import com.soch.uam.dto.ConfigDTO;
import com.soch.uam.dto.OnboardApprovalPendingDTO;
import com.soch.uam.dto.OnboardingApprovalDTO;
import com.soch.uam.dto.OnboardingUserNotesDTO;
import com.soch.uam.dto.TempUserDTO;
import com.soch.uam.dto.UserDTO;
import com.soch.uam.response.OnboardingReq;
import com.soch.uam.response.UserReq;
import com.soch.uam.response.UserSVCResp;

public interface UserService {
		public UserDTO signUpUser(UserDTO userDTO);
		public boolean isUserIdAvailable(String userId);
		//checkDuplicateId
		boolean validateRegister(String authToken);
		public UserDTO signInUser(UserDTO userDTO);
		public List<ConfigDTO> getAppConfig();
		
		public boolean forgotUserId(String email);
		
		public UserDTO forgotPassword(String userId);
		
		public UserDTO validateQA(UserDTO userDTO);
		
		public void sendOTP(String userId);
		
		public boolean validateOTP(String otp);
		
		public Set<Integer> gerUserRole(String userId);
		
		public boolean validateDemoUser(UserDTO user);
		
		public boolean resetPwd(UserDTO user);
		
		public boolean forcePWDChange();
		
		public UserDTO searchUser(UserReq userReq);
		
		public UserDTO changeUserStatus(String userId);
		public UserDTO changeLockStatus(String userId);
		
		public UserDTO resetUserPwd(String userId);
		
		public void logOut(String token);
		
		public UserSVCResp getPasswordPolicy();
		
		public Integer onBoardingUser(OnboardingReq onboardingReq);
		
		public Set<OnboardApprovalPendingDTO> getPendingReq(int userId);
		
		public TempUserDTO getTempUser(String userId);
		public UserSVCResp approveRejectUser(TempUserDTO tempUserDTO);
		
		public UserSVCResp getOnboardUser(String token);
		
		public Set<OnboardingUserNotesDTO> getTempUserNotes(String id);
		//getOnboardUser
		public boolean validateToken(String token, Integer userId);
		public Set<OnboardingApprovalDTO>  getApprovalList(Integer roleId);
}
