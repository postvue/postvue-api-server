package com.postvue.feelogserver.app.posts.dto.rsp.create;

import com.postvue.feelogserver.app.posts.dto.rsp.get.SnsPostRsp;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SnsPostCreateRsp {
	private Boolean isReposted;
	private SnsPostRsp snsPost;
}
