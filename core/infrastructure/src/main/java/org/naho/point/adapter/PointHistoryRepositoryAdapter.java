package org.naho.point.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.learning.repository.LearningPathNodeJpaRepository;
import org.naho.pagination.PageData;
import org.naho.pagination.PageMeta;
import org.naho.point.command.PointHistoryQueryCommand;
import org.naho.point.entity.PointHistoryEntity;
import org.naho.point.mapper.PointHistoryEntityMapper;
import org.naho.point.model.PointHistory;
import org.naho.point.port.out.PointHistoryRepositoryPort;
import org.naho.point.repository.PointHistoryJpaRepository;
import org.naho.point.specification.PointHistorySpecification;
import org.naho.shared.exception.ApplicationException;
import org.naho.user.entity.UserEntity;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.repository.UserJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointHistoryRepositoryAdapter implements PointHistoryRepositoryPort {
    private final PointHistoryEntityMapper pointHistoryEntityMapper;
    private final PointHistoryJpaRepository pointHistoryJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final LearningPathNodeJpaRepository learningPathNodeJpaRepository;

    @Override
    public PointHistory save(PointHistory pointHistory) {
        PointHistoryEntity pointHistoryEntity =
                pointHistoryEntityMapper.domainToEntity(pointHistory);

        UserEntity user = userJpaRepository.findById(pointHistory.getUserId())
                .orElseThrow(() -> new ApplicationException(
                        UserErrorCode.USER_NOT_FOUND,
                        UserDetailMessageKey.USER_ID_NOT_FOUND,
                        pointHistory.getUserId()
                ));

        pointHistoryEntity.setUser(user);

        if (pointHistory.getLearningPathNodeId() != null) {
            pointHistoryEntity.setLearningPathNode(
                    learningPathNodeJpaRepository.getReferenceById(
                            pointHistory.getLearningPathNodeId()
                    )
            );
        }

        PointHistoryEntity savedPointHistory =
                pointHistoryJpaRepository.save(pointHistoryEntity);

        return pointHistoryEntityMapper.entityToDomain(savedPointHistory);
    }

    @Override
    public PageData<PointHistory> findAllByUserId(PointHistoryQueryCommand command, Long userId) {
        Pageable pageable = PageRequest.of(
                command.page(),
                command.size(),
                Sort.by(
                        Sort.Direction.valueOf(command.sortDirection().name()),
                        command.sortColumn().getColumnName()
                )
        );

        Specification<PointHistoryEntity> specification =
                Specification.allOf(
                        PointHistorySpecification.hasUserId(userId),
                        PointHistorySpecification.hasTransactionType(command.transactionType()),
                        PointHistorySpecification.hasAmountType(command.amountType()),
                        PointHistorySpecification.transactionTimeBetween(
                                command.transactionTimeFrom(),
                                command.transactionTimeTo()
                        )
                );

        Page<PointHistoryEntity> page = pointHistoryJpaRepository.findAll(specification, pageable);

        return PageData.<PointHistory>builder()
                .pageMeta(PageMeta.builder()
                        .currentPage(page.getNumber())
                        .pageSize(page.getSize())
                        .totalPages(page.getTotalPages())
                        .totalElements(page.getTotalElements())
                        .hasNext(page.hasNext())
                        .hasPrevious(page.hasPrevious())
                        .build()
                )
                .data(page.getContent()
                        .stream()
                        .map(pointHistoryEntityMapper::entityToDomain)
                        .toList()
                )
                .build();
    }
}
