package cn.zachqie.scaffold.start.config.filter;

import cn.zachqie.scaffold.type.utils.RequestIdUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName MDCFilter
 * @Author qiezhichao
 * @Date 2023/3/7 16:46
 */
@Component
public class MDCFilter implements Filter {

    private static final String REQUEST_ID_HEADER = "x-request-id";
    private static final String REQUEST_ID = "requestId";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // do nothing
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestId = httpRequest.getHeader(REQUEST_ID_HEADER);
        if (StringUtils.isBlank(requestId)) {
            RequestIdUtils.generateRequestId();
            requestId = RequestIdUtils.getRequestId().toString();
        }
        MDC.put(REQUEST_ID, requestId);
        httpResponse.setHeader(REQUEST_ID_HEADER, requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            RequestIdUtils.clearRequestId();
            MDC.clear();
        }
    }

    @Override
    public void destroy() {
        // do nothing
    }
}
