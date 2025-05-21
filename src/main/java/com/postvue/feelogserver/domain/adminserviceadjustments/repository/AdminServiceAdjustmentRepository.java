package com.postvue.feelogserver.domain.adminserviceadjustments.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.postvue.feelogserver.domain.adminserviceadjustments.AdminServiceAdjustment;
import com.postvue.feelogserver.domain.snsusers.SnsUser;

@Repository
public interface AdminServiceAdjustmentRepository extends JpaRepository<AdminServiceAdjustment, Long>,
	JpaSpecificationExecutor<AdminServiceAdjustment> {

	List<AdminServiceAdjustment> findAllByServiceType(String serviceType);

	Optional<AdminServiceAdjustment> findAllByServiceTypeAndPropLong1id(String serviceType, Long shortId);

	List<AdminServiceAdjustment> findAllByServiceTypeOrderByCreatedAtDesc(String serviceType, Pageable pageable);
}
