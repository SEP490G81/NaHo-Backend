package org.naho.initializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.learning.port.in.CrudUserLearningProgressInputPort;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Order(4)
@Component
@RequiredArgsConstructor
public class UserLearningProgressInitializer implements ApplicationRunner {
    private static final Long ADMIN_USER_ID = 1L;
    private final UserLearningProgressRepositoryPort userLearningProgressRepositoryPort;
    private final CrudUserLearningProgressInputPort crudUserLearningProgressInputPort;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        if (userLearningProgressRepositoryPort.existsByUserId(ADMIN_USER_ID)) {
            log.info("UserLearningProgress Data existed!");
        } else {
            log.info("Initializing UserLearningProgress Data...");
            crudUserLearningProgressInputPort.initUserLearningProgress(ADMIN_USER_ID);
            log.info("UserLearningProgress Data initialized!");
        }
    }
}
