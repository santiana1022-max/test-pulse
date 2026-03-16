package ink.testpulse.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import ink.testpulse.common.Result;
import ink.testpulse.dto.EnvironmentSaveRequest;
import ink.testpulse.entity.Environment;
import ink.testpulse.service.EnvironmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目环境管理控制层
 */
@RestController
@RequestMapping("/api/environment")
public class EnvironmentController {

    @Autowired
    private EnvironmentService environmentService;

    /**
     * 新增或修改环境配置
     */
    @PostMapping("")
    public Result<Long> saveOrUpdate(@Validated @RequestBody EnvironmentSaveRequest request) {
        Environment environment = new Environment();
        BeanUtils.copyProperties(request, environment);

        // MyBatis-Plus 自带的 saveOrUpdate：有 ID 就更新，没 ID 就插入
        environmentService.saveOrUpdate(environment);
        return Result.success(environment.getId());
    }

    /**
     * 获取指定项目下的所有环境列表
     */
    @GetMapping("/list/{projectId}")
    public Result<List<Environment>> listByProjectId(@PathVariable Long projectId) {
        List<Environment> list = environmentService.list(
                new LambdaQueryWrapper<Environment>()
                        .eq(Environment::getProjectId, projectId)
                        .orderByDesc(Environment::getCreateTime)
        );
        return Result.success(list);
    }
}