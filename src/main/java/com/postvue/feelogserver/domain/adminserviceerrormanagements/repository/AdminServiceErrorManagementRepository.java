package com.postvue.feelogserver.domain.adminserviceerrormanagements.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.postvue.feelogserver.domain.adminserviceadjustments.AdminServiceAdjustment;
import com.postvue.feelogserver.domain.adminserviceerrormanagements.AdminServiceErrorManagement;

@Repository
public interface AdminServiceErrorManagementRepository extends JpaRepository<AdminServiceErrorManagement, Long>,
	JpaSpecificationExecutor<AdminServiceErrorManagement> {
}
