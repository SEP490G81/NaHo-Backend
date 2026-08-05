package org.naho.social.report.adapter;


import org.naho.social.report.entity.ReportEntity;
import org.naho.social.report.mapper.ReportEntityMapper;
import org.naho.social.report.model.Report;
import org.naho.social.report.port.out.ReportRepositoryPort;
import org.naho.social.report.repository.ReportJpaRepository;
import org.naho.social.report.type.ReportType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ReportRepositoryAdapter implements ReportRepositoryPort {

    private final ReportJpaRepository reportJpaRepository;
    private final ReportEntityMapper reportEntityMapper;

    public ReportRepositoryAdapter(ReportJpaRepository reportJpaRepository,
                                   ReportEntityMapper reportEntityMapper) {
        this.reportJpaRepository = reportJpaRepository;
        this.reportEntityMapper = reportEntityMapper;
    }

    @Override
    public Optional<Report> findById(Long id) {
        return reportJpaRepository.findById(id)
                .map(reportEntityMapper::entityToDomain);
    }

    @Override
    public List<Report> findByReportTypeIn(List<ReportType> reportTypes) {
        List<ReportEntity> entities = reportJpaRepository.findByReportTypeIn(reportTypes);
        return entities.stream()
                .map(reportEntityMapper::entityToDomain)
                .toList();
    }

    @Override
    public Report save(Report report) {
        ReportEntity entity = reportEntityMapper.domainToEntity(report);
        ReportEntity savedEntity = reportJpaRepository.save(entity);
        return reportEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public List<Report> findByUserId(Long userId) {
        if (userId == null) {
            return List.of();
        }
        List<ReportEntity> entities = reportJpaRepository.findByUserId(userId);
        return entities.stream()
                .map(reportEntityMapper::entityToDomain)
                .toList();
    }
}
