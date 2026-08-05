package org.naho.social.report.port.out;

import org.naho.social.report.model.Report;
import org.naho.social.report.type.ReportType;

import java.util.List;
import java.util.Optional;

public interface ReportRepositoryPort {
    Optional<Report> findById(Long id);

    List<Report> findByReportTypeIn(List<ReportType> reportTypes);

    Report save(Report report);

    List<Report> findByUserId(Long userId);
}
