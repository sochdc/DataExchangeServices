package com.soch.uam.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.soch.uam.domain.SecurityQAEntity;
import com.soch.uam.domain.UserEntity;
import com.soch.uam.dto.SecurityQADTO;
import com.soch.uam.dto.UserDTO;

public class CopyBeanProperties {
	
	public static UserDTO copyUserPerperties(UserEntity userEntity)
	{
		UserDTO userDTO = new UserDTO();
		userDTO.setFirstName(userEntity.getFirstName());
		userDTO.setId(userEntity.getId());
		userDTO.setLastName(userEntity.getLastName());
		userDTO.setEmailId(userEntity.getEmailId());
		if(userEntity.getDateOfBirth() != null)
		userDTO.setDateOfBirth(convertFormat(userEntity.getDateOfBirth()));
		userDTO.setUserId(userEntity.getUserId());
		userDTO.setActiveFlag(userEntity.getActiveFlag());
		//if(userEntity.getSSN() != null)
		//userDTO.setSSN(truncateSSN(userEntity.getSSN()));
		userDTO.setPassword(userEntity.getPassowrd());
		userDTO.setLockFlag(userEntity.getLockFlag());
		if(!userEntity.getSecurityQA().isEmpty())
			userDTO.setSecurityQA(copySecurityQAProperties(userEntity.getSecurityQA()));
		return userDTO; 
	}
	
	private static Set<SecurityQADTO>  copySecurityQAProperties(Set<SecurityQAEntity> securityQAEntities)
	{
		Set<SecurityQADTO> securityQADTOs = new HashSet<SecurityQADTO>();
		
		SecurityQADTO securityQADTO = null;
		
		for(SecurityQAEntity securityQAEntity : securityQAEntities)
		{
			securityQADTO = new SecurityQADTO();
			securityQADTO.setAnswer(securityQAEntity.getAnswer());
			
			securityQADTO.setQuestion(securityQAEntity.getQuestion());
			securityQADTOs.add(securityQADTO);
		}
		
		return securityQADTOs;
	}
	
	private static String convertFormat(Date date)
	{
		SimpleDateFormat sf = new SimpleDateFormat("MM/dd/yyyy");
		return sf.format(date);
	}
	
	private static String truncateSSN(String SSN)
	{
		System.out.println(SSN.split("-")[2]);
		return SSN.split("-")[2] ;
	}

}
