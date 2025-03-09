package com.postvue.feelogserver.domain.snsuserreport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.postvue.feelogserver.domain.snsuserreport.SnsUserReport;

@Repository
public interface SnsUserReportRepository extends JpaRepository<SnsUserReport,Long>, JpaSpecificationExecutor<SnsUserReport> {
}
