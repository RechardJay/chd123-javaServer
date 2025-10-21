package jay.chd123.problem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jay.chd123.problem.entity.db.ProblemTag;

import java.util.List;

public interface ProblemTagService extends IService<ProblemTag> {
    /**
     * 根据tag筛选出所有复合条件的题目id。 包含即复合
     * @param tagList 标签列表
     * @return 符合要求的题目id
     */
    List<Integer> findProblemIdsByTags(List<String> tagList);
}
