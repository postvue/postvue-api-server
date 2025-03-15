package com.postvue.feelogserver.app.posts.dto.req.create.admin;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

public record AdminSnsPostComposeCreateListReq(List<AdminSnsPostComposeCreateReq> snsPostComposeList) {
	@JsonCreator
	public AdminSnsPostComposeCreateListReq(
		@JsonProperty("snsPostComposeList") List<AdminSnsPostComposeCreateReq> snsPostComposeList) {
		this.snsPostComposeList = snsPostComposeList;
	}
}
