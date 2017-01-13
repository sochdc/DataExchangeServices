package com.soch.uam.service;

import java.util.List;
import java.util.Set;

import com.soch.uam.dto.ConfigDTO;
import com.soch.uam.dto.UserDTO;

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
}
