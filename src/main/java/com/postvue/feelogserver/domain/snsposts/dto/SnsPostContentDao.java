package com.postvue.feelogserver.domain.snsposts.dto;

import com.postvue.feelogserver.domain.snsposts.vo.PostContentType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SnsPostContentDao {
	private PostContentType postContentType;
	private Integer ascSortNum;
	private String content;
	private Boolean isLink;
	private String previewImg;
	private String fileType;
	private String bucketUrl;
	private Boolean isUploaded;
}
