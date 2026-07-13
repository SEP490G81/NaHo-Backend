package org.naho.point.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.point.PointSummaryDetailMessageKey;
import org.naho.point.entity.PointSummaryEntity;
import org.naho.point.exception.PointSummaryErrorCode;
import org.naho.point.mapper.PointSummaryEntityMapper;
import org.naho.point.model.PointSummary;
import org.naho.point.port.out.PointSummaryRepositoryPort;
import org.naho.point.repository.PointSummaryJpaRepository;
import org.naho.shared.exception.ApplicationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PointSummaryRepositoryAdapter implements PointSummaryRepositoryPort {
    private final PointSummaryJpaRepository pointSummaryJpaRepository;
    private final PointSummaryEntityMapper pointSummaryEntityMapper;

    @Override
    public Optional<PointSummary> findById(Long id) {
        return pointSummaryJpaRepository.findById(id)
                .map(pointSummaryEntityMapper::entityToDomain);
    }

    @Override
    public PointSummary save(PointSummary pointSummary) {
        PointSummaryEntity entity;
        if (pointSummary.getId() != null) {
            entity = pointSummaryJpaRepository.findById(pointSummary.getId())
                    .orElseThrow(() -> new ApplicationException(
                            PointSummaryErrorCode.POINT_SUMMARY_NOT_FOUND,
                            PointSummaryDetailMessageKey.POINT_SUMMARY_NOT_FOUND,
                            pointSummary.getId()
                    ));
            entity.setTotalPoint(pointSummary.getTotalPoint());
        } else {
            entity = pointSummaryEntityMapper.domainToEntity(pointSummary);
        }
        PointSummaryEntity savedEntity = pointSummaryJpaRepository.save(entity);

        return pointSummaryEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public Optional<PointSummary> findByUserId(Long userId) {
        return pointSummaryJpaRepository.findByUser_Id(userId)
                .map(pointSummaryEntityMapper::entityToDomain);
    }
}
