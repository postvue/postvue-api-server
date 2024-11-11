package com.postvue.feelogserver.core.config;

import org.springframework.stereotype.Component;

import com.postvue.feelogserver.global.constant.IdConfigConst;

@Component
public class SnowflakeComponent extends Snowflake {

	public SnowflakeComponent() {
		super(IdConfigConst.workerId, IdConfigConst.datacenterId);
	}
}
