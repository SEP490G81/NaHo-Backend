package org.naho.user.adapter.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.shared.port.out.EmailPort;
import org.naho.user.event.UserPlanUpgradedEvent;
import org.naho.user.model.User;
import org.naho.user.port.out.UserRepositoryPort;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserPlanUpgradedEventListener {

    private final EmailPort emailPort;
    private final UserRepositoryPort userRepositoryPort;

    @EventListener
    public void handleUserPlanUpgradedEvent(UserPlanUpgradedEvent event) {
        log.info("Received UserPlanUpgradedEvent for user id: {}", event.userId());
        
        try {
            Optional<User> userOpt = userRepositoryPort.findById(event.userId());
            if (userOpt.isEmpty()) {
                log.warn("User with id {} not found. Cannot send upgrade email.", event.userId());
                return;
            }
            User user = userOpt.get();
            String email = user.getEmail().getValue();
            String fullName = user.getFullName() != null ? user.getFullName() : user.getUsername().getValue();

            String subject = "NaHo - Chúc mừng nâng cấp tài khoản thành công!";
            Map<String, Object> variables = Map.of(
                "fullName", fullName,
                "newPlanName", event.newPlanName()
            );
            
            emailPort.sendEmail(email, subject, "upgrade-email", variables);
            
            log.info("Successfully requested to send upgrade notification email to: {}", email);
        } catch (Exception e) {
            log.error("Failed to process upgrade notification email for user id: {}", event.userId(), e);
        }
    }
}
