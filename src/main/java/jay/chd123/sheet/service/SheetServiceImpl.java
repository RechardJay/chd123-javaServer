package jay.chd123.sheet.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jay.chd123.problem.service.servieImpl.ProblemServiceImpl;
import jay.chd123.sheet.entity.db.Sheet;
import jay.chd123.sheet.mapper.SheetMapper;
import jay.chd123.sheet.mapper.SheetProblemMapper;
import org.springframework.stereotype.Service;

@Service
public class SheetServiceImpl extends ServiceImpl<SheetMapper, Sheet> {
    private final SheetProblemMapper sheetProblemMapper;
    private final ProblemServiceImpl problemServiceImpl;


    public SheetServiceImpl(SheetProblemMapper sheetProblemMapper, ProblemServiceImpl problemServiceImpl) {
        this.sheetProblemMapper = sheetProblemMapper;
        this.problemServiceImpl = problemServiceImpl;
    }
}
