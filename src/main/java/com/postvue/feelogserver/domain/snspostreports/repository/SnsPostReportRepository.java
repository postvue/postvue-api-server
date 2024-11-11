package com.postvue.feelogserver.domain.snspostreports.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.postvue.feelogserver.domain.snspostreports.SnsPostReport;

@Repository
public interface SnsPostReportRepository extends JpaRepository<SnsPostReport,Long>, JpaSpecificationExecutor<SnsPostReport> {
}
