package ink.testpulse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ink.testpulse.entity.Environment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EnvironmentMapper extends BaseMapper<Environment> {
}