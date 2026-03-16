package ink.testpulse.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ink.testpulse.dto.InterfaceInfoResponse;
import ink.testpulse.dto.InterfaceInfoSaveRequest;
import ink.testpulse.dto.InterfaceQueryRequest;
import ink.testpulse.entity.InterfaceInfo;

import java.util.List;

public interface InterfaceInfoService extends IService<InterfaceInfo> {

    /**
     * 保存或更新接口信息
     */
    void saveInterfaceInfo(InterfaceInfoSaveRequest request);

    /**
     * 分页多条件查询接口列表
     */
    Page<InterfaceInfoResponse> queryPage(InterfaceQueryRequest request);

    /**
     * 根据模块ID获取接口列表
     */
    List<InterfaceInfoResponse> getListByModuleId(Long moduleId);

    /**
     * 获取接口详情
     */
    InterfaceInfoResponse getInterfaceDetail(Long id);

    /**
     * 安全删除接口
     */
    void deleteInterface(Long id);
}