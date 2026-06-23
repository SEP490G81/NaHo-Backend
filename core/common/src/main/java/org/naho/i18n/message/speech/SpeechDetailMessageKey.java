package org.naho.i18n.message.speech;

public final class SpeechDetailMessageKey {
    private SpeechDetailMessageKey() {
    }

    // Success
    public static final String SPEECH_PRONUNCIATION_ASSESSMENT_SUCCESSFULLY =
            "speech.pronunciation.assessment.successfully";

    // Audio / STT input
    public static final String SPEECH_AUDIO_FILE_EMPTY =
            "speech.audio.file.empty";

    public static final String SPEECH_AUDIO_FILE_TOO_LARGE =
            "speech.audio.file.too.large";

    public static final String SPEECH_AUDIO_PROCESSING_TIMEOUT =
            "speech.audio.processing.timeout";

    public static final String SPEECH_AUDIO_CONVERT_TIMEOUT =
            "speech.audio.convert.timeout";

    public static final String SPEECH_AUDIO_CONVERT_FAILED =
            "speech.audio.convert.failed";

    public static final String SPEECH_AUDIO_FFMPEG_NOT_AVAILABLE =
            "speech.audio.ffmpeg.not.available";

    public static final String SPEECH_AUDIO_CONVERT_INTERRUPTED =
            "speech.audio.convert.interrupted";

    // Azure Speech / Recognition
    public static final String SPEECH_AZURE_SERVICE_UNKNOWN_ERROR_OCCUR =
            "speech.azure.service.unknown.error.occur";

    public static final String SPEECH_AZURE_API_ERROR =
            "speech.azure.api.error";

    public static final String SPEECH_AZURE_N_BEST_NODE_NULL =
            "speech.azure.n.best.node.null";

    public static final String SPEECH_RECOGNITION_CANCELLED =
            "speech.recognition.cancelled";

    public static final String SPEECH_RECOGNITION_NO_MATCH =
            "speech.recognition.no.match";

    public static final String SPEECH_AZURE_CONNECTION_INTERRUPTED =
            "speech.azure.connection.interrupted";

    // Text / TTS
    public static final String SPEECH_TEXT_BLANK =
            "speech.text.blank";

    public static final String SPEECH_TEXT_INVALID_RANGE =
            "speech.text.invalid.range";

    public static final String SPEECH_SYNTHESIS_CANCELLED =
            "speech.synthesis.cancelled";

    public static final String SPEECH_SYNTHESIS_UNKNOWN_ERROR_OCCUR =
            "speech.synthesis.unknown.error.occur";

    public static final String SPEECH_AUDIO_DURATION_INVALID =
            "speech.audio.duration.invalid";

    public static final String SPEECH_AUDIO_DURATION_EXCEEDED =
            "speech.audio.duration.exceeded";

    public static final String SPEECH_AZURE_EXECUTION_FAILED =
            "speech.azure.execution.failed";

}
