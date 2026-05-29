package org.naho.speech.azure.constant;

public final class AzureSpeechApplicationMessageKey {

    private AzureSpeechApplicationMessageKey() {
    }

    // success
    public static final String SPEECH_PRONUNCIATION_ASSESSMENT_SUCCESSFULLY =
            "speech.pronunciation.assessment.successfully";

    // speech assessment
    public static final String SPEECH_AUDIO_NOT_VALID_TITLE =
            "speech.audio.not.valid.title";
    public static final String SPEECH_AUDIO_FILE_EMPTY =
            "speech.audio.file.empty";

    public static final String SPEECH_AZURE_SERVICE_ERROR_TITLE =
            "speech.azure.service.error.title";

    public static final String SPEECH_AZURE_SERVICE_UNKNOWN_ERROR_OCCUR =
            "speech.azure.service.unknown.error.occur";

    public static final String SPEECH_AZURE_N_BEST_NODE_NULL =
            "speech.azure.n.best.node.null";

    public static final String SPEECH_RECOGNITION_CANCELLED =
            "speech.recognition.cancelled";

    public static final String SPEECH_RECOGNITION_NO_MATCH =
            "speech.recognition.no.match";

    public static final String SPEECH_AZURE_CONNECTION_INTERRUPTED =
            "speech.azure.connection.interrupted";

    // text to speech
    public static final String SPEECH_SYNTHESIS_CANCELLED =
            "speech.synthesis.cancelled";
    public static final String SPEECH_SYNTHESIS_UNKNOWN_ERROR_OCCUR =
            "speech.synthesis.unknown.error.occur";
    // text
    public static final String SPEECH_TEXT_NOT_VALID_TITLE =
            "speech.text.not.valid.title";
    public static final String SPEECH_TEXT_BLANK =
            "speech.text.blank=";
    public static final String SPEECH_TEXT_INVALID_RANGE = "speech.text.invalid.range";
}