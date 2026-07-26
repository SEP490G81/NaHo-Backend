package org.naho.social.report.port.out;

import org.naho.social.model.Report;
import org.naho.social.reaction.type.ReportType;

import java.util.List;
import java.util.Optional;

public interface ReportRepositoryPort {
    Optional<Report> findById(Long id);

    List<Report> findByReportTypeIn(List<ReportType> reportTypes);
}
