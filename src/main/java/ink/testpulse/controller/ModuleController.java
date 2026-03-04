package ink.testpulse.controller;

import ink.testpulse.common.Result;
import ink.testpulse.common.ResultCode;
import ink.testpulse.entity.Module;
import ink.testpulse.entity.vo.ModuleVO;
import ink.testpulse.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 模块管理控制层
 */
@RestController
@RequestMapping("/api/module")
public class ModuleController {

    @Autowired
    private ModuleService moduleService;

    /**
     * 新增模块
     */
    @PostMapping("/create")
    public Result<String> create(@RequestBody Module module) {
        // 调用 Service 层包含层级校验的创建方法
        boolean success = moduleService.createModule(module);
        return success ? Result.success("模块创建成功") : Result.error(ResultCode.ERROR);
    }

    /**
     * 获取指定项目的模块树
     * @param projectId 项目ID
     */
    @GetMapping("/tree/{projectId}")
    public Result<List<ModuleVO>> getTree(@PathVariable Long projectId) {
        List<ModuleVO> tree = moduleService.getModuleTree(projectId);
        return Result.success(tree);
    }

    /**
     * 获取单个模块详情
     */
    @GetMapping("/{id}")
    public Result<Module> getDetail(@PathVariable Long id) {
        Module module = moduleService.getById(id);
        return module != null ? Result.success(module) : Result.error(ResultCode.MODULE_PARENT_NOT_FOUND);
    }

    /**
     * 更新模块信息
     */
    @PutMapping("/update")
    public Result<String> update(@RequestBody Module module) {
        // 更新模块名或描述，直接使用 MyBatis-Plus 的 updateById
        boolean success = moduleService.updateById(module);
        return success ? Result.success("模块更新成功") : Result.error(ResultCode.ERROR);
    }

    /**
     * 安全删除模块
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        // 调用 Service 层包含联动校验的安全删除方法
        boolean success = moduleService.deleteModule(id);
        return success ? Result.success("模块已成功删除") : Result.error(ResultCode.ERROR);
    }
}