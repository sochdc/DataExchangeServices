package com.soch.uam.util;

import java.util.Date;

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

}
