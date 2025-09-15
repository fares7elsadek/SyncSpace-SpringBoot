package com.fares7elsadek.syncspace.config.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

@Configuration
public class AsyncConfigurations {
    @Bean(name = "syncspace-executor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor delegate = new ThreadPoolTaskExecutor();
        delegate.setCorePoolSize(5);
        delegate.setMaxPoolSize(10);
        delegate.setQueueCapacity(25);
        delegate.initialize();
        return new DelegatingSecurityContextAsyncTaskExecutor(delegate);
    }
}
