package ink.testpulse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 接口在线调试-真实响应结果 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterfaceDebugResponse {

    /**
     * HTTP 状态码 (如: 200, 404, 500)
     */
    private Integer statusCode;

    /**
     * 真实的响应头字典
     */
    private Map<String, String> responseHeaders;

    /**
     * 真实的响应体内容 (通常为 JSON 或 HTML 字符串)
     */
    private String responseBody;

    /**
     * 请求总耗时 (单位: 毫秒)
     */
    private Long responseTime;
}