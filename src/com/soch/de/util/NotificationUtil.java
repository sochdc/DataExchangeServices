package com.soch.de.util;

import javax.transaction.Transaction;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class NotificationUtil {
	private static SessionFactory factory; 
	private static Session session = null;
	public static void main(String[] args)
	{
		 try{
	         factory = new Configuration().configure().buildSessionFactory();
	      }catch (Throwable ex) { 
	         System.err.println("Failed to create sessionFactory object." + ex);
	         throw new ExceptionInInitializerError(ex); 
	      }
		 
		 session = factory.openSession();
	     org.hibernate.Transaction tx = null;
	     
	      try{
	         tx = session.beginTransaction();
	       
	         tx.commit();
	      }catch (HibernateException e) {
	         if (tx!=null) tx.rollback();
	         e.printStackTrace(); 
	      }finally {
	         session.close(); 
	      }
	}
	
	

}
