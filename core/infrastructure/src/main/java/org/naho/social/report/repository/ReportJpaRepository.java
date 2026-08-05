package org.naho.social.report.repository;

import org.naho.social.report.entity.ReportEntity;
import org.naho.social.report.type.ReportType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportJpaRepository extends JpaRepository<ReportEntity, Long> {
    @Override
    @EntityGraph(attributePaths = {"files"})
    Optional<ReportEntity> findById(Long id);

    @EntityGraph(attributePaths = {"files"})
    List<ReportEntity> findByReportTypeIn(List<ReportType> reportTypes);

    @EntityGraph(attributePaths = {"files"})
    List<ReportEntity> findByUserId(Long userId);
}

