package com.atguigu.yygh.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;


@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {
    @Override
    @Cacheable(value = "dict",keyGenerator = "keyGenerator")
    public List<Dict> getDictChildrenByParentId(Long id) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id",id);
        List<Dict> dicts = baseMapper.selectList(queryWrapper);
        for (Dict dict : dicts) {
            dict.setHasChildren(hasDictChildren(dict.getId()));
        }
        return dicts;
    }

    @Override
    public void downloadDictExcelList(HttpServletResponse response) {
        List<Dict> dicts = baseMapper.selectList(null);
        if (dicts!=null && dicts.size()>0){
            List<DictEeVo> collects = dicts.stream().map(dict -> {
                DictEeVo dictEeVo = new DictEeVo();
                BeanUtils.copyProperties(dict, dictEeVo);
                return dictEeVo;
            }).collect(Collectors.toList());
            try {
//                System.out.println(collects);
                response.setContentType("application/vnd.ms-excel");
                response.setCharacterEncoding("utf-8");
                response.setHeader("Content-disposition", "attachment;filename="+"dict.xlsx");
                EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("dict").doWrite(collects);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    @CacheEvict(value = "dict",allEntries = true)
    public void uploadDictExcelList(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(),DictEeVo.class, new ExcelAnalysisEventListener(baseMapper)).sheet().doRead();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Dict getDictByDictCode(String dictCode) {
        QueryWrapper<Dict> dictWrapper = new QueryWrapper<>();
        dictWrapper.eq("dict_code", dictCode);
        Dict dict = baseMapper.selectOne(dictWrapper);
        return dict;
    }

    @Override
    public Dict getDictByDictCodeAndvalue(String dictCode, String value) {
        Dict dictByDictCode = getDictByDictCode(dictCode);
        QueryWrapper<Dict> dictWrapper = new QueryWrapper<>();
        dictWrapper.eq("value", value);
        dictWrapper.eq("parent_id", dictByDictCode.getId());
        Dict dict = baseMapper.selectOne(dictWrapper);
        return dict;
    }

    /**
     * 判断字典是否存在子类型
     * @param id
     * @return
     */
    public Boolean hasDictChildren(Long id) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id",id);
        Integer count = baseMapper.selectCount(queryWrapper);
        return count>0;
    }
    class ExcelAnalysisEventListener extends AnalysisEventListener<DictEeVo> {
        private BaseMapper<Dict> baseMapper;

        public ExcelAnalysisEventListener(BaseMapper baseMapper) {
            this.baseMapper = baseMapper;
        }

        @Override
        public void invoke(DictEeVo dictEeVo, AnalysisContext analysisContext) {
            Dict dict = new Dict();
            BeanUtils.copyProperties(dictEeVo,dict);
            System.out.println(dict);
            baseMapper.insert(dict);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext analysisContext) {

        }
    }
}
