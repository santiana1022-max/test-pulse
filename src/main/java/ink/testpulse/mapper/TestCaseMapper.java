package ink.testpulse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ink.testpulse.entity.TestCase;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TestCaseMapper extends BaseMapper<TestCase> {
}