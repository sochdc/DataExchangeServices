package com.soch.uam.daoimpl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.soch.uam.dao.UserDAO;
import com.soch.uam.domain.ConfigEntity;
import com.soch.uam.domain.DemoUserEntity;
import com.soch.uam.domain.LoginEntity;
import com.soch.uam.domain.OTPEntity;
import com.soch.uam.domain.OnboardApprovalAuditEntity;
import com.soch.uam.domain.OnboardApprovalPendingEntity;
import com.soch.uam.domain.OnboardingUserNotesEntity;
import com.soch.uam.domain.PolicyConfigEntity;
import com.soch.uam.domain.PolicySrcEntity;
import com.soch.uam.domain.PwdHistoryEntity;
import com.soch.uam.domain.QuestionaireEntity;
import com.soch.uam.domain.SecauthtokenEntity;
import com.soch.uam.domain.TempUserEntity;
import com.soch.uam.domain.TempUserRoleEntity;
import com.soch.uam.domain.UserActivityEntity;
import com.soch.uam.domain.UserEntity;
import com.soch.uam.domain.UserRoleEntity;

@Component
public class UserDAOImpl implements UserDAO{
	
	@Autowired
    private SessionFactory sessionFactory;
	
	@Override
	public Integer saveUser(UserEntity userEntity) {
		Integer id = null;
		try {
			id = (Integer) this.sessionFactory.getCurrentSession().save(userEntity);		
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return id;
	}

	@Override
	public UserEntity getUser(String userId) {
		
		List<UserEntity> userList;
		UserEntity userEntity = null;
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(UserEntity.class);
		criteria.add(Restrictions.eq("userId", userId));
		//criteria.add(Restrictions.eq("activeFlag", true));
		//criteria.setFetchMode("address", FetchMode.JOIN);
		//criteria.setFetchMode("securityQA", FetchMode.JOIN);
		userList = criteria.list();
		if(userList.size() > 0) {
			userEntity = userList.get(0);
		}
		
		return userEntity;
	}
	
	@Override
	public UserEntity getUser(Integer id) {
		
		List<UserEntity> userList;
		UserEntity userEntity = null;
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(UserEntity.class);
		criteria.add(Restrictions.eq("id", id));
		//criteria.add(Restrictions.eq("activeFlag", true));
		//criteria.setFetchMode("address", FetchMode.JOIN);
		//criteria.setFetchMode("securityQA", FetchMode.JOIN);
		userList = criteria.list();
		if(userList.size() > 0) {
			userEntity = userList.get(0);
		}
		
		return userEntity;
	}

	@Override
	public void updateUser(UserEntity userEntity) {
		try {
			this.sessionFactory.getCurrentSession().update(userEntity);		
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}

	@Override
	public void saveAuthToken(SecauthtokenEntity secauthtokenEntity) {
			this.sessionFactory.getCurrentSession().save(secauthtokenEntity);		
	}
	
	@Override
	public void updateAuthToken(SecauthtokenEntity secauthtokenEntity) {
			this.sessionFactory.getCurrentSession().update(secauthtokenEntity);		
	}

	@SuppressWarnings("unchecked")
	@Override
	public SecauthtokenEntity getAuthToken(String authToken) {
		SecauthtokenEntity secauthtokenEntity = null;
		
		List<SecauthtokenEntity> secauthtokenEntities;
		UserEntity userEntity = null;
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(SecauthtokenEntity.class);
		criteria.add(Restrictions.eq("authToken", authToken));
		criteria.add(Restrictions.eq("status", true));
		secauthtokenEntities = criteria.list();
		if(secauthtokenEntities.size() > 0)
			secauthtokenEntity = secauthtokenEntities.get(0);
		
		return secauthtokenEntity;
	}
	
	@Override
	public void saveUserLogin(LoginEntity loginEntity) {
			this.sessionFactory.getCurrentSession().save(loginEntity);		
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ConfigEntity> getAppConfig() {
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(ConfigEntity.class);
		
		List<ConfigEntity> configEntities = criteria.list();
		
		return configEntities;
	}

	@Override
	public String getUserIdOnEmail(String emailId) {
		
		String userId = null;
		List<UserEntity> userList;
		UserEntity userEntity = null;
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(UserEntity.class);
		criteria.add(Restrictions.ilike("emailId", emailId));
		userList = criteria.list();
		
		if(!userList.isEmpty())
		{
			userId = userList.get(0).getUserId();
		}
		return userId;
	}

	@Override
	public List<QuestionaireEntity> getQuestionnaire() {
		
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(QuestionaireEntity.class);
		
		return criteria.list();
	}

	@Override
	public List<PolicySrcEntity> getPolicies() {
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(PolicySrcEntity.class);
		return criteria.list();
	}

	@Override
	public void saveOTP(OTPEntity optEntity) {
		this.sessionFactory.getCurrentSession().save(optEntity);
	}

	@Override
	public void updateOTP(OTPEntity optEntity) {
		this.sessionFactory.getCurrentSession().update(optEntity);
		
	}

	@Override
	public OTPEntity getOTP(String OTP) {
		List<OTPEntity> otpEntities;
		OTPEntity otpEntity = null;
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(OTPEntity.class);
		criteria.add(Restrictions.eq("oneTimePwd", OTP));
		criteria.add(Restrictions.eq("activeFlag", true));
		//criteria.setFetchMode("address", FetchMode.JOIN);
		//criteria.setFetchMode("securityQA", FetchMode.JOIN);
		otpEntities = criteria.list();
		if(otpEntities.size() > 0) {
			otpEntity = otpEntities.get(0);
		}
		return otpEntity;
	}

	@Override
	public Set<UserRoleEntity> getUserRole(String userId) {
		List<UserEntity> userList;
		UserEntity userEntity = null;
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(UserEntity.class);
		criteria.add(Restrictions.eq("userId", userId));
		criteria.add(Restrictions.eq("activeFlag", true));
		criteria.setFetchMode("userRoleEntities", FetchMode.JOIN);
		//criteria.setFetchMode("securityQA", FetchMode.JOIN);
		userList = criteria.list();
		if(userList.size() > 0) {
			userEntity = userList.get(0);
			System.out.println("userEntity.getUserRoleEntities() "+ userEntity.getUserRoleEntities().size());
			return userEntity.getUserRoleEntities();
		}
		
		
		return null;
	}

	@Override
	public DemoUserEntity validateDemoUser(String userId, String password) {
		
		List<DemoUserEntity> userList;
		DemoUserEntity userEntity = null;
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(DemoUserEntity.class);
		criteria.add(Restrictions.ilike("userId", userId ));
		criteria.add(Restrictions.eq("password", password));
		//criteria.add(Restrictions.eq("activeFlag", true));
		//criteria.setFetchMode("address", FetchMode.JOIN);
		//criteria.setFetchMode("securityQA", FetchMode.JOIN);
		userList = criteria.list();
		if(userList.size() > 0) {
			userEntity = userList.get(0);
		}
		
		return userEntity;
		
	}

	@Override
	public boolean forcePWDChange() {
		this.sessionFactory.getCurrentSession().createSQLQuery("update USER_TB set pwd_change_flag=1").executeUpdate(); 
		return false;
	}

	@Override
	public void addTempUser(TempUserEntity tempUserEntity) {
		 this.sessionFactory.getCurrentSession().save(tempUserEntity);	
		
	}

	@Override
	public List<OnboardApprovalPendingEntity> fetchUserPendingRequest(Integer userId) {
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(OnboardApprovalPendingEntity.class);
		if(userId != null)
			criteria.add(Restrictions.eq("userEntity.id", userId));
		List<OnboardApprovalPendingEntity> onboardApprovalPendingEntities = criteria.list();
		return onboardApprovalPendingEntities;
	}

	@Override
	public TempUserEntity getTempUser(String userId) {
		TempUserEntity tempUserEntity = null;
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(TempUserEntity.class);
		criteria.add(Restrictions.eq("employeeId", userId));
		criteria.add(Restrictions.eq("pendingApproval", true));
		List<TempUserEntity> tempUserEntities = criteria.list();
		if(!tempUserEntities.isEmpty())
		tempUserEntity = (TempUserEntity) criteria.list().get(0);
		return tempUserEntity;
	}
	
	@Override
	public TempUserEntity getApprovedTempUser(String userId) {
		TempUserEntity tempUserEntity = null;
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(TempUserEntity.class);
		criteria.add(Restrictions.eq("employeeId", userId));
		List<TempUserEntity> tempUserEntities = criteria.list();
		if(!tempUserEntities.isEmpty())
		tempUserEntity = (TempUserEntity) criteria.list().get(0);
		return tempUserEntity;
	}

	@Override
	public void updateTempUser(TempUserEntity tempUserEntity) {
		try {
			this.sessionFactory.getCurrentSession().update(tempUserEntity);		
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}

	@Override
	public OnboardApprovalPendingEntity getOnboardApprovalPendingEntity(String employeeId, int userid, int approverId) {
		System.out.println("approverId "+approverId);
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(OnboardApprovalPendingEntity.class);
		if(employeeId != null)
			criteria.add(Restrictions.eq("tempUserEntity.employeeId", employeeId));
		else
			criteria.add(Restrictions.eq("requestUserEntity.id", userid));
		
		criteria.add(Restrictions.eq("userEntity.id", approverId));
		criteria.add(Restrictions.eq("pendingApproval", true));
		return (OnboardApprovalPendingEntity) criteria.list().get(0);
	}

	@Override
	public void updateOnboardApprovalPendingEntity(OnboardApprovalPendingEntity onboardApprovalPendingEntity) {
		this.sessionFactory.getCurrentSession().merge(onboardApprovalPendingEntity);
		this.sessionFactory.getCurrentSession().update(onboardApprovalPendingEntity);	
		
	}

	@Override
	public List<OnboardingUserNotesEntity> getOnboardingUserNotes(String id) {
		
		return null;
	}

	@Override
	public void saveOnboardingApprovalAudit(OnboardApprovalAuditEntity onboardApprovalAuditEntity) {
		this.sessionFactory.getCurrentSession().save(onboardApprovalAuditEntity);	
		
	}

	@Override
	public void saveTempUserRole(TempUserRoleEntity tempUserRoleEntity) {
		this.sessionFactory.getCurrentSession().save(tempUserRoleEntity);	
		
	}

	@Override
	public void saveOnboardApprovalPendingEntity(OnboardApprovalPendingEntity onboardApprovalPendingEntity) {
		this.sessionFactory.getCurrentSession().save(onboardApprovalPendingEntity);	
		
	}

	@Override
	public List<OnboardApprovalPendingEntity> getOnboardApprovalPendingEntity(String employeeId) {
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(OnboardApprovalPendingEntity.class);
		criteria.add(Restrictions.eq("tempUserEntity.employeeId", employeeId));
		criteria.add(Restrictions.eq("pendingApproval", true));
		return criteria.list();
	}

	@Override
	public UserRoleEntity getUserRoleOnUserRoleId(Integer userRoleId) {
		UserRoleEntity userRoleEntity = null;
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(UserRoleEntity.class);
		criteria.add(Restrictions.eq("userRoleId", userRoleId));
		List<UserRoleEntity>  userRoleEntities = criteria.list();
		if(userRoleEntities.size() >0)
			userRoleEntity = userRoleEntities.get(0);
		return userRoleEntity;
	}

	@Override
	public boolean checkPreviousPassword(String passowrd, int Id,int count) {
		// TODO Auto-generated method stub
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(PwdHistoryEntity.class);
		criteria.add(Restrictions.eq("userEntity.id", Id));
		criteria.add(Restrictions.eq("password", passowrd));
		criteria.addOrder(Order.desc("createdTs"));
		criteria.setMaxResults(count);
		List<PwdHistoryEntity>  pwdHistoryEntities = criteria.list();
		if(pwdHistoryEntities.isEmpty())
			return true;
		else
			return false;
	}

	@Override
	public void saveUserActivity(UserActivityEntity userActivityEntity) {
		this.sessionFactory.getCurrentSession().save(userActivityEntity);
		
	}

	@Override
	public List<OnboardApprovalAuditEntity> getApprovedRequests(String userId) {
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(OnboardApprovalAuditEntity.class);
		if(userId != null)
			criteria.add(Restrictions.eq("userTbId", userId));
		return criteria.list();
	}

	@Override
	public List<OnboardApprovalPendingEntity> fetchUserRequestedBy(String userId) {
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(OnboardApprovalPendingEntity.class,"pending");
		

		criteria.createAlias("pending.tempUserEntity", "tempUserEntity");
		if(userId != null)
			criteria.add(Restrictions.eq("tempUserEntity.createdBy", userId));
		List<OnboardApprovalPendingEntity> onboardApprovalPendingEntities = criteria.list();
		return onboardApprovalPendingEntities;
	}

	@Override
	public List<UserEntity> getUsers() {
		List<UserEntity> userList;
		UserEntity userEntity = null;
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(UserEntity.class);
		criteria.add(Restrictions.eq("activeFlag", true));
		//criteria.setFetchMode("address", FetchMode.JOIN);
		//criteria.setFetchMode("securityQA", FetchMode.JOIN);
		userList = criteria.list();
		return userList;
	}
	
	@Override
	public List<OnboardApprovalPendingEntity> fetchUserPendingRequestsForReport(Integer userId,Date beginDate, Date endDate) {
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(OnboardApprovalPendingEntity.class);
		if(userId != 0)
			criteria.add(Restrictions.eq("userEntity.id", userId));
		if(beginDate != null)
		{
			criteria.add(Restrictions.ge("requestDate", beginDate));
			criteria.add(Restrictions.le("requestDate", beginDate));
		}
		List<OnboardApprovalPendingEntity> onboardApprovalPendingEntities = criteria.list();
		return onboardApprovalPendingEntities;
	}

	@Override
	public List<OnboardApprovalAuditEntity> getApprovedRequestsForReport(int userId, Date beginDate, Date endDate) {
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(OnboardApprovalAuditEntity.class);
		if(userId != 0)
			criteria.add(Restrictions.eq("userTbId", getUser(userId).getUserId()));
		if(beginDate != null)
		{
			criteria.add(Restrictions.ge("aprovedOn", beginDate));
			criteria.add(Restrictions.le("aprovedOn", beginDate));
		}
		return criteria.list();
	}

	@Override
	public List<OnboardApprovalPendingEntity> fetchUserRequestedByForReport(int userId, Date beginDate, Date endDate) {
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(OnboardApprovalPendingEntity.class,"pending");
		criteria.createAlias("pending.tempUserEntity", "tempUserEntity");
		if(userId != 0)
			criteria.add(Restrictions.eq("tempUserEntity.createdBy",  getUser(userId).getUserId()));
		if(beginDate != null)
		{
			criteria.add(Restrictions.ge("requestDate", beginDate));
			criteria.add(Restrictions.le("requestDate", beginDate));
		}
		List<OnboardApprovalPendingEntity> onboardApprovalPendingEntities = criteria.list();
		return onboardApprovalPendingEntities;
	}

	@Override
	public List<TempUserEntity> getTempUsersCreatedBy(String userId) {
		TempUserEntity tempUserEntity = null;
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(TempUserEntity.class);
		criteria.add(Restrictions.eq("createdBy", userId));
		List<TempUserEntity> tempUserEntities = criteria.list();
		return tempUserEntities;
	}

}
