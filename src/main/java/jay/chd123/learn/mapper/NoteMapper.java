package jay.chd123.learn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import jay.chd123.learn.entity.Note;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoteMapper extends BaseMapper<Note> {
}
