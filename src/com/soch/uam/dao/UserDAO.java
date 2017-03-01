package com.soch.uam.dao;

import java.util.List;
import java.util.Set;

import com.soch.uam.domain.ConfigEntity;
import com.soch.uam.domain.DemoUserEntity;
import com.soch.uam.domain.LoginEntity;
import com.soch.uam.domain.OTPEntity;
import com.soch.uam.domain.OnboardApprovalPendingEntity;
import com.soch.uam.domain.OnboardingUserNotesEntity;
import com.soch.uam.domain.PolicyConfigEntity;
import com.soch.uam.domain.QuestionaireEntity;
import com.soch.uam.domain.SecauthtokenEntity;
import com.soch.uam.domain.TempUserEntity;
import com.soch.uam.domain.UserEntity;
import com.soch.uam.domain.UserRoleEntity;
import com.soch.uam.dto.OnboardingUserNotesDTO;

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
	
	public void addTempUser(TempUserEntity tempUserEntity);
	
	public List<OnboardApprovalPendingEntity> fetchUserPendingRequest(int userId);
	
	public TempUserEntity getTempUser(String userId);
	
	public void updateTempUser(TempUserEntity tempUserEntity);
	
	public OnboardApprovalPendingEntity getOnboardApprovalPendingEntity(String employeeId);
	
	public void updateOnboardApprovalPendingEntity(OnboardApprovalPendingEntity onboardApprovalPendingEntity);
	
	public List<OnboardingUserNotesEntity> getOnboardingUserNotes(String id);
	
	
	//getTempUser
	
}
