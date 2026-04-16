package com.smartarchive.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {

    /** 名称避免与 FlowableJobConfiguration 的 taskExecutor Bean 冲突 */
    @Bean(name = "pendingArchiveBatchExecutor")
    public Executor pendingArchiveBatchExecutor() {
        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
        ex.setCorePoolSize(2);
        ex.setMaxPoolSize(4);
        ex.setQueueCapacity(200);
        ex.setThreadNamePrefix("pending-archive-import-");
        ex.initialize();
        return ex;
    }
}
