package org.naho.point.usecase;

import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.pagination.PageData;
import org.naho.point.command.PointHistoryCommand;
import org.naho.point.command.PointHistoryQueryCommand;
import org.naho.point.mapper.PointHistoryCommandMapper;
import org.naho.point.mapper.PointHistoryResultMapper;
import org.naho.point.model.PointHistory;
import org.naho.point.port.in.CrudPointHistoryInputPort;
import org.naho.point.port.out.PointHistoryRepositoryPort;
import org.naho.point.result.PointHistoryResult;
import org.naho.shared.port.out.TransactionPort;

public class CrudPointHistoryUseCase implements CrudPointHistoryInputPort {
    private final PointHistoryCommandMapper pointHistoryCommandMapper;
    private final PointHistoryResultMapper pointHistoryResultMapper;
    private final PointHistoryRepositoryPort pointHistoryRepositoryPort;
    private final UserLearningProgressRepositoryPort userLearningProgressRepositoryPort;
    private final TransactionPort transactionPort;

    public CrudPointHistoryUseCase(
            PointHistoryCommandMapper pointHistoryCommandMapper,
            PointHistoryResultMapper pointHistoryResultMapper,
            PointHistoryRepositoryPort pointHistoryRepositoryPort,
            TransactionPort transactionPort,
            UserLearningProgressRepositoryPort userLearningProgressRepositoryPort
    ) {
        this.pointHistoryCommandMapper = pointHistoryCommandMapper;
        this.pointHistoryResultMapper = pointHistoryResultMapper;
        this.pointHistoryRepositoryPort = pointHistoryRepositoryPort;
        this.transactionPort = transactionPort;
        this.userLearningProgressRepositoryPort = userLearningProgressRepositoryPort;
    }

    @Override
    public PointHistoryResult createPointHistory(PointHistoryCommand command) {
        PointHistory pointHistory = pointHistoryCommandMapper.commandToDomain(command);
        PointHistory savedPointHistory = pointHistoryRepositoryPort.save(pointHistory);
        return pointHistoryResultMapper.domainToResult(savedPointHistory);
    }

    @Override
    public PageData<PointHistoryResult> findAllByUserId(PointHistoryQueryCommand command, Long userId) {
        PageData<PointHistory> pageData = pointHistoryRepositoryPort.findAllByUserId(command, userId);

        return PageData.<PointHistoryResult>builder()
                .pageMeta(pageData.getPageMeta())
                .data(pageData.getData()
                        .stream()
                        .map(pointHistoryResultMapper::domainToResult)
                        .toList()
                )
                .build();
    }
}
