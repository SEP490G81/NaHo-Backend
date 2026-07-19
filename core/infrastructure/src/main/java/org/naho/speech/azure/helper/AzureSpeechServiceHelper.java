package org.naho.speech.azure.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.PushAudioInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.i18n.message.speech.SpeechDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.naho.speech.azure.constant.AzurePronunciationScoreKey;
import org.naho.speech.azure.exception.AzureSpeechErrorCode;
import org.naho.speech.model.SpeechAssessment;
import org.naho.speech.model.WordAssessment;
import org.naho.speech.type.SpeechAssessmentErrorType;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class AzureSpeechServiceHelper {
    private static final int MAX_AUDIO_SIZE_BYTES = 20 * 1024 * 1024; // 20MB
    private static final double MAX_AUDIO_DURATION_SECONDS = 150.0;
    private static final long EXTRA_PROCESSING_TIMEOUT_SECONDS = 30L;
    private static final long MAX_PROCESSING_TIMEOUT_SECONDS = 180L;
    private static final int FFMPEG_MAX_CONCURRENT_PROCESS = 10;
    private static final int FFMPEG_BUFFER_SIZE = 4096;
    private static final Semaphore FFMPEG_SEMAPHORE = new Semaphore(FFMPEG_MAX_CONCURRENT_PROCESS);
    private final ObjectMapper objectMapper;

    public Path createTempInputAudioFile(byte[] audioBytes) {
        try {
            Path inputFile = Files.createTempFile("input_audio_", ".audio");
            Files.write(inputFile, audioBytes);
            return inputFile;
        } catch (IOException e) {
            throw new InfrastructureException(
                    AzureSpeechErrorCode.SPEECH_AUDIO_NOT_VALID,
                    SpeechDetailMessageKey.SPEECH_AUDIO_FILE_EMPTY
            );
        }
    }

    public void validateAudioSize(byte[] rawAudioBytes) {
        if (rawAudioBytes == null || rawAudioBytes.length == 0) {
            throw new InfrastructureException(
                    AzureSpeechErrorCode.SPEECH_AUDIO_NOT_VALID,
                    SpeechDetailMessageKey.SPEECH_AUDIO_FILE_EMPTY
            );
        }

        if (rawAudioBytes.length > MAX_AUDIO_SIZE_BYTES) {
            throw new InfrastructureException(
                    AzureSpeechErrorCode.SPEECH_AUDIO_NOT_VALID,
                    SpeechDetailMessageKey.SPEECH_AUDIO_FILE_TOO_LARGE
            );
        }
    }

    public double probeAudioDurationSeconds(Path inputFile) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffprobe",
                    "-v", "error",
                    "-show_entries", "format=duration",
                    "-of", "default=noprint_wrappers=1:nokey=1",
                    inputFile.toString()
            );

            Process process = processBuilder.start();
            boolean finished = process.waitFor(10, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                throw new InfrastructureException(
                        AzureSpeechErrorCode.SPEECH_AUDIO_NOT_VALID,
                        SpeechDetailMessageKey.SPEECH_AUDIO_DURATION_INVALID
                );
            }

            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
            String error = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8).trim();

            if (process.exitValue() != 0 || output.isBlank()) {
                log.error("FFprobe failed. output={}, error={}", output, error);
                throw new InfrastructureException(
                        AzureSpeechErrorCode.SPEECH_AUDIO_NOT_VALID,
                        SpeechDetailMessageKey.SPEECH_AUDIO_DURATION_INVALID
                );
            }

            return Double.parseDouble(output);

        } catch (InfrastructureException e) {
            throw e;
        } catch (Exception e) {
            log.error("Cannot determine audio duration", e);
            throw new InfrastructureException(
                    AzureSpeechErrorCode.SPEECH_AUDIO_NOT_VALID,
                    SpeechDetailMessageKey.SPEECH_AUDIO_DURATION_INVALID
            );
        }
    }

    public void validateAudioDuration(double durationSeconds) {
        if (durationSeconds <= 0 || Double.isNaN(durationSeconds) || Double.isInfinite(durationSeconds)) {
            throw new InfrastructureException(
                    AzureSpeechErrorCode.SPEECH_AUDIO_NOT_VALID,
                    SpeechDetailMessageKey.SPEECH_AUDIO_DURATION_INVALID
            );
        }

        if (durationSeconds > MAX_AUDIO_DURATION_SECONDS) {
            throw new InfrastructureException(
                    AzureSpeechErrorCode.SPEECH_AUDIO_NOT_VALID,
                    SpeechDetailMessageKey.SPEECH_AUDIO_DURATION_EXCEEDED
            );
        }
    }

    public long calculateProcessingTimeoutSeconds(double durationSeconds) {
        return Math.min(
                (long) Math.ceil(durationSeconds) + EXTRA_PROCESSING_TIMEOUT_SECONDS,
                MAX_PROCESSING_TIMEOUT_SECONDS
        );
    }

    public CompletableFuture<Void> streamAzurePcmByFfmpegAsync(Path inputFile, PushAudioInputStream pushStream) {
        return CompletableFuture.runAsync(() -> {
            boolean acquired = false;
            Process process = null;

            try {
                FFMPEG_SEMAPHORE.acquire();
                acquired = true;

                ProcessBuilder processBuilder = new ProcessBuilder(
                        "ffmpeg",
                        "-hide_banner",
                        "-loglevel", "error",
                        "-y",
                        "-i", inputFile.toString(),
                        "-vn",
                        // Tạm thời bỏ bộ lọc silenceremove để tránh bị mất đoạn ghi âm sau khi lặng im
                        // "-af", "silenceremove=start_periods=1:start_duration=0.2:start_threshold=-45dB:stop_periods=1:stop_duration=0.2:stop_threshold=-45dB",
                        "-acodec", "pcm_s16le",
                        "-ac", "1",
                        "-ar", "16000",
                        "-f", "s16le",
                        "pipe:1"
                );

                process = processBuilder.start();
                Process ffmpegProcess = process;

                CompletableFuture<String> errorFuture = CompletableFuture.supplyAsync(() -> readStreamAsString(ffmpegProcess.getErrorStream()));

                byte[] buffer = new byte[FFMPEG_BUFFER_SIZE];
                try (InputStream inputStream = process.getInputStream()) {
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        byte[] chunk = new byte[bytesRead];
                        System.arraycopy(buffer, 0, chunk, 0, bytesRead);
                        pushStream.write(chunk);
                    }
                }

                int exitCode = process.waitFor();
                String ffmpegError = errorFuture.get(3, TimeUnit.SECONDS);

                if (exitCode != 0) {
                    log.error("FFmpeg streaming failed: {}", ffmpegError);
                    throw new InfrastructureException(
                            AzureSpeechErrorCode.SPEECH_AUDIO_NOT_VALID,
                            SpeechDetailMessageKey.SPEECH_AUDIO_CONVERT_FAILED,
                            ffmpegError
                    );
                }

            } catch (InfrastructureException e) {
                throw e;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new InfrastructureException(
                        AzureSpeechErrorCode.SPEECH_AUDIO_NOT_VALID,
                        SpeechDetailMessageKey.SPEECH_AUDIO_CONVERT_INTERRUPTED
                );
            } catch (IOException e) {
                log.error("FFmpeg is not available or cannot start", e);
                throw new InfrastructureException(
                        AzureSpeechErrorCode.SPEECH_AUDIO_NOT_VALID,
                        SpeechDetailMessageKey.SPEECH_AUDIO_FFMPEG_NOT_AVAILABLE
                );
            } catch (Exception e) {
                log.error("Unexpected FFmpeg streaming error", e);
                throw new InfrastructureException(
                        AzureSpeechErrorCode.SPEECH_AUDIO_NOT_VALID,
                        SpeechDetailMessageKey.SPEECH_AUDIO_CONVERT_FAILED,
                        e.getMessage()
                );
            } finally {
                try {
                    pushStream.close();
                } catch (Exception ignored) {
                }

                if (process != null && process.isAlive()) {
                    process.destroyForcibly();
                }

                if (acquired) {
                    FFMPEG_SEMAPHORE.release();
                }
            }
        });
    }

    private String readStreamAsString(InputStream inputStream) {
        try (InputStream is = inputStream; ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            is.transferTo(baos);
            return baos.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }

    public PronunciationAssessmentConfig createPronunciationAssessmentConfig(String referenceText) {
        String normalizedReferenceText = referenceText == null ? "" : referenceText.trim();
        boolean miscueEnabled = !normalizedReferenceText.isEmpty();

        PronunciationAssessmentConfig config = new PronunciationAssessmentConfig(
                normalizedReferenceText,
                PronunciationAssessmentGradingSystem.HundredMark,
                PronunciationAssessmentGranularity.Word,
                miscueEnabled
        );

        config.enableProsodyAssessment();
        return config;
    }

    public SpeechAssessment processResult(SpeechRecognitionResult result) {
        if (result.getReason() == ResultReason.RecognizedSpeech) {
            String jsonResult = result.getProperties().getProperty(PropertyId.SpeechServiceResponse_JsonResult);

            try {
                JsonNode root = objectMapper.readTree(jsonResult);
                JsonNode nBestNode = root.path(AzurePronunciationScoreKey.N_BEST).get(0);
                if (nBestNode == null) {
                    throw new InfrastructureException(
                            AzureSpeechErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                            SpeechDetailMessageKey.SPEECH_AZURE_N_BEST_NODE_NULL
                    );
                }

                String displayResultText = nBestNode.path(AzurePronunciationScoreKey.DISPLAY).asText();
                JsonNode pronNode = nBestNode.path(AzurePronunciationScoreKey.PRONUNCIATION_ASSESSMENT);

                double accuracyScore = pronNode.path(AzurePronunciationScoreKey.ACCURACY_SCORE).asDouble(0.0);
                double fluencyScore = pronNode.path(AzurePronunciationScoreKey.FLUENCY_SCORE).asDouble(0.0);
                double completenessScore = pronNode.path(AzurePronunciationScoreKey.COMPLETENESS_SCORE).asDouble(0.0);
                double pronScore = pronNode.path(AzurePronunciationScoreKey.PRON_SCORE).asDouble(0.0);

                List<WordAssessment> wordList = new ArrayList<>();
                JsonNode wordsNode = nBestNode.path(AzurePronunciationScoreKey.WORDS);
                if (wordsNode.isArray()) {
                    for (JsonNode wordNode : wordsNode) {
                        String wordStr = wordNode.path(AzurePronunciationScoreKey.WORD).asText();
                        JsonNode wordPronNode = wordNode.path(AzurePronunciationScoreKey.PRONUNCIATION_ASSESSMENT);
                        double wordAccuracy = wordPronNode.path(AzurePronunciationScoreKey.ACCURACY_SCORE).asDouble(0.0);
                        String errorType = wordPronNode.path(AzurePronunciationScoreKey.ERROR_TYPE).asText(AzurePronunciationScoreKey.NONE).toUpperCase();

                        wordList.add(WordAssessment.builder()
                                .word(wordStr)
                                .accuracyScore(wordAccuracy)
                                .errorType(SpeechAssessmentErrorType.valueOf(errorType))
                                .build());
                    }
                }

                return SpeechAssessment.builder()
                        .transcriptText(displayResultText)
                        .accuracyScore(accuracyScore)
                        .fluencyScore(fluencyScore)
                        .completenessScore(completenessScore)
                        .pronunciationScore(pronScore)
                        .words(wordList)
                        .build();

            } catch (IOException e) {
                PronunciationAssessmentResult sdkResult = PronunciationAssessmentResult.fromResult(result);
                return SpeechAssessment.builder()
                        .transcriptText(result.getText())
                        .accuracyScore(sdkResult.getAccuracyScore())
                        .fluencyScore(sdkResult.getFluencyScore())
                        .completenessScore(sdkResult.getCompletenessScore())
                        .pronunciationScore(sdkResult.getPronunciationScore())
                        .words(List.of())
                        .build();
            }
        } else if (result.getReason() == ResultReason.NoMatch) {
            throw new InfrastructureException(
                    AzureSpeechErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                    SpeechDetailMessageKey.SPEECH_RECOGNITION_NO_MATCH
            );
        } else if (result.getReason() == ResultReason.Canceled) {
            CancellationDetails cancellation = CancellationDetails.fromResult(result);
            throw new InfrastructureException(
                    AzureSpeechErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                    SpeechDetailMessageKey.SPEECH_RECOGNITION_CANCELLED,
                    cancellation.getErrorDetails()
            );
        } else {
            throw new InfrastructureException(
                    AzureSpeechErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                    SpeechDetailMessageKey.SPEECH_AZURE_SERVICE_UNKNOWN_ERROR_OCCUR
            );
        }
    }

    public SpeechAssessment mergeAssessments(List<SpeechAssessment> assessments) {
        if (assessments.isEmpty()) {
            return null;
        }
        if (assessments.size() == 1) {
            return assessments.getFirst();
        }

        StringBuilder fullTranscript = new StringBuilder();
        List<WordAssessment> allWords = new ArrayList<>();

        double totalAccuracy = 0.0;
        double totalFluency = 0.0;
        double totalCompleteness = 0.0;
        double totalPronScore = 0.0;
        int totalWordCount = 0;

        for (SpeechAssessment segment : assessments) {
            if (segment.getTranscriptText() != null && !segment.getTranscriptText().isEmpty()) {
                if (!fullTranscript.isEmpty()) {
                    fullTranscript.append(" ");
                }
                fullTranscript.append(segment.getTranscriptText());
            }

            if (segment.getWords() != null) {
                allWords.addAll(segment.getWords());
            }

            int wordCount = segment.getWords() != null ? segment.getWords().size() : 0;
            if (wordCount > 0) {
                totalAccuracy += segment.getAccuracyScore() * wordCount;
                totalFluency += segment.getFluencyScore() * wordCount;
                totalCompleteness += segment.getCompletenessScore() * wordCount;
                totalPronScore += segment.getPronunciationScore() * wordCount;
                totalWordCount += wordCount;
            } else {
                totalAccuracy += segment.getAccuracyScore();
                totalFluency += segment.getFluencyScore();
                totalCompleteness += segment.getCompletenessScore();
                totalPronScore += segment.getPronunciationScore();
                totalWordCount += 1;
            }
        }

        double finalAccuracy = totalWordCount > 0 ? totalAccuracy / totalWordCount : 0.0;
        double finalFluency = totalWordCount > 0 ? totalFluency / totalWordCount : 0.0;
        double finalCompleteness = totalWordCount > 0 ? totalCompleteness / totalWordCount : 0.0;
        double finalPronScore = totalWordCount > 0 ? totalPronScore / totalWordCount : 0.0;

        return SpeechAssessment.builder()
                .transcriptText(fullTranscript.toString())
                .accuracyScore(finalAccuracy)
                .fluencyScore(finalFluency)
                .completenessScore(finalCompleteness)
                .pronunciationScore(finalPronScore)
                .words(allWords)
                .build();
    }
}
