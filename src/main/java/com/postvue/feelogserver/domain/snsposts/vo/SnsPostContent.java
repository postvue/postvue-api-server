package com.postvue.feelogserver.domain.snsposts.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@AllArgsConstructor
public class SnsPostContent {
	private PostContentType postContentType;
	private Integer ascSortNum;
	private String content;
	private Boolean isLink;
	private String previewImg;
	private String fileType;
	private String bucketUrl;
	private Boolean isUploaded;

	public void setIsUploaded(Boolean isUploaded) {
		this.isUploaded = isUploaded;
	}
}
