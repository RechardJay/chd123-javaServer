package jay.chd123.problem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import jay.chd123.problem.entity.db.ProblemTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface ProblemTagMapper extends BaseMapper<ProblemTag> {
    @Select({
            "<script>",
            "SELECT DISTINCT sId FROM problem_tags",
            "WHERE tagName IN",
            "<foreach collection='tagList' item='tag' open='(' separator=',' close=')'>",
            "#{tag}",
            "</foreach>",
            "</script>"
    })
    List<Integer> findProblemIdsWithAnyTags(@Param("tagList") List<String> tagList);
}
