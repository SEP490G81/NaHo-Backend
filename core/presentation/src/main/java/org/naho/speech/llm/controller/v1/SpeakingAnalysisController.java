package org.naho.speech.llm.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.file.constant.FileFolderConstant;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.port.out.FileValidatorPort;
import org.naho.file.result.StoredFile;
import org.naho.i18n.message.speech.SpeechDetailMessageKey;
import org.naho.pagination.PageData;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.shared.exception.PresentationException;
import org.naho.speech.azure.exception.AzureSpeechErrorCode;
import org.naho.speech.llm.command.SpeakingAnalysisCommand;
import org.naho.speech.llm.command.SpeakingHistoryFilterCommand;
import org.naho.speech.llm.dto.mapper.SpeakingAnalysisResponseMapper;
import org.naho.speech.llm.dto.request.SpeakingHistoryQueryRequest;
import org.naho.speech.llm.dto.response.SpeakingAnalysisResponse;
import org.naho.speech.llm.dto.response.SpeakingHistoryDetailResponse;
import org.naho.speech.llm.dto.response.SpeakingHistoryListItemResponse;
import org.naho.speech.llm.port.in.SpeakingAnalysisInputPort;
import org.naho.speech.llm.result.SpeakingAnalysisResult;
import org.naho.speech.llm.result.SpeakingHistoryListItemResult;
import org.naho.subscription.port.in.GetActiveSubscriptionInputPort;
import org.naho.subscription.result.SubscriptionPlanResult;
import org.naho.subscription.type.PlanCode;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SpeakingAnalysisController {

    private final SpeakingAnalysisInputPort speakingAnalysisInputPort;
    private final SpeakingAnalysisResponseMapper speakingAnalysisResponseMapper;
    private final FileStorageServicePort fileStorageServicePort;
    private final FileValidatorPort fileValidatorPort;
    private final GetActiveSubscriptionInputPort getActiveSubscriptionInputPort;

    @ApiResponseMessage(message = SpeechDetailMessageKey.SPEAKING_ANALYSIS_SUCCESS)
    @PostMapping(
            value = "/analysis",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<SpeakingAnalysisResponse> uploadAudioAndAnalyzeSpeaking(
            @RequestPart("file") MultipartFile file,
            @RequestParam("speakingQuestionId") Long speakingQuestionId,
            @RequestParam("durationSec") Integer durationSec,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        // lấy gói đăng kí của user
        SubscriptionPlanResult subscriptionPlan = getActiveSubscriptionInputPort
                .getUserActiveSubscriptionPlan(payload.userId());

        try {
            StoredFile storedFile = null;
            byte[] audioBytes = file.getBytes();

            // nếu đang dùng gói FREE thì không lưu file
            if (!subscriptionPlan.code().equals(PlanCode.FREE)) {
                // validate xem có phải file .wav không?
                // và validate xem thời lượng có hợp lệ không?
                fileValidatorPort.validateWavFileAndDuration(
                        audioBytes,
                        subscriptionPlan.maxAnswerTimeSeconds()
                );
                // Step 1: Lưu file vào local
                storedFile = fileStorageServicePort.saveFileToLocal(file, FileFolderConstant.RECORDINGS, false);
            }

            SpeakingAnalysisCommand command = SpeakingAnalysisCommand.builder()
                    .userId(payload.userId())
                    .speakingQuestionId(speakingQuestionId)
                    .durationSec(durationSec)
                    .storedFile(storedFile)
                    .audioBytes(audioBytes)
                    .build();

            SpeakingAnalysisResult result = speakingAnalysisInputPort.analyzeSpeaking(command);

            SpeakingAnalysisResponse response = speakingAnalysisResponseMapper
                    .resultToResponse(result);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IOException e) {
            throw new PresentationException(
                    AzureSpeechErrorCode.SPEECH_AUDIO_NOT_VALID,
                    SpeechDetailMessageKey.SPEECH_AUDIO_FILE_EMPTY,
                    e.getMessage()
            );
        }
    }

    @PostMapping(value = "/speaking-histories", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponseMessage(message = SpeechDetailMessageKey.SPEAKING_HISTORY_GET_ALL_SUCCESS)
    public ResponseEntity<PageData<SpeakingHistoryListItemResponse>> getUserHistoryList(
            @AuthenticationPrincipal AccessTokenPayload payload,
            @RequestBody(required = false) SpeakingHistoryQueryRequest request
    ) {
        if (request == null) {
            request = new SpeakingHistoryQueryRequest();
        }
        SpeakingHistoryFilterCommand command = speakingAnalysisResponseMapper.requestToCommand(request, payload.userId());

        PageData<SpeakingHistoryListItemResult> result = speakingAnalysisInputPort.getUserHistoryList(command);

        PageData<SpeakingHistoryListItemResponse> response = PageData.<SpeakingHistoryListItemResponse>builder()
                .pageMeta(result.getPageMeta())
                .data(result.getData()
                        .stream()
                        .map(speakingAnalysisResponseMapper::toListItemResponse)
                        .toList()
                )
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/speaking-histories/{historyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponseMessage(message = SpeechDetailMessageKey.SPEAKING_HISTORY_GET_DETAIL_SUCCESS)
    public ResponseEntity<SpeakingHistoryDetailResponse> getSpeakingHistoryDetail(
            @PathVariable("historyId") Long historyId
    ) {
        var result = speakingAnalysisInputPort.getHistoryDetail(historyId);
        return ResponseEntity.ok(speakingAnalysisResponseMapper.toDetailResponse(result));
    }
}
