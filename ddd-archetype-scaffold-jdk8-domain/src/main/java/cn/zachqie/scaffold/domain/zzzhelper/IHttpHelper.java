package cn.zachqie.scaffold.domain.zzzhelper;

import java.io.InputStream;
import java.util.Map;

/**
 * HTTP 端口接口（领域层只声明能力，不绑定任何序列化框架）。
 * 返回原始报文字符串，由调用方自行选择反序列化方式。
 *
 * @ClassName IHttpHelper
 * @Author qiezhichao
 * @Date 2026/6/26 14:55
 */
public interface IHttpHelper {

    public String doGet(String url, Map<String, Object> params);
    public String doGet(String url);


    public String doPost(String apiUrl, Map<String, String> header, Map<String, Object> params);
    public String doPost(String apiUrl, Map<String, Object> params);
    public String doPost(String apiUrl);


    /**
     * @desc 执行sse请求，这里使用 Executor 接口，是由于需要从SSE请求获取 InputStream
     *       但又不能直接返回 InputStream，因为我们需要关闭 CloseableHttpResponse，
     *       如果直接返回 InputStream，一旦 CloseableHttpResponse 关闭了
     *       返回的 InputStream 就无法读取数据，会抛出异常。
     *       因此采用函数式编程，将要处理的业务逻辑函数当成参数传入。
     * @author qiezhichao
     * @date 2025/6/6 17:39
     * @param
     * @return
     */
    public void doGet4Sse(String url, Map<String, Object> params, Executor helper);
    public interface Executor {
        public void doExecuteAfterSSE(InputStream inputStream) throws Exception;
    }
}
