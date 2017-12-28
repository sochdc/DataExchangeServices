package com.soch.de.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AppUtil {
	
	public static boolean validateTime(int minutes, Date fromDate)
	{
		boolean returnValue = false;
		//System.out.println("fromDate "+fromDate+" Current Date"+new Date());
		long timeDiff = (new Date()).getTime() - fromDate.getTime();
		long timeDiffMins  = timeDiff / (60 * 1000) % 60;
		
		//System.out.println("timeDiffMins "+timeDiffMins+" "+minutes);
		
		if(timeDiffMins < minutes)
			returnValue = true;
		return returnValue;
				
	}
	
	public static long calculateDaysTillToday(Date inputDt)
	{
		Calendar cal1 = new GregorianCalendar();
	     Calendar cal2 = new GregorianCalendar();

	     SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

	     cal1.setTime(inputDt);
	     

	    //cal1.set(2008, 8, 1); 
	     //cal2.set(2008, 9, 31);
	     //System.out.println("Days= "+ (cal2.getTime().getTime() - cal1.getTime().getTime()) / (1000 * 60 * 60 * 24));
	     
	     long days = (cal2.getTime().getTime() - cal1.getTime().getTime()) / (1000 * 60 * 60 * 24);
	     return days;
	}
}
