package com.postvue.feelogserver.endpoint.dto;

import com.postvue.feelogserver.domain.snstagposts.SnsTagPost;

public record SnsTagPostEndpointDto(
	String id,
	String snsTag_id,
	String snsTag_tagName,
	String snsPost_id
) {

	public static SnsTagPostEndpointDto fromEntity(SnsTagPost snsTagPost){
		return new SnsTagPostEndpointDto(
			snsTagPost.getId().toString(),
			snsTagPost.getSnsTag().getId().toString(),
			snsTagPost.getSnsTag().getTagName(),
			snsTagPost.getSnsPost().getId().toString()
		);
	}
}
