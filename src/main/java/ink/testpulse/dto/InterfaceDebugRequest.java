package ink.testpulse.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 接口在线调试-请求参数 DTO
 */
@Data
public class InterfaceDebugRequest {

    /**
     * 选中的环境ID (如果不传，则退化为自由模式，必须传完整的url)
     */
    private Long environmentId;

    /**
     * 接口路径 (如: /api/v1/login)
     */
    private String path;

    /**
     * 完整的请求URL (用于没有选中环境时的自由发包)
     */
    private String url;

    /**
     * 请求方式 (GET/POST/PUT/DELETE等)
     */
    @NotBlank(message = "请求方法不能为空")
    private String method;

    private Object requestHeaders;
    private Object requestParams;
    private String requestBodyType;
    private String requestBody;
}