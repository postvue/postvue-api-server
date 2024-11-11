package com.postvue.feelogserver.app.session.dto.pub;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppWebsocketInitPub {
	private String msgType;
	private String msgContent;
}
