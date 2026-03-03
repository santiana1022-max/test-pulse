package ink.testpulse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ink.testpulse.entity.Project;
import org.apache.ibatis.annotations.Mapper;

/**
 * 项目表 Mapper 接口
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
    // 继承 BaseMapper 后，基本的 CRUD (增删改查) 已经自动拥有了
}