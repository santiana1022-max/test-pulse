package ink.testpulse.controller;

import ink.testpulse.common.Result;
import ink.testpulse.dto.ModuleSaveRequest;
import ink.testpulse.dto.ModuleTreeResponse;
import ink.testpulse.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/module")
public class ModuleController {

    @Autowired
    private ModuleService moduleService;

    /**
     * 新增或修改模块
     */
    @PostMapping("/save")
    public Result<String> save(@Validated @RequestBody ModuleSaveRequest request) {
        moduleService.saveModule(request);
        return Result.success("模块保存成功");
    }

    /**
     * 获取指定项目的模块树 (核心接口)
     */
    @GetMapping("/tree/{projectId}")
    public Result<List<ModuleTreeResponse>> getTree(@PathVariable Long projectId) {
        // 调用下沉后的树形组装算法
        return Result.success(moduleService.getModuleTree(projectId));
    }

    /**
     * 获取单个模块详情 (脱敏版)
     */
    @GetMapping("/{id}")
    public Result<ModuleTreeResponse> getDetail(@PathVariable Long id) {
        return Result.success(moduleService.getModuleDetail(id));
    }

    /**
     * 安全删除模块 (包含级联校验)
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        moduleService.deleteModule(id);
        return Result.success("模块已成功删除");
    }
}