package org.naho.social.report.repository;

import org.naho.social.reaction.type.ReportType;
import org.naho.social.report.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportJpaRepository extends JpaRepository<ReportEntity, Long> {
    List<ReportEntity> findByReportTypeIn(List<ReportType> reportTypes);
}
