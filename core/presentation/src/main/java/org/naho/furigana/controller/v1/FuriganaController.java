package org.naho.furigana.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.furigana.command.GenerateFuriganaCommand;
import org.naho.furigana.dto.mapper.FuriganaRequestMapper;
import org.naho.furigana.dto.mapper.FuriganaResponseMapper;
import org.naho.furigana.dto.request.GenerateFuriganaRequest;
import org.naho.furigana.dto.response.FuriganaResponse;
import org.naho.furigana.port.in.GenerateFuriganaInputPort;
import org.naho.furigana.result.FuriganaResult;
import org.naho.i18n.message.furigana.FuriganaDetailMessageKey;
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

    private final GenerateFuriganaInputPort generateFuriganaInputPort;
    private final FuriganaRequestMapper furiganaRequestMapper;
    private final FuriganaResponseMapper furiganaResponseMapper;

    @PostMapping("/generate")
    @ApiResponseMessage(message = FuriganaDetailMessageKey.FURIGANA_GENERATE_SUCCESSFULLY)
    public ResponseEntity<FuriganaResponse> generate(@RequestBody GenerateFuriganaRequest request) {
        GenerateFuriganaCommand command = furiganaRequestMapper.requestToCommand(request);
        FuriganaResult result = generateFuriganaInputPort.generate(command);
        FuriganaResponse response = furiganaResponseMapper.resultToResponse(result);
        return ResponseEntity.ok(response);
    }
}
