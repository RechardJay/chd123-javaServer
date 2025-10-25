package jay.chd123.learn.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jay.chd123.learn.entity.Note;
import jay.chd123.learn.mapper.NoteMapper;
import org.springframework.stereotype.Service;

@Service
public class NoteServiceImpl extends ServiceImpl<NoteMapper, Note> {
}
