package ink.testpulse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ink.testpulse.entity.Module;
import org.apache.ibatis.annotations.Mapper;

/**
 * 模块数据访问层
 */
@Mapper
public interface ModuleMapper extends BaseMapper<Module> {
    // 基础的增删改查已由 BaseMapper 提供
    // 后续如果有极其复杂的定制化 SQL（比如跨越多表的复杂统计），可以写在这里和对应的 XML 中
}