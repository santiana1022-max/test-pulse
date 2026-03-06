package ink.testpulse.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import ink.testpulse.common.Result;
import ink.testpulse.common.ResultCode;
import ink.testpulse.entity.InterfaceInfo;
import ink.testpulse.service.InterfaceInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 接口信息管理控制层
 */
@RestController
@RequestMapping("/api/interface")
public class InterfaceInfoController {

    @Autowired
    private InterfaceInfoService interfaceInfoService;

    /**
     * 新增接口
     */
    @PostMapping("/create")
    public Result<String> create(@RequestBody InterfaceInfo interfaceInfo) {
        // 调用 Service 层的防腐层逻辑，确保项目和模块的关联正确
        boolean success = interfaceInfoService.saveInterfaceInfo(interfaceInfo);
        return success ? Result.success("接口创建成功") : Result.error(ResultCode.ERROR);
    }

    /**
     * 根据模块ID获取接口列表 (点击左侧模块树时使用)
     * @param moduleId 模块ID
     */
    @GetMapping("/list/{moduleId}")
    public Result<List<InterfaceInfo>> getListByModule(@PathVariable Long moduleId) {
        LambdaQueryWrapper<InterfaceInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InterfaceInfo::getModuleId, moduleId)
                .orderByDesc(InterfaceInfo::getCreateTime); // 默认按最新创建排序
        List<InterfaceInfo> list = interfaceInfoService.list(wrapper);
        return Result.success(list);
    }

    /**
     * 获取单个接口详情
     */
    @GetMapping("/{id}")
    public Result<InterfaceInfo> getDetail(@PathVariable Long id) {
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        return interfaceInfo != null ? Result.success(interfaceInfo) : Result.error(ResultCode.ERROR);
    }

    /**
     * 更新接口信息
     */
    @PutMapping("/update")
    public Result<String> update(@RequestBody InterfaceInfo interfaceInfo) {
        boolean success = interfaceInfoService.updateById(interfaceInfo);
        return success ? Result.success("接口更新成功") : Result.error(ResultCode.ERROR);
    }

    /**
     * 删除接口
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = interfaceInfoService.removeById(id);
        return success ? Result.success("接口删除成功") : Result.error(ResultCode.ERROR);
    }
}