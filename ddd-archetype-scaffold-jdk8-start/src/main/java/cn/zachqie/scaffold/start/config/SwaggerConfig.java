package cn.zachqie.scaffold.start.config;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

/**
 * @ClassName SwaggerConfig
 * @Author qiezhichao
 * @Date 2022/11/1 15:24
 */
@EnableSwagger2
@Configuration
@EnableSwaggerBootstrapUI  // 配置登录用户和密码才需要
public class SwaggerConfig {

    @Bean
    public Docket customImplementation() {
        ApiInfo apiInfo = new ApiInfoBuilder()
                .termsOfServiceUrl("服务地址")
                .title("XXX Service")
                .description("XXX 服务Swagger")
                .version("1.0.0")
                .build();

        return new Docket(SWAGGER_2)
                .apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build();
//                .globalOperationParameters(this.parameters());
    }

    private List<Parameter> parameters() {
        Parameter platformCodeHeader = new ParameterBuilder()
                .name("platform-code")
                .description("平台编码")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(true)
                .build();
        Parameter accessTokenHeader = new ParameterBuilder()
                .name("access_token")
                .description("AccessToken, 登录接口该字段不用传")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(true)
                .build();
        return Lists.newArrayList(platformCodeHeader, accessTokenHeader);
    }

    @Bean
    public static BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
        return new BeanPostProcessor() {

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof WebMvcRequestHandlerProvider) {
                    customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
                }
                return bean;
            }

            private <T extends RequestMappingInfoHandlerMapping> void customizeSpringfoxHandlerMappings(List<T> mappings) {
                List<T> copy = mappings.stream()
                        .filter(mapping -> mapping.getPatternParser() == null)
                        .collect(Collectors.toList());
                mappings.clear();
                mappings.addAll(copy);
            }

            @SuppressWarnings("unchecked")
            private List<RequestMappingInfoHandlerMapping> getHandlerMappings(Object bean) {
                try {
                    Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
                    field.setAccessible(true);
                    return (List<RequestMappingInfoHandlerMapping>) field.get(bean);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }
        };
    }
}
