package org.naho.speech.llm.conversation.port.in;

import org.naho.speech.llm.conversation.result.SuggestedTopicsResult;

public interface SuggestedTopicsInputPort {
    SuggestedTopicsResult getSuggestedTopics();

}
