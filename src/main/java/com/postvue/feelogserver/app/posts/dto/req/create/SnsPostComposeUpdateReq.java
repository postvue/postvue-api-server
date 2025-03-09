package com.postvue.feelogserver.app.posts.dto.req.create;

import java.util.List;

import com.postvue.feelogserver.app.posts.dto.common.PostContent;
import com.postvue.feelogserver.global.constant.PostConst;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SnsPostComposeUpdateReq extends SnsPostComposeCreateReq{
	private List<String> existPostContentList;
}
