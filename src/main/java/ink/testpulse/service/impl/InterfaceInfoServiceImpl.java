package ink.testpulse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ink.testpulse.common.BusinessException;
import ink.testpulse.common.ResultCode;
import ink.testpulse.entity.InterfaceInfo;
import ink.testpulse.entity.Module;
import ink.testpulse.mapper.InterfaceInfoMapper;
import ink.testpulse.service.InterfaceInfoService;
import ink.testpulse.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ink.testpulse.dto.InterfaceQueryRequest;
import org.springframework.util.StringUtils;


@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo> implements InterfaceInfoService {

    @Autowired
    private ModuleService moduleService;

    @Override
    public boolean saveInterfaceInfo(InterfaceInfo interfaceInfo) {
        // 1. 校验所属模块是否存在
        Module module = moduleService.getById(interfaceInfo.getModuleId());
        if (module == null) {
            // 复用昨天定义的模块不存在异常
            throw new BusinessException(ResultCode.MODULE_PARENT_NOT_FOUND);
        }

        // 2. 强制数据对齐：使用数据库中模块所属的 projectId 覆盖前端可能传错的 projectId
        interfaceInfo.setProjectId(module.getProjectId());

        // 3. 执行保存逻辑
        return this.save(interfaceInfo);
    }

    @Override
    public Page<InterfaceInfo> queryPage(InterfaceQueryRequest request) {
        // 1. 初始化分页对象
        Page<InterfaceInfo> page = new Page<>(request.getCurrent(), request.getSize());

        // 2. 构造查询条件
        LambdaQueryWrapper<InterfaceInfo> wrapper = new LambdaQueryWrapper<>();

        // projectId 是硬性前置条件（防止跨项目查数据）
        wrapper.eq(request.getProjectId() != null, InterfaceInfo::getProjectId, request.getProjectId());

        // 动态条件拼装：如果前端传了对应的值，才加入 SQL 条件中
        wrapper.eq(request.getModuleId() != null, InterfaceInfo::getModuleId, request.getModuleId())
                .eq(request.getStatus() != null, InterfaceInfo::getStatus, request.getStatus())
                .like(StringUtils.hasText(request.getName()), InterfaceInfo::getName, request.getName());

        // 3. 默认按创建时间倒序排列，新创建的在前面
        wrapper.orderByDesc(InterfaceInfo::getCreateTime);

        // 4. 执行分页查询并返回
        return this.page(page, wrapper);
    }
}