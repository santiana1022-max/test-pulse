package ink.testpulse.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 接口信息响应 DTO (用于列表和详情脱敏展示)
 */
@Data
public class InterfaceInfoResponse {

    private Long id;

    private Long projectId;

    private Long moduleId;

    private String name;

    private String path;

    private String method;

    private Integer status;

    private Object requestHeaders;

    private Object requestParams;

    private String requestBodyType;

    private String requestBody;

    private String responseBody;

    private String createBy;

    private String updateBy;

    /**
     * 创建时间 (格式化输出，解决前端数组乱码问题)
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    // 💡 注意：这里没有 deleted 字段，彻底屏蔽底层逻辑删除标识！
}