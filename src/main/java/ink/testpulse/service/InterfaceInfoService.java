package ink.testpulse.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ink.testpulse.dto.InterfaceQueryRequest;
import ink.testpulse.entity.InterfaceInfo;

public interface InterfaceInfoService extends IService<InterfaceInfo> {

    boolean saveInterfaceInfo(InterfaceInfo interfaceInfo);

    /**
     * 分页多条件查询接口列表
     * @param request 查询条件 DTO
     * @return 分页结果
     */
    Page<InterfaceInfo> queryPage(InterfaceQueryRequest request);
}