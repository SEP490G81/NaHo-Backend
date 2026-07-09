package org.naho.point.usecase;

import org.naho.i18n.message.point.PointSummaryDetailMessageKey;
import org.naho.point.command.PointHistoryCommand;
import org.naho.point.command.PointHistoryQueryCommand;
import org.naho.point.exception.PointSummaryErrorCode;
import org.naho.point.mapper.PointHistoryCommandMapper;
import org.naho.point.mapper.PointHistoryResultMapper;
import org.naho.point.model.PointHistory;
import org.naho.point.model.PointSummary;
import org.naho.point.port.in.CrudPointHistoryInputPort;
import org.naho.point.port.out.PointHistoryRepositoryPort;
import org.naho.point.port.out.PointSummaryRepositoryPort;
import org.naho.point.result.PointHistoryResult;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;

import java.time.Instant;
import java.util.List;

public class CrudPointHistoryUseCase implements CrudPointHistoryInputPort {
    private final PointHistoryCommandMapper pointHistoryCommandMapper;
    private final PointHistoryResultMapper pointHistoryResultMapper;
    private final PointHistoryRepositoryPort pointHistoryRepositoryPort;
    private final PointSummaryRepositoryPort pointSummaryRepositoryPort;
    private final TransactionPort transactionPort;

    public CrudPointHistoryUseCase(
            PointHistoryCommandMapper pointHistoryCommandMapper,
            PointHistoryResultMapper pointHistoryResultMapper,
            PointHistoryRepositoryPort pointHistoryRepositoryPort,
            TransactionPort transactionPort,
            PointSummaryRepositoryPort pointSummaryRepositoryPort
    ) {
        this.pointHistoryCommandMapper = pointHistoryCommandMapper;
        this.pointHistoryResultMapper = pointHistoryResultMapper;
        this.pointHistoryRepositoryPort = pointHistoryRepositoryPort;
        this.transactionPort = transactionPort;
        this.pointSummaryRepositoryPort = pointSummaryRepositoryPort;
    }

    @Override
    public PointHistoryResult createPointHistory(PointHistoryCommand command) {
        return transactionPort.execute(() -> doCreatePointHistory(command));
    }

    @Override
    public List<PointHistoryResult> findAllByUserId(PointHistoryQueryCommand command, Long userId) {
        return pointHistoryRepositoryPort.findAllByUserId(command, userId)
                .stream()
                .map(pointHistoryResultMapper::domainToResult)
                .toList();
    }

    private PointHistoryResult doCreatePointHistory(PointHistoryCommand command) {
        Instant now = Instant.now();

        // update point of history to point summary
        PointSummary pointSummary = pointSummaryRepositoryPort.findByUserId(command.userId())
                .orElseThrow(() -> new ApplicationException(
                        PointSummaryErrorCode.POINT_SUMMARY_NOT_FOUND,
                        PointSummaryDetailMessageKey.POINT_SUMMARY_NOT_FOUND_BY_USER_ID,
                        command.userId()
                ));

        pointSummary.addPoint(command.point());

        pointSummaryRepositoryPort.save(pointSummary);

        // create point history
        PointHistory pointHistory = pointHistoryCommandMapper.commandToDomain(command, now);

        PointHistory savedPointHistory = pointHistoryRepositoryPort.save(pointHistory);

        return pointHistoryResultMapper.domainToResult(savedPointHistory);
    }
}
