package org.naho.point.usecase;

import org.naho.i18n.message.point.PointSummaryDetailMessageKey;
import org.naho.point.command.PointSummaryCommand;
import org.naho.point.exception.PointSummaryErrorCode;
import org.naho.point.mapper.PointSummaryResultMapper;
import org.naho.point.model.PointSummary;
import org.naho.point.port.in.CrudPointSummaryInputPort;
import org.naho.point.port.out.PointSummaryRepositoryPort;
import org.naho.point.result.PointSummaryResult;
import org.naho.shared.exception.ApplicationException;

public class CrudPointSummaryUseCase implements CrudPointSummaryInputPort {

    private final PointSummaryRepositoryPort pointSummaryRepositoryPort;
    private final PointSummaryResultMapper pointSummaryResultMapper;

    public CrudPointSummaryUseCase(
            PointSummaryRepositoryPort pointSummaryRepositoryPort,
            PointSummaryResultMapper pointSummaryResultMapper
    ) {
        this.pointSummaryRepositoryPort = pointSummaryRepositoryPort;
        this.pointSummaryResultMapper = pointSummaryResultMapper;
    }

    @Override
    public PointSummaryResult updateTotalPoint(PointSummaryCommand command) {
        PointSummary pointSummary = pointSummaryRepositoryPort.findById(command.id())
                .orElseThrow(() -> new ApplicationException(
                        PointSummaryErrorCode.POINT_SUMMARY_NOT_FOUND,
                        PointSummaryDetailMessageKey.POINT_SUMMARY_NOT_FOUND,
                        command.id()
                ));
        pointSummary.setTotalPoint(command.point());
        PointSummary saved = pointSummaryRepositoryPort.save(pointSummary);
        return pointSummaryResultMapper.domainToResult(saved);
    }

    @Override
    public PointSummaryResult addPoint(PointSummaryCommand command) {
        PointSummary pointSummary = pointSummaryRepositoryPort.findById(command.id())
                .orElseThrow(() -> new ApplicationException(
                        PointSummaryErrorCode.POINT_SUMMARY_NOT_FOUND,
                        PointSummaryDetailMessageKey.POINT_SUMMARY_NOT_FOUND,
                        command.id()
                ));
        pointSummary.addPoint(command.point());
        PointSummary saved = pointSummaryRepositoryPort.save(pointSummary);
        return pointSummaryResultMapper.domainToResult(saved);
    }

    @Override
    public PointSummaryResult findPointSummaryByUserId(Long userId) {
        if (userId == null) {
            throw new ApplicationException(
                    PointSummaryErrorCode.POINT_SUMMARY_USER_ID_INVALID,
                    PointSummaryDetailMessageKey.POINT_SUMMARY_USER_ID_BLANK
            );
        }

        PointSummary pointSummary = pointSummaryRepositoryPort.findByUserId(userId)
                .orElseThrow(() -> new ApplicationException(
                        PointSummaryErrorCode.POINT_SUMMARY_NOT_FOUND,
                        PointSummaryDetailMessageKey.POINT_SUMMARY_NOT_FOUND_BY_USER_ID,
                        userId
                ));
        
        return pointSummaryResultMapper.domainToResult(pointSummary);
    }
}
