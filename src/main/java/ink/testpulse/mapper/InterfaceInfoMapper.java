package ink.testpulse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ink.testpulse.entity.InterfaceInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 接口信息数据访问层
 */
@Mapper
public interface InterfaceInfoMapper extends BaseMapper<InterfaceInfo> {
}