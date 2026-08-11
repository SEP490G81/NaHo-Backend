package org.naho.notification.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.notification.event.NotificationCreatedEvent;
import org.naho.notification.model.Notification;
import org.naho.notification.port.out.NotificationRepositoryPort;
import org.naho.notification.type.NotificationType;
import org.naho.payment.event.PaymentConfirmedEvent;
import org.naho.payment.event.SubscriptionUpgradedEvent;
import org.naho.social.comment.event.CommentRepliedEvent;
import org.naho.social.reaction.event.ReactionCreatedEvent;
import org.naho.social.report.event.ReportCreatedEvent;
import org.naho.social.report.event.ReportStatusUpdatedEvent;
import org.naho.user.model.User;
import org.naho.user.port.out.UserRepositoryPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private static final Locale DEFAULT_LOCALE = new Locale("vi", "VN");
    private final NotificationRepositoryPort notificationRepositoryPort;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final UserRepositoryPort userRepositoryPort;
    private final LearningPathNodeRepositoryPort learningPathNodeRepositoryPort;
    private final MessageSource messageSource;

    @EventListener
    public void handleCommentReplied(CommentRepliedEvent event) {
        String replierName = getFullName(event.replierId());
        String targetUrl = getQuestionUrl(event.questionId(), "#comment-" + event.newCommentId());

        String title = messageSource.getMessage("notification.social.comment.replied.title", null, "Co nguoi tra loi binh luan cua ban", DEFAULT_LOCALE);
        String content = messageSource.getMessage("notification.social.comment.replied.content", new Object[]{replierName}, replierName + " vua tra loi binh luan cua ban.", DEFAULT_LOCALE);

        saveAndPublish(Notification.builder()
                .userId(event.parentAuthorId())
                .type(NotificationType.SOCIAL)
                .title(title)
                .content(content)
                .targetUrl(targetUrl)
                .isRead(false)
                .build());
    }

    @EventListener
    public void handleReactionCreated(ReactionCreatedEvent event) {
        String reactorName = getFullName(event.reactorId());
        String targetUrl = getQuestionUrl(event.questionId(), "#comment-" + event.commentId());

        String title = messageSource.getMessage("notification.social.reaction.created.title", null, "Co nguoi thich binh luan cua ban", DEFAULT_LOCALE);
        String content = messageSource.getMessage("notification.social.reaction.created.content", new Object[]{reactorName}, reactorName + " vua tha cam xuc vao binh luan cua ban.", DEFAULT_LOCALE);

        saveAndPublish(Notification.builder()
                .userId(event.commentAuthorId())
                .type(NotificationType.SOCIAL)
                .title(title)
                .content(content)
                .targetUrl(targetUrl)
                .isRead(false)
                .build());
    }

    @EventListener
    public void handleReportCreated(ReportCreatedEvent event) {
        String reporterName = getFullName(event.reporterId());
        String targetUrl = "/admin/reports/" + event.reportId();

        String title = messageSource.getMessage("notification.report.created.title", null, "Co bao cao moi", DEFAULT_LOCALE);
        String content = messageSource.getMessage("notification.report.created.content", new Object[]{reporterName}, reporterName + " vua gui mot bao cao moi", DEFAULT_LOCALE);

        // Notify all admins
        List<User> admins = userRepositoryPort.findByFilters(null, "ADMIN", null);
        for (User admin : admins) {
            saveAndPublish(Notification.builder()
                    .userId(admin.getId())
                    .type(NotificationType.REPORT)
                    .title(title)
                    .content(content)
                    .targetUrl(targetUrl)
                    .isRead(false)
                    .build());
        }
    }

    @EventListener
    public void handleReportStatusUpdated(ReportStatusUpdatedEvent event) {
        String title = messageSource.getMessage("notification.report.resolved.title", null, "Bao cao da duoc xu ly", DEFAULT_LOCALE);
        String content = messageSource.getMessage("notification.report.resolved.content", null, "Bao cao cua ban da duoc quan tri vien xu ly thanh cong.", DEFAULT_LOCALE);

        saveAndPublish(Notification.builder()
                .userId(event.reporterId())
                .type(NotificationType.SYSTEM)
                .title(title)
                .content(content)
                .isRead(false)
                .build());
    }

    @EventListener
    public void handleSubscriptionUpgraded(SubscriptionUpgradedEvent event) {
        String title = messageSource.getMessage("notification.payment.subscription.upgraded.title", null, "Nang cap goi thanh cong", DEFAULT_LOCALE);
        String content = messageSource.getMessage("notification.payment.subscription.upgraded.content", new Object[]{event.planName()}, "Goi cua ban da duoc quan tri vien nang cap thanh " + event.planName() + ".", DEFAULT_LOCALE);

        saveAndPublish(Notification.builder()
                .userId(event.targetUserId())
                .type(NotificationType.PAYMENT)
                .title(title)
                .content(content)
                .isRead(false)
                .build());
    }

    @EventListener
    public void handlePaymentConfirmed(PaymentConfirmedEvent event) {
        String title = messageSource.getMessage("notification.payment.confirmed.title", null, "Nang cap goi thanh cong", DEFAULT_LOCALE);
        String content = messageSource.getMessage("notification.payment.confirmed.content", new Object[]{event.planName()}, "Chuc mung ban da nang cap thanh cong goi " + event.planName() + ". Hay trai nghiem ngay nhung tinh nang cao cap!", DEFAULT_LOCALE);

        saveAndPublish(Notification.builder()
                .userId(event.userId())
                .type(NotificationType.PAYMENT)
                .title(title)
                .content(content)
                .isRead(false)
                .build());
    }

    private void saveAndPublish(Notification notification) {
        try {
            Notification saved = notificationRepositoryPort.save(notification);
            applicationEventPublisher.publishEvent(new NotificationCreatedEvent(saved.getUserId(), saved));
        } catch (Exception e) {
            log.error("Failed to save and publish notification for user: {}", notification.getUserId(), e);
        }
    }

    private String getFullName(Long userId) {
        String defaultName = messageSource.getMessage("notification.default.user.name", null, "Mot nguoi dung", DEFAULT_LOCALE);
        return userRepositoryPort.findById(userId)
                .map(User::getFullName)
                .orElse(defaultName);
    }

    private String getQuestionUrl(Long questionId, String hash) {
        return learningPathNodeRepositoryPort.getFrontendUrlPath(questionId)
                .map(path -> path + hash)
                .orElse("/speaking-questions/" + questionId + hash);
    }
}
