package ink.testpulse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 接口保存与更新请求 DTO
 */
@Data
public class InterfaceInfoSaveRequest {

    /**
     * 接口ID (更新时必传，新增时为空)
     */
    private Long id;

    /**
     * 所属模块ID (核心外键，必须指定归属)
     */
    @NotNull(message = "所属模块不能为空")
    private Long moduleId;

    /**
     * 所属项目ID (前端可传可不传，Service层会通过 moduleId 强制对齐)
     */
    private Long projectId;

    /**
     * 接口名称
     */
    @NotBlank(message = "接口名称不能为空")
    private String name;

    /**
     * 接口路径 (如: /api/v1/login)
     */
    @NotBlank(message = "接口路径不能为空")
    private String path;

    /**
     * 请求方式 (GET/POST/PUT/DELETE等)
     */
    @NotBlank(message = "请求方式不能为空")
    private String method;

    /**
     * 状态 (0-草稿, 1-发布, 2-废弃)
     */
    private Integer status;

    /**
     * 请求头配置 (前端传来的 JSON 对象/数组)
     */
    private Object requestHeaders;

    /**
     * URL参数配置 (前端传来的 JSON 对象/数组)
     */
    private Object requestParams;

    /**
     * 请求体类型 (none/form-data/json/raw等)
     */
    private String requestBodyType;

    /**
     * 请求体内容
     */
    private String requestBody;

    /**
     * 预期响应体
     */
    private String responseBody;
}