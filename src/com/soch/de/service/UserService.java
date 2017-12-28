package com.soch.de.service;

import java.util.List;
import java.util.Set;

import com.soch.de.domain.OnboardApprovalPendingEntity;
import com.soch.de.domain.TempUserEntity;
import com.soch.uam.dto.ConfigDTO;
import com.soch.uam.dto.OnboardApprovalPendingDTO;
import com.soch.uam.dto.OnboardApprovedDTO;
import com.soch.uam.dto.OnboardingApprovalDTO;
import com.soch.uam.dto.OnboardingUserNotesDTO;
import com.soch.uam.dto.TempUserDTO;
import com.soch.uam.dto.UserDTO;
import com.soch.uam.request.AddUserRoleReq;
import com.soch.uam.request.UserProfileResp;
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
		
		public UserDTO forgotPassword(String userId,boolean tempPwd);
		
		public UserDTO validateQA(UserDTO userDTO);
		
		public void sendOTP(String userId);
		
		public boolean validateOTP(String otp);
		
		public Set<Integer> gerUserRole(String userId);
		
		public boolean validateDemoUser(UserDTO user);
		
		public int resetPwd(UserDTO user);
		
		public boolean forcePWDChange();
		
		public UserProfileResp searchUser(UserReq userReq);
		
		public UserDTO changeUserStatus(UserReq userReq);
		public UserDTO changeLockStatus(UserReq userReq);
		
		public UserDTO resetUserPwd(UserReq userReq);
		
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
		
		public UserProfileResp addRole(AddUserRoleReq addUserRoleReq);
		
		public UserProfileResp getUserProfile(int id);
		
		public UserProfileResp deleteRole(Integer userRoleId);
		
		public boolean refreshToken(String token, Integer userId);
		List<OnboardApprovedDTO> getApprovedRequests(int userId);
		Set<OnboardApprovalPendingDTO> getCreatedBy(int userId);
		
		List<UserDTO> getUsers();
}
