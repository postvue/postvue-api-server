package com.postvue.feelogserver.core.config;

import java.lang.reflect.Member;
import java.util.EnumSet;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;
import org.hibernate.generator.EventTypeSets;
import org.hibernate.id.factory.spi.CustomIdGeneratorCreationContext;

import com.postvue.feelogserver.global.constant.IdConfigConst;

public class SnowflakeIdGenerator extends Snowflake implements BeforeExecutionGenerator {
	public SnowflakeIdGenerator(SnowflakeId snowflakeId, Member idMember,
		CustomIdGeneratorCreationContext creationContext) {
		super(IdConfigConst.workerId, IdConfigConst.datacenterId);

		if (this.getWorkerId() > this.getMaxWorkerId() || this.getWorkerId() < 0) {
			throw new IllegalArgumentException(
				String.format("worker Id can't be greater than %d or less than 0", this.getMaxWorkerId()));
		}
		if (this.getDatacenterId() > this.getMaxDatacenterId() || this.getDatacenterId() < 0) {
			throw new IllegalArgumentException(
				String.format("datacenter Id can't be greater than %d or less than 0", this.getMaxDatacenterId()));
		}

	}

	@Override
	public Object generate(SharedSessionContractImplementor session, Object owner, Object currentValue,
		EventType eventType) {
		return nextId();
	}

	@Override
	public EnumSet<EventType> getEventTypes() {
		return EventTypeSets.INSERT_ONLY;
	}
}
