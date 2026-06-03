package org.naho.furigana.usecase;

import org.naho.furigana.command.AnalyzeFuriganaCommand;
import org.naho.furigana.constant.FuriganaApplicationMessageKey;
import org.naho.furigana.exception.FuriganaApplicationErrorCode;
import org.naho.furigana.mapper.FuriganaResultMapper;
import org.naho.furigana.model.FuriganaText;
import org.naho.furigana.port.in.AnalyzeFuriganaInputPort;
import org.naho.furigana.port.out.FuriganaAnalysisPort;
import org.naho.furigana.result.FuriganaResult;
import org.naho.shared.exception.ApplicationException;

public class AnalyzeFuriganaUseCase implements AnalyzeFuriganaInputPort {

    private final FuriganaAnalysisPort furiganaAnalysisPort;
    private final FuriganaResultMapper furiganaResultMapper;

    public AnalyzeFuriganaUseCase(FuriganaAnalysisPort furiganaAnalysisPort, FuriganaResultMapper furiganaResultMapper) {
        this.furiganaAnalysisPort = furiganaAnalysisPort;
        this.furiganaResultMapper = furiganaResultMapper;
    }

    @Override
    public FuriganaResult analyze(AnalyzeFuriganaCommand command) {
        if (command == null || command.text() == null || command.text().trim().isEmpty()) {
            throw new ApplicationException(
                    FuriganaApplicationErrorCode.FURIGANA_ANALYZE_FAILED,
                    FuriganaApplicationMessageKey.FURIGANA_TEXT_EMPTY
            );
        }
        FuriganaText domainText = furiganaAnalysisPort.analyze(command.text());
        return furiganaResultMapper.domainToResult(domainText);
    }
}
