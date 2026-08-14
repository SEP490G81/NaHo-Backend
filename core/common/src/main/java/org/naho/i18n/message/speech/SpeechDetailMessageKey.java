package org.naho.i18n.message.speech;

public final class SpeechDetailMessageKey {
    // Success
    public static final String SPEECH_PRONUNCIATION_ASSESSMENT_SUCCESSFULLY =
            "speech.pronunciation.assessment.successfully";
    public static final String SPEAKING_HISTORY_GET_ALL_SUCCESS =
            "speaking.history.get.all.success";
    public static final String SPEAKING_HISTORY_GET_DETAIL_SUCCESS =
            "speaking.history.get.detail.success";
    public static final String SPEAKING_ANALYSIS_SUCCESS =
            "speaking.analysis.success";
    public static final String SPEAKING_TOPICS_GET_SUCCESS =
            "speaking.topics.get.success";
    public static final String SPEAKING_TOPIC_SESSION_START_SUCCESS =
            "speaking.topic.session.start.success";
    public static final String SPEAKING_CONVERSATION_START_SUCCESS =
            "speaking.conversation.start.success";
    public static final String SPEAKING_MESSAGE_SEND_SUCCESS =
            "speaking.message.send.success";
    public static final String SPEAKING_AUDIO_MESSAGE_PROCESS_SUCCESS =
            "speaking.audio.message.process.success";
    public static final String SPEAKING_SESSION_END_SUCCESS =
            "speaking.session.end.success";
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

    // Azure Cost Management
    public static final String AZURE_COST_SUMMARY_GET_SUCCESS =
            "azure.cost.summary.get.success";
    public static final String AZURE_COST_CHART_GET_SUCCESS =
            "azure.cost.chart.get.success";
    public static final String AZURE_COST_API_FETCH_FAILED =
            "azure.cost.api.fetch.failed";
    public static final String AZURE_COST_INVALID_TIMEFRAME =
            "azure.cost.invalid.timeframe";

    // AWS Cost Management
    public static final String AWS_COST_SUMMARY_GET_SUCCESS =
            "aws.cost.summary.get.success";
    public static final String AWS_COST_CHART_GET_SUCCESS =
            "aws.cost.chart.get.success";

    private SpeechDetailMessageKey() {
    }

}

