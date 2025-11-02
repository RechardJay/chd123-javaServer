package jay.chd123.learn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import jay.chd123.learn.entity.db.AlbumNoteRelation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AlbumNotesMapper extends BaseMapper<AlbumNoteRelation> {
}
