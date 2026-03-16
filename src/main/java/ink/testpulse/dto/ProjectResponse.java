package ink.testpulse.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目详情响应体 (用于回显给前端)
 */
@Data
public class ProjectResponse {

    private Long id;

    private String name;

    private String identifier;

    private String description;

    private String owner;

    // --- 以下全是后端控制和统计的只读字段，大方地展示给前端 ---

    private Integer interfaceCount;

    private Integer caseCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastExecuteTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}