package jay.chd123.problem.service.servieImpl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jay.chd123.global.customException.BusinessException;
import jay.chd123.global.entity.BasePageReturn;
import jay.chd123.problem.entity.db.Problem;
import jay.chd123.problem.entity.db.ProblemCase;
import jay.chd123.problem.entity.db.ProblemTag;
import jay.chd123.problem.mapper.ProblemMapper;
import jay.chd123.problem.service.CaseService;
import jay.chd123.problem.service.ProblemService;
import jay.chd123.problem.service.TagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProblemServiceImpl extends ServiceImpl<ProblemMapper, Problem> implements ProblemService {
    final CaseService caseService;
    final TagService tagService;

    public ProblemServiceImpl(CaseService caseService, TagService tagService) {
        this.caseService = caseService;
        this.tagService = tagService;
    }
    @Override
    @Transactional
    public Integer createProblem(Problem problem) {
        this.save(problem);
        int id = problem.getId();
        addTagAndCaseOfProblem(problem);
        return id;
    }
    @Override
    @Transactional
    public Integer updateProblem(Problem problem) {
        this.updateById(problem);
        int id = problem.getId();
        QueryWrapper<ProblemTag> tagDelete = new QueryWrapper<>();
        tagDelete.eq("sId", id);
        tagService.remove(tagDelete);
        QueryWrapper<ProblemCase> caseDelete = new QueryWrapper<>();
        caseDelete.eq("sId", id);
        caseDelete.eq("type", ProblemCase.TYPE.EXAMPLE.name());
        caseService.remove(caseDelete);
        addTagAndCaseOfProblem(problem);
        return id;
    }

    @Override
    public Problem getProblemDetail(Integer id, String problemId) {
        Problem info;
        if(id != null){
            info = baseMapper.selectById(id);
        }
        else if(StrUtil.isNotBlank(problemId)){
            QueryWrapper<Problem> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("problemId", problemId);
            info = baseMapper.selectOne(queryWrapper);
        }else{
            throw new BusinessException(-1,"缺少查询参数");
        }
        if(info==null){
            throw new BusinessException(-1,"没有匹配数据");
        }
        id = info.getId();
        //查询案例信息，注意只有测试案例
        QueryWrapper<ProblemCase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sId",id).eq("type", ProblemCase.TYPE.EXAMPLE.name());
        List<ProblemCase> cases = caseService.list(queryWrapper);
        List<String> inputList = new ArrayList<>();
        List<String> outputList = new ArrayList<>();
        cases.forEach(i ->{
            inputList.add(i.getInput());
            outputList.add(i.getOutput());
        });
        info.setInputs(inputList);
        info.setOutputs(outputList);
        //查询标签信息
        QueryWrapper<ProblemTag> tagQueryWrapper = new QueryWrapper<>();
        tagQueryWrapper.eq("sId",id);
        List<ProblemTag> tags = tagService.list(tagQueryWrapper);
        List<String> tagList = new ArrayList<>();
        tags.forEach(i-> tagList.add(i.getTagName()));
        info.setTags(tagList);
        return info;
    }
    private void addTagAndCaseOfProblem(Problem problem) {
        int id = problem.getId();
        String sCode = problem.getCode();
        List<String> inputs = problem.getInputs();
        List<String> outputs = problem.getOutputs();
        List<ProblemCase> caseList = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            if(i == inputs.size()||i==outputs.size()) break;
            ProblemCase pCase = ProblemCase.builder()
                    .sId(id)
                    .sCode(sCode)
                    .input(inputs.get(i))
                    .output(outputs.get(i))
                    .type(ProblemCase.TYPE.EXAMPLE.name())
                    .build();
            caseList.add(pCase);
        }
        List<ProblemTag> tagList = new ArrayList<>();
        if(problem.getTags()!=null){
            for(String tagName : problem.getTags()) {
                ProblemTag tag = new ProblemTag();
                tag.setSId(id);
                tag.setSCode(sCode);
                tag.setTagName(tagName);
                tagList.add(tag);
            }
        }
        tagService.saveBatch(tagList);
        caseService.saveBatch(caseList);
    }
    @Override
    public BasePageReturn<Problem.ProblemItem> getProblemPage(int page, int pageSize, List<String> tags) {
        Page<Problem> pageInfo = new Page<>(page, pageSize);
        int total;
        List<Problem.ProblemItem> items ;
        //无tag标签筛选,直接分页后补充标签信息
        if(tags!=null){ //前端可能不传tags
            tags.removeIf(StrUtil::isBlank);
        }
        if(CollectionUtil.isEmpty(tags)){
            //查询所有Problem（分页后）
            IPage<Problem> problems = this.page(pageInfo);
            //获取总记录数量
            total =(int) problems.getTotal();
            //处理记录实体列表
            List<Problem> records = problems.getRecords();
            List<Integer> sIdList = new ArrayList<>();
            items = records.stream().map(Problem.ProblemItem::new)
                    .peek(i->sIdList.add(i.getId()))
                    .collect(Collectors.toList());
            QueryWrapper<ProblemTag> tagQueryWrapper = new QueryWrapper<>();
            tagQueryWrapper.eq("sId",sIdList);
            List<ProblemTag> tagList = tagService.list(tagQueryWrapper);
            Map<Integer,List<String>> tagsMap = new HashMap<>();
            for(ProblemTag tag:tagList){
                List<String> list = tagsMap.getOrDefault(tag.getSId(), new ArrayList<>());
                list.add(tag.getTagName());
                tagsMap.put(tag.getSId(), list);
            }
            for (Problem.ProblemItem item : items) {
                Integer id = item.getId();
                List<String> list = tagsMap.getOrDefault(id, Collections.emptyList());
                item.setTags(list);
            }

        } else{
            List<Integer> sIdList = tagService.findProblemIdsByTags(tags);
            if(sIdList.isEmpty()){
                return new BasePageReturn<>(1,pageSize,0);
            }
            //获取所有tag
            QueryWrapper<ProblemTag> tagQueryWrapper = new QueryWrapper<>();
            tagQueryWrapper.in("sId",sIdList);
            List<ProblemTag> tagList = tagService.list(tagQueryWrapper);
            Map<Integer,List<String>> tagsMap = new HashMap<>();
            for(ProblemTag tag:tagList){
                List<String> list = tagsMap.getOrDefault(tag.getSId(), Collections.emptyList());
                list.add(tag.getTagName());
                tagsMap.put(tag.getSId(), list);
            }
            QueryWrapper<Problem> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("id",sIdList);
            Page<Problem> problems = this.page(pageInfo, queryWrapper);
            List<Problem> records = problems.getRecords();
            items = records.stream().map(Problem.ProblemItem::new)
                    .collect(Collectors.toList());
            for (Problem.ProblemItem item : items) {
                Integer id = item.getId();
                List<String> list = tagsMap.getOrDefault(id, Collections.emptyList());
                item.setTags(list);
            }
            total =(int) problems.getTotal();
        }
        BasePageReturn<Problem.ProblemItem> res = new BasePageReturn<>(page,pageSize,total);
        res.setRecords(items);
        return res;
    }
}
