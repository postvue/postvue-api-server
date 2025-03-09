package com.postvue.feelogserver.domain.adminserviceadjustments.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.postvue.feelogserver.domain.adminserviceadjustments.AdminServiceAdjustment;
import com.postvue.feelogserver.domain.snsusers.SnsUser;

@Repository
public interface AdminServiceAdjustmentRepository extends JpaRepository<AdminServiceAdjustment, Long>,
	JpaSpecificationExecutor<AdminServiceAdjustment> {

	List<AdminServiceAdjustment> findAllByServiceType(String serviceType);
}
