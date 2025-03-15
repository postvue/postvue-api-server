package com.postvue.feelogserver.endpoint.common;

import org.springframework.stereotype.Service;

import com.postvue.feelogserver.core.security.CustomUserDetails;
import com.postvue.feelogserver.core.security.JwtTokenProvider;
import com.postvue.feelogserver.domain.snsusers.SnsUser;
import com.postvue.feelogserver.domain.snsusers.repository.SnsUserRepository;
import com.postvue.feelogserver.domain.snsusers.vo.SnsAppRole;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthTokenValidProcess {
	private final JwtTokenProvider jwtTokenProvider;
	private final SnsUserRepository snsUserRepository;
	public SnsUser processValidToken (String token){
		if (token == null || token.isEmpty()) {
			throw new BadRequestErrorException("권한이 없습니다.");
		}

		jwtTokenProvider.validateToken(token);
		CustomUserDetails userDetails = jwtTokenProvider.getUserDetails(token);
		Long snsUserId = (userDetails == null) ? null : Long.valueOf(userDetails.getUserId());

		if (snsUserId == null){
			throw new BadRequestErrorException("해당 계정은 없습니다.");
		}

		SnsUser snsUser =  snsUserRepository.findById(snsUserId).orElseThrow(
			() -> new BadRequestErrorException("해당 계정은 없습니다.")
		);

		if (snsUser.getSnsAppRole() != SnsAppRole.ROLE_ADMIN){
			throw new BadRequestErrorException("접근 권한이 없습니다.");
		}

		return snsUser;
	}
}
