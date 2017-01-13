package com.soch.uam.util;

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
		userDTO.setLastName(userEntity.getLastName());
		userDTO.setUserId(userEntity.getUserId());
		userDTO.setActiveFlag(userEntity.getActiveFlag());
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

}
