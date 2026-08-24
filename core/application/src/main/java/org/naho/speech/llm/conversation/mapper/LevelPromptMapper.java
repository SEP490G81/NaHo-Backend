package org.naho.speech.llm.conversation.mapper;

import org.naho.i18n.message.llm.LlmDetailMessageKey;
import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.llm.conversation.exception.LlmApplicationError;

public class LevelPromptMapper {

    public String mapMarugotoLevelToPrompt(MarugotoLevel marugotoLevel) {
        if (marugotoLevel == null) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_MARUGOTO_LEVEL_INVALID,
                    LlmDetailMessageKey.LLM_MARUGOTO_LEVEL_NULL
            );
        }
        return switch (marugotoLevel) {
            case STARTER_A1 ->
                    "`STARTER_A1` (4–8 words/sentence max. Basic daily vocab. Simple statements or Yes/No/Choice questions only)";
            case ELEMENTARY_1_A2 ->
                    "`ELEMENTARY_1_A2` (1–2 simple clauses. Routines/hobbies. Simple 5W1H (いつ/どこ/何/だれ), te-form, past tense)";
            case ELEMENTARY_2_A2 ->
                    "`ELEMENTARY_2_A2` (1–2 compound sentences. Experiences (~たことがある), desires (~たい), simple reasons (~から/ので))";
            case PRE_INTERMEDIATE_A2_B1 ->
                    "`PRE_INTERMEDIATE_A2_B1` (Multi-clause. Conditionals (~たら/ば), conjectures (~はず/かもしれない), opinions (~について))";
            case INTERMEDIATE_1_B1 ->
                    "`INTERMEDIATE_1_B1` (Connected discourse (実は/例えば/一方で). Comparisons, logical reasoning)";
            case INTERMEDIATE_2_B1 ->
                    "`INTERMEDIATE_2_B1` (Rich idiomatic Japanese, nuanced opinions, cultural/business discussion)";
            default -> throw new ApplicationException(
                    LlmApplicationError.LLM_MARUGOTO_LEVEL_INVALID,
                    LlmDetailMessageKey.LLM_MARUGOTO_LEVEL_UNSUPPORTED,
                    marugotoLevel
            );
        };
    }

    public String mapFormalityLevelToPrompt(FormalityLevel formalityLevel) {
        if (formalityLevel == null) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_FORMALITY_LEVEL_INVALID,
                    LlmDetailMessageKey.LLM_FORMALITY_LEVEL_NULL
            );
        }
        return switch (formalityLevel) {
            case INFORMAL ->
                    "`INFORMAL` (Plain form ONLY (だ/だよ/ね/の？/てる/〜て/〜ない？). NEVER use です/ます/keigo. Casual reactions (うん/へえ/そうなんだ))";
            case NEUTRAL ->
                    "`NEUTRAL` (Standard polite (です/ます/でした/ません). Polite reactions (はい/そうですね/なるほど))";
            case FORMAL -> "`FORMAL` (Professional keigo (でございます/〜ております/〜いたします/承知いたしました))";
            default -> throw new ApplicationException(
                    LlmApplicationError.LLM_FORMALITY_LEVEL_INVALID,
                    LlmDetailMessageKey.LLM_FORMALITY_LEVEL_UNSUPPORTED,
                    formalityLevel
            );
        };
    }
}



