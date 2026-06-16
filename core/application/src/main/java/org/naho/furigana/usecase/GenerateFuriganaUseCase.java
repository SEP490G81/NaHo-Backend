package org.naho.furigana.usecase;

import org.naho.furigana.command.GenerateFuriganaCommand;
import org.naho.furigana.exception.FuriganaErrorCode;
import org.naho.furigana.mapper.FuriganaResultMapper;
import org.naho.furigana.model.FuriganaText;
import org.naho.furigana.port.in.GenerateFuriganaInputPort;
import org.naho.furigana.port.out.FuriganaGenerationPort;
import org.naho.furigana.result.FuriganaResult;
import org.naho.i18n.message.furigana.FuriganaDetailMessageKey;
import org.naho.shared.exception.ApplicationException;

public class GenerateFuriganaUseCase implements GenerateFuriganaInputPort {

    private final FuriganaGenerationPort furiganaGenerationPort;
    private final FuriganaResultMapper furiganaResultMapper;

    public GenerateFuriganaUseCase(FuriganaGenerationPort furiganaGenerationPort, FuriganaResultMapper furiganaResultMapper) {
        this.furiganaGenerationPort = furiganaGenerationPort;
        this.furiganaResultMapper = furiganaResultMapper;
    }

    @Override
    public FuriganaResult generate(GenerateFuriganaCommand command) {
        if (command == null || command.text() == null || command.text().trim().isEmpty()) {
            throw new ApplicationException(
                    FuriganaErrorCode.FURIGANA_ANALYZE_FAILED,
                    FuriganaDetailMessageKey.FURIGANA_TEXT_EMPTY
            );
        }
        FuriganaText domainText = furiganaGenerationPort.generate(command.text());
        return furiganaResultMapper.domainToResult(domainText);
    }
}
