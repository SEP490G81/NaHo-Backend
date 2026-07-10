package org.naho.point.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.point.entity.PointSummaryEntity;
import org.naho.point.mapper.PointSummaryEntityMapper;
import org.naho.point.model.PointSummary;
import org.naho.point.port.out.PointSummaryRepositoryPort;
import org.naho.point.repository.PointSummaryJpaRepository;
import org.naho.shared.exception.ApplicationException;
import org.naho.user.entity.UserEntity;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PointSummaryRepositoryAdapter implements PointSummaryRepositoryPort {
    private final PointSummaryJpaRepository pointSummaryJpaRepository;
    private final PointSummaryEntityMapper pointSummaryEntityMapper;
    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<PointSummary> findById(Long id) {
        return pointSummaryJpaRepository.findById(id)
                .map(pointSummaryEntityMapper::entityToDomain);
    }

    @Override
    public PointSummary save(PointSummary pointSummary) {
        PointSummaryEntity entity = pointSummaryEntityMapper.domainToEntity(pointSummary);

        UserEntity user = userJpaRepository.findById(pointSummary.getUserId())
                .orElseThrow(() -> new ApplicationException(
                        UserErrorCode.USER_NOT_FOUND,
                        UserDetailMessageKey.USER_ID_NOT_FOUND,
                        pointSummary.getUserId()
                ));

        entity.setUser(user);
        PointSummaryEntity savedEntity = pointSummaryJpaRepository.save(entity);
        
        return pointSummaryEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public Optional<PointSummary> findByUserId(Long userId) {
        return pointSummaryJpaRepository.findByUser_Id(userId)
                .map(pointSummaryEntityMapper::entityToDomain);
    }
}
