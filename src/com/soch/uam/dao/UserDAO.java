package com.soch.uam.dao;

import java.util.List;
import java.util.Set;

import com.soch.uam.domain.ConfigEntity;
import com.soch.uam.domain.DemoUserEntity;
import com.soch.uam.domain.LoginEntity;
import com.soch.uam.domain.OTPEntity;
import com.soch.uam.domain.PolicyConfigEntity;
import com.soch.uam.domain.QuestionaireEntity;
import com.soch.uam.domain.SecauthtokenEntity;
import com.soch.uam.domain.UserEntity;
import com.soch.uam.domain.UserRoleEntity;

public interface UserDAO {
	
	public Integer saveUser(UserEntity userEntity);
	
	public void updateUser(UserEntity userEntity);
	
	public UserEntity getUser(String userId);
	
	public void saveAuthToken(SecauthtokenEntity secauthtokenEntity);
	
	public SecauthtokenEntity getAuthToken(String authToken);

	void updateAuthToken(SecauthtokenEntity secauthtokenEntity);
	
	public void saveUserLogin(LoginEntity loginEntity);
	
	public List<ConfigEntity> getAppConfig();
	
	public String getUserIdOnEmail(String emailId);
	
	public List<QuestionaireEntity> getQuestionnaire();
	
	public List<PolicyConfigEntity> getPolicies();
	
	public void saveOTP(OTPEntity otpEntity);
	
	public void updateOTP(OTPEntity otpEntity);
	
	public OTPEntity getOTP(String OTP);
	
	public Set<UserRoleEntity> getUserRole(String userId);
	
	public DemoUserEntity validateDemoUser(String userId, String password);
	
	public boolean forcePWDChange();
	
}
