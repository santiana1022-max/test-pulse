package ink.testpulse.controller;

import ink.testpulse.common.Result;
import ink.testpulse.dto.EnvironmentSaveRequest;
import ink.testpulse.entity.Environment;
import ink.testpulse.service.EnvironmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/environment")
public class EnvironmentController {

    @Autowired
    private EnvironmentService environmentService;

    /**
     * 新增或修改环境配置
     */
    @PostMapping("/save")
    public Result<Long> save(@Validated @RequestBody EnvironmentSaveRequest request) {
        // 逻辑下沉到 Service
        return Result.success(environmentService.saveEnvironment(request));
    }

    /**
     * 获取指定项目下的所有环境列表 (脱敏后的列表)
     */
    @GetMapping("/list/{projectId}")
    public Result<List<Environment>> listByProjectId(@PathVariable Long projectId) {
        // 注意：如果以后 Environment 也有很多统计字段或脱敏需求，建议这里也转为 EnvironmentResponse
        // 目前先下沉逻辑到 Service 保证架构一致性
        return Result.success(environmentService.listByProjectId(projectId));
    }

    /**
     * 删除环境
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        environmentService.removeById(id);
        return Result.success("环境配置已删除");
    }
}