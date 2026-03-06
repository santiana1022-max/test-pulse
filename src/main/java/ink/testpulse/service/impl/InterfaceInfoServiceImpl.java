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
}