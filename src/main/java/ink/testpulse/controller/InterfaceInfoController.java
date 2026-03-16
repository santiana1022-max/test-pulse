package ink.testpulse.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ink.testpulse.common.Result;
import ink.testpulse.dto.InterfaceInfoResponse;
import ink.testpulse.dto.InterfaceInfoSaveRequest;
import ink.testpulse.dto.InterfaceQueryRequest;
import ink.testpulse.service.InterfaceInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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
     * 分页多条件查询接口列表 (脱敏输出)
     */
    @PostMapping("/page")
    public Result<Page<InterfaceInfoResponse>> pageQuery(@Validated @RequestBody InterfaceQueryRequest request) {
        return Result.success(interfaceInfoService.queryPage(request));
    }

    /**
     * 新增或更新接口 (合并原本的 create 和 update)
     */
    @PostMapping("/save")
    public Result<String> save(@Validated @RequestBody InterfaceInfoSaveRequest request) {
        interfaceInfoService.saveInterfaceInfo(request);
        return Result.success("接口保存成功");
    }

    /**
     * 根据模块ID获取接口列表 (脱敏输出)
     */
    @GetMapping("/list/{moduleId}")
    public Result<List<InterfaceInfoResponse>> getListByModule(@PathVariable Long moduleId) {
        return Result.success(interfaceInfoService.getListByModuleId(moduleId));
    }

    /**
     * 获取单个接口详情 (脱敏输出)
     */
    @GetMapping("/{id}")
    public Result<InterfaceInfoResponse> getDetail(@PathVariable Long id) {
        return Result.success(interfaceInfoService.getInterfaceDetail(id));
    }

    /**
     * 安全删除接口
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        interfaceInfoService.deleteInterface(id);
        return Result.success("接口删除成功");
    }
}