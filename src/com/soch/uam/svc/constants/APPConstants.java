package com.soch.uam.svc.constants;

public class APPConstants {
	
	public static String REG_EMAIL_SUB = "Account Activation Required";
	public static String REG_EMAIL_TEXT ="Welcome to the UAM portal. <br><h4 style=\"color:RED\">IMPORTANT: FURTHER ACTION IS REQUIRED TO ACTIVATE YOUR ACCOUNT!</h4> " +
			" <h4 >FOLLOW THE WEB ADDRESS BELOW TO ACTIVATE YOUR ACCOUNT:</h4>" +
			"<a href=\"url\" target=\"_blank\">url"+
			" </a> <br> <h5>If not clickable, please copy and paste the address to your browser.</h5><h4 style=\"color:RED\">DO NOT SHARE THIS LINK. IT WILL EXPIRE IN 24 HOURS.</h4> <br><br>";
	
	public static String REG_EMAIL_URL = "/UAMSvcUI/registerVerification.html?token=tokenParam";
	
	
	public static String FORGOT_USERID_SUB = "UserId retreived";
	
	public static String FORGOT_USERID_TEXT ="Greetings, <br><h4 style=\"color:RED\">The requested user id is userid</h4> " +
			" <p >This is an automated email. Please do not reply.</p>";
	
	
	public static String FORGOT_PWD_SUB = "Reset Password";
	
	public static String FORGOT_PWD_TEXT ="Greetings, <br><h4 style=\"color:RED\">Please reset the password <a href=\"url\" target=\"_blank\">Here"+
			" </a> </h4> " +
			" <p >This is an automated email. Please do not reply.</p>";
	
	public static String PWD_RESET_URL = "/UAMSvcUI/pwdReset.html";
	
	public static String OTP_EMAIL_SUB = "One Time Password";
	public static String OTP_EMAIL_TEXT ="Greetings, <br><h4 style=\"color:RED\">Please find requested One Time Passwor otpwd.</h4> <br>" +
			" <h4 >This token is valid for 30 mins.</h4>" +
			"This is an auto generated email. Please do not respond.";
	
	public static Integer IDENTITY_GRP_ID = 1;

}
