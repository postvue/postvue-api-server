package com.postvue.feelogserver.global.common.tables.mixin;

import java.time.LocalDateTime;

public interface TimeMixins {

	LocalDateTime getCreatedAt();

	void setCreatedAt(LocalDateTime createdAt);

	LocalDateTime getLastUpdatedAt();

	void setLastUpdatedAt(LocalDateTime lastUpdatedAt);

	Long getLastUpdatedByid();

	void setLastUpdatedByid(Long lastUpdatedBy);
}
