package jay.chd123.problem.service.servieImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jay.chd123.problem.entity.db.Tag;
import jay.chd123.problem.mapper.TagMapper;
import jay.chd123.problem.service.TagService;
import org.springframework.stereotype.Service;

@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
}
