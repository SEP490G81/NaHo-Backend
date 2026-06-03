package org.naho.furigana.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.furigana.command.AnalyzeFuriganaCommand;
import org.naho.furigana.constant.FuriganaApplicationMessageKey;
import org.naho.furigana.dto.mapper.FuriganaRequestMapper;
import org.naho.furigana.dto.mapper.FuriganaResponseMapper;
import org.naho.furigana.dto.request.AnalyzeFuriganaRequest;
import org.naho.furigana.dto.response.FuriganaResponse;
import org.naho.furigana.port.in.AnalyzeFuriganaInputPort;
import org.naho.furigana.result.FuriganaResult;
import org.naho.shared.annotation.ApiResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/furigana")
@RequiredArgsConstructor
public class FuriganaController {

    private final AnalyzeFuriganaInputPort analyzeFuriganaInputPort;
    private final FuriganaRequestMapper furiganaRequestMapper;
    private final FuriganaResponseMapper furiganaResponseMapper;

    @PostMapping("/analyze")
    @ApiResponseMessage(message = FuriganaApplicationMessageKey.FURIGANA_ANALYZE_SUCCESSFULLY)
    public ResponseEntity<FuriganaResponse> analyze(@RequestBody AnalyzeFuriganaRequest request) {
        AnalyzeFuriganaCommand command = furiganaRequestMapper.requestToCommand(request);
        FuriganaResult result = analyzeFuriganaInputPort.analyze(command);
        FuriganaResponse response = furiganaResponseMapper.resultToResponse(result);
        return ResponseEntity.ok(response);
    }
}
