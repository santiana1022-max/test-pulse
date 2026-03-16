package ink.testpulse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ink.testpulse.common.BusinessException;
import ink.testpulse.common.ResultCode;
import ink.testpulse.dto.InterfaceInfoResponse;
import ink.testpulse.dto.InterfaceInfoSaveRequest;
import ink.testpulse.dto.InterfaceQueryRequest;
import ink.testpulse.entity.InterfaceInfo;
import ink.testpulse.entity.Module;
import ink.testpulse.mapper.InterfaceInfoMapper;
import ink.testpulse.service.InterfaceInfoService;
import ink.testpulse.service.ModuleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo> implements InterfaceInfoService {

    @Autowired
    private ModuleService moduleService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveInterfaceInfo(InterfaceInfoSaveRequest request) {
        // 1. 校验所属模块是否存在
        Module module = moduleService.getById(request.getModuleId());
        if (module == null) {
            throw new BusinessException(ResultCode.MODULE_PARENT_NOT_FOUND);
        }

        // 2. DTO 转 Entity
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        // 如果是更新，先查出原数据，避免丢字段
        if (request.getId() != null) {
            interfaceInfo = this.getById(request.getId());
            if (interfaceInfo == null) {
                throw new BusinessException(ResultCode.ERROR, "该接口不存在或已被删除");
            }
        }

        BeanUtils.copyProperties(request, interfaceInfo);

        // 3. 强制数据对齐：使用数据库中模块所属的 projectId 覆盖前端可能传错的数据
        interfaceInfo.setProjectId(module.getProjectId());

        // 4. 执行保存或更新
        this.saveOrUpdate(interfaceInfo);
    }

    @Override
    public Page<InterfaceInfoResponse> queryPage(InterfaceQueryRequest request) {
        Page<InterfaceInfo> page = new Page<>(request.getCurrent(), request.getSize());

        LambdaQueryWrapper<InterfaceInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(request.getProjectId() != null, InterfaceInfo::getProjectId, request.getProjectId())
                .eq(request.getModuleId() != null, InterfaceInfo::getModuleId, request.getModuleId())
                .eq(request.getStatus() != null, InterfaceInfo::getStatus, request.getStatus())
                .like(StringUtils.hasText(request.getName()), InterfaceInfo::getName, request.getName())
                .orderByDesc(InterfaceInfo::getCreateTime);

        // 获取底层分页数据
        Page<InterfaceInfo> interfacePage = this.page(page, wrapper);

        // 流式转换为 DTO 分页对象
        Page<InterfaceInfoResponse> responsePage = new Page<>(interfacePage.getCurrent(), interfacePage.getSize(), interfacePage.getTotal());
        List<InterfaceInfoResponse> responseList = interfacePage.getRecords().stream().map(info -> {
            InterfaceInfoResponse response = new InterfaceInfoResponse();
            BeanUtils.copyProperties(info, response);
            return response;
        }).collect(Collectors.toList());

        responsePage.setRecords(responseList);
        return responsePage;
    }

    @Override
    public List<InterfaceInfoResponse> getListByModuleId(Long moduleId) {
        List<InterfaceInfo> list = this.list(new LambdaQueryWrapper<InterfaceInfo>()
                .eq(InterfaceInfo::getModuleId, moduleId)
                .orderByDesc(InterfaceInfo::getCreateTime));

        return list.stream().map(info -> {
            InterfaceInfoResponse response = new InterfaceInfoResponse();
            BeanUtils.copyProperties(info, response);
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public InterfaceInfoResponse getInterfaceDetail(Long id) {
        InterfaceInfo interfaceInfo = this.getById(id);
        if (interfaceInfo == null) {
            throw new BusinessException(ResultCode.ERROR, "接口不存在");
        }
        InterfaceInfoResponse response = new InterfaceInfoResponse();
        BeanUtils.copyProperties(interfaceInfo, response);
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteInterface(Long id) {
        InterfaceInfo interfaceInfo = this.getById(id);
        if (interfaceInfo == null) {
            throw new BusinessException(ResultCode.ERROR, "接口不存在");
        }

        // TODO: 强校验预留 - 未来在这里校验该接口是否被测试用例(TestCase)绑定
        // 如果被绑定，抛出异常阻止删除

        this.removeById(id);
    }
}