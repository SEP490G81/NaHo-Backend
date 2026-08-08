package org.naho.config.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    private static final String RETRY_UPLOAD_FILE_TO_CLOUD_PREFIX = "Async-retry-upload-file-to-cloud-";
    private static final String DELETE_FILE_IN_CLOUD_PREFIX = "Async-delete-file-in-cloud-";

    @Bean
    public Executor retryUploadFileToCloudAsync() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix(RETRY_UPLOAD_FILE_TO_CLOUD_PREFIX);
        executor.initialize();
        return executor;
    }

    @Bean
    public Executor deleteFileInCloudExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix(DELETE_FILE_IN_CLOUD_PREFIX);
        executor.initialize();
        return executor;
    }
}
