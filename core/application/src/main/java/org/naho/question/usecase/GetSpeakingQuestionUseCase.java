package org.naho.question.usecase;

import org.naho.i18n.message.question.SpeakingQuestionDetailMessageKey;
import org.naho.question.command.FindSpeakingQuestionCommand;
import org.naho.question.exception.SpeakingQuestionErrorCode;
import org.naho.question.mapper.SpeakingQuestionResultMapper;
import org.naho.question.model.SpeakingQuestion;
import org.naho.question.port.in.GetSpeakingQuestionInputPort;
import org.naho.question.port.out.SpeakingQuestionRepositoryPort;
import org.naho.question.result.SpeakingQuestionListItemResult;
import org.naho.question.result.SpeakingQuestionResult;
import org.naho.shared.exception.ApplicationException;
import org.naho.subscription.port.in.GetActiveSubscriptionInputPort;
import org.naho.subscription.result.SubscriptionPlanResult;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.type.RoleName;

import java.util.List;

public class GetSpeakingQuestionUseCase implements GetSpeakingQuestionInputPort {
    private final SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort;
    private final SpeakingQuestionResultMapper speakingQuestionResultMapper;
    private final GetActiveSubscriptionInputPort getActiveSubscriptionInputPort;
    private final RoleRepositoryPort roleRepositoryPort;

    public GetSpeakingQuestionUseCase(
            SpeakingQuestionRepositoryPort speakingQuestionRepositoryPort,
            SpeakingQuestionResultMapper speakingQuestionResultMapper,
            GetActiveSubscriptionInputPort getActiveSubscriptionInputPort,
            RoleRepositoryPort roleRepositoryPort
    ) {
        this.speakingQuestionRepositoryPort = speakingQuestionRepositoryPort;
        this.speakingQuestionResultMapper = speakingQuestionResultMapper;
        this.getActiveSubscriptionInputPort = getActiveSubscriptionInputPort;
        this.roleRepositoryPort = roleRepositoryPort;
    }

    /**
     * Lấy thông tin của 1 SpeakingQuestion theo id
     * Nếu user không có gói vip thì không trả ra sampleAnswer
     *
     * @param command chứa id của SpeakingQuestion và user id
     * @return SpeakingQuestionResult
     */
    @Override
    public SpeakingQuestionResult findById(FindSpeakingQuestionCommand command) {
        if (command.speakingQuestionId() == null) {
            throw new ApplicationException(
                    SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND,
                    SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_NOT_FOUND
            );
        }

        SpeakingQuestion speakingQuestion = speakingQuestionRepositoryPort
                .findById(command.speakingQuestionId())
                .orElseThrow(() -> new ApplicationException(
                        SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND,
                        SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_NOT_FOUND,
                        command.speakingQuestionId()
                ));

        // lấy ra gói đăng kí của người dùng
        SubscriptionPlanResult subscriptionPlanResult = getActiveSubscriptionInputPort
                .getUserActiveSubscriptionPlan(command.userId());
        
        // kiểm tra xem có phải là Admin hoặc Content Manager không
        List<String> userRoles = roleRepositoryPort.findRoleNamesByUserId(command.userId());
        boolean isContentManagerOrAdmin = userRoles.contains(RoleName.ADMIN.name())
                || userRoles.contains(RoleName.CONTENT_MANAGER.name());

        return speakingQuestionResultMapper.domainToResult(
                speakingQuestion,
                subscriptionPlanResult.sampleAnswerEnabled()
        );
    }

    /**
     * Lấy thông tin của 1 SpeakingQuestion không đầy đủ thông tin theo id
     * Nếu user không có gói vip thì không trả ra sampleAnswer
     *
     * @param command chứa id của SpeakingQuestion và user id
     * @return SpeakingQuestionListItemResult
     */
    @Override
    public SpeakingQuestionListItemResult findSpeakingQuestionListItem(FindSpeakingQuestionCommand command) {
        if (command.speakingQuestionId() == null) {
            throw new ApplicationException(
                    SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND,
                    SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_NOT_FOUND
            );
        }

        SpeakingQuestion speakingQuestion = speakingQuestionRepositoryPort
                .findById(command.speakingQuestionId())
                .orElseThrow(() -> new ApplicationException(
                        SpeakingQuestionErrorCode.SPEAKING_QUESTION_NOT_FOUND,
                        SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_NOT_FOUND,
                        command.speakingQuestionId()
                ));

        // lấy ra gói đăng kí của người dùng
        SubscriptionPlanResult subscriptionPlanResult = getActiveSubscriptionInputPort
                .getUserActiveSubscriptionPlan(command.userId());

        return speakingQuestionResultMapper.domainToListItemResult(
                speakingQuestion,
                subscriptionPlanResult.sampleAnswerEnabled() || isContentManagerOrAdmin
        );
    }
}

