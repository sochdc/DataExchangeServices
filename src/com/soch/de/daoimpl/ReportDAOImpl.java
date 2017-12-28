package com.soch.de.daoimpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.soch.de.dao.ReportDAO;
import com.soch.de.domain.LoginEntity;
import com.soch.de.domain.UserActivityEntity;

@Component
public class ReportDAOImpl implements ReportDAO {

	@Autowired
    private SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	@Override
	public List<LoginEntity> getActiveLogin() {
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(LoginEntity.class);
		criteria.add(Restrictions.eq("loginStatus",true));
		criteria.addOrder(Order.desc("loginTs"));
		return criteria.list();
	}

	@Override
	public List<UserActivityEntity> userActivityReport(int userId, String beginDate, String endDate) {
		 
		  SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		  Date startDate = null;
		  Date maxDate = null;
		    try {
				startDate = formatter.parse(beginDate);
				maxDate = formatter.parse(endDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		    maxDate = new Date(maxDate.getTime() + TimeUnit.DAYS.toMillis(1));
		   
		    Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(UserActivityEntity.class);
		    if(userId != 0)
		    	criteria.add(Restrictions.eq("userEntity.id",userId));
			criteria.add(Restrictions.ge("activityCreateTs",startDate));
			criteria.add(Restrictions.lt("activityCreateTs",maxDate));
		return criteria.list();
	}

	@Override
	public List<LoginEntity> getInactiveUsersForReport() {
		
		DetachedCriteria dates = DetachedCriteria.forClass(LoginEntity.class, "f")
			    .setProjection( Projections.projectionList()
		                .add(Projections.groupProperty("userEntity"))
		                .add(Projections.max("loginTs")));

	
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(LoginEntity.class);
		
		
		criteria.setProjection(Projections.projectionList()
                .add(Projections.groupProperty("userEntity"))
                .add(Projections.max("loginTs")));
		criteria.addOrder(Order.desc("loginTs"));
		
 
		return criteria.list();
	}
	
	
}
