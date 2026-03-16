package ink.testpulse.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ink.testpulse.common.BusinessException;
import ink.testpulse.common.ResultCode;
import ink.testpulse.dto.InterfaceDebugRequest;
import ink.testpulse.dto.InterfaceDebugResponse;
import ink.testpulse.entity.Environment;
import ink.testpulse.service.EnvironmentService;
import ink.testpulse.service.HttpEngineService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class HttpEngineServiceImpl implements HttpEngineService {

    @Autowired
    private OkHttpClient okHttpClient;

    @Autowired
    private ObjectMapper objectMapper;

    // 引入环境服务
    @Autowired
    private EnvironmentService environmentService;

    @Override
    public InterfaceDebugResponse executeRequest(InterfaceDebugRequest request) {
        try {
            // 1. 智能拼装最终的 URL (核心改造点)
            String finalUrlStr = buildFinalUrl(request);

            HttpUrl.Builder urlBuilder = HttpUrl.parse(finalUrlStr).newBuilder();
            if (urlBuilder == null) {
                throw new BusinessException( "非法的请求URL格式: " + finalUrlStr);
            }

            // 解析并添加 Query Params
            List<Map<String, String>> params = parseObjectToList(request.getRequestParams());
            for (Map<String, String> param : params) {
                if (StringUtils.hasText(param.get("name"))) {
                    urlBuilder.addQueryParameter(param.get("name"), param.get("value"));
                }
            }
            HttpUrl finalUrl = urlBuilder.build();

            // 2. 构建 Request.Builder 并设置 URL 和 Headers
            Request.Builder requestBuilder = new Request.Builder().url(finalUrl);

            List<Map<String, String>> headers = parseObjectToList(request.getRequestHeaders());
            for (Map<String, String> header : headers) {
                if (StringUtils.hasText(header.get("name"))) {
                    requestBuilder.addHeader(header.get("name"), header.get("value"));
                }
            }

            // 3. 构建请求体 Body
            RequestBody requestBody = buildRequestBody(request);
            if (requestBody == null && HttpMethodRequiresBody(request.getMethod())) {
                requestBody = RequestBody.create(new byte[0], null);
            }
            requestBuilder.method(request.getMethod().toUpperCase(), requestBody);

            // 4. 发送请求并计算耗时
            Request okHttpRequest = requestBuilder.build();
            long startTime = System.currentTimeMillis();

            try (Response response = okHttpClient.newCall(okHttpRequest).execute()) {
                long endTime = System.currentTimeMillis();

                // 5. 封装响应结果
                Map<String, String> responseHeaders = new HashMap<>();
                for (String name : response.headers().names()) {
                    responseHeaders.put(name, response.header(name));
                }

                String responseBodyStr = response.body() != null ? response.body().string() : "";

                return InterfaceDebugResponse.builder()
                        .statusCode(response.code())
                        .responseHeaders(responseHeaders)
                        .responseBody(responseBodyStr)
                        .responseTime(endTime - startTime)
                        .build();
            }

        } catch (IOException e) {
            log.error("接口调试请求发送失败", e);
            throw new BusinessException( "请求发送失败: " + e.getMessage());
        }
    }

    /**
     * 根据是否选择环境，动态拼装最终的请求 URL
     */
    private String buildFinalUrl(InterfaceDebugRequest request) {
        // 如果前端传了环境ID，则走拼装逻辑
        if (request.getEnvironmentId() != null) {
            Environment env = environmentService.getById(request.getEnvironmentId());
            if (env == null) {
                throw new BusinessException( "所选的运行环境不存在");
            }
            String baseUrl = env.getBaseUrl();
            String path = request.getPath() == null ? "" : request.getPath();

            // 优雅处理斜杠，防止出现 http://api.com//login 这种双斜杠错误
            if (baseUrl.endsWith("/") && path.startsWith("/")) {
                return baseUrl + path.substring(1);
            } else if (!baseUrl.endsWith("/") && !path.startsWith("/") && StringUtils.hasText(path)) {
                return baseUrl + "/" + path;
            }
            return baseUrl + path;
        }

        // 如果没传环境ID，必须传完整的URL
        if (!StringUtils.hasText(request.getUrl())) {
            throw new BusinessException("未选择环境时，必须提供完整的请求URL");
        }
        return request.getUrl();
    }

    private boolean HttpMethodRequiresBody(String method) {
        String upperMethod = method.toUpperCase();
        return "POST".equals(upperMethod) || "PUT".equals(upperMethod) || "PATCH".equals(upperMethod);
    }

    private RequestBody buildRequestBody(InterfaceDebugRequest request) {
        String method = request.getMethod().toUpperCase();
        if ("GET".equals(method) || "DELETE".equals(method)) {
            return null;
        }

        String bodyType = request.getRequestBodyType();
        String bodyContent = request.getRequestBody();

        if ("json".equalsIgnoreCase(bodyType) && StringUtils.hasText(bodyContent)) {
            return RequestBody.create(bodyContent, MediaType.parse("application/json; charset=utf-8"));
        }
        return null;
    }

    private List<Map<String, String>> parseObjectToList(Object obj) {
        if (obj == null) return List.of();
        try {
            String jsonStr = objectMapper.writeValueAsString(obj);
            return objectMapper.readValue(jsonStr, new TypeReference<List<Map<String, String>>>() {});
        } catch (Exception e) {
            log.warn("解析参数失败: {}", obj, e);
            return List.of();
        }
    }
}