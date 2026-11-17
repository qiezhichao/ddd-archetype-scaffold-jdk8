package cn.zachqie.scaffold.start.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

@Slf4j
@Configuration
@EnableAsync
public class ThreadPoolConfig implements AsyncConfigurer {

    /********************************* Spring 事件处理线程池配置 *******************************/
    /** 核心线程数 */
    @Value("${thread-pool.executor.event-async.core-pool-size}")
    private Integer corePoolSize;

    /** 最大线程数 */
    @Value("${thread-pool.executor.event-async.max-pool-size}")
    private Integer maxPoolSize;

    /** 队列容量 */
    @Value("${thread-pool.executor.event-async.queue-capacity}")
    private Integer queueCapacity;

    /** 线程名称前缀 */
    @Value("${thread-pool.executor.event-async.thread-name-prefix}")
    private String threadNamePrefix;

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setTaskDecorator(runnable -> {
            Map<String, String> parentMdc = MDC.getCopyOfContextMap();
            return () -> {
                try {
                    if (Objects.nonNull(parentMdc) && !parentMdc.isEmpty()) {
                        MDC.setContextMap(parentMdc);
                    }
                    runnable.run();
                } finally {
                    MDC.clear();
                }
            };
        });
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

    /********************************* xxx处理线程池配置 *******************************/
    /** 核心线程数 */
    @Value("${thread-pool.executor.xxx.core-pool-size}")
    private Integer xxxCorePoolSize;
    /** 最大线程数 */
    @Value("${thread-pool.executor.xxx.max-pool-size}")
    private Integer xxxMaxPoolSize;
    /** 最大等待时间 */
    @Value("${thread-pool.executor.xxx.keep-alive-time-sec}")
    private Integer xxxKeepAliveTime;
    /** 最大队列数 */
    @Value("${thread-pool.executor.xxx.block-queue-size}")
    private Integer xxxBlockQueueSize;
    @Value("${thread-pool.executor.xxx.thread-name-prefix}")
    private String xxxThreadNamePrefix;
    /*
     * AbortPolicy：丢弃任务并抛出RejectedExecutionException异常。
     * DiscardPolicy：直接丢弃任务，但是不会抛出异常
     * DiscardOldestPolicy：将最早进入队列的任务删除，之后再尝试加入队列的任务被拒绝
     * CallerRunsPolicy：如果任务添加线程池失败，那么主线程自己执行该任务
     * */
    @Value("${thread-pool.executor.xxx.policy}")
    private String xxxPolicy;
    @Bean("ChatMessageSendThreadPoolTaskExecutor")
    public ThreadPoolTaskExecutor chatMessageSendTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(xxxCorePoolSize);
        executor.setMaxPoolSize(xxxMaxPoolSize);
        executor.setQueueCapacity(xxxBlockQueueSize);
        executor.setThreadNamePrefix(xxxThreadNamePrefix);
        executor.setTaskDecorator(runnable -> {
            Map<String, String> parentMdc = MDC.getCopyOfContextMap();
            return () -> {
                try {
                    if (Objects.nonNull(parentMdc) && !parentMdc.isEmpty()) {
                        MDC.setContextMap(parentMdc);
                    }
                    runnable.run();
                } finally {
                    MDC.clear();
                }
            };
        });
        executor.initialize();
        return executor;
    }








    /****************************************************************************************************************/

//    @Bean("AIThreadPoolExecutor")
//    @ConditionalOnMissingBean(ThreadPoolExecutor.class)
//    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties properties) {
//        RejectedExecutionHandler handler;
//        switch (properties.getPolicy()){
//            case "DiscardPolicy":
//                handler = new ThreadPoolExecutor.DiscardPolicy();
//                break;
//            case "DiscardOldestPolicy":
//                handler = new ThreadPoolExecutor.DiscardOldestPolicy();
//                break;
//            case "CallerRunsPolicy":
//                handler = new ThreadPoolExecutor.CallerRunsPolicy();
//                break;
//            case "AbortPolicy":
//            default:
//                handler = new ThreadPoolExecutor.AbortPolicy();
//                break;
//        }
//        // 创建线程池
//        return new ThreadPoolExecutor(
//                properties.getCorePoolSize(),
//                properties.getMaxPoolSize(),
//                properties.getKeepAliveTime(),
//                TimeUnit.SECONDS,
//                new LinkedBlockingQueue<>(properties.getBlockQueueSize()),
//                new NamedThreadFactory("AI-Process"),
//                handler);
//    }
//
//
//
//
//    /**
//     * @desc 自定义线程工程，实现线程自定义名称
//     * @author qiezhichao
//     * @date 2025/4/15 13:55
//     */
//    static class NamedThreadFactory implements ThreadFactory {
//        private static final AtomicInteger poolNumber = new AtomicInteger(1);
//        private final ThreadGroup group;
//        private final AtomicInteger threadNumber = new AtomicInteger(1);
//        private final String namePrefix;
//
//        NamedThreadFactory(String name) {
//            SecurityManager s = System.getSecurityManager();
//            group = (s != null) ? s.getThreadGroup() :
//                    Thread.currentThread().getThreadGroup();
//
//            if (StringUtils.isBlank(name)) {
//                name = "pool";
//            }
//            namePrefix = name + "-" + poolNumber.getAndIncrement() + "-thread-";
//        }
//
//        public Thread newThread(Runnable r) {
//            Thread t = new Thread(group, r,
//                    namePrefix + threadNumber.getAndIncrement(),
//                    0);
//            if (t.isDaemon())
//                t.setDaemon(false);
//            if (t.getPriority() != Thread.NORM_PRIORITY)
//                t.setPriority(Thread.NORM_PRIORITY);
//            return t;
//        }
//    }

}
