package com.postvue.feelogserver.app.recomm.dto.rsp;

import com.postvue.feelogserver.domain.snstagposts.dao.SnsRecommTagDao;

import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class SnsRecommTagDaoImpl implements SnsRecommTagDao {
	private String tagName;
	private Long tagId;
	private String tagRepsBatchContent;
	private String tagRepsBatchContentType;

	@Override
	public String getTagName() {
		return this.tagName;
	}

	@Override
	public Long getTagId() {
		return this.tagId;
	}

	@Override
	public String getTagRepsBatchContent() {
		return this.tagRepsBatchContent;
	}

	@Override
	public String getTagRepsBatchContentType() {
		return this.tagRepsBatchContentType;
	}
}
