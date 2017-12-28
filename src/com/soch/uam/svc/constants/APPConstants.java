package com.soch.uam.svc.constants;

public class APPConstants {
	
	public static String REG_EMAIL_SUB = "Account Activation Required";
	public static String REG_EMAIL_TEXT ="Welcome to the IAM portal. <br><h4 style=\"color:RED\">IMPORTANT: FURTHER ACTION IS REQUIRED TO ACTIVATE YOUR ACCOUNT!</h4> " +
			" <h4 >FOLLOW THE WEB ADDRESS BELOW TO ACTIVATE YOUR ACCOUNT:</h4>" +
			"<a href=\"url\" target=\"_blank\">url"+
			" </a> <br> <h5>If not clickable, please copy and paste the address to your browser.</h5><h4 style=\"color:RED\">DO NOT SHARE THIS LINK. IT WILL EXPIRE IN 24 HOURS.</h4> <br><br>";
	
	public static String REG_EMAIL_URL = "/Internal_UI/registerVerification.html?token=tokenParam";
	
	
	public static String FORGOT_USERID_SUB = "UserId retreived";
	
	public static String FORGOT_USERID_TEXT ="Greetings, <br><h4 style=\"color:RED\">The requested user id is userid</h4> " +
			" <p >This is an automated email. Please do not reply.</p>";
	
	
	public static String FORGOT_PWD_SUB = "Reset Password";
	
	public static String FORGOT_PWD_TEXT ="Greetings, <br><h4 style=\"color:RED\">Please reset the password <a href=\"url\" target=\"_blank\">Here"+
			" </a> </h4> " +
			" <p >This is an automated email. Please do not reply.</p>";
	
	public static String PWD_RESET_URL = "/Internal_UI/pwdReset.html";
	
	public static String OTP_EMAIL_SUB = "One Time Password";
	public static String OTP_EMAIL_TEXT ="Greetings, <br><h4 style=\"color:RED\">Please find requested One Time Passwor otpwd.</h4> <br>" +
			" <h4 >This token is valid for 30 Hour(s).</h4>" +
			"This is an auto generated email. Please do not respond.";
	
	public static String TEMP_PWD_EMAIL_TEXT ="Greetings, <br><h4 style=\"color:RED\">Please login temporory password to reset password otpwd.</h4> <br>" +
			"This is an auto generated email. Please do not respond.";
	
	public static Integer IDENTITY_GRP_ID = 1;
	public static Integer ACCESS_GRP_ID = 2;
	
	public static String REQ_APPROVE_EMAIL_SUB = "Request for Approval";
	public static String REQ_APPROVE_MAIL_TEXT ="Dear apprFname apprLastName <br><h4 style=\"color:RED\">The following user request is pending for approval.</h4> " +
			"<h4>tempFirstName tempLastName</h4> <br><br>";
	
	public static String REQ_REJECT_EMAIL_SUB = "User onboard Rejected";
	public static String REQ_REJECT_MAIL_TEXT ="Dear apprFname apprLastName <br><h4 style=\"color:RED\">The following user request is rejected for approval.</h4> " +
			"<h4>tempFirstName tempLastName</h4> <br><br>";
	
	
	public static String SYSTEM_STR = "SYSTEM";
	
	public static String ONBOARD_EMAIL_SUB = "Welcome Onboard";
	public static String ONBOARD_EMAIL_TEXT ="Welcome to Orgname. <br><h4 style=\"color:RED\">IMPORTANT: Please complete the registration process.</h4> " +
			" <h4 >FOLLOW THE WEB ADDRESS BELOW TO COMPLETE YOUR REGISTRATION:</h4>" +
			"<a href=\"url\" target=\"_blank\">url"+
			" </a> ";
	
	
	public static String ROLE_CHANGE_REQ_EMAIL_SUB = "Request for Role Change Approval";
	public static String  ROLE_CHANGE_REQ_MAIL_TEXT ="Dear apprFname apprLastName <br><h4 style=\"color:RED\">The following user request is pending for approval.</h4> " +
			"<h4>reqFirstName reqLastName</h4> <br><br>";
	
	public static String SOURCE_INTERNAL ="INTERNAL";
	public static String SOURCE_EXTERNAL ="EXTERNAL";
	
	public static String SR_AR_SPECIALIST = "Accounts Payable Specialist Sr";
	
	public static String SR_AP_SPECIALIST = "Accounts Receiveable Specialist Sr";
	
	public static String LOGIN_SUCCESS_ACTIVITY = "LOGIN SUCCESS";
	
	public static String LOGIN_FAIL_ACTIVITY = "LOGIN FAILED";
	
	public static String PASSWORD_RESET_ACTIVITY = "RESET PASSWORD";
	
	public static String FORGOT_USER_ACTIVITY = "FORGOT USER";
	
	public static String FORGOT_USER_PASSWORD = "FORGOT USER PASSWORD";
}
