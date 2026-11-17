package cn.zachqie.scaffold.infrastructure.zzzhelper;

import cn.zachqie.scaffold.domain.zzzhelper.IHttpHelper;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName DefaultHttpHelper
 * @Author qiezhichao
 * @Date 2026/6/26 15:04
 */
@Slf4j
@Component
public class DefaultHttpHelper implements IHttpHelper, DisposableBean {

    private final CloseableHttpClient httpClient;
    private final CloseableHttpClient httpsClient;
    private final RequestConfig requestConfig;

    public DefaultHttpHelper() {
        this.requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(5000, TimeUnit.MILLISECONDS)
                .setResponseTimeout(5000, TimeUnit.MILLISECONDS)
                .build();

        // HTTP 客户端连接池
        PoolingHttpClientConnectionManager httpPool = new PoolingHttpClientConnectionManager();
        httpPool.setMaxTotal(100);
        httpPool.setDefaultMaxPerRoute(20);
        this.httpClient = HttpClients.custom()
                .setConnectionManager(httpPool)
                .setDefaultRequestConfig(requestConfig)
                .build();

        // HTTPS 客户端连接池（信任所有证书）
        PoolingHttpClientConnectionManager httpsPool = PoolingHttpClientConnectionManagerBuilder
                .create()
                .setSSLSocketFactory(createSSLConnSocketFactory())
                .build();
        httpsPool.setMaxTotal(100);
        httpsPool.setDefaultMaxPerRoute(20);
        this.httpsClient = HttpClients.custom()
                .setConnectionManager(httpsPool)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    private SSLConnectionSocketFactory createSSLConnSocketFactory() {
        try {
            SSLContext sslContext = new SSLContextBuilder()
                    .loadTrustMaterial(null, new TrustAllStrategy()).build();
            return new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        } catch (GeneralSecurityException e) {
            log.error("createSSLConnSocketFactory error, {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create SSL socket factory", e);
        }
    }

    private CloseableHttpClient getHttpClient(String apiUrl) {
        return apiUrl.startsWith("https") ? httpsClient : httpClient;
    }

    private static String buildGetMethodURL(String url, Map<String, Object> params) {
        if (StringUtils.isBlank(url) || CollectionUtils.isEmpty( params)) {
            return url;
        }

        String apiUrl = url;
        StringBuffer param = new StringBuffer();
        int i = 0;
        for (String key : params.keySet()) {
            if (i == 0) {
                param.append("?");
            } else {
                param.append("&");
            }
            param.append(key).append("=").append(params.get(key));
            i++;
        }
        apiUrl += param;
        return apiUrl;
    }

    private HttpPost buildHttpPost(String apiUrl, Map<String, String> header) {
        HttpPost httpPost = new HttpPost(apiUrl);
        httpPost.setConfig(requestConfig);
        if (!CollectionUtils.isEmpty( header)) {
            for (String key : header.keySet()) {
                httpPost.setHeader(key, header.get(key));
            }
        }
        return httpPost;
    }

    /*****************************************************************************************************/

    @Override
    public String doGet(String url, Map<String, Object> params) {
        log.info("doGet begin, url={}, params={}", url, params);
        String apiUrl = buildGetMethodURL(url, params);
        log.info("apiUrl={}", apiUrl);
        CloseableHttpClient httpClient = getHttpClient(apiUrl);
        HttpGet httpGet = new HttpGet(apiUrl);

        String result = null;
        try (CloseableHttpResponse httpResponse = httpClient.execute(httpGet);) {
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                InputStream inputStream = entity.getContent();
                result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            }
            log.info("doGet end, result={}", result);
            return result;
        } catch (IOException e) {
            log.error("doGet Map error, {}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public String doGet(String url) {
        return doGet(url, new HashMap<>());
    }

    @Override
    public String doPost(String apiUrl, Map<String, String> header, Map<String, Object> params) {
        log.info("doPost begin, apiUrl={}, header={}, params={}", apiUrl, header, params);
        CloseableHttpClient httpClient = getHttpClient(apiUrl);
        HttpPost httpPost = buildHttpPost(apiUrl, header);
        // 设置请求头
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setEntity(new StringEntity(JSON.toJSONString(params), ContentType.APPLICATION_JSON));

        String result = null;
        try (CloseableHttpResponse httpResponse = httpClient.execute(httpPost)) {
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity);
            log.info("doPost end, result={}", result);
            return result;
        } catch (Exception e) {
            log.error("doPost error, {}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public String doPost(String apiUrl, Map<String, Object> params) {
        return doPost(apiUrl, null, params);
    }

    @Override
    public String doPost(String apiUrl) {
        return doPost(apiUrl, null, null);
    }

    @Override
    public void doGet4Sse(String url, Map<String, Object> params, Executor helper) {
        log.info("doGet4Sse begin, url={}, params={}", url, params);
        String apiUrl = buildGetMethodURL(url, params);
        log.info("apiUrl={}", apiUrl);
        CloseableHttpClient httpClient = getHttpClient(apiUrl);
        HttpGet httpGet = new HttpGet(apiUrl);

        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            if (Objects.isNull(entity)) {
                log.warn("remote call entity is null, doGet4Sse end...");
                return;
            }

            log.info("doGet4Sse end...");
            InputStream inputStream = entity.getContent();
            // 执行业务操作
            helper.doExecuteAfterSSE(inputStream);
        } catch (Exception e) {
            log.error("doGet4Sse error, {}", e.getMessage(), e);
        } finally {
            if (Objects.nonNull(httpResponse)) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    log.error("close HttpResponse error, {}", e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        log.info("Closing HTTP clients...");
        if (httpClient != null) {
            httpClient.close();
        }
        if (httpsClient != null) {
            httpsClient.close();
        }
    }
}
