package ink.testpulse.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 接口在线调试-请求参数 DTO
 */
@Data
public class InterfaceDebugRequest {

    /**
     * 完整的请求URL (如: http://localhost:8080/api/login)
     */
    @NotBlank(message = "请求URL不能为空")
    private String url;

    /**
     * 请求方式 (GET/POST/PUT/DELETE等)
     */
    @NotBlank(message = "请求方法不能为空")
    private String method;

    /**
     * 请求头配置 (通常是 List<Map<String, String>> 结构)
     */
    private Object requestHeaders;

    /**
     * URL参数配置 (Query参数)
     */
    private Object requestParams;

    /**
     * 请求体类型 (none/form-data/json/raw等)
     */
    private String requestBodyType;

    /**
     * 请求体内容 (JSON字符串或表单配置)
     */
    private String requestBody;
}