package jay.chd123.problem.service.servieImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jay.chd123.problem.entity.db.ProblemTag;
import jay.chd123.problem.mapper.ProblemTagMapper;
import jay.chd123.problem.service.ProblemTagService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProblemTagServiceImpl extends ServiceImpl<ProblemTagMapper, ProblemTag> implements ProblemTagService {
    @Override
    public List<Integer> findProblemIdsByTags(List<String> tagList){
        List<Integer> ids = baseMapper.findProblemIdsWithAnyTags(tagList);
        return ids;
    }
}
