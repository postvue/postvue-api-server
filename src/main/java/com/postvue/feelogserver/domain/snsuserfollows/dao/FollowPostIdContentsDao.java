package com.postvue.feelogserver.domain.snsuserfollows.dao;

import java.util.List;

import com.postvue.feelogserver.domain.snsposts.dto.SnsPostContentDao;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowPostIdContentsDao {
	private Long postId;
	private List<SnsPostContentDao> postContents;
}
