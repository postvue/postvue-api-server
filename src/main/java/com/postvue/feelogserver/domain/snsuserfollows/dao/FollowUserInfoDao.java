package com.postvue.feelogserver.domain.snsuserfollows.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class FollowUserInfoDao {
	private String followUserId;
	private String username;
	private String profilePath;
}
