package cn.zachqie.scaffold.start.config.interceptor;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @ClassName ContextInterceptor
 * @Author qiezhichao
 * @Date 2023/5/23 09:50
 */
@Component
@Slf4j
public class ContextInterceptor implements HandlerInterceptor {

    private static final List<String> excludePaths = Lists.newArrayList(
            // swaggerUI 路径过滤
            "/doc.html", "/webjars/bycdao-ui", "/swagger-resources", "/error"
    );

    @Value("${spring.profiles.active}")
    private String env;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        // 允许跨域请求，如果在网关层已经设置，则这里不用再次设置
//        resp.setHeader("Access-Control-Allow-Origin", "*");
//        resp.setHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS,DELETE");
//        resp.setHeader("Access-Control-Max-Age", "3600");
//        resp.setHeader("Access-Control-Allow-Headers",
//                String.join(",", Lists.newArrayList("x-requested-with", "Content-Type")));
//        resp.setHeader("Access-Control-Allow-Credentials", "true");

        // 白名单验证
        String servletPath = req.getServletPath();
        log.info("ContextInterceptor servletPath: {}", servletPath);
        if (StringUtils.startsWithAny(servletPath, excludePaths.toArray(new String[0]))) {
            return true;
        }

//        // 设置环境信息
//        ContextHelper.setEnv(env);
//
//        // 获取accessToken
//        String accessToken = req.getHeader(KEY_ACCESS_TOKEN);
//        Assert.notBlank(accessToken, "用户认证失败");
//        // 用户认证
//        UserEntity userEntity = userService.authUserByAccessToken(accessToken);
//        Assert.notNull(userEntity, "用户认证失败");
//
//        log.info("用户认证成功, userId: {}", userEntity.getUserId());
//        ContextHelper.setUserContext(userEntity);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse resp, Object handler, Exception ex) {
        try {
            // 资源清理
        } catch (Exception e) {
            log.error("clear context exception, error msg: {}, current thread: {}", e.getMessage(), Thread.currentThread().getName());
        }
    }
}
