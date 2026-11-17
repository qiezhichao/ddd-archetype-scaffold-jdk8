package cn.zachqie.scaffold.start.config;

import cn.zachqie.scaffold.start.config.interceptor.ContextInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @ClassName InterceptorConfig
 * @Author qiezhichao
 * @Date 2023/3/28 17:22
 */
@Configuration
@Slf4j
public class InterceptorConfig implements WebMvcConfigurer {

    @Resource
    private ContextInterceptor contextInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(contextInterceptor);
    }

}
