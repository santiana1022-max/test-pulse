package ink.testpulse.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ink.testpulse.common.BusinessException;
import ink.testpulse.common.ResultCode;
import ink.testpulse.dto.InterfaceDebugRequest;
import ink.testpulse.dto.InterfaceDebugResponse;
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

    @Override
    public InterfaceDebugResponse executeRequest(InterfaceDebugRequest request) {
        try {
            // 1. 构建 URL (如果有 Query 参数，拼接到 URL 后面)
            HttpUrl.Builder urlBuilder = HttpUrl.parse(request.getUrl()).newBuilder();
            if (urlBuilder == null) {
                throw new BusinessException(ResultCode.VALIDATE_FAILED);
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

            // 解析并添加 Headers
            List<Map<String, String>> headers = parseObjectToList(request.getRequestHeaders());
            for (Map<String, String> header : headers) {
                if (StringUtils.hasText(header.get("name"))) {
                    requestBuilder.addHeader(header.get("name"), header.get("value"));
                }
            }

            // 3. 构建请求体 Body (仅 POST/PUT/PATCH 等需要)
            RequestBody requestBody = buildRequestBody(request);

            // OkHttp 要求 POST/PUT 必须有 Body，如果前端传了 none，我们需要造一个空的
            if (requestBody == null && HttpMethodRequiresBody(request.getMethod())) {
                requestBody = RequestBody.create(new byte[0], null);
            }

            // 设置请求方法和 Body
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
     * 判断 HTTP 方法是否强制要求带 Body
     */
    private boolean HttpMethodRequiresBody(String method) {
        String upperMethod = method.toUpperCase();
        return "POST".equals(upperMethod) || "PUT".equals(upperMethod) || "PATCH".equals(upperMethod);
    }

    /**
     * 根据类型构建 RequestBody
     */
    private RequestBody buildRequestBody(InterfaceDebugRequest request) {
        String method = request.getMethod().toUpperCase();
        if ("GET".equals(method) || "DELETE".equals(method)) {
            return null; // GET 和 DELETE 通常不需要 Body
        }

        String bodyType = request.getRequestBodyType();
        String bodyContent = request.getRequestBody();

        if ("json".equalsIgnoreCase(bodyType) && StringUtils.hasText(bodyContent)) {
            return RequestBody.create(bodyContent, MediaType.parse("application/json; charset=utf-8"));
        }
        // 预留扩展: form-data, x-www-form-urlencoded 等可以在这里后续补充

        return null;
    }

    /**
     * 将前端传来的 Object (可能是 JSON 数组) 转换为 List<Map<String, String>>
     */
    private List<Map<String, String>> parseObjectToList(Object obj) {
        if (obj == null) {
            return List.of();
        }
        try {
            // 将 Object 先转成 JSON 字符串，再反序列化为 List<Map>，保证类型安全
            String jsonStr = objectMapper.writeValueAsString(obj);
            return objectMapper.readValue(jsonStr, new TypeReference<List<Map<String, String>>>() {});
        } catch (Exception e) {
            log.warn("解析参数失败: {}", obj, e);
            return List.of();
        }
    }
}