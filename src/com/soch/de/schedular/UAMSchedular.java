package com.soch.de.schedular;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.soch.de.dao.CommonDAO;
import com.soch.de.domain.OnboardApprovalPendingEntity;
import com.soch.de.domain.OnboardingApprovalEntity;

@Component
public class UAMSchedular {
	
	@Autowired
	private CommonDAO commonDAO ;
	
	public void sendPendingNotifications()
	{
		List<OnboardApprovalPendingEntity> onboardApprovalPendingEntities = commonDAO.getAllPendingRequests();
		int sla;
		Date requestDate;
		Set<OnboardingApprovalEntity> onboardingApprovalEntities;
		for(OnboardApprovalPendingEntity approvalPendingEntity : onboardApprovalPendingEntities)
		{
			onboardingApprovalEntities = approvalPendingEntity.getUserEntity().getOnboardingApprovalEntity();
			for(OnboardingApprovalEntity onboardingApprovalEntity : onboardingApprovalEntities)
			{
				if(onboardingApprovalEntity.getRolesEntity().getRoleId() == approvalPendingEntity.getRoleID())
				{
					sla = onboardingApprovalEntity.getSla();
					requestDate = approvalPendingEntity.getRequestDate();
					if(isSLAMissed(requestDate,sla))
					{
						System.out.println("SLA Missed");
					}
				}
			}
			
		}
	}
	
	private boolean isSLAMissed(Date reqDate, int sla)
	{
		Date currentDate = new Date();
		long diff =  (reqDate.getTime() - currentDate.getTime()) / (1000 * 60 * 60 * 24);
		System.out.println(diff);
		if(diff > sla)
			return true;
		return false;
		
	}

}
